package org.greenrobot.organicmaps.routing;

import androidx.annotation.DrawableRes;

import org.greenrobot.organicmaps.R;

public enum TransitStepType
{
  // A specific icon for different intermediate points is calculated dynamically in TransitStepView.
  INTERMEDIATE_POINT(R.drawable.mn_org_map_ic_20px_route_planning_walk),
  PEDESTRIAN(R.drawable.mn_org_map_ic_20px_route_planning_walk),
  SUBWAY(R.drawable.mn_org_map_ic_20px_route_planning_metro),
  TRAIN(R.drawable.mn_org_map_ic_20px_route_planning_train),
  LIGHT_RAIL(R.drawable.mn_org_map_ic_20px_route_planning_lightrail),
  MONORAIL(R.drawable.mn_org_map_ic_20px_route_planning_monorail),
  RULER(R.drawable.mn_org_map_ic_ruler_route);

  @DrawableRes
  private final int mDrawable;

  TransitStepType(@DrawableRes int drawable)
  {
    mDrawable = drawable;
  }

  @DrawableRes
  public int getDrawable()
  {
    return mDrawable;
  }
}
