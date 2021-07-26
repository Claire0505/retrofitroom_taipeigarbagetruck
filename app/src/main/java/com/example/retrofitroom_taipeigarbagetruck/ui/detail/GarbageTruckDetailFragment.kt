package com.example.retrofitroom_taipeigarbagetruck.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.retrofitroom_taipeigarbagetruck.R
import com.example.retrofitroom_taipeigarbagetruck.databinding.FragmentGarbageTruckBinding
import com.example.retrofitroom_taipeigarbagetruck.databinding.FragmentGarbageTruckDetailBinding

class GarbageTruckDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(activity).application

        val binding = FragmentGarbageTruckDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this

        // 此行從 GarbageTruckProperty Safe Args 中獲取選定的對象。
        val garbageTruckProperty =
            GarbageTruckDetailFragmentArgs.fromBundle(arguments!!).selectedProperty

        val viewModelFactory = GarbageTruckDetailViewModelFactory(garbageTruckProperty, application)

        binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(GarbageTruckDetailViewModel::class.java)

        return binding.root
    }
}