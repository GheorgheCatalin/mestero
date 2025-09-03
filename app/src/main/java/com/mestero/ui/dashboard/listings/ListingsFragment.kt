package com.mestero.ui.dashboard.listings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mestero.databinding.FragmentListingsBinding
import com.mestero.ui.adapters.ListingAdapter
import dagger.hilt.android.AndroidEntryPoint
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

@AndroidEntryPoint
class ListingsFragment : Fragment() {

    private var _binding: FragmentListingsBinding? = null
    private val binding get() = _binding!!
    private val args: ListingsFragmentArgs by navArgs()
    private val viewModel: ListingsViewModel by viewModels()

    private lateinit var listingAdapter: ListingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListingsBinding.inflate(inflater, container, false)
        
        // Hide the default ActionBar
        //(activity as? AppCompatActivity)?.supportActionBar?.hide() // custom toolbar
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()

        viewModel.loadListings(args.categoryId, args.subcategoryId)
    }

    private fun setupToolbar() {
        // Set the title in the system ActionBar 
        (activity as? AppCompatActivity)?.supportActionBar?.title = args.title
    }

    private fun setupRecyclerView() {
        listingAdapter = ListingAdapter { listing ->
            try {
                val action = ListingsFragmentDirections.actionListingsToListingDetail(listing.id)
                findNavController().navigate(action)
            } catch (e: Exception) {
                // Handle navigation error - fallback to a toast / log
                Log.e("ListingsFragment", "Navigation error", e)
            }
        }

        binding.listingsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listingAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshListings(args.categoryId, args.subcategoryId)
        }
    }

    private fun observeViewModel() {
        viewModel.listingsState.observe(viewLifecycleOwner) { uiState ->
            binding.swipeRefreshLayout.isRefreshing = false
            
            when (uiState) {
                is ListingsUiState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.listingsRecyclerView.isVisible = false
                    binding.emptyStateLayout.isVisible = false
                    binding.errorLayout.isVisible = false
                }
                
                is ListingsUiState.Success -> {
                    binding.progressBar.isVisible = false
                    binding.listingsRecyclerView.isVisible = true
                    binding.emptyStateLayout.isVisible = false
                    binding.errorLayout.isVisible = false
                    
                    listingAdapter.submitList(uiState.listings)
                }
                
                is ListingsUiState.Empty -> {
                    binding.progressBar.isVisible = false
                    binding.listingsRecyclerView.isVisible = false
                    binding.emptyStateLayout.isVisible = true
                    binding.errorLayout.isVisible = false
                    
                    listingAdapter.submitList(emptyList())
                }
                
                is ListingsUiState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.listingsRecyclerView.isVisible = false
                    binding.emptyStateLayout.isVisible = false
                    binding.errorLayout.isVisible = true
                    
                    // Show error message
                    binding.errorTextView.text = uiState.message
                    binding.retryButton.setOnClickListener {
                        viewModel.refreshListings(args.categoryId, args.subcategoryId)
                    }
                    
                    listingAdapter.submitList(emptyList())

                    Toast.makeText(context, "Failed to load listings", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Ensure ActionBar is hidden when returning to this fragment
       // (activity as? AppCompatActivity)?.supportActionBar?.hide()  // custom toolbar
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Only show the ActionBar if we're navigating back to the main screen
        //(activity as? AppCompatActivity)?.supportActionBar?.show() // custom toolbar
        _binding = null
    }
} 