package com.mestero.ui.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mestero.activities.AuthenticationActivity
import com.mestero.constants.Constants.SHARED_INTRO_DONE_FLAG
import com.mestero.constants.Constants.SHARED_PREF_INTRO
import com.mestero.databinding.FragmentOnboardingThirdBinding
import androidx.core.content.edit


class ThirdFragment : Fragment() {
    private lateinit var binding: FragmentOnboardingThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.thirdStartBtn.setOnClickListener {
            // Mark onboarding as completed and navigate to authentication
            val sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_INTRO, Context.MODE_PRIVATE)
            sharedPreferences.edit { putBoolean(SHARED_INTRO_DONE_FLAG, true) }

            val intent = Intent(requireActivity(), AuthenticationActivity::class.java)
            startActivity(intent)
        }
    }
}
