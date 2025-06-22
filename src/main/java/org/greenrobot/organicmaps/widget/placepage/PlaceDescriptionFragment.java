package org.greenrobot.organicmaps.widget.placepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import org.greenrobot.organicmaps.R;
import org.greenrobot.organicmaps.base.BaseMwmFragment;
import org.greenrobot.organicmaps.util.Utils;
import org.greenrobot.organicmaps.util.WindowInsetUtils;

import java.util.Objects;

public class PlaceDescriptionFragment extends BaseMwmFragment
{
  public static final String EXTRA_DESCRIPTION = "description";
  private static final String SOURCE_SUFFIX = "<p><b>wikipedia.org</b></p>";

  @SuppressWarnings("NullableProblems")
  @NonNull
  private String mDescription;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    mDescription = Objects.requireNonNull(requireArguments()
                                                 .getString(EXTRA_DESCRIPTION));
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState)
  {
    View root = inflater.inflate(R.layout.mn_org_map_fragment_place_description, container, false);
    WebView webView = root.findViewById(R.id.webview);
    webView.loadData(mDescription + SOURCE_SUFFIX, Utils.TEXT_HTML, Utils.UTF_8);
    webView.setVerticalScrollBarEnabled(true);
    ViewCompat.setOnApplyWindowInsetsListener(root, WindowInsetUtils.PaddingInsetsListener.excludeTop());
    return root;
  }
}
