package org.greenrobot.organicmaps.util;

import android.content.Context;

import androidx.annotation.NonNull;

import org.greenrobot.organicmaps.maplayer.Mode;

import java.io.IOException;
import java.util.Locale;

public final class SharedPropertiesUtils {
    private static final String PREFS_SHOULD_SHOW_LAYER_MARKER_FOR = "ShouldShowGuidesLayerMarkerFor";

    //Utils class
    private SharedPropertiesUtils() {
        throw new IllegalStateException("Try instantiate utility class SharedPropertiesUtils");
    }

    public static boolean isStatisticsEnabled(@NonNull Context context) {
        return SharedPrefUtils.INSTANCE.isStatisticsEnabled(context);
    }

    public static void setShouldShowEmulateBadStorageSetting(@NonNull Context context, boolean show) {
        SharedPrefUtils.INSTANCE.setShouldShowEmulateBadStorageSetting(show);
    }

    public static boolean shouldShowEmulateBadStorageSetting(@NonNull Context context) {

        return SharedPrefUtils.INSTANCE.shouldShowEmulateBadStorageSetting();
    }

    /**
     * @param context context
     * @throws IOException if "Emulate bad storage" is enabled in preferences
     */
    public static void emulateBadExternalStorage(@NonNull Context context) throws IOException {
        SharedPrefUtils.INSTANCE.emulateBadExternalStorage();
    }

    public static boolean shouldShowNewMarkerForLayerMode(@NonNull Context context,
                                                          @NonNull Mode mode) {
        return switch (mode) {
            case SUBWAY, TRAFFIC, ISOLINES -> false;
            default ->
                    getBoolean(context, PREFS_SHOULD_SHOW_LAYER_MARKER_FOR + mode.name().toLowerCase(Locale.ENGLISH), true);
        };
    }

    public static void setLayerMarkerShownForLayerMode(@NonNull Context context, @NonNull Mode mode) {
        putBoolean(context, PREFS_SHOULD_SHOW_LAYER_MARKER_FOR + mode.name()
                .toLowerCase(Locale.ENGLISH), false);
    }

    private static boolean getBoolean(@NonNull Context context, @NonNull String key, boolean defValue) {
        return SharedPrefUtils.INSTANCE.getPref(context).getBoolean(key, defValue);
    }

    private static void putBoolean(@NonNull Context context, @NonNull String key, boolean value) {
        SharedPrefUtils.INSTANCE.getPref(context).edit().putBoolean(key, value)
                .apply();

    }
}
