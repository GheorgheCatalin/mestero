package com.mestero.ui.dashboard.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mestero.databinding.FragmentSearchResultsBinding
import com.mestero.ui.adapters.ListingAdapter
import com.mestero.data.models.CategoryManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchResultsFragment : Fragment() {

    private var _binding: FragmentSearchResultsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SearchResultsViewModel by viewModels()
    private val args: SearchResultsFragmentArgs by navArgs()
    
    private lateinit var searchResultsAdapter: ListingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearchFunctionality()
        observeViewModel()
        
        // Start search with the query from navigation arguments
        val searchQuery = args.searchQuery
        val categoryId = args.categoryId // Safe navigation args access
        
        binding.searchEditText.setText(searchQuery)
        
        // Check if this is a category specific search
        if (!categoryId.isNullOrEmpty()) {
            val categoryName = CategoryManager.getCategoryById(categoryId)?.title ?: "this category"
            binding.searchQueryText.text = "Results for \"$searchQuery\" in category \"$categoryName\""
            viewModel.searchInSpecificCategory(searchQuery, categoryId)
        } else {
            binding.searchQueryText.text = "Results for \"$searchQuery\""
            viewModel.searchListings(searchQuery)
        }
    }

    private fun setupRecyclerView() {
        searchResultsAdapter = ListingAdapter { listing ->
            val action = SearchResultsFragmentDirections.actionSearchResultsToListingDetail(listing.id)
            findNavController().navigate(action)
        }
        
        binding.searchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchResultsAdapter
        }
    }
    
    private fun setupSearchFunctionality() {
        binding.searchIcon.setOnClickListener {
            performSearch()
        }
        
        binding.searchEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                performSearch()
                true
            } else {
                false
            }
        }
    }
    
    private fun performSearch() {
        val query = binding.searchEditText.text?.toString()?.trim()
        
        if (query.isNullOrBlank()) {
            Toast.makeText(context, "Please enter a search term", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Update the results display
        val categoryId = args.categoryId
        
        if (!categoryId.isNullOrEmpty()) {
            val categoryName = CategoryManager.getCategoryById(categoryId)?.title ?: "this category"
            binding.searchQueryText.text = "Results for \"$query\" in category \"$categoryName\""
            viewModel.searchInSpecificCategory(query, categoryId)
        } else {
            binding.searchQueryText.text = "Results for \"$query\""
            viewModel.searchListings(query)
        }
    }

    private fun observeViewModel() {
        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchResultsUiState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.searchResultsRecyclerView.isVisible = false
                    binding.emptyStateLayout.isVisible = false
                }
                is SearchResultsUiState.Success -> {
                    binding.progressBar.isVisible = false
                    binding.searchResultsRecyclerView.isVisible = true
                    binding.emptyStateLayout.isVisible = false
                    searchResultsAdapter.submitList(state.listings)
                }
                is SearchResultsUiState.Empty -> {
                    binding.progressBar.isVisible = false
                    binding.searchResultsRecyclerView.isVisible = false
                    binding.emptyStateLayout.isVisible = true
                    binding.emptyStateText.text = state.message
                }
                is SearchResultsUiState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.searchResultsRecyclerView.isVisible = false
                    binding.emptyStateLayout.isVisible = true
                    binding.emptyStateText.text = state.message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}