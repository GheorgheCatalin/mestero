package com.mestero.ui.dashboard.services

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mestero.R
import com.mestero.data.models.CategoryManager
import com.mestero.databinding.FragmentServicesBinding
import com.mestero.ui.adapters.CategoryAdapter

class ServicesFragment : Fragment() {

    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchFunctionality()
    }

    private fun setupRecyclerView() {
        binding.categoriesRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = CategoryAdapter(CategoryManager.categories) { category ->
                // Navigate to subcategories with safe-args
                val action = ServicesFragmentDirections.actionServicesToSubcategories(
                    categoryId = category.id,
                    categoryTitle = category.title
                )
                findNavController().navigate(action)
            }
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
        
        try {
            val action = ServicesFragmentDirections.actionServicesToSearchResults(query, null)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(context, "Error performing search", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}