package com.mestero.ui.authentication;

import android.content.Intent
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mestero.databinding.FragmentAuthLoginBinding
import com.mestero.R
import com.mestero.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentAuthLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            val email = binding.loginEmailEt.text.toString()
            val password = binding.loginPasswordEt.text.toString()
            
            // TODO: Add client-side validation
            authViewModel.signIn(email, password)
        }

        binding.noAccBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.forgotPassword.setOnClickListener {
            // TODO: Implement forgot password functionality
        }

        observeAuthState()
    }

    private fun goToDashboardActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


    private fun observeAuthState() {
        authViewModel.authState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is AuthResult.Success -> {
                    binding.progressBar.isVisible = false
                    // Navigate to main screen
                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()

                    goToDashboardActivity()
                }
                is AuthResult.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}