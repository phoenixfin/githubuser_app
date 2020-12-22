package com.phoenix.consumerapp.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.phoenix.consumerapp.MainActivity.Companion.alarmReceiver
import com.phoenix.consumerapp.R

class SettingFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var ALARM_ON: String
    private lateinit var alarmPreference: SwitchPreference

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)
        init()
        setSummaries()
    }

    private fun init() {
        ALARM_ON = resources.getString(R.string.key_alarm)
        alarmPreference = findPreference<SwitchPreference> (ALARM_ON) as SwitchPreference
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == ALARM_ON) {
            alarmPreference.isChecked = sharedPreferences.getBoolean(ALARM_ON, false)
            if (alarmPreference.isChecked) {
                val repeatTime = "09:00"
                val repeatMessage = "Let's find popular message on Github!"
                alarmReceiver.setRepeatingAlarm(activity, repeatTime, repeatMessage)
            } else {
                alarmReceiver.cancelAlarm(activity)
            }
        }
    }

    private fun setSummaries() {
        val sh = preferenceManager.sharedPreferences
        alarmPreference.isChecked = sh.getBoolean(ALARM_ON, false)
    }
}