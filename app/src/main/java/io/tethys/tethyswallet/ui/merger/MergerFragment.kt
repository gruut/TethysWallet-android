package io.tethys.tethyswallet.ui.merger


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.tethys.tethyswallet.R
import io.tethys.tethyswallet.Reply
import io.tethys.tethyswallet.databinding.FragmentMergerBinding
import io.tethys.tethyswallet.ui.NavigationController
import io.tethys.tethyswallet.ui.Result
import io.tethys.tethyswallet.ui.common.fragment.BaseFragment
import io.tethys.tethyswallet.ui.common.view.ValidatableView
import io.tethys.tethyswallet.ui.common.view.validators.*
import io.tethys.tethyswallet.utils.ProgressTimeLatch
import io.tethys.tethyswallet.utils.ext.observe
import timber.log.Timber
import javax.inject.Inject

class MergerFragment : BaseFragment() {
    private lateinit var binding: FragmentMergerBinding
    private val validatableViews: ArrayList<ValidatableView> = ArrayList()
    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var navigationController: NavigationController
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val mergerViewModel: MergerViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(MergerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMergerBinding.inflate(inflater, container!!, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val progressTimeLatch = ProgressTimeLatch {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
        }

        initValidators()

        mergerViewModel.isLoading.observe(this) { isLoading ->
            progressTimeLatch.loading = isLoading ?: false
        }
        mergerViewModel.reply.observe(this) { result ->
            when (result) {
                is Result.Failure -> {
                    Timber.e(result.e)
                }
                is Result.Success -> {
                    when (result.data.status) {
                        Reply.Status.SUCCESS -> {
                            activity?.finish()
                        }
                        else -> {
                            Timber.e("Error Message From Merger: ${result.data.status}")
                        }
                    }
                }
            }
        }

        binding.container.setOnClickListener { hideKeyboard() }
        binding.ok.setOnClickListener(this::onOkClick)
        binding.mergerPasswordCheck.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    hideKeyboard()
                    onOkClick(null)
                    return@setOnEditorActionListener true
                }
                else -> return@setOnEditorActionListener false
            }
        }

        lifecycle.addObserver(mergerViewModel)
    }

    private fun initValidators() {
        val minLength = 14
        validatableViews.addAll(
            arrayOf(
                binding.mergerPortLayout.register(
                    listOf(
                        RequiredValidator(getString(R.string.validation_err_required)),
                        NumberOnlyValidator(getString(R.string.validation_err_number_only))
                    )
                ),
                binding.mergerIpLayout.register(
                    listOf(
                        RequiredValidator(getString(R.string.validation_err_required)),
                        IpValidator(getString(R.string.validation_err_ip))
                    )
                ),
                binding.mergerPasswordLayout.register(
                    listOf(
                        RequiredValidator(getString(R.string.validation_err_required)),
                        MinLengthValidator(
                            getString(R.string.validation_err_minimum, minLength),
                            minLength
                        )
                    )
                ),
                binding.mergerPasswordCheckLayout.register(
                    listOf(
                        RequiredValidator(getString(R.string.validation_err_required)),
                        MinLengthValidator(
                            getString(R.string.validation_err_minimum, minLength),
                            minLength
                        ),
                        RetypeValidator(
                            getString(R.string.validation_err_not_matched),
                            binding.mergerPassword
                        )
                    )
                )
            )
        )
    }

    private fun onOkClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        val validations = validatableViews.map { it.validateAsCompletable() }
        compositeDisposable.clear()
        compositeDisposable.add(
            Completable.mergeDelayError(validations)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onComplete = {
                        mergerViewModel.sendUserInfo(
                            binding.ipAddress,
                            binding.portNumber?.toIntOrNull(),
                            binding.password
                        )
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
        fun newInstance(): MergerFragment = MergerFragment()
    }
}
