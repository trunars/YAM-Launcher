package eu.ottop.yamlauncher.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.ServiceInfo
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity.ACCESSIBILITY_SERVICE
import eu.ottop.yamlauncher.R
import eu.ottop.yamlauncher.settings.SharedPreferenceManager

class GestureUtils(private val context: Context) {

    private val sharedPreferenceManager = SharedPreferenceManager(context)
    private val logger = Logger.getInstance(context)

    fun getSwipeInfo(launcherApps: LauncherApps, direction: String): Pair<LauncherActivityInfo?, Int?> {
        val app = sharedPreferenceManager.getGestureInfo(direction)

        if (app != null && app.size >= 3) {
            try {
                val componentNameStr = app.getOrNull(1) ?: return Pair(null, null)
                if (componentNameStr.isEmpty()) return Pair(null, null)
                
                val componentName = ComponentName.unflattenFromString(componentNameStr)
                val profileIndex = app.getOrNull(2)?.toIntOrNull()
                
                if (componentName != null && profileIndex != null && profileIndex in launcherApps.profiles.indices) {
                    return try {
                        Pair(
                            launcherApps.resolveActivity(
                                Intent().setComponent(componentName), launcherApps.profiles[profileIndex]
                            ), profileIndex
                        )
                    } catch (e: Exception) {
                        logger.e("GestureUtils", "Failed to resolve gesture app for $direction", e)
                        return Pair(null, null)
                    }
                }
            } catch (e: Exception) {
                logger.e("GestureUtils", "Error parsing gesture info for $direction", e)
            }
        }
        return Pair(null, null)
    }

    fun isAccessibilityServiceEnabled(service: Class<out AccessibilityService>): Boolean {
        val am = context.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)

        for (enabledService in enabledServices) {
            val enabledServiceInfo: ServiceInfo = enabledService.resolveInfo.serviceInfo
            if (enabledServiceInfo.packageName.equals(context.packageName) && enabledServiceInfo.name.equals(
                    service.name
                )
            ) return true
        }

        return false
    }

    fun promptEnableAccessibility() {
        MaterialAlertDialogBuilder(context).apply {
            setTitle(context.getString(R.string.confirm_title))
            setMessage(context.getString(R.string.screenlock_confirmation))
            setPositiveButton(context.getString(R.string.confirm_yes)) { _, _ ->
                // Perform action on confirmation
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            setNegativeButton(context.getString(R.string.confirm_no)) { _, _ ->

            }

        }.create().show()
    }
}
