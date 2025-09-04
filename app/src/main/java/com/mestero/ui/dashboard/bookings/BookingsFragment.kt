package com.mestero.ui.dashboard.bookings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mestero.R
import com.mestero.data.UserType
import com.mestero.databinding.FragmentBookingsBinding
import com.mestero.network.auth.AccountService
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.ui.adapters.BookingRequestAdapter
import com.mestero.ui.adapters.ClientBookingRequestAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BookingsFragment : Fragment() {
    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!
    private var userType: UserType = UserType.CLIENT

    private val providerViewModel: BookingsViewModel by viewModels()
    private lateinit var providerAdapter: BookingRequestAdapter

    private val clientViewModel: ClientBookingsViewModel by viewModels()
    private lateinit var clientAdapter: ClientBookingRequestAdapter
    
    @Inject
    lateinit var accountService: AccountService
    @Inject
    lateinit var firestoreRepository: FirestoreRepository


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserTypeForSetup()
    }

    private fun fetchUserTypeForSetup() {
        if (accountService.currentUserId.isEmpty()) {
            userType = UserType.CLIENT
            setupForUserType()
            return
        }

        accountService.fetchUserType(
            onResult = { fetchedUserType ->
                userType = fetchedUserType
                setupForUserType()
            }
        )
    }

    private fun setupForUserType() {
        when (userType) {
            UserType.PROVIDER -> setupProviderView()
            UserType.CLIENT -> setupClientView()
        }
    }

    private fun setupProviderView() {
        providerAdapter = BookingRequestAdapter(
            onAcceptClick = { booking ->
                providerViewModel.acceptBooking(booking.id)
            },
            onRejectClick = { booking ->
                providerViewModel.rejectBooking(booking.id, getString(R.string.provider_declined_request))
            },
            onCompleteClick = { booking ->
                showCompleteBookingDialog(booking)
            },
            onHideClick = { booking ->
                showHideBookingDialog(booking, true)
            },
            onListingClick = { booking ->
                navigateToListingDetail(booking.listingId)
            }
        )

        binding.recyclerViewBookings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = providerAdapter
        }

        observeProviderVM()
    }

    private fun setupClientView() {
        clientAdapter = ClientBookingRequestAdapter(
            onReviewClick = { booking ->
                clientViewModel.handleReviewClick(booking)
            },

            onContactClick = { contactInfo, type ->
                clientViewModel.handleContactClick(contactInfo, type)
            },

            onHideClick = { booking ->
                showHideBookingDialog(booking, false)
            },
            onListingClick = { booking ->
                navigateToListingDetail(booking.listingId)
            }
        )

        binding.recyclerViewBookings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = clientAdapter
        }

        observeClientVM()
    }

    private fun observeProviderVM() {
        providerViewModel.providerBookingsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BookingsUiState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.recyclerViewBookings.isVisible = false
                    binding.textViewEmpty.isVisible = false
                }
                is BookingsUiState.Success -> {
                    binding.progressBar.isVisible = false
                    binding.recyclerViewBookings.isVisible = true
                    binding.textViewEmpty.isVisible = false
                    providerAdapter.submitList(state.bookings)
                }
                is BookingsUiState.Empty -> {
                    binding.progressBar.isVisible = false
                    binding.recyclerViewBookings.isVisible = false
                    binding.textViewEmpty.isVisible = true
                    binding.textViewEmpty.text = getString(R.string.no_booking_requests_received)
                }
                is BookingsUiState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.recyclerViewBookings.isVisible = false
                    binding.textViewEmpty.isVisible = true
                    binding.textViewEmpty.text = state.message
                }
            }
        }

        providerViewModel.updateResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                val message = result.getOrNull() ?: getString(R.string.success)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } else {
                val error = result.exceptionOrNull()
                Toast.makeText(context, getString(R.string.error_colon_message, error?.message ?: ""), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeClientVM() {
        clientViewModel.clientBookingsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ClientBookingsUiState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.recyclerViewBookings.isVisible = false
                    binding.textViewEmpty.isVisible = false
                }
                is ClientBookingsUiState.Success -> {
                    binding.progressBar.isVisible = false
                    binding.recyclerViewBookings.isVisible = true
                    binding.textViewEmpty.isVisible = false
                    clientAdapter.submitList(state.bookings)
                }
                is ClientBookingsUiState.Empty -> {
                    binding.progressBar.isVisible = false
                    binding.recyclerViewBookings.isVisible = false
                    binding.textViewEmpty.isVisible = true
                    binding.textViewEmpty.text = getString(R.string.no_booking_requests_sent)
                }
                is ClientBookingsUiState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.recyclerViewBookings.isVisible = false
                    binding.textViewEmpty.isVisible = true
                    binding.textViewEmpty.text = state.message
                }
            }
        }

        clientViewModel.contactAction.observe(viewLifecycleOwner) { contactPair ->
            contactPair?.let { (contactInfo, type) ->
                handleContactAction(contactInfo, type)

                // Clear action to prevent re-triggering on config changes
                clientViewModel.clearContactAction()
            }
        }

        clientViewModel.reviewAction.observe(viewLifecycleOwner) { booking ->
            booking?.let {
                showReviewDialog(it)

                // Clear action to prevent re-triggering on config changes
                clientViewModel.clearReviewAction()
            }
        }

        clientViewModel.updateResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                val message = result.getOrNull() ?: getString(R.string.success)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } else {
                val error = result.exceptionOrNull()
                Toast.makeText(context, getString(R.string.error_colon_message, error?.message ?: ""), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showCompleteBookingDialog(booking: com.mestero.data.models.BookingRequestModel) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.mark_as_completed))
            .setMessage(getString(R.string.mark_completed_confirmation, booking.listingTitle, booking.clientName))
            .setPositiveButton(getString(R.string.yes_complete)) { _, _ ->
                providerViewModel.completeBooking(booking.id)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showHideBookingDialog(booking: com.mestero.data.models.BookingRequestModel, isProvider: Boolean) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.hide_booking))
            .setMessage(getString(R.string.hide_booking_confirmation))
            .setPositiveButton(getString(R.string.hide)) { _, _ ->
                if (isProvider) {
                    providerViewModel.hideBooking(booking.id)
                } else {
                    clientViewModel.hideBooking(booking.id)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun handleContactAction(contactInfo: String, type: String) {
        when (type) {
            "email" -> {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$contactInfo")
                }
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, getString(R.string.no_email_app_found), Toast.LENGTH_SHORT).show()
                }
            }
            "phone" -> {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$contactInfo")
                }
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, getString(R.string.cannot_make_phone_calls), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showReviewDialog(booking: com.mestero.data.models.BookingRequestModel) {
        val reviewDialog = ReviewDialog(
            context = requireContext(),
            booking = booking,
            onReviewSubmitted = { review ->
                clientViewModel.submitReview(review)
            }
        )
        reviewDialog.show()
    }

    private fun navigateToListingDetail(listingId: String) {
        try {
            val action = BookingsFragmentDirections.actionBookingsToListingDetail(listingId)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.error_navigating_listing_details), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 