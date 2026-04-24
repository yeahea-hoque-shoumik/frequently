package com.prime.frequently.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.prime.frequently.R
import com.prime.frequently.constants.AppConstants
import com.prime.frequently.repository.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat() {

    // Wrap the PreferenceFragmentCompat RecyclerView inside fragment_settings.xml
    // so the "Settings" title header is visible above the preference list.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefView = super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        root.findViewById<FrameLayout>(R.id.settings_container).addView(
            prefView,
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        )
        return root
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        wireClearData()
    }

    private fun wireClearData() {
        findPreference<Preference>(AppConstants.PREF_CLEAR_DATA)?.setOnPreferenceClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_clear_data_title)
                .setMessage(R.string.dialog_clear_data_msg)
                .setPositiveButton(R.string.action_clear) { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        SessionRepository(requireContext()).deleteAll()
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
            true
        }
    }
}
