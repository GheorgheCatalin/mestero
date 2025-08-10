package com.mestero.ui.dashboard.listingDetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.mestero.data.UserType
import com.mestero.data.models.CategoryManager
import com.mestero.data.models.ListingModel
import com.mestero.data.models.UserModel
import com.mestero.databinding.FragmentListingDetailBinding
import com.mestero.utils.FormatUtils
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri

@AndroidEntryPoint
class ListingDetailFragment : Fragment() {
    private var _binding: FragmentListingDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ListingDetailFragmentArgs by navArgs()

    private val viewModel: ListingDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListingDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        observeViewModel()

        viewModel.loadListingDetail(args.listingId)
    }

    private fun setupClickListeners() {
        binding.retryButton.setOnClickListener {
            viewModel.loadListingDetail(args.listingId)
        }

        binding.reserveButton.setOnClickListener {
            // Ensure listing is retrieved from DB before starting booking flow
            val currentState = viewModel.listingDetailState.value
            if (currentState is ListingDetailUiState.Success) {
                showBookingDialog(currentState.listing)
            } else {
                Toast.makeText(context, "Please wait for listing to load", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.viewProviderButton.setOnClickListener {
            // TODO could navigate to provider profile - if necessary
            Toast.makeText(context, "Provider profile feature coming soon!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.listingDetailState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ListingDetailUiState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.errorLayout.isVisible = false
                    binding.contentLayout.isVisible = false
                }

                is ListingDetailUiState.Success -> {
                    binding.progressBar.isVisible = false
                    binding.errorLayout.isVisible = false
                    binding.contentLayout.isVisible = true
                    
                    bindListingData(state.listing)
                    bindProviderData(state.provider)
                }

                is ListingDetailUiState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.errorLayout.isVisible = true
                    binding.contentLayout.isVisible = false
                    
                    binding.errorTextView.text = state.message
                }
            }
        }
        
        viewModel.bookingResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                val message = result.getOrNull() ?: "Success"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            } else {
                val error = result.exceptionOrNull()
                Toast.makeText(context, "Error: ${error?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun bindListingData(listing: ListingModel) {
        binding.apply {
            // Header information
            titleTextView.text = listing.title
            descriptionTextView.text = listing.description
            priceTextView.text = listing.formattedPrice
            locationTextView.text = listing.displayLocation

            // Categories
            val categoryTitle = CategoryManager.getCategoryById(listing.category)?.title ?: listing.category
            val subcategoryTitle = CategoryManager.getSubcategoryById(listing.subcategory)?.title ?: listing.subcategory
            categoryTextView.text = categoryTitle
            subcategoryTextView.text = subcategoryTitle

            // Stats
            if (listing.ratingCount > 0) {
                ratingTextView.text = FormatUtils.formatRatingWithCount(listing.ratingAvg.toFloat(), listing.ratingCount)
                ratingTextView.isVisible = true
            } else {
                ratingTextView.isVisible = false
            }

            viewsTextView.text = "${listing.views} views"

            // Contact information
            setupContactInfo(listing)
        }
    }

    private fun bindProviderData(provider: UserModel?) {
        binding.apply {
            if (provider != null) {
                providerSection.isVisible = true
                
                // Provider name and initials
                val fullName = "${provider.firstName} ${provider.lastName}".trim()
                providerNameTextView.text = fullName.ifEmpty { "Provider" }
                
                // Provider avatar (initials)
                val initials = "${provider.firstName.firstOrNull() ?: ""}${provider.lastName.firstOrNull() ?: ""}".uppercase()
                providerAvatarTextView.text = initials.ifEmpty { "P" }  // TODO - delete?
                
                // Provider type
                val typeText = when (provider.userType) {
                    UserType.PROVIDER -> "Professional Provider"
                    UserType.CLIENT -> "Client"
                }
                providerTypeTextView.text = typeText
                
                // Provider rating
                if (provider.reviewCount > 0) {
                    providerRatingTextView.text = "${provider.rating} (${provider.reviewCount} reviews)"
                    providerRatingTextView.isVisible = true
                } else {
                    providerRatingTextView.text = "No reviews yet"
                }
                
            } else {
                providerSection.isVisible = false
            }
        }
    }

    private fun setupContactInfo(listing: ListingModel) {
        binding.apply {
            // Phone
            if (listing.phoneNumber.isNotBlank()) {
                phoneLayout.isVisible = true
                phoneTextView.text = listing.phoneNumber
                callButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${listing.phoneNumber}")
                    }
                    startActivity(intent)
                }
            } else {
                phoneLayout.isVisible = false
            }

            // Email
            if (listing.email.isNotBlank()) {
                emailLayout.isVisible = true
                emailTextView.text = listing.email
                emailButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:${listing.email}".toUri()
                        putExtra(Intent.EXTRA_SUBJECT, "Inquiry about: ${listing.title}")
                    }
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                emailLayout.isVisible = false
            }

            // Website
            if (listing.website.isNotBlank()) {
                websiteLayout.isVisible = true
                websiteTextView.text = listing.website
                websiteTextView.setOnClickListener {
                    val url = if (listing.website.startsWith("http")) {
                        listing.website
                    } else {
                        "https://${listing.website}"
                    }
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No browser app found", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                websiteLayout.isVisible = false
            }

            // Hide contact info section if no contact info available
            contactInfoSection.isVisible = listing.hasContactInfo
        }
    }

    private fun showBookingDialog(listing: ListingModel) {
        val dialog = BookingRequestDialog(
            context = requireActivity(),
            listing = listing
        ) { notes ->
            viewModel.createBookingRequest(listing, notes)
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 