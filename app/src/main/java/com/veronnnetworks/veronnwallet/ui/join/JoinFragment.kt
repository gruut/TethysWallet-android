package com.veronnnetworks.veronnwallet.ui.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.veronnnetworks.veronnwallet.databinding.FragmentJoinBinding
import com.veronnnetworks.veronnwallet.ui.NavigationController
import com.veronnnetworks.veronnwallet.ui.Result
import com.veronnnetworks.veronnwallet.ui.common.fragment.BaseFragment
import com.veronnnetworks.veronnwallet.utils.ProgressTimeLatch
import com.veronnnetworks.veronnwallet.utils.ext.observe
import timber.log.Timber
import javax.inject.Inject

class JoinFragment : BaseFragment() {

    private lateinit var binding: FragmentJoinBinding

    @Inject
    lateinit var navigationController: NavigationController
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val joinViewModel: JoinViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(JoinViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (joinViewModel.isKeyExist()) {
            navigationController.navigateToMainActivity()
        } else {
            joinViewModel.generateECKeyPair()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJoinBinding.inflate(inflater, container!!, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val progressTimeLatch = ProgressTimeLatch {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
        }

        binding.autonym.setOnClickListener {
            navigationController.navigateToJoinAutonym(false)
        }
        binding.anonym.setOnClickListener {
            navigationController.navigateToMainActivity()
        }

        joinViewModel.isLoading.observe(this) { isLoading ->
            progressTimeLatch.loading = isLoading ?: false
        }
        joinViewModel.generateResult.observe(this, Observer { result ->
            when (result) {
                is Result.Failure -> {
                    Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                    Timber.e(result.e)
                }
            }
        })

        lifecycle.addObserver(joinViewModel)
    }

    companion object {
        fun newInstance(): JoinFragment = JoinFragment()
    }
}
