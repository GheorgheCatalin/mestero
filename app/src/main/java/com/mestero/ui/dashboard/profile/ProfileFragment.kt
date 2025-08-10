package com.mestero.ui.dashboard.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.mestero.R
import com.mestero.activities.AuthenticationActivity
import com.mestero.data.UserType
import com.mestero.data.models.UserModel
import com.mestero.databinding.FragmentProfileBinding
import com.mestero.utils.FormatUtils
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.fragment.findNavController

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
        viewModel.loadUserProfile()
    }

    private fun setupClickListeners() {
        binding.editProfileOption.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profile_to_editProfile)
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.settingsOption.setOnClickListener {
            // TODO navigate to settings screen
            Toast.makeText(context, "Settings - TODO", Toast.LENGTH_SHORT).show()
        }

        binding.logoutOption.setOnClickListener {
            auth.signOut()

            val intent = Intent(requireActivity(), AuthenticationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            updateUI(user)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
    }

    private fun updateUI(user: UserModel) {
        binding.apply {
            // Basic info
            userName.text = user.displayName
            userEmail.text = user.email
            userTypeTag.text = user.userType.name

            // Contact information
            phoneText.text = if (user.hasPhoneNumber) user.phoneNumber else "Phone number not set"
            phoneText.setTextColor(
                resources.getColor(
                    if (user.hasPhoneNumber) R.color.main_text_color else R.color.gray1,
                    null
                )
            )
            locationText.text = if (user.hasLocation) user.location else "Location not set"
            locationText.setTextColor(
                resources.getColor(
                    if (user.hasLocation) R.color.main_text_color else R.color.gray1,
                    null
                )
            )

            // Provider-specific information (show only for providers and only if data exists)
            // Website (only show if set for provider)
            websiteLayout.isVisible = user.hasWebsite
            if (user.hasWebsite) {
                websiteText.text = user.website
            }

            if (user.userType == UserType.PROVIDER && user.reviewCount > 0) {
                ratingBar.rating = user.rating
                ratingText.text = FormatUtils.formatRatingWithCount(user.rating, user.reviewCount)
                ratingLayout.isVisible = true
            } else {
                ratingLayout.isVisible = false
            }

            if (user.userType == UserType.PROVIDER && user.hasExperience) {
                experienceText.text = "${user.experienceLevel} Level"
                experienceLayout.isVisible = true
            } else {
                experienceLayout.isVisible = false
            }

            if (user.userType == UserType.PROVIDER && user.hasSkills) {
                displaySkills(user.skills)
                skillsLayout.isVisible = true
            } else {
                skillsLayout.isVisible = false
            }
        }
    }

    private fun displaySkills(skills: List<String>) {
        binding.skillsContainer.removeAllViews()

        skills.forEach { skill ->
            val skillView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_skill_chip, binding.skillsContainer, false) as TextView
            skillView.text = skill
            binding.skillsContainer.addView(skillView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 