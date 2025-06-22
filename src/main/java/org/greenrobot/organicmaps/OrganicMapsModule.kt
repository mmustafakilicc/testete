package org.greenrobot.organicmaps

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import org.greenrobot.organicmaps.background.OsmUploadWork
import org.greenrobot.organicmaps.bookmarks.data.BookmarkManager
import org.greenrobot.organicmaps.content.AttractionLocation
import org.greenrobot.organicmaps.display.DisplayManager
import org.greenrobot.organicmaps.downloader.Android7RootCertificateWorkaround
import org.greenrobot.organicmaps.downloader.DownloaderNotifier
import org.greenrobot.organicmaps.location.LocationHelper
import org.greenrobot.organicmaps.location.LocationState
import org.greenrobot.organicmaps.location.SensorHelper
import org.greenrobot.organicmaps.location.TrackRecorder
import org.greenrobot.organicmaps.location.TrackRecordingService
import org.greenrobot.organicmaps.maplayer.isolines.IsolinesManager
import org.greenrobot.organicmaps.maplayer.subway.SubwayManager
import org.greenrobot.organicmaps.maplayer.traffic.TrafficManager
import org.greenrobot.organicmaps.routing.NavigationService
import org.greenrobot.organicmaps.routing.RoutingController
import org.greenrobot.organicmaps.sdk.search.SearchEngine
import org.greenrobot.organicmaps.settings.StoragePathManager
import org.greenrobot.organicmaps.sound.TtsPlayer
import org.greenrobot.organicmaps.util.Config
import org.greenrobot.organicmaps.util.ConnectionState
import org.greenrobot.organicmaps.util.KmlDocumentData
import org.greenrobot.organicmaps.util.SharedPropertiesUtils
import org.greenrobot.organicmaps.util.StorageUtils
import org.greenrobot.organicmaps.util.ThemeSwitcher
import org.greenrobot.organicmaps.util.UiUtils
import org.greenrobot.organicmaps.util.Utils
import org.greenrobot.organicmaps.util.log.Logger
import org.greenrobot.organicmaps.util.log.LogsManager
import java.io.IOException
import java.lang.ref.WeakReference

class OrganicMapsModule private constructor(
    val application: Application,
) {

    private var suggestionDialog: DialogFragment? = null
    fun getSuggestionDialog() = suggestionDialog

    private lateinit var mSubwayManager: SubwayManager
    fun getSubwayManager() = mSubwayManager

    private lateinit var mIsolinesManager: IsolinesManager
    fun getIsoLinesManager() = mIsolinesManager

    private lateinit var mLocationHelper: LocationHelper
    fun getLocationHelper() = mLocationHelper

    private lateinit var mSensorHelper: SensorHelper
    fun getSensorHelper() = mSensorHelper

    private lateinit var mDisplayManager: DisplayManager
    fun getDisplayManager() = mDisplayManager

    private var mProcessLifecycleObserver: LifecycleObserver? = null

    private var mTopActivity: WeakReference<Activity>? = null
    fun getTopActivity(): Activity?{
        return if (mTopActivity != null) mTopActivity!!.get() else null
    }

    private var _kmlDocumentData: KmlDocumentData? = null
    fun getKmlData() = _kmlDocumentData

    @Volatile
    private var mFrameworkInitialized = false

    @Volatile
    private var mPlatformInitialized = false

    companion object {

        var appVersionCode = 1
        var appVersionName = "1.0"
        var applicationId = "org.greenrobot.organicmaps"
        var fileProviderAuthority = "org.greenrobot.organicmaps.provider"

        private val TAG: String = OrganicMapsModule::class.java.getSimpleName()

        @JvmStatic
        private var INSTANCE: OrganicMapsModule? = null

        @JvmStatic
        fun get(): OrganicMapsModule {
            return INSTANCE ?: throw IllegalStateException("OrganicMapsModule is not configured")
        }

        @JvmStatic
        fun configure(
            application: Application,
            lifecycleObserver: LifecycleObserver,
            appVersionCode: Int,
            appVersionName: String,
            applicationId: String,
            fileProviderAuthority: String,
        ) {
            System.loadLibrary("organicmaps")
            INSTANCE = OrganicMapsModule(application)
            this.get().initSettings()
            this.appVersionCode = appVersionCode
            this.appVersionName = appVersionName
            this.applicationId = applicationId
            this.fileProviderAuthority = fileProviderAuthority
            this.get().mSubwayManager = SubwayManager(this.get().application)
            this.get().mIsolinesManager = IsolinesManager(this.get().application)
            this.get().mLocationHelper = LocationHelper(this.get().application)
            this.get().mSensorHelper = SensorHelper(this.get().application)
            this.get().mDisplayManager = DisplayManager()
            this.get().mProcessLifecycleObserver = lifecycleObserver
        }

        @JvmStatic
        fun show(context: Context) {
            context.startActivity(Intent(context, InitialActivity::class.java))
        }

        @JvmStatic
        fun show(context: Context, kmlDocumentData: KmlDocumentData) {
            this.get()._kmlDocumentData = kmlDocumentData
            context.startActivity(Intent(context, InitialActivity::class.java))
        }
    }

    init {
        System.loadLibrary("organicmaps")
    }

    @Throws(IOException::class)
    fun init(onComplete: Runnable): Boolean {
        initNativePlatform()
        return initNativeFramework(onComplete)
    }

    private fun initSettings(){
        LogsManager.INSTANCE.initFileLogging(application);
        Android7RootCertificateWorkaround.initializeIfNeeded(application)


        // Set configuration directory as early as possible.
        // Other methods may explicitly use Config, which requires settingsDir to be set.
        val settingsPath = StorageUtils.getSettingsPath(application)
        if (!StorageUtils.createDirectory(settingsPath)) throw AssertionError("Can't create settingsDir $settingsPath")
        Logger.d(TAG, "Settings path = $settingsPath")
        nativeSetSettingsDir(settingsPath)

        Config.init(application)

        ConnectionState.INSTANCE.initialize(application)

        DownloaderNotifier.createNotificationChannel(application)
        NavigationService.createNotificationChannel(application)
        TrackRecordingService.createNotificationChannel(application)
    }

    @Throws(IOException::class)
    private fun initNativePlatform() {
        if (mPlatformInitialized) return

        val apkPath = StorageUtils.getApkPath(application)
        Logger.d(TAG, "Apk path = $apkPath")
        // Note: StoragePathManager uses Config, which requires SettingsDir to be set.
        val writablePath = StoragePathManager.findMapsStorage(application)
        Logger.d(TAG, "Writable path = $writablePath")
        val privatePath = StorageUtils.getPrivatePath(application)
        Logger.d(TAG, "Private path = $privatePath")
        val tempPath = StorageUtils.getTempPath(application)
        Logger.d(TAG, "Temp path = $tempPath")

        // If platform directories are not created it means that native part of app will not be able
        // to work at all. So, we just ignore native part initialization in this case, e.g. when the
        // external storage is damaged or not available (read-only).
        createPlatformDirectories(writablePath, privatePath, tempPath)

        nativeInitPlatform(
            application,
            apkPath,
            writablePath,
            privatePath,
            tempPath, UiUtils.isTablet(application)
        )
        Config.setStoragePath(writablePath)
        Config.setStatisticsEnabled(SharedPropertiesUtils.isStatisticsEnabled(application))

        mPlatformInitialized = true
        Logger.i(TAG, "Platform initialized")
    }

    @Throws(IOException::class)
    private fun createPlatformDirectories(
        writablePath: String,
        privatePath: String,
        tempPath: String
    ) {
        SharedPropertiesUtils.emulateBadExternalStorage(application)

        StorageUtils.requireDirectory(writablePath)
        StorageUtils.requireDirectory(privatePath)
        StorageUtils.requireDirectory(tempPath)
    }

    private fun initNativeFramework(onComplete: Runnable): Boolean {
        if (mFrameworkInitialized) return false

        nativeInitFramework(onComplete)

        initNativeStrings()
        ThemeSwitcher.INSTANCE.initialize(application)
        SearchEngine.INSTANCE.initialize()
        BookmarkManager.loadBookmarks()
        TtsPlayer.INSTANCE.initialize(application)
        ThemeSwitcher.INSTANCE.restart(false)
        RoutingController.get().initialize(application)
        TrafficManager.INSTANCE.initialize()
        SubwayManager.from(application).initialize()
        IsolinesManager.from(application).initialize()
        mProcessLifecycleObserver?.let { ProcessLifecycleOwner.get().lifecycle.addObserver(it) }

        Logger.i(TAG, "Framework initialized")
        mFrameworkInitialized = true
        return true
    }

    private fun initNativeStrings() {
        nativeAddLocalization("core_entrance", application.getString(R.string.core_entrance))
        nativeAddLocalization("core_exit", application.getString(R.string.core_exit))
        nativeAddLocalization("core_my_places", application.getString(R.string.core_my_places))
        nativeAddLocalization(
            "core_my_position",
            application.getString(R.string.core_my_position)
        )
        nativeAddLocalization(
            "core_placepage_unknown_place",
            application.getString(R.string.core_placepage_unknown_place)
        )
        nativeAddLocalization("postal_code", application.getString(R.string.postal_code))
        nativeAddLocalization("wifi", application.getString(R.string.category_wifi))
    }

    fun onBackground() {
        Logger.d(TAG)

        nativeOnTransit(false)

        OsmUploadWork.startActionUploadOsmChanges(application)

        if (mDisplayManager.isDeviceDisplayUsed != true) Logger.i(
            LocationState.LOCATION_TAG,
            "Android Auto is active, keeping location in the background"
        )
        else if (RoutingController.get().isNavigating) Logger.i(
            LocationState.LOCATION_TAG,
            "Navigation is in progress, keeping location in the background"
        )
        else if (!Map.isEngineCreated() || LocationState.getMode() == LocationState.PENDING_POSITION) Logger.i(
            LocationState.LOCATION_TAG,
            "PENDING_POSITION mode, keeping location in the background"
        )
        else if (TrackRecorder.nativeIsTrackRecordingEnabled()) Logger.i(
            LocationState.LOCATION_TAG,
            "Track Recordr is active, keeping location in the background"
        )
        else {
            Logger.i(LocationState.LOCATION_TAG, "Stopping location in the background")
            mLocationHelper.stop()
        }
    }

    fun onForeground() {
        Logger.d(TAG)

        nativeOnTransit(true)

        mLocationHelper.resumeLocationInForeground()
    }

    fun onActivityResumed(activity: Activity) {
        Logger.d(TAG, "activity = $activity")
        Utils.showOnLockScreen(Config.isShowOnLockScreenEnabled(), activity)
        mSensorHelper.setRotation(activity.windowManager.defaultDisplay.rotation)
        mTopActivity = WeakReference<Activity>(activity)
    }

    fun onActivityPaused(activity: Activity) {
        Logger.d(TAG, "activity = $activity")
        mTopActivity = null
    }

    fun arePlatformAndCoreInitialized(): Boolean {
        return mFrameworkInitialized && mPlatformInitialized
    }

    private external fun nativeOnTransit(foreground: Boolean)
    private external fun nativeSetSettingsDir(settingsPath: String?)
    private external fun nativeInitPlatform(
        context: Context?,
        apkPath: String?,
        writablePath: String?,
        privatePath: String?,
        tmpPath: String?,
        isTablet: Boolean
    )

    private external fun nativeInitFramework(onComplete: Runnable)
    private external fun nativeAddLocalization(name: String?, value: String?)
}