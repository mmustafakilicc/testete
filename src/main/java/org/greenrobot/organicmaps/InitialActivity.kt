package org.greenrobot.organicmaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.greenrobot.organicmaps.display.DisplayManager
import org.greenrobot.organicmaps.downloader.DownloaderActivity
import org.greenrobot.organicmaps.intent.Factory
import org.greenrobot.organicmaps.util.Config
import org.greenrobot.organicmaps.util.LocationUtils
import org.greenrobot.organicmaps.util.SharingUtils
import org.greenrobot.organicmaps.util.SharingUtils.SharingIntent
import org.greenrobot.organicmaps.util.Utils
import org.greenrobot.organicmaps.util.concurrency.UiThread
import org.greenrobot.organicmaps.util.log.Logger
import org.greenrobot.qwerty.common.PermissionUtils
import java.io.IOException

class InitialActivity : AppCompatActivity() {

    private var mCanceled = false

    private val mApiRequest = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result: ActivityResult ->
            setResult(result.resultCode, result.data)
            finish()
        })
    private var mShareLauncher: ActivityResultLauncher<SharingIntent?>? = null

    private val mInitCoreDelayedTask = Runnable {
        this.initialize()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mn_org_map_activity_initial)
        setupViews()
    }

    private fun setupViews() {
        UiThread.cancelDelayedTasks(mInitCoreDelayedTask)

        mShareLauncher = SharingUtils.RegisterLauncher(this)

        if (DisplayManager.from(this).isCarDisplayUsed) {
            startActivity(Intent(this, MapPlaceholderActivity::class.java))
            finish()
        }

        UiThread.cancelDelayedTasks(mInitCoreDelayedTask)
    }

    private fun checkLocationPermissions() {
        PermissionUtils.checkPermission(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            listener = object : PermissionUtils.PermissionListener{
                override fun onPermissionGranted() {
                    Config.setLocationRequested()
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Config.setLocationRequested()
                }
            }
        )
    }

    private fun initialize() {
        val asyncContinue: Boolean
        try {
            asyncContinue = OrganicMapsModule.get().init(Runnable { this.processNavigation() })
        } catch (error: IOException) {
            showFatalErrorDialog(
                R.string.dialog_error_storage_title,
                R.string.dialog_error_storage_message,
                error
            )
            return
        }

        if (Config.isFirstLaunch(this) && LocationUtils.checkLocationPermission(this)) {
            val locationHelper = OrganicMapsModule.get().getLocationHelper()
            locationHelper.onEnteredIntoFirstRun()
            if (!locationHelper.isActive) locationHelper.start()
        }

        if (!asyncContinue) processNavigation()
    }

    @SuppressLint("UnsafeIntentLaunch")
    @Keep
    @Suppress("unused")
    fun processNavigation() {
        if (isDestroyed) {
            Logger.w(
                TAG,
                "Ignore late callback from core because activity is already destroyed"
            )
            return
        }

        val safeIntent = intent ?: return

        if (isManageSpaceActivity(safeIntent)) {
            intent.setComponent(ComponentName(this, DownloaderActivity::class.java))
        } else {
            intent.setComponent(ComponentName(this, DownloadResourcesLegacyActivity::class.java))
        }


        safeIntent.setFlags(safeIntent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION)

        if (Factory.isStartedForApiResult(safeIntent)) {
            // Wait for the result from MwmActivity for API callers.
            mApiRequest.launch(safeIntent)
            return
        }

        Config.setFirstStartDialogSeen(this)
        startActivity(safeIntent)
        finish()

    }

    private fun showFatalErrorDialog(
        @StringRes titleId: Int,
        @StringRes messageId: Int,
        error: Exception?
    ) {
        mCanceled = true
        MaterialAlertDialogBuilder(this, R.style.MwmTheme_AlertDialog)
            .setTitle(titleId)
            .setMessage(messageId)
            .setPositiveButton(
                R.string.report_a_bug
            ) { dialog, which ->
                Utils.sendBugReport(
                    mShareLauncher!!,
                    this,
                    "Fatal Error",
                    Log.getStackTraceString(error)
                )
            }
            .setCancelable(false)
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (mCanceled) return
        if (!Config.isLocationRequested()) {
            Logger.d(TAG, "Requesting location permissions")
            checkLocationPermissions()
            return
        }

        UiThread.runLater(mInitCoreDelayedTask, DELAY)
    }

    override fun onPause() {
        super.onPause()
        UiThread.cancelDelayedTasks(mInitCoreDelayedTask)
    }

    override fun onDestroy() {
        super.onDestroy()
        mApiRequest.unregister()
    }

    private fun isManageSpaceActivity(intent: Intent): Boolean {
        val component = intent.component

        if (Intent.ACTION_VIEW != intent.action) return false
        if (component == null) return false

        val manageSpaceActivityName = ".ManageSpaceActivity"

        return manageSpaceActivityName == component.className
    }

    companion object {
        private val TAG: String = InitialActivity::class.java.getSimpleName()
        private const val DELAY: Long = 100
    }
}