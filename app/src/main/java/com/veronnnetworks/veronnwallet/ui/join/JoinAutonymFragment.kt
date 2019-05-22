package com.veronnnetworks.veronnwallet.ui.join


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.veronnnetworks.veronnwallet.R
import com.veronnnetworks.veronnwallet.databinding.FragmentJoinAutonymBinding
import com.veronnnetworks.veronnwallet.model.UserInfo
import com.veronnnetworks.veronnwallet.ui.NavigationController
import com.veronnnetworks.veronnwallet.ui.Result
import com.veronnnetworks.veronnwallet.ui.common.fragment.BaseFragment
import com.veronnnetworks.veronnwallet.ui.common.view.ValidatableView
import com.veronnnetworks.veronnwallet.ui.common.view.validators.DateValidator
import com.veronnnetworks.veronnwallet.ui.common.view.validators.RequiredValidator
import com.veronnnetworks.veronnwallet.utils.ProgressTimeLatch
import com.veronnnetworks.veronnwallet.utils.ext.observe
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class JoinAutonymFragment : BaseFragment() {
    private lateinit var binding: FragmentJoinAutonymBinding
    private val validatableViews: ArrayList<ValidatableView> = ArrayList()
    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var navigationController: NavigationController
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val joinViewModel: JoinViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(JoinViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJoinAutonymBinding.inflate(inflater, container!!, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val progressTimeLatch = ProgressTimeLatch {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
        }
        initValidators()

        binding.userInfo = UserInfo()
        binding.join.setOnClickListener(this::onJoinClick)
        binding.container.setOnClickListener {
            hideKeyboard()
        }
        joinViewModel.isLoading.observe(this) { isLoading ->
            progressTimeLatch.loading = isLoading ?: false
        }
        joinViewModel.storeResult.observe(this, Observer { result ->
            when (result) {
                is Result.Success -> {
                    navigationController.navigateToMainActivity()
                }
                is Result.Failure -> {
                    Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                    Timber.e(result.e)
                }
            }
        })
    }

    private fun initValidators() {
        validatableViews.addAll(
            arrayOf(
                binding.userInfoView.userNameLayout.register(
                    RequiredValidator(getString(R.string.validation_err_required))
                ),
                binding.userInfoView.userBirthLayout.register(
                    DateValidator(getString(R.string.validation_err_date))
                ),
                binding.userInfoView.userMobileLayout.register(
                    RequiredValidator(getString(R.string.validation_err_required))
                )
            )
        )
    }

    private fun onJoinClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        hideKeyboard()

        val validations = validatableViews.map { it.validateAsCompletable() }
        compositeDisposable.clear()
        compositeDisposable.add(
            Completable.mergeDelayError(validations)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onComplete = {
                        joinViewModel.getCertificate(binding.userInfo!!)
                    },
                    onError = {
                        Toast.makeText(
                            activity,
                            R.string.validation_error_occurred,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
        )
    }

    companion object {
        fun newInstance(): JoinAutonymFragment = JoinAutonymFragment()
    }
}
