package com.veronnnetworks.veronnwallet.ui.merger


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.veronnnetworks.veronnwallet.databinding.FragmentMergerBinding
import com.veronnnetworks.veronnwallet.model.MergerInfo
import com.veronnnetworks.veronnwallet.ui.NavigationController
import com.veronnnetworks.veronnwallet.ui.common.fragment.BaseFragment
import javax.inject.Inject

class MergerFragment : BaseFragment() {

    private lateinit var binding: FragmentMergerBinding

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

        binding.mergerInfo = MergerInfo()
        binding.ok.setOnClickListener {
            mergerViewModel.sendUserInfo(binding.mergerInfo!!)
        }

        lifecycle.addObserver(mergerViewModel)
    }

    companion object {
        fun newInstance(): MergerFragment = MergerFragment()
    }
}
