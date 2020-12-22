package com.phoenix.githubuser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.phoenix.githubuser.fragment.SettingFragment

class SettingsActivity : AppCompatActivity() {
    private val TITLE = "Settings"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingFragment())
            .commit()
        supportActionBar?.title = TITLE
    }
}