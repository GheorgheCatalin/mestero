package com.mestero.ui.dashboard.home

import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.mestero.R
import com.mestero.data.UserType
import com.mestero.databinding.FragmentHomeBinding
import com.mestero.ui.adapters.CategoryHorizontalAdapter
import com.mestero.ui.adapters.ListingAdapter
import com.mestero.utils.Analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var newPostsAdapter: ListingAdapter
    private lateinit var categoriesAdapter: CategoryHorizontalAdapter
    private lateinit var lastSeenAdapter: ListingAdapter
    private lateinit var myPostsAdapter: ListingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()

        // Pull-to-refresh
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun setupRecyclerViews() {
        // Setup New Posts RV
        newPostsAdapter = ListingAdapter { listing ->
            navigateToListingDetail(listing.id)
        }
        binding.newPostsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = newPostsAdapter
        }
        PagerSnapHelper().attachToRecyclerView(binding.newPostsRecyclerView)

        // Setup Categories RV
        categoriesAdapter = CategoryHorizontalAdapter(emptyList()) { category ->
            // Log category selection
            Analytics.logEvent(Analytics.Events.CATEGORY_SELECTED, mapOf(
                Analytics.Params.CATEGORY to category.titleResId,
                "category_id" to category.id
            ))
            
            // Navigate directly to subcategories for the selected category
            try {
                val action = HomeFragmentDirections.actionHomeToSubcategories(
                    categoryId = category.id,
                    categoryTitle = category.getLocalizedTitle(requireContext())
                )
                findNavController().navigate(action)
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoriesAdapter
        }

        // Setup Last Seen RV
        lastSeenAdapter = ListingAdapter { listing ->
            navigateToListingDetail(listing.id)
        }
        binding.lastSeenRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = lastSeenAdapter
        }
        PagerSnapHelper().attachToRecyclerView(binding.lastSeenRecyclerView)

        // Setup My Posts RV
        myPostsAdapter = ListingAdapter { listing ->
            navigateToListingDetail(listing.id)
        }
        binding.myPostsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = myPostsAdapter
        }
        PagerSnapHelper().attachToRecyclerView(binding.myPostsRecyclerView)
    }

    private fun setupClickListeners() {
        binding.seeAllCategories.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_to_services)
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        
        // Search functionality
        binding.searchIcon.setOnClickListener {
            performSearch()
        }

        // entry point for conversations
        binding.openConversationsIcon.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_global_conversations)
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        
        // Handle search when user presses Enter/Search on keyboard
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
        
        try {
            val action = HomeFragmentDirections.actionHomeToSearchResults(query, null)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(context, "Error performing search", Toast.LENGTH_SHORT).show()
            Log.e("HomeFragment", "Search navigation error", e)
        }
    }

    private fun navigateToListingDetail(listingId: String) {
        try {
            val action = HomeFragmentDirections.actionHomeToListingDetail(listingId)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(context, "Error naigating to listing details", Toast.LENGTH_SHORT).show()
            Log.e("HomeFragment", "Navigation error", e)
        }
    }

    private fun observeViewModel() {
        viewModel.userType.observe(viewLifecycleOwner) { userType ->
            updateUIForUserType(userType)
        }

        // Disabled welcome text - replaced by app title logo
        // viewModel.userName.observe(viewLifecycleOwner) { userName ->
        //     binding.welcomeText.text = "Welcome, $userName"
        // }

        viewModel.newPosts.observe(viewLifecycleOwner) { listings ->
            newPostsAdapter.submitList(listings)
            binding.newPostsEmptyState.isVisible = listings.isEmpty()
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoriesAdapter.submit(categories)
        }

        viewModel.mostViewed.observe(viewLifecycleOwner) { listings ->
            lastSeenAdapter.submitList(listings)
            binding.lastSeenEmptyState.isVisible = listings.isEmpty()
        }

        viewModel.myPosts.observe(viewLifecycleOwner) { listings ->
            myPostsAdapter.submitList(listings)
            binding.myPostsEmptyState.isVisible = listings.isEmpty()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun updateUIForUserType(userType: UserType) {
        when (userType) {
            UserType.CLIENT -> {
                // Show CLIENT sections (categories always visible, last seen visible)
                binding.categoriesLabel.isVisible = true
                binding.seeAllCategories.isVisible = true
                binding.categoriesRecyclerView.isVisible = true
                binding.divider2.isVisible = true
                
                // Show "Most Viewed", hide "My Listings"
                binding.lastSeenLabel.isVisible = true
                binding.lastSeenRecyclerView.isVisible = true
                binding.divider3.isVisible = true
                binding.lastSeenEmptyState.isVisible = true
                
                binding.myPostsLabel.isVisible = false
                binding.myPostsRecyclerView.isVisible = false
                binding.divider4.isVisible = false
                binding.myPostsEmptyState.isVisible = false
            }
            UserType.PROVIDER -> {
                // Hide categories for provider
                binding.categoriesLabel.isVisible = false
                binding.seeAllCategories.isVisible = false
                binding.categoriesRecyclerView.isVisible = false
                binding.divider2.isVisible = false
                
                // Hide "Most Viewed", show "My Listings"
                binding.lastSeenLabel.isVisible = false
                binding.lastSeenRecyclerView.isVisible = false
                binding.divider3.isVisible = false
                binding.lastSeenEmptyState.isVisible = false
                
                binding.myPostsLabel.isVisible = true
                binding.myPostsRecyclerView.isVisible = true
                binding.divider4.isVisible = true
                binding.myPostsEmptyState.isVisible = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to home so new listings appear
        viewModel.refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}