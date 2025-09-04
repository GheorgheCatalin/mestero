package com.mestero.ui.dashboard.subcategories

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mestero.databinding.FragmentSubcategoriesBinding
import com.mestero.ui.adapters.SubcategoryAdapter
import com.mestero.data.models.CategoryManager
import com.mestero.R

class SubcategoriesFragment : Fragment() {
    private var _binding: FragmentSubcategoriesBinding? = null
    private val binding get() = _binding!!
    private val args: SubcategoriesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubcategoriesBinding.inflate(inflater, container, false)

        // Hide main activity's ActionBar to prevent double toolbar
       // (activity as? AppCompatActivity)?.supportActionBar?.hide() // custom toolbar
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the title in the system ActionBar
        (activity as? AppCompatActivity)?.supportActionBar?.title = args.categoryTitle

        setupRecyclerView()
        setupSearchFunctionality()
    }

    private fun setupRecyclerView() {
        val subcategories = CategoryManager.getSubcategoriesForCategoryId(args.categoryId)
        
        binding.subcategoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SubcategoryAdapter(subcategories) { subcategory ->
                // Navigate to listings fragment for a specific subcategory
                val action = SubcategoriesFragmentDirections.actionSubcategoriesToListings(
                        categoryId = args.categoryId,
                        subcategoryId = subcategory.id,
                        title = subcategory.getLocalizedTitle(requireContext())
                    )
                findNavController().navigate(action)
            }
        }
    }
    
    private fun setupSearchFunctionality() {
        binding.searchIcon.setOnClickListener {
            performCategorySearch()
        }
        
        binding.searchEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                performCategorySearch()
                true
            } else {
                false
            }
        }
    }
    
    private fun performCategorySearch() {
        val query = binding.searchEditText.text?.toString()?.trim()
        
        if (query.isNullOrBlank()) {
            Toast.makeText(context, getString(R.string.search_empty_query), Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            // Navigate to search results with category-specific search
            val action = SubcategoriesFragmentDirections.actionSubcategoriesToSearchResults(
                searchQuery = query,
                categoryId = args.categoryId
            )
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.error_search_failed), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Bug fix - ensure ActionBar remains hidden after returning to this fragment
        //(activity as? AppCompatActivity)?.supportActionBar?.hide() // custom toolbar
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Restore ActionBar when leaving this fragment
        //(activity as? AppCompatActivity)?.supportActionBar?.show()
        _binding = null
    }
} 