package com.lubosoft.smsforwarder

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        private const val DAY_NIGHT_MODE_PREFERENCE_KEY = "light_dark_mode_settings"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val darkThemePreference = preferenceScreen.findPreference(DAY_NIGHT_MODE_PREFERENCE_KEY)
        darkThemePreference.setOnPreferenceChangeListener { _, newValue  ->
            val switchPreferenceValue = newValue as Boolean
            if (switchPreferenceValue) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            activity?.recreate()
            true
        }
    }
}