package com.mestero.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.mestero.ui.intro.OnboardingAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mestero.R
import com.mestero.constants.Constants.SHARED_INTRO_DONE_FLAG
import com.mestero.constants.Constants.SHARED_PREF_INTRO
import com.mestero.databinding.ActivityIntroBinding
import com.mestero.network.auth.AccountService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    //TODO data binding
    private lateinit var binding: ActivityIntroBinding
    @Inject
    lateinit var accountService: AccountService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Entry point Activity
        // Show Onboarding or navigate to main flow based on whether intro was completed previosuly
        val sharedPrefs = getSharedPreferences(SHARED_PREF_INTRO, Context.MODE_PRIVATE)
        val introDone = sharedPrefs.getBoolean(SHARED_INTRO_DONE_FLAG, false)
        if (introDone) {
            handlePostIntroFlow()
        } else {
            handleIntroFlow()
        }
    }

    private fun handleIntroFlow() {
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_intro
        )

        val viewPager = binding.viewPager
        //val adapter = OnboardingAdapter(this, 3)
        viewPager.adapter = OnboardingAdapter(this, 3)
        TabLayoutMediator(binding.dotsTab, viewPager) { _, _ -> }.attach()
    }


    private fun handlePostIntroFlow() {
        // TODO - a splash layout ?
        // setContentView(R.layout. - slash screen-)

        // Navigate based on user login status
        val intent = if (accountService.hasUser()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, AuthenticationActivity::class.java)
        }
        startActivity(intent)

        // Prevent the user from navigating back to the intro screens
        finish()
    }

}