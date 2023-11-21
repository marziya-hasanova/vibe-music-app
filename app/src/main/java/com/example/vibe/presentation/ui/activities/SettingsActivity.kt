package com.example.vibe.presentation.ui.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.vibe.R
import com.example.vibe.databinding.ActivitySettingsBinding
import com.example.vibe.presentation.ui.viewModels.MusicPlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val musicPlayerViewModel by viewModels<MusicPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.settings)
            setDisplayHomeAsUpEnabled(true)
        }

        musicPlayerViewModel.isSleepTimerTriggered.observe(this) {
            if (it) {
                binding.timerSwitch.isChecked = false
            }
        }

        binding.timerSwitch.setOnCheckedChangeListener { _, isChecked ->
            musicPlayerViewModel.setSleepTimer(isChecked)
        }
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