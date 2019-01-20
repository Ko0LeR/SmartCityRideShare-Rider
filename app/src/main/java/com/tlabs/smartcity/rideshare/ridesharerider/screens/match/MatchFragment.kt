package com.tlabs.smartcity.rideshare.ridesharerider.screens.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.tlabs.smartcity.rideshare.ridesharerider.databinding.MatchFragmentBinding
import com.tlabs.smartcity.rideshare.ridesharerider.util.ScopedFragment

class MatchFragment : ScopedFragment() {
    private val viewModel: MatchViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MatchViewModel::class.java)
    }


    private lateinit var binding: MatchFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MatchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.msg?.let {
            binding.msg.text = it
        }
    }
}