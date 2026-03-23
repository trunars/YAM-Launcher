package eu.ottop.yamlauncher.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import eu.ottop.yamlauncher.R

/**
 * UI settings fragment.
 * Contains preferences for text color, fonts, sizing, and appearance.
 */
class UISettingsFragment : PreferenceFragmentCompat(), TitleProvider {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.ui_preferences, rootKey)
    }

    override fun getTitle(): String {
        return getString(R.string.ui_settings_title)
    }
}
