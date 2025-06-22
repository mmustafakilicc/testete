package org.greenrobot.organicmaps.downloader;

import androidx.fragment.app.Fragment;

import org.greenrobot.organicmaps.base.BaseMwmFragmentActivity;
import org.greenrobot.organicmaps.base.OnBackPressListener;

public class DownloaderActivity extends BaseMwmFragmentActivity
{
  public static final String EXTRA_OPEN_DOWNLOADED = "open downloaded";

  @Override
  protected Class<? extends Fragment> getFragmentClass()
  {
    return DownloaderFragment.class;
  }

  @Override
  public void onBackPressed()
  {
    OnBackPressListener fragment = (OnBackPressListener)getSupportFragmentManager().findFragmentById(getFragmentContentResId());
      if (fragment != null && !fragment.onBackPressed()) super.onBackPressed();
  }
}
