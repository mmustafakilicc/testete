package org.greenrobot.organicmaps.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import org.greenrobot.organicmaps.OrganicMapsModule
import org.greenrobot.organicmaps.R
import org.greenrobot.organicmaps.widget.placepage.CoordinatesFormat
import java.io.IOException

object SharedPrefUtils {

    fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
    }

    private const val NOOB_ALERT_SHOWN = "alert_for_noob_was_shown"

    private const val PREF_OSM_TOKEN: String =
        "OsmToken" // Unused after migration from OAuth1 to OAuth2
    private const val PREF_OSM_SECRET: String =
        "OsmSecret" // Unused after migration from OAuth1 to OAuth2
    private const val PREF_OSM_USERNAME: String = "OsmUsername"
    private const val PREF_OSM_CHANGESETS_COUNT: String = "OsmChangesetsCount"
    private const val PREF_OSM_OAUTH2_TOKEN: String = "OsmOAuth2Token"


    fun isNoobAlertShown(context: Context): Boolean {
        val prefs = context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
        return prefs.getBoolean(NOOB_ALERT_SHOWN, false)
    }

    fun noobAlertShown(context: Context) {
        context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .edit {
                putBoolean(NOOB_ALERT_SHOWN, true)
            }
    }

    fun isAuthorized(context: Context): Boolean {
        return context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .contains(PREF_OSM_OAUTH2_TOKEN)
    }

    fun containsOAuth1Credentials(context: Context): Boolean {
        val prefs = context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
        return prefs.contains(PREF_OSM_TOKEN) && prefs.contains(PREF_OSM_SECRET)
    }

    fun clearOAuth1Credentials(context: Context) {
        context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .edit {
                remove(PREF_OSM_TOKEN)
                remove(PREF_OSM_SECRET)
            }
    }

    fun getAuthToken(context: Context): String {
        val prefs = context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
        return prefs.getString(PREF_OSM_OAUTH2_TOKEN, "") ?: ""
    }

    fun getUserName(context: Context): String {
        val prefs = context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
        return prefs.getString(PREF_OSM_USERNAME, "") ?: ""
    }

    fun setAuthorization(context: Context, oauthToken: String?, username: String?) {
        context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .edit {
                putString(PREF_OSM_OAUTH2_TOKEN, oauthToken)
                putString(PREF_OSM_USERNAME, username)
            }
    }

    fun clearAuthorization(context: Context) {
        context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .edit {
                remove(PREF_OSM_TOKEN)
                remove(PREF_OSM_SECRET)
                remove(PREF_OSM_USERNAME)
                remove(PREF_OSM_OAUTH2_TOKEN)
            }
    }

    fun getOsmChangesetsCount(context: Context): Int {
        val prefs = context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
        return prefs.getInt(PREF_OSM_CHANGESETS_COUNT, 0)
    }


    fun setOsmChangesetsCount(context: Context, count: Int) {
        context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .edit {
                putInt(PREF_OSM_CHANGESETS_COUNT, count)
            }
    }

    private const val KEY_PREF_LAST_SEARCHED_TAB: String = "LastSearchTab"
    fun getLastSearchedTab(context: Context): Int {
        val prefs = context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
        return prefs.getInt(KEY_PREF_LAST_SEARCHED_TAB, 0)
    }

    fun setLastSearchedTab(context: Context, tab: Int) {
        context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .edit {
                putInt(KEY_PREF_LAST_SEARCHED_TAB, tab)
            }
    }

    private const val KEY_APP_LAUNCH_NUMBER: String = "LaunchNumber"
    private const val KEY_APP_LAST_SESSION_TIMESTAMP: String = "LastSessionTimestamp"
    private const val KEY_APP_FIRST_INSTALL_VERSION_CODE: String = "FirstInstallVersion"
    private const val KEY_APP_LAST_INSTALL_VERSION_CODE: String = "LastInstallVersion"
    private const val KEY_MISC_FIRST_START_DIALOG_SEEN: String = "FirstStartDialogSeen"

    fun increaseAppLauncher(context: Context) {
        val prefs = context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
        val count = prefs.getInt(KEY_APP_LAUNCH_NUMBER, 0)
        prefs.edit {
            putInt(KEY_APP_LAUNCH_NUMBER, count + 1)
        }
    }

    fun setFirstInstallVersion(context: Context, versionCode: Int) {
        val prefs = context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
        val count = prefs.getInt(KEY_APP_LAUNCH_NUMBER, 0)
        val v = prefs.getInt(KEY_APP_FIRST_INSTALL_VERSION_CODE, 0)
        if (count == 0 || v == 0) {
            prefs.edit {
                putInt(KEY_APP_FIRST_INSTALL_VERSION_CODE, versionCode)
            }
        }
    }

    fun setLastSessionTimeStamp(context: Context, timeMillis: Long) {
        context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .edit {
                putLong(KEY_APP_LAST_SESSION_TIMESTAMP, timeMillis)
            }
    }

    fun setLastInstallVersion(context: Context, versionCode: Int) {
        context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .edit {
                putInt(KEY_APP_LAST_INSTALL_VERSION_CODE, versionCode)
            }
    }

    fun isFirstLaunch(context: Context): Boolean {
        return !context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .getBoolean(KEY_MISC_FIRST_START_DIALOG_SEEN, false)
    }

    fun setFirstStartDialogSeen(context: Context) {
        context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .edit {
                putBoolean(KEY_MISC_FIRST_START_DIALOG_SEEN, true)
            }
    }

    fun cleanUpLegacyCounters(context: Context) {
        context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .edit {
                // Clean up legacy counters.
                remove("FirstInstallFlavor")
                remove("SessionNumber")
                remove("WhatsNewShownVersion")
                remove("LastRatedSession")
                remove("RatedDialog")
            }
    }

    const val KEY_PREF_STATISTICS: String = "StatisticsEnabled"
    fun isStatisticsEnabled(context: Context): Boolean {
        return context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .getBoolean(KEY_PREF_STATISTICS, true)
    }

    private const val PREFS_SHOW_EMULATE_BAD_STORAGE_SETTING: String =
        "ShowEmulateBadStorageSetting"

    fun setShouldShowEmulateBadStorageSetting(show: Boolean) {
        PreferenceManager
            .getDefaultSharedPreferences(OrganicMapsModule.get().application)
            .edit {
                putBoolean(PREFS_SHOW_EMULATE_BAD_STORAGE_SETTING, show)
            }
    }

    fun shouldShowEmulateBadStorageSetting(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(OrganicMapsModule.get().application)
            .getBoolean(PREFS_SHOW_EMULATE_BAD_STORAGE_SETTING, false)
    }

    @Throws(IOException::class)
    fun emulateBadExternalStorage() {
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(OrganicMapsModule.get().application)
        val key: String? =
            OrganicMapsModule.get().application.getString(R.string.mn_org_map_pref_emulate_bad_external_storage)
        if (prefs.getBoolean(key, false)) {
            // Emulate one time only -> reset setting to run normally next time.
            prefs.edit { putBoolean(key, false) }
            throw IOException("Bad external storage error injection")
        }
    }

    private const val PREF_COORDINATES_FORMAT: String = "coordinates_format"
    fun getCoordinateFormat(context: Context): Int{
        return context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .getInt(PREF_COORDINATES_FORMAT, CoordinatesFormat.LatLonDecimal.id)
    }

    fun setCoordinateFormat(context: Context, formatId: Int){
        context.getSharedPreferences(
            context.getString(R.string.mn_org_map_pref_file_name),
            Context.MODE_PRIVATE
        )
            .edit {
                putInt(PREF_COORDINATES_FORMAT, formatId)
            }
    }

}