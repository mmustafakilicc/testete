package org.greenrobot.organicmaps.intent;

import static org.greenrobot.organicmaps.api.Const.EXTRA_PICK_POINT;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.IntentCompat;

import org.greenrobot.organicmaps.Framework;
import org.greenrobot.organicmaps.Map;
import org.greenrobot.organicmaps.MwmActivity;
import org.greenrobot.organicmaps.OrganicMapsModule;
import org.greenrobot.organicmaps.api.ParsedRoutingData;
import org.greenrobot.organicmaps.api.ParsedSearchRequest;
import org.greenrobot.organicmaps.api.RequestType;
import org.greenrobot.organicmaps.api.RoutePoint;
import org.greenrobot.organicmaps.bookmarks.data.BookmarkManager;
import org.greenrobot.organicmaps.bookmarks.data.FeatureId;
import org.greenrobot.organicmaps.bookmarks.data.MapObject;
import org.greenrobot.organicmaps.editor.OsmLoginActivity;
import org.greenrobot.organicmaps.routing.RoutingController;
import org.greenrobot.organicmaps.sdk.search.SearchEngine;
import org.greenrobot.organicmaps.search.SearchActivity;
import org.greenrobot.organicmaps.util.StorageUtils;
import org.greenrobot.organicmaps.util.concurrency.ThreadPool;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Factory {
    public static boolean isStartedForApiResult(@NonNull Intent intent) {
        // Previously, we relied on the implicit FORWARD_RESULT_FLAG to detect if the caller was
        // waiting for a result. However, this approach proved to be less reliable than using
        // the explicit EXTRA_PICK_POINT flag.
        // https://github.com/organicmaps/organicmaps/pull/8910
        return intent.getBooleanExtra(EXTRA_PICK_POINT, false);
    }

    public static class KmzKmlProcessor implements IntentProcessor {
        @Override
        public boolean process(@NonNull Intent intent, @NonNull MwmActivity activity) {
            // See KML/KMZ/KMB intent filters in manifest.
            final List<Uri> uris;
            if (Intent.ACTION_VIEW.equals(intent.getAction()))
                uris = Collections.singletonList(intent.getData());
            else if (Intent.ACTION_SEND.equals(intent.getAction()))
                uris = Collections.singletonList(IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri.class));
            else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction()))
                uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            else
                uris = null;
            if (uris == null)
                return false;

            final File tempDir = new File(StorageUtils.getTempPath(OrganicMapsModule.get().getApplication()));
            final ContentResolver resolver = activity.getContentResolver();
            ThreadPool.getStorage().execute(() -> BookmarkManager.INSTANCE.importBookmarksFiles(resolver, uris, tempDir));
            return false;
        }
    }

    public static class UrlProcessor implements IntentProcessor {
        private static final int SEARCH_IN_VIEWPORT_ZOOM = 16;

        @Override
        public boolean process(@NonNull Intent intent, @NonNull MwmActivity target) {
            final Uri uri = intent.getData();
            if (uri == null)
                return false;

            switch (Framework.nativeParseAndSetApiUrl(uri.toString())) {
                case RequestType.INCORRECT:
                    return false;

                case RequestType.MAP:
                    SearchEngine.INSTANCE.cancelInteractiveSearch();
                    Map.executeMapApiRequest();
                    return true;

                case RequestType.ROUTE:
                    SearchEngine.INSTANCE.cancelInteractiveSearch();
                    final ParsedRoutingData data = Framework.nativeGetParsedRoutingData();
                    RoutingController.get().setRouterType(data.mRouterType);
                    final RoutePoint from = data.mPoints[0];
                    final RoutePoint to = data.mPoints[1];
                    RoutingController.get().prepare(MapObject.createMapObject(FeatureId.EMPTY, MapObject.API_POINT,
                                    from.mName, "", from.mLat, from.mLon),
                            MapObject.createMapObject(FeatureId.EMPTY, MapObject.API_POINT,
                                    to.mName, "", to.mLat, to.mLon));
                    return true;
                case RequestType.SEARCH: {
                    SearchEngine.INSTANCE.cancelInteractiveSearch();
                    final ParsedSearchRequest request = Framework.nativeGetParsedSearchRequest();
                    final double[] latlon = Framework.nativeGetParsedCenterLatLon();
                    if (latlon != null) {
                        Framework.nativeStopLocationFollow();
                        Framework.nativeSetViewportCenter(latlon[0], latlon[1], SEARCH_IN_VIEWPORT_ZOOM);
                        // We need to update viewport for search api manually because of drape engine
                        // will not notify subscribers when search activity is shown.
                        if (!request.mIsSearchOnMap)
                            Framework.nativeSetSearchViewport(latlon[0], latlon[1], SEARCH_IN_VIEWPORT_ZOOM);
                    }
                    SearchActivity.start(target, request.mQuery, request.mLocale, request.mIsSearchOnMap);
                    return true;
                }
                case RequestType.CROSSHAIR: {
                    SearchEngine.INSTANCE.cancelInteractiveSearch();
                    target.showPositionChooserForAPI(Framework.nativeGetParsedAppName());

                    final double[] latlon = Framework.nativeGetParsedCenterLatLon();
                    if (latlon != null) {
                        Framework.nativeStopLocationFollow();
                        Framework.nativeSetViewportCenter(latlon[0], latlon[1], SEARCH_IN_VIEWPORT_ZOOM);
                    }

                    return true;
                }
                case RequestType.OAUTH2: {
                    SearchEngine.INSTANCE.cancelInteractiveSearch();

                    final String oauth2code = Framework.nativeGetParsedOAuth2Code();
                    OsmLoginActivity.OAuth2Callback(target, oauth2code);

                    return true;
                }

                // Menu and Settings url types should be implemented to support deeplinking.
                case RequestType.MENU:
                case RequestType.SETTINGS:
            }

            return false;
        }
    }
}
