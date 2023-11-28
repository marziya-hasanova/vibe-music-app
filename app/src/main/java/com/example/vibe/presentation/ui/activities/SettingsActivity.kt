package com.example.vibe.presentation.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.vibe.R
import com.example.vibe.utils.THEME_PREFERENCES
import com.example.vibe.utils.THEME_PREFERENCES_KEY
import com.example.vibe.databinding.ActivitySettingsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.settings)
            setDisplayHomeAsUpEnabled(true)
        }

        val isDarkMode = sharedPreferences.getBoolean(THEME_PREFERENCES_KEY, false)
        updateSwitchColors(isDarkMode)
        updateActionBarColor(this, isDarkMode)

        binding.themeSwitch.isChecked = isDarkMode
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPreferences.edit()) {
                putBoolean(THEME_PREFERENCES_KEY, isChecked)
                apply()
            }
            applyTheme(this)
            restartApp()
        }

    }

    companion object {
        fun applyTheme(context: Context) {
            val sharedPreferences =
                context.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
            val isDarkMode = sharedPreferences.getBoolean(THEME_PREFERENCES_KEY, false)

            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }

        fun updateActionBarColor(context: Context, isDarkMode: Boolean) {
            val actionBar = (context as AppCompatActivity).supportActionBar
            val actionBarColor = if (isDarkMode) {
                ContextCompat.getColor(context, R.color.my_dark_background)
            } else {
                ContextCompat.getColor(context, R.color.my_light_secondary)
            }
            actionBar?.setBackgroundDrawable(ColorDrawable(actionBarColor))
        }
        fun updateBottomNavigationViewColor(context: Context, isDarkMode: Boolean) {
            val bottomNavigationView = (context as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            val bottomNavigationViewColor = if (isDarkMode) {
                ContextCompat.getColor(context, R.color.my_dark_background)
            } else {
                ContextCompat.getColor(context, R.color.my_light_secondary)
            }
            bottomNavigationView?.itemBackground = ColorDrawable(bottomNavigationViewColor)
        }
    }

    private fun updateSwitchColors(isDarkMode: Boolean) {
        val thumbColor = if (isDarkMode) {
            ContextCompat.getColor(this, R.color.my_dark_primary_variant)
        } else {
            ContextCompat.getColor(this, R.color.my_light_primary_variant)
        }
        val trackColor = if (isDarkMode) {
            ContextCompat.getColor(this, R.color.my_light_onPrimary)
        } else {
            ContextCompat.getColor(this, R.color.my_dark_onPrimary)
        }
        binding.themeSwitch.thumbTintList = ColorStateList.valueOf(thumbColor)
        binding.themeSwitch.trackTintList = ColorStateList.valueOf(trackColor)
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}