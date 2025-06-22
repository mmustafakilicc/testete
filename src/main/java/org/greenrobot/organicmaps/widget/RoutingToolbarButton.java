package org.greenrobot.organicmaps.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatRadioButton;

import org.greenrobot.organicmaps.R;
import org.greenrobot.organicmaps.util.ThemeUtils;

public class RoutingToolbarButton extends AppCompatRadioButton
{
  private boolean mInProgress;
  @DrawableRes
  private int mIcon;

  public RoutingToolbarButton(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    initView();
  }

  public RoutingToolbarButton(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    initView();
  }

  public RoutingToolbarButton(Context context)
  {
    super(context);
    initView();
  }

  private void initView()
  {
    setBackgroundResource(ThemeUtils.isNightTheme(getContext()) ? R.drawable.mn_org_map_routing_toolbar_button_night
                                                                : R.drawable.mn_org_map_routing_toolbar_button);
    setButtonTintList(ThemeUtils.isNightTheme(getContext()) ? R.color.mn_org_map_routing_toolbar_icon_tint_night
                                                            : R.color.mn_org_map_routing_toolbar_icon_tint);
  }

  public void progress()
  {
    if (mInProgress)
      return;

    setButtonDrawable(mIcon);
    mInProgress = true;
    setActivated(false);
    setSelected(true);
  }

  public void activate()
  {
    if (!mInProgress)
    {
      setButtonDrawable(mIcon);
      setSelected(false);
      setActivated(true);
    }
  }

  public void complete()
  {
    mInProgress = false;
    activate();
  }

  public void deactivate()
  {
    setActivated(false);
    mInProgress = false;
  }

  public void setButtonTintList(@ColorRes int color)
  {
    setButtonTintList(AppCompatResources.getColorStateList(getContext(), color));
  }

  public void setIcon(@DrawableRes int icon)
  {
    mIcon = icon;
    setButtonDrawable(icon);
  }
}
