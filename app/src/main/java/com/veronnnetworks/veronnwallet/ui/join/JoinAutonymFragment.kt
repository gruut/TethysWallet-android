package com.veronnnetworks.veronnwallet.ui.join


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.veronnnetworks.veronnwallet.databinding.FragmentJoinAutonymBinding
import com.veronnnetworks.veronnwallet.model.UserInfo
import com.veronnnetworks.veronnwallet.ui.NavigationController
import com.veronnnetworks.veronnwallet.ui.Result
import com.veronnnetworks.veronnwallet.ui.common.fragment.BaseFragment
import com.veronnnetworks.veronnwallet.utils.ProgressTimeLatch
import com.veronnnetworks.veronnwallet.utils.ext.observe
import timber.log.Timber
import javax.inject.Inject

class JoinAutonymFragment : BaseFragment() {

    private lateinit var binding: FragmentJoinAutonymBinding

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

        binding.userInfo = UserInfo()
        binding.join.setOnClickListener {
            hideKeyboard()
            joinViewModel.getCertificate(binding.userInfo!!)
        }
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

    companion object {
        fun newInstance(): JoinAutonymFragment = JoinAutonymFragment()
    }
}
