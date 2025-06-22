package org.greenrobot.organicmaps.editor;

import androidx.fragment.app.Fragment;

import org.greenrobot.organicmaps.base.BaseMwmFragmentActivity;

public class ReportActivity extends BaseMwmFragmentActivity
{
  @Override
  protected Class<? extends Fragment> getFragmentClass()
  {
    return ReportFragment.class;
  }
}
