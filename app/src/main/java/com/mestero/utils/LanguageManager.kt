package com.mestero.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LanguageManager {
    private const val PREFS_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_ROMANIAN = "ro"
    

    fun getLanguage(context: Context): String {
        val savedLanguage = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, null)
        
        // If no language saved, detect system's language
        if (savedLanguage == null) {
            val systemLanguage = getSystemLanguage(context)
            return if (systemLanguage == LANGUAGE_ROMANIAN) LANGUAGE_ROMANIAN else LANGUAGE_ENGLISH
        }
        
        return savedLanguage
    }
    

    fun saveLanguage(context: Context, language: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }
    

    fun applyLanguage(context: Context): Context {
        val language = getLanguage(context)
        return setLocale(context, language)
    }
    

    private fun setLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)

        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
    

    private fun getSystemLanguage(context: Context): String {
        return context.resources
            .configuration
            .locales[0]
            .language
    }
    

    fun getLanguageDisplayName(language: String): String {
        return when (language) {
            LANGUAGE_ENGLISH -> "English"
            LANGUAGE_ROMANIAN -> "Română"
            else -> "English"
        }
    }
    

    fun getAvailableLanguages(): Map<String, String> {
        return mapOf(
            LANGUAGE_ENGLISH to "English",
            LANGUAGE_ROMANIAN to "Română"
        )
    }
}
