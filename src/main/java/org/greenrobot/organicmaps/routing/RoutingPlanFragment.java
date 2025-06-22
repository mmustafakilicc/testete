package org.greenrobot.organicmaps.routing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.organicmaps.Framework;
import org.greenrobot.organicmaps.MwmActivity;
import org.greenrobot.organicmaps.R;
import org.greenrobot.organicmaps.base.BaseMwmFragment;

public class RoutingPlanFragment extends BaseMwmFragment
{

  private RoutingPlanController mPlanController;

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
  {
    final MwmActivity activity = (MwmActivity) requireActivity();
    View res = inflater.inflate(R.layout.mn_org_map_fragment_routing, container, false);
    mPlanController = new RoutingPlanController(res, activity, activity.startDrivingOptionsForResult, activity, activity);
    return res;
  }

  public void updateBuildProgress(int progress, @Framework.RouterType int router)
  {
    mPlanController.updateBuildProgress(progress, router);
  }

  @Override
  public boolean onBackPressed()
  {
    return RoutingController.get().cancel();
  }

  public void restoreRoutingPanelState(@NonNull Bundle state)
  {
    mPlanController.restoreRoutingPanelState(state);
  }

  public void saveRoutingPanelState(@NonNull Bundle outState)
  {
    mPlanController.saveRoutingPanelState(outState);
  }

  public void showAddStartFrame()
  {
    mPlanController.showAddStartFrame();
  }

  public void showAddFinishFrame()
  {
    mPlanController.showAddFinishFrame();
  }

}
