package com.mestero.ui.dashboard.addListing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mestero.R
import com.mestero.data.models.CategoryManager
import com.mestero.data.models.Category
import com.mestero.data.models.Subcategory
import com.mestero.data.models.PricingModel
import com.mestero.data.models.PricingType
import com.mestero.data.models.PricingUnit
import com.mestero.data.models.ListingModel
import com.mestero.databinding.FragmentAddListingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddListingFragment : Fragment() {

    private var _binding: FragmentAddListingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddListingViewModel by viewModels()

    private var currentPricingType = PricingType.FIXED
    private var currentPricingUnit = PricingUnit.TOTAL
    private var selectedCategory: Category? = null
    private var selectedSubcategory: Subcategory? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupCategorySelection()
        setupPricingSection()

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Re-setup dropdowns to fix adapter clearing bug
        setupPricingUnitDropdown()
        setupCountyDropdown()
    }

    private fun setupUI() {
        binding.submitButton.setOnClickListener {
            createListing()
        }

        binding.addPhotosButton.setOnClickListener {
            // Todo?
        }
    }

    private fun observeViewModel() {
        viewModel.createListingResult.observe(viewLifecycleOwner) { result ->
            result?.let { createListingResult ->
                when {
                    createListingResult.isSuccess -> {
                        Toast.makeText(
                            requireContext(),
                            "Listing created successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Navigate back to home after creating listing
                        findNavController().navigateUp()
                    }

                    createListingResult.isFailure -> {
                        val error = createListingResult
                            .exceptionOrNull()?.message ?: "Unknown error occurred"

                        Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
                    }
                }
                viewModel.clearCreateListingResult()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.submitButton.isEnabled = !isLoading
            binding.submitButton.text = if (isLoading) "Creating..." else "Create Listing"
        }
    }

    private fun setupCategorySelection() {
        val categories = CategoryManager.categories
        // Setup category dropdown
        val categoryTitles = categories.map { it.title }
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categoryTitles
        )
        binding.categoryAutoComplete.setAdapter(categoryAdapter)

        // Handle category selection
        binding.categoryAutoComplete.setOnItemClickListener { _, _, position, _ ->
            selectedCategory = categories[position]
            // Reset subcategory when category changes
            selectedSubcategory = null
            binding.subcategoryAutoComplete.setText("", false)

            setupSubcategoryDropdownForSelectedCategory()
        }
    }

    private fun setupSubcategoryDropdownForSelectedCategory() {
        selectedCategory?.let { category ->
            val subcategories = category.subcategories

            // Setup sub category dropdown
            val subcategoryTitles = subcategories.map { it.title }
            val subcategoryAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                subcategoryTitles
            )
            binding.subcategoryAutoComplete.setAdapter(subcategoryAdapter)
            binding.subcategoryInputLayout.isVisible = true

            // Handle subcategory selection
            binding.subcategoryAutoComplete.setOnItemClickListener { _, _, position, _ ->
                selectedSubcategory = subcategories[position]
            }
        }
    }

    private fun setupPricingSection() {
        setupPricingUnitDropdown()

        setupCountyDropdown()

        // Handle changes of pricing type
        binding.priceTypeToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                currentPricingType =
                    when (checkedId) {
                        R.id.btnFixed -> PricingType.FIXED
                        R.id.btnRange -> PricingType.RANGE
                        R.id.btnAgreed -> PricingType.TO_BE_AGREED
                        else -> PricingType.FIXED
                    }

                updatePricingLayoutsVisibilityBasedOnType()
            }
        }

        // Default selection = "Fixed"
        binding.btnFixed.isChecked = true
        updatePricingLayoutsVisibilityBasedOnType()
    }

    private fun setupPricingUnitDropdown() {
        val units = PricingUnit.values().map { it.getDisplayText() }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, units)
        binding.pricingUnitAutoComplete.setAdapter(adapter)

        // Set default selection
        binding.pricingUnitAutoComplete.setText(PricingUnit.TOTAL.getDisplayText(), false)

        binding.pricingUnitAutoComplete.setOnItemClickListener { _, _, position, _ ->
            currentPricingUnit = PricingUnit.values()[position]
        }
    }

    private fun setupCountyDropdown() {
        val counties = listOf(
            "Online Services",
            "No Preference",
            "Alba", "Arad", "Argeș", "Bacău", "Bihor", "Bistrița-Năsăud", "Botoșani", "Brăila", "Brașov",
            "București", "Buzău", "Călărași", "Caraș-Severin", "Cluj", "Constanța", "Covasna", "Dâmbovița",
            "Dolj", "Galați", "Giurgiu", "Gorj", "Harghita", "Hunedoara", "Ialomița", "Iași", "Ilfov",
            "Maramureș", "Mehedinți", "Mureș", "Neamț", "Olt", "Prahova", "Sălaj", "Satu Mare", "Sibiu",
            "Suceava", "Teleorman", "Timiș", "Tulcea", "Vâlcea", "Vaslui", "Vrancea"
        )


        binding.countyAutoComplete.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                counties)
            )

        // Set default to "No Pref"
        binding.countyAutoComplete.setText("No Preference", false)
    }

    private fun updatePricingLayoutsVisibilityBasedOnType() {
        when (currentPricingType) {
            PricingType.FIXED -> {
                binding.fixedPriceInputLayout.isVisible = true
                binding.rangePriceContainer.isVisible = false
                binding.toBeAgreedCard.isVisible = false
                binding.pricingUnitInputLayout.isVisible = true
            }

            PricingType.RANGE -> {
                binding.fixedPriceInputLayout.isVisible = false
                binding.rangePriceContainer.isVisible = true
                binding.toBeAgreedCard.isVisible = false
                binding.pricingUnitInputLayout.isVisible = true
            }

            PricingType.TO_BE_AGREED -> {
                binding.fixedPriceInputLayout.isVisible = false
                binding.rangePriceContainer.isVisible = false
                binding.toBeAgreedCard.isVisible = true
                binding.pricingUnitInputLayout.isVisible = false
            }
        }
    }

    private fun createPricingModel(): PricingModel? {
        return when (currentPricingType) {
            PricingType.FIXED -> {
                val priceText = binding.fixedPriceEditText.text.toString()
                if (priceText.isBlank()) {
                    binding.fixedPriceInputLayout.error = "Price is required"
                    return null
                }

                val price = priceText.toDoubleOrNull()
                if (price == null || price <= 0) {
                    binding.fixedPriceInputLayout.error = "Please enter a valid price"
                    return null
                }

                binding.fixedPriceInputLayout.error = null
                PricingModel.createFixed(price, currentPricingUnit)
            }

            PricingType.RANGE -> {
                val minPriceText = binding.minPriceEditText.text.toString()
                val maxPriceText = binding.maxPriceEditText.text.toString()
                if (minPriceText.isBlank()) {
                    binding.minPriceInputLayout.error = "Min price is required"
                    return null
                }
                if (maxPriceText.isBlank()) {
                    binding.maxPriceInputLayout.error = "Max price is required"
                    return null
                }

                val minPrice = minPriceText.toDoubleOrNull()
                val maxPrice = maxPriceText.toDoubleOrNull()
                if (minPrice == null || minPrice <= 0) {
                    binding.minPriceInputLayout.error = "Please enter a valid min price"
                    return null
                }
                if (maxPrice == null || maxPrice <= 0) {
                    binding.maxPriceInputLayout.error = "Please enter a valid max price"
                    return null
                }
                if (minPrice >= maxPrice) {
                    binding.maxPriceInputLayout.error = "Max price must be greater than min price"
                    return null
                }

                binding.minPriceInputLayout.error = null
                binding.maxPriceInputLayout.error = null
                PricingModel.createRange(minPrice, maxPrice, currentPricingUnit)
            }

            PricingType.TO_BE_AGREED -> {
                PricingModel.createToBeAgreed()
            }
        }
    }

    private fun createListing() {
        // Validate title--
        val title = binding.titleEditText.text.toString().trim()
        if (title.isBlank()) {
            binding.titleInputLayout.error = "Title is required"
            return
        }
        if (title.length < ListingModel.MIN_TITLE_LENGTH || title.length > ListingModel.MAX_TITLE_LENGTH) {
            binding.titleInputLayout.error =
                "Title must be between ${ListingModel.MIN_TITLE_LENGTH} and ${ListingModel.MAX_TITLE_LENGTH} characters"
            return
        }
        binding.titleInputLayout.error = null

        // Validate description
        val description = binding.descriptionEditText.text.toString().trim()
        if (description.isBlank()) {
            binding.descriptionInputLayout.error = "Description is required"
            return
        }
        if (description.length < ListingModel.MIN_DESCRIPTION_LENGTH || description.length > ListingModel.MAX_DESCRIPTION_LENGTH) {
            binding.descriptionInputLayout.error =
                "Description must be between ${ListingModel.MIN_DESCRIPTION_LENGTH} and ${ListingModel.MAX_DESCRIPTION_LENGTH} characters"
            return
        }
        binding.descriptionInputLayout.error = null

        // Validate category selection
        if (selectedCategory == null) {
            binding.categoryInputLayout.error = "Please select a category"
            return
        }
        binding.categoryInputLayout.error = null

        // Validate subcategory selection
        if (selectedSubcategory == null) {
            binding.subcategoryInputLayout.error = "Please select a subcategory"
            return
        }
        binding.subcategoryInputLayout.error = null

        // Validate pricing
        val pricingModel = createPricingModel() ?: return

        val county = binding.countyAutoComplete.text.toString().ifBlank { "No Preference" }
        val phoneNumber = binding.phoneEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val website = binding.websiteEditText.text.toString().trim()
        val specificLocations = binding.locationEditText.text.toString().trim()

        // Validate email format if provided
        if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = "Please enter a valid email address"
            return
        }
        binding.emailInputLayout.error = null

        // Validate website format if provided
        if (website.isNotBlank() && !android.util.Patterns.WEB_URL.matcher(website).matches()) {
            binding.websiteInputLayout.error = "Please enter a valid website URL"
            return
        }
        binding.websiteInputLayout.error = null

        // Create listing with the provided data
        val listing = ListingModel(
            title = title,
            description = description,
            category = selectedCategory!!.id,
            subcategory = selectedSubcategory!!.id,
            county = county,
            specificLocations = specificLocations,
            phoneNumber = phoneNumber,
            email = email,
            website = website,
            pricingModel = pricingModel,
            providerId = "" // Set in the VM using AccountService
        )
        viewModel.createListing(listing)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 