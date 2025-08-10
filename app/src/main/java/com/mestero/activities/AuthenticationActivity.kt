package com.mestero.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mestero.R
import com.mestero.databinding.ActivityAuthenticationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO decide between frag manager or nav controller  -  or use startDestination in nav graph
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.auth_fragment_container, LoginFragment())
//            .commit()
    }


    // Todo remove - handled in Login fragment
    fun goToDashboardActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}
