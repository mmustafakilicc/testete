Organicmaps
=============

# Dependencies:

> implementation 'com.gitlab.tikipstudio.mnopqr:organicmaps:XXXX'

# Usage:

- Application implements ActivityLifecycleCallbacks
```
    private val mProcessLifecycleObserver: LifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            OrganicMapsModule.get().onForeground()
        }

        override fun onStop(owner: LifecycleOwner) {
            OrganicMapsModule.get().onBackground()
        }
    }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {}

        override fun onActivityResumed(activity: Activity) {
            OrganicMapsModule.get().onActivityResumed(activity)
        }

        override fun onActivityPaused(activity: Activity) {
            OrganicMapsModule.get().onActivityPaused(activity)
        }

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}

```

- Application > onCreate
```
        OrganicMapsModule.configure(
            this,
            mProcessLifecycleObserver,
            BuildConfig.VERSION_CODE,
            BuildConfig.VERSION_NAME,
            BuildConfig.APPLICATION_ID,
            BuildConfig.FILE_PROVIDER_AUTHORITY
        )
```
- app build.gradle
...
android.applicationVariants.configureEach { variant ->
    def authorityValue = variant.applicationId + ".provider"
    def authority = "\"" + authorityValue + "\""
    variant.buildConfigField 'String', 'FILE_PROVIDER_AUTHORITY', authority
    def flavor = variant.getMergedFlavor()
    flavor.manifestPlaceholders += [FILE_PROVIDER_PLACEHOLDER : authorityValue]
    variant.resValue 'string', 'app_id', variant.applicationId
}
...

- app build.gradle, under android tag, add the following:
```
    ndkVersion '27.2.12479018'

    defaultConfig {
        multiDexEnabled true
    }
    buildTypes {
        debug {
            versionNameSuffix '-debug'
            jniDebuggable true
            minifyEnabled false
            ndk.debugSymbolLevel = 'none'
        }
    }

    compileOptions {
        coreLibraryDesugaringEnabled true
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = '17'
    }

    buildFeatures {
       dataBinding = true
       viewBinding = true
       buildConfig = true
    }
    androidResources {
        ignoreAssetsPattern '!.svn:!.git:!.DS_Store:!*.scc:.*:<dir>_*:!CVS:!thumbs.db:!picasa.ini:!*~'
        noCompress = ['txt', 'bin', 'html', 'png', 'json', 'mwm', 'ttf', 'sdf', 'ui', 'config', 'csv', 'spv', 'obj']
    }
```
- dependencies
...
    implementation project(':organicmaps')
    coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:2.1.5'
...

- project build.gradle
...

android.native.buildOutput=verbose

enableVulkanDiagnostics=OFF
enableTrace=OFF
supportedLocalizations=af,ar,az,be,bg,ca,cs,da,de,el,en,en_GB,es,es_MX,et,eu,fa,fi,fr,fr_CA,iw,hi,hu,in,it,ja,ko,lt,lv,mr,mt,nb,nl,pl,pt,pt_BR,ro,ru,sk,sr,sv,sw,th,tr,uk,vi,zh,zh_HK,zh_MO,zh_TW

...

- Where you want to start the module interface
```
Organicmaps.show(this)
```# testete
