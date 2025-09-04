package com.mestero.ui.dashboard.profile

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mestero.R
import com.mestero.data.UserType
import com.mestero.data.models.UserModel
import com.mestero.databinding.FragmentEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditProfileViewModel by viewModels()
    
    private val selectedSkills = mutableListOf<String>()
    private val experienceLevels = arrayOf(getString(R.string.beginner), getString(R.string.intermediate), getString(R.string.expert)) // TODO could refactor later

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupClickListeners()
        observeViewModel()
        viewModel.loadCurrentProfile()
    }

    private fun setupUI() {
        // Setup experience level dropdown
        val experienceAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            experienceLevels
        )
        binding.experienceDropdown.setAdapter(experienceAdapter)

        // Setup dropdown for location(county)
        setupLocationDropdown()

        // Setup skill input handling
        binding.skillInput.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                addSkill()
                true
            } else {
                false
            }
        }
        binding.skillInput.setOnEditorActionListener { _, _, _ ->
            addSkill()
            true
        }
    }

    private fun setupLocationDropdown() {
        val counties = listOf(
            getString(R.string.online_services),
            getString(R.string.no_preference),
            "Alba", "Arad", "Argeș", "Bacău", "Bihor", "Bistrița-Năsăud", "Botoșani", "Brăila", "Brașov",
            "București", "Buzău", "Călărași", "Caraș-Severin", "Cluj", "Constanța", "Covasna", "Dâmbovița",
            "Dolj", "Galați", "Giurgiu", "Gorj", "Harghita", "Hunedoara", "Ialomița", "Iași", "Ilfov",
            "Maramureș", "Mehedinți", "Mureș", "Neamț", "Olt", "Prahova", "Sălaj", "Satu Mare", "Sibiu",
            "Suceava", "Teleorman", "Timiș", "Tulcea", "Vâlcea", "Vaslui", "Vrancea"
        )
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, counties)
        binding.locationEdit.setAdapter(adapter)
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            saveProfile()
        }
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            populateFields(user)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.saveButton.isEnabled = !isLoading
            binding.saveButton.text = if (isLoading) getString(R.string.saving) else getString(R.string.save_changes)
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigateUp()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun populateFields(user: UserModel) {
        binding.apply {
            firstNameEdit.setText(user.firstName)
            lastNameEdit.setText(user.lastName)
            emailEdit.setText(user.email)
            phoneEdit.setText(user.phoneNumber)
            
            // Set location fallback to "No Preference"
            val userLocation = user.location.ifEmpty {
                getString(R.string.no_preference)
            }
            locationEdit.setText(userLocation, false)
            
            websiteEdit.setText(user.website)

            // Show provider section only for provider users
            if (user.userType == UserType.PROVIDER) {
                providerInfoCard.isVisible = true
                
                if (user.experienceLevel.isNotEmpty()) {
                    experienceDropdown.setText(user.experienceLevel, false)
                }

                // Populate existing skills
                selectedSkills.clear()
                selectedSkills.addAll(user.skills)
                updateSkillsDisplay()
            } else {
                providerInfoCard.isVisible = false
            }
        }
    }

    private fun addSkill() {
        val skillText = binding.skillInput.text?.toString()?.trim()
        if (!skillText.isNullOrEmpty() && !selectedSkills.contains(skillText)) {
            selectedSkills.add(skillText)
            updateSkillsDisplay()

            binding.skillInput.text?.clear()
        }
    }

    private fun updateSkillsDisplay() {
        binding.skillsContainer.removeAllViews()
        
        selectedSkills.forEach { skill ->
            val skillChip = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_skill_chip_editable, binding.skillsContainer, false)
            
            val skillText = skillChip.findViewById<TextView>(R.id.skill_text)
            val removeButton = skillChip.findViewById<View>(R.id.remove_skill)
            
            skillText.text = skill
            removeButton.setOnClickListener {
                selectedSkills.remove(skill)
                updateSkillsDisplay()
            }
            
            binding.skillsContainer.addView(skillChip)
        }
    }

    private fun saveProfile() {
        val firstName = binding.firstNameEdit.text?.toString()?.trim() ?: ""
        val lastName = binding.lastNameEdit.text?.toString()?.trim() ?: ""
        val phone = binding.phoneEdit.text?.toString()?.trim() ?: ""
        val location = binding.locationEdit.text?.toString()?.trim() ?: ""
        val website = binding.websiteEdit.text?.toString()?.trim() ?: ""
        val experienceLevel = binding.experienceDropdown.text?.toString()?.trim() ?: ""

        // Validation - prevent clearing name fields that already have data
        // Only block when  user is trying to clear required name fields that were previously filled
        val currentUserData = viewModel.userProfile.value
        if (firstName.isEmpty() && !currentUserData?.firstName.isNullOrBlank()) {
            binding.firstNameEdit.error = getString(R.string.first_name_cannot_be_cleared)
            return
        }

        if (lastName.isEmpty() && !currentUserData?.lastName.isNullOrBlank()) {
            binding.lastNameEdit.error = getString(R.string.last_name_cannot_be_cleared)
            return
        }

        // Website URL validation (if entered)
        if (website.isNotEmpty() && !isValidUrl(website)) {
            binding.websiteEdit.error = getString(R.string.please_enter_valid_website_url)
            return
        }

        viewModel.saveProfile(
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phone,
            location = location,
            website = website,
            experienceLevel = experienceLevel,
            skills = selectedSkills.toList()
        )
    }

    private fun isValidUrl(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 