package com.mestero.ui.dashboard.myListings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mestero.databinding.FragmentMyServicesBinding
import com.mestero.ui.adapters.ListingAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyListingsFragment : Fragment() {

    private var _binding: FragmentMyServicesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyListingsViewModel by viewModels()
    
    private lateinit var listingAdapter: ListingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        setupErrorHandling()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        listingAdapter = ListingAdapter { listing ->
            try {
                val action = MyListingsFragmentDirections.actionMyServicesToListingDetail(listing.id)
                findNavController().navigate(action)
            } catch (e: Exception) {
                // Handle navigation error - fallback to a toast / log
                Log.e("MyListingsFragment", "Navigation error", e)
            }
        }

        binding.recyclerViewServices.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listingAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshListings()
        }
    }

    private fun setupErrorHandling() {
        binding.buttonRetry.setOnClickListener {
            viewModel.refreshListings()
        }
    }

    private fun observeViewModel() {
        viewModel.myListingsState.observe(viewLifecycleOwner) { uiState ->

            binding.swipeRefreshLayout.isRefreshing = false
            
            when (uiState) {
                is MyListingsResult.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.recyclerViewServices.isVisible = false
                    binding.textViewEmpty.isVisible = false
                    binding.errorLayout.isVisible = false
                }
                
                is MyListingsResult.Success -> {
                    binding.progressBar.isVisible = false
                    binding.recyclerViewServices.isVisible = true
                    binding.textViewEmpty.isVisible = false
                    binding.errorLayout.isVisible = false
                    
                    listingAdapter.submitList(uiState.listings)
                }
                
                is MyListingsResult.Empty -> {
                    binding.progressBar.isVisible = false
                    binding.recyclerViewServices.isVisible = false
                    binding.textViewEmpty.isVisible = true
                    binding.errorLayout.isVisible = false
                    
                    listingAdapter.submitList(emptyList())
                }
                
                is MyListingsResult.Error -> {
                    binding.progressBar.isVisible = false
                    binding.recyclerViewServices.isVisible = false
                    binding.textViewEmpty.isVisible = false
                    binding.errorLayout.isVisible = true
                    binding.textViewError.text = uiState.message
                    listingAdapter.submitList(emptyList())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 