package com.mestero.ui.authentication;

import android.content.Intent
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mestero.R
import com.mestero.activities.MainActivity
import com.mestero.data.UserType
import com.mestero.data.models.UserModel
import com.mestero.databinding.FragmentAuthRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentAuthRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerBtn.isEnabled = false

        // Enable register button only when fields are filled
        setUpTextWatchersForRegisterActivation()

        // Toggle UI by user type
        initUserTypeToggle()

        initClickListeners()
        observeAuthState()
    }

    private fun initUserTypeToggle() {
        binding.userTypeToggle.check(binding.clientButton.id)
        setProviderFieldsVisible(false)

        binding.userTypeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            setProviderFieldsVisible(checkedId == binding.providerButton.id)
        }
    }

    private fun initClickListeners() {
        binding.registerGoToLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.registerBtn.setOnClickListener {
            val user = validateRegistrationInputs()
            if (user != null) {
                val password = binding.registerPasswordEt.text.toString()
                authViewModel.registerUsingEmailAndPassword(user, password)
            }
        }
    }

    private fun setProviderFieldsVisible(isProvider: Boolean) {
        // Always show both name fields; last name is optional in case of provider use
        binding.registerNameLayout.isVisible = true
        binding.registerSurnameLayout.isVisible = true

        // Contact options shown only for provider
        binding.registerPhoneLayout.isVisible = isProvider
        binding.registerLocationLayout.isVisible = isProvider
        binding.registerWebsiteLayout.isVisible = isProvider
        binding.providerHelperTv.isVisible = isProvider

        // Hints: provider -> First name or company + Last name (optional); client -> First + Last Name
        binding.registerNameLayout.hint =
            if (isProvider) getString(R.string.full_name_or_company_txt) else getString(R.string.firstname_txt)
        binding.registerSurnameLayout.hint =
            if (isProvider) getString(R.string.lastname_optional_txt) else getString(R.string.lastname_txt)

    }

    private fun validateRegistrationInputs(): UserModel? {
        val email = binding.registerEmailEt.text.toString().trim()
        val firstNameOrCompanyName = binding.registerNamedEt.text.toString().trim()
        val lastName = binding.registerSurnameEt.text.toString().trim()
        val password = binding.registerPasswordEt.text.toString()
        val confirmPassword = binding.registerConfirmPasswordEt.text.toString()

        // Email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.registerEmailLayout.error = getString(R.string.invalid_email)
            return null
        } else binding.registerEmailLayout.error = null

        val isProvider = binding.userTypeToggle.checkedButtonId == binding.providerButton.id

        // Name validation
        if (firstNameOrCompanyName.isEmpty()) {
            binding.registerNameLayout.error =
                if (isProvider)
                    getString(R.string.first_name_company_required)
                else
                    getString(R.string.first_name_required)

            return null
        } else binding.registerNameLayout.error = null

        // Password validation
        if (password.length < 6) {
            binding.registerPasswordLayout.error = getString(R.string.password_min_length)
            return null
        } else binding.registerPasswordLayout.error = null

        if (password != confirmPassword) {
            binding.registerConfirmPasswordLayout.error = getString(R.string.passwords_do_not_match)
            return null
        } else binding.registerConfirmPasswordLayout.error = null


        return UserModel(
            email = email,
            firstName = firstNameOrCompanyName,
            lastName = lastName,
            userType = if (isProvider) UserType.PROVIDER else UserType.CLIENT,
            phoneNumber = binding.registerPhoneEt.text?.toString()?.trim() ?: "",
            location = binding.registerLocationEt.text?.toString()?.trim() ?: "",
            website = binding.registerWebsiteEt.text?.toString()?.trim() ?: ""
        )
    }

    private fun setUpTextWatchersForRegisterActivation() {
        val textWatchers = listOf(
            binding.registerEmailEt,
            binding.registerNamedEt,
            binding.registerSurnameEt,
            binding.registerPasswordEt,
            binding.registerConfirmPasswordEt
        )

        textWatchers.forEach { editText ->
            editText.addTextChangedListener { checkAllFieldsFilled() }
        }
    }

    private fun checkAllFieldsFilled() {
        val email = binding.registerEmailEt.text.toString()
        val firstName = binding.registerNamedEt.text.toString()
        val lastName = binding.registerSurnameEt.text.toString()
        val password = binding.registerPasswordEt.text.toString()
        val confirmPassword = binding.registerConfirmPasswordEt.text.toString()

        binding.registerBtn.isEnabled = email.isNotEmpty() &&
                    firstName.isNotEmpty() &&
                    password.isNotEmpty() &&
                    confirmPassword.isNotEmpty()
    }


    private fun observeAuthState() {
        authViewModel.authState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is AuthResult.Success -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), getString(R.string.register_successful), Toast.LENGTH_SHORT)
                        .show()

                    goToDashboardActivity()
                }

                is AuthResult.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun goToDashboardActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}