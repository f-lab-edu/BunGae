package com.example.bungae.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bungae.databinding.FragmentHomeBinding
import com.example.bungae.adpater.Adapter
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment() : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

    private val adapter by lazy { Adapter() }

    private val homeViewModel by lazy {
        HomeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recyclerviewHome.adapter = adapter
        binding.recyclerviewHome.layoutManager = LinearLayoutManager(activity)

        homeViewModel.getFireStorage()

        homeViewModel.itemList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        homeViewModel.message.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
        return root
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.getFireStorage()
        homeViewModel.itemList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}