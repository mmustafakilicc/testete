package org.greenrobot.organicmaps.util;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import org.greenrobot.organicmaps.OrganicMapsModule;
import org.greenrobot.organicmaps.R;

public final class Config {
    private static final String KEY_APP_STORAGE = "StoragePath";

    private static final String KEY_DOWNLOADER_AUTO = "AutoDownloadEnabled";
    private static final String KEY_PREF_ZOOM_BUTTONS = "ZoomButtonsEnabled";
    private static final String KEY_PREF_USE_GS = "UseGoogleServices";

    private static final String KEY_MISC_DISCLAIMER_ACCEPTED = "IsDisclaimerApproved";
    private static final String KEY_PREF_KAYAK_DISPLAY = "DisplayKayak";
    private static final String KEY_MISC_KAYAK_ACCEPTED = "IsKayakApproved";
    private static final String KEY_MISC_LOCATION_REQUESTED = "LocationRequested";
    private static final String KEY_MISC_UI_THEME = "UiTheme";
    private static final String KEY_MISC_UI_THEME_SETTINGS = "UiThemeSettings";
    private static final String KEY_MISC_USE_MOBILE_DATA = "UseMobileData";
    private static final String KEY_MISC_USE_MOBILE_DATA_TIMESTAMP = "UseMobileDataTimestamp";
    private static final String KEY_MISC_USE_MOBILE_DATA_ROAMING = "UseMobileDataRoaming";
    private static final String KEY_MISC_KEEP_SCREEN_ON = "KeepScreenOn";

    private static final String KEY_MISC_SHOW_ON_LOCK_SCREEN = "ShowOnLockScreen";
    private static final String KEY_MISC_AGPS_TIMESTAMP = "AGPSTimestamp";
    private static final String KEY_DONATE_URL = "DonateUrl";
    private static final String KEY_PREF_SEARCH_HISTORY = "SearchHistoryEnabled";

    /**
     * True if the first start animation has been seen.
     */
    private static final String KEY_MISC_FIRST_START_DIALOG_SEEN = "FirstStartDialogSeen";

    private Config() {
    }

    @SuppressWarnings("ConstantConditions") // BuildConfig
    private static boolean isFdroid() {
        return false;
    }

    private static int getInt(String key, int def) {
        return nativeGetInt(key, def);
    }

    private static long getLong(String key, long def) {
        return nativeGetLong(key, def);
    }

    private static float getFloat(@NonNull final String key, final float def) {
        return (float) nativeGetDouble(key, def);
    }

    @NonNull
    private static String getString(String key) {
        return getString(key, "");
    }

    @NonNull
    private static String getString(String key, String def) {
        return nativeGetString(key, def);
    }

    private static boolean getBool(String key) {
        return getBool(key, false);
    }

    private static boolean getBool(String key, boolean def) {
        return nativeGetBoolean(key, def);
    }

    private static void setInt(String key, int value) {
        nativeSetInt(key, value);
    }

    private static void setLong(String key, long value) {
        nativeSetLong(key, value);
    }

    private static void setFloat(@NonNull final String key, final float value) {
        nativeSetDouble(key, value);
    }

    private static void setString(String key, String value) {
        nativeSetString(key, value);
    }

    private static void setBool(String key) {
        setBool(key, true);
    }

    private static void setBool(String key, boolean value) {
        nativeSetBoolean(key, value);
    }

    public static String getStoragePath() {
        return getString(KEY_APP_STORAGE);
    }

    public static void setStoragePath(String path) {
        setString(KEY_APP_STORAGE, path);
    }

    public static boolean isAutodownloadEnabled() {
        return getBool(KEY_DOWNLOADER_AUTO, true);
    }

    public static void setAutodownloadEnabled(boolean enabled) {
        setBool(KEY_DOWNLOADER_AUTO, enabled);
    }

    public static boolean showZoomButtons() {
        return getBool(KEY_PREF_ZOOM_BUTTONS, true);
    }

    public static void setShowZoomButtons(boolean show) {
        setBool(KEY_PREF_ZOOM_BUTTONS, show);
    }

    public static void setStatisticsEnabled(boolean enabled) {
        setBool(SharedPrefUtils.KEY_PREF_STATISTICS, enabled);
    }

    public static boolean isKeepScreenOnEnabled() {
        return getBool(KEY_MISC_KEEP_SCREEN_ON, false);
    }

    public static void setKeepScreenOnEnabled(boolean enabled) {
        setBool(KEY_MISC_KEEP_SCREEN_ON, enabled);
    }

    public static boolean isShowOnLockScreenEnabled() {
        // Disabled by default on Android 7.1 and earlier devices.
        // See links below for details:
        // https://github.com/organicmaps/organicmaps/issues/2857
        // https://github.com/organicmaps/organicmaps/issues/3967
        final boolean defaultValue = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
        return getBool(KEY_MISC_SHOW_ON_LOCK_SCREEN, defaultValue);
    }

    public static void setShowOnLockScreenEnabled(boolean enabled) {
        setBool(KEY_MISC_SHOW_ON_LOCK_SCREEN, enabled);
    }

    public static boolean useGoogleServices() {
        // F-droid users expect non-free networks to be disabled by default
        // https://t.me/organicmaps/47334
        // Additionally, in the µG play-services-location library which is used for
        // F-droid builds, GMS api availability is stubbed and always returns true.
        // https://github.com/microg/GmsCore/issues/2309
        // For more details, see the discussion in
        // https://github.com/organicmaps/organicmaps/pull/9575
        return getBool(KEY_PREF_USE_GS, !isFdroid());
    }

    public static void setUseGoogleService(boolean use) {
        setBool(KEY_PREF_USE_GS, use);
    }

    public static boolean isRoutingDisclaimerAccepted() {
        return getBool(KEY_MISC_DISCLAIMER_ACCEPTED);
    }

    public static void acceptRoutingDisclaimer() {
        setBool(KEY_MISC_DISCLAIMER_ACCEPTED);
    }

    public static boolean isKayakDisplayEnabled() {
        // Kayak is disabled by default in F-Droid build,
        // unless a user has already accepted its disclaimer before.
        return getBool(KEY_PREF_KAYAK_DISPLAY, !isFdroid() || isKayakDisclaimerAccepted());
    }

    public static void setKayakDisplay(boolean enabled) {
        setBool(KEY_PREF_KAYAK_DISPLAY, enabled);
    }

    public static boolean isKayakDisclaimerAccepted() {
        return getBool(KEY_MISC_KAYAK_ACCEPTED);
    }

    public static void acceptKayakDisclaimer() {
        setBool(KEY_MISC_KAYAK_ACCEPTED);
    }

    public static boolean isLocationRequested() {
        return getBool(KEY_MISC_LOCATION_REQUESTED);
    }

    public static void setLocationRequested() {
        setBool(KEY_MISC_LOCATION_REQUESTED);
    }

    @NonNull
    public static String getCurrentUiTheme(@NonNull Context context) {
        String defaultTheme = OrganicMapsModule.get().getApplication().getString(R.string.mn_org_map_theme_default);
        String res = getString(KEY_MISC_UI_THEME, defaultTheme);

        if (ThemeUtils.isValidTheme(context, res))
            return res;

        return defaultTheme;
    }

    static void setCurrentUiTheme(@NonNull Context context, @NonNull String theme) {
        if (getCurrentUiTheme(context).equals(theme))
            return;

        setString(KEY_MISC_UI_THEME, theme);
    }

    @NonNull
    public static String getUiThemeSettings(@NonNull Context context) {
        String autoTheme = OrganicMapsModule.get().getApplication().getString(R.string.mn_org_map_theme_auto);
        String res = getString(KEY_MISC_UI_THEME_SETTINGS, autoTheme);
        if (ThemeUtils.isValidTheme(context, res) || ThemeUtils.isAutoTheme(context, res) || ThemeUtils.isNavAutoTheme(context, res))
            return res;

        return autoTheme;
    }

    public static boolean setUiThemeSettings(@NonNull Context context, String theme) {
        if (getUiThemeSettings(context).equals(theme))
            return false;

        setString(KEY_MISC_UI_THEME_SETTINGS, theme);
        return true;
    }

    public static boolean isLargeFontsSize() {
        return nativeGetLargeFontsSize();
    }

    public static void setLargeFontsSize(boolean value) {
        nativeSetLargeFontsSize(value);
    }

    @NonNull
    public static NetworkPolicy.Type getUseMobileDataSettings() {
        int value = getInt(KEY_MISC_USE_MOBILE_DATA, NetworkPolicy.NONE);

        if (value < 0 || value >= NetworkPolicy.Type.values().length)
            return NetworkPolicy.Type.ASK;

        return NetworkPolicy.Type.values()[value];
    }

    public static void setUseMobileDataSettings(@NonNull NetworkPolicy.Type value) {
        setInt(KEY_MISC_USE_MOBILE_DATA, value.ordinal());
        setBool(KEY_MISC_USE_MOBILE_DATA_ROAMING, ConnectionState.INSTANCE.isInRoaming());
    }

    public static void setMobileDataTimeStamp(long timestamp) {
        setLong(KEY_MISC_USE_MOBILE_DATA_TIMESTAMP, timestamp);
    }

    static long getMobileDataTimeStamp() {
        return getLong(KEY_MISC_USE_MOBILE_DATA_TIMESTAMP, 0L);
    }

    static boolean getMobileDataRoaming() {
        return getBool(KEY_MISC_USE_MOBILE_DATA_ROAMING, false);
    }

    public static void setAgpsTimestamp(long timestamp) {
        setLong(KEY_MISC_AGPS_TIMESTAMP, timestamp);
    }

    public static long getAgpsTimestamp() {
        return getLong(KEY_MISC_AGPS_TIMESTAMP, 0L);
    }

    public static boolean isTransliteration() {
        return nativeGetTransliteration();
    }

    public static void setTransliteration(boolean value) {
        nativeSetTransliteration(value);
    }

    public static boolean isNY() {
        return getBool("NY");
    }

    @NonNull
    @SuppressWarnings("ConstantConditions") // BuildConfig
    public static String getDonateUrl(@NonNull Context context) {
        final String url = getString(KEY_DONATE_URL);
        return url;
    }

    public static void init(@NonNull Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.prefs_main, false);


        // Update counters.
        SharedPrefUtils.INSTANCE.setFirstInstallVersion(context, OrganicMapsModule.Companion.getAppVersionCode());
        SharedPrefUtils.INSTANCE.increaseAppLauncher(context);
        SharedPrefUtils.INSTANCE.setLastSessionTimeStamp(context, System.currentTimeMillis());
        SharedPrefUtils.INSTANCE.setLastInstallVersion(context, OrganicMapsModule.Companion.getAppVersionCode());

        // Clean up legacy counters.
        SharedPrefUtils.INSTANCE.cleanUpLegacyCounters(context);

        // Migrate ENABLE_SCREEN_SLEEP to KEEP_SCREEN_ON.
        final String KEY_MISC_ENABLE_SCREEN_SLEEP = "EnableScreenSleep";
        if (nativeHasConfigValue(KEY_MISC_ENABLE_SCREEN_SLEEP)) {
            nativeSetBoolean(KEY_MISC_KEEP_SCREEN_ON, !getBool(KEY_MISC_ENABLE_SCREEN_SLEEP, false));
            nativeDeleteConfigValue(KEY_MISC_ENABLE_SCREEN_SLEEP);
        }
    }

    public static boolean isFirstLaunch(@NonNull Context context) {
        return SharedPrefUtils.INSTANCE.isFirstLaunch(context);
    }

    public static void setFirstStartDialogSeen(@NonNull Context context) {
        SharedPrefUtils.INSTANCE.setFirstStartDialogSeen(context);
    }

    public static boolean isSearchHistoryEnabled() {
        return getBool(KEY_PREF_SEARCH_HISTORY, true);
    }

    public static void setSearchHistoryEnabled(boolean enabled) {
        setBool(KEY_PREF_SEARCH_HISTORY, enabled);
    }

    public static class TTS {
        interface Keys {
            String ENABLED = "TtsEnabled";
            String LANGUAGE = "TtsLanguage";
            String VOLUME = "TtsVolume";
            String STREETS = "TtsStreetNames";
        }

        public interface Defaults {
            boolean ENABLED = true;

            float VOLUME_MIN = 0.0f;
            float VOLUME_MAX = 1.0f;
            float VOLUME = VOLUME_MAX;

            boolean STREETS = false; // TTS may mangle some languages, do not announce streets by default
        }

        public static boolean isEnabled() {
            return getBool(Keys.ENABLED, Defaults.ENABLED);
        }

        public static void setEnabled(final boolean enabled) {
            setBool(Keys.ENABLED, enabled);
        }

        @NonNull
        public static String getLanguage() {
            return getString(Keys.LANGUAGE);
        }

        public static void setLanguage(@NonNull final String language) {
            setString(Keys.LANGUAGE, language);
        }

        public static float getVolume() {
            return getFloat(Keys.VOLUME, Defaults.VOLUME);
        }

        public static void setVolume(final float volume) {
            setFloat(Keys.VOLUME, volume);
        }

        public static boolean getAnnounceStreets() {
            return getBool(Keys.STREETS, Defaults.STREETS);
        }

        public static void setAnnounceStreets(boolean enabled) {
            setBool(Keys.STREETS, enabled);
        }

    }

    private static native boolean nativeHasConfigValue(String name);

    private static native boolean nativeDeleteConfigValue(String name);

    private static native boolean nativeGetBoolean(String name, boolean defaultValue);

    private static native void nativeSetBoolean(String name, boolean value);

    private static native int nativeGetInt(String name, int defaultValue);

    private static native void nativeSetInt(String name, int value);

    private static native long nativeGetLong(String name, long defaultValue);

    private static native void nativeSetLong(String name, long value);

    private static native double nativeGetDouble(String name, double defaultValue);

    private static native void nativeSetDouble(String name, double value);

    private static native String nativeGetString(String name, String defaultValue);

    private static native void nativeSetString(String name, String value);

    private static native boolean nativeGetLargeFontsSize();

    private static native void nativeSetLargeFontsSize(boolean value);

    private static native boolean nativeGetTransliteration();

    private static native void nativeSetTransliteration(boolean value);
}
