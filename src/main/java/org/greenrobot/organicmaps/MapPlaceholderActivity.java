package org.greenrobot.organicmaps;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.organicmaps.base.BaseMwmFragmentActivity;
import org.greenrobot.organicmaps.display.DisplayChangedListener;
import org.greenrobot.organicmaps.display.DisplayManager;
import org.greenrobot.organicmaps.display.DisplayType;

public class MapPlaceholderActivity extends BaseMwmFragmentActivity implements DisplayChangedListener
{
  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private DisplayManager mDisplayManager;
  private boolean mRemoveDisplayListener = true;

  @Override
  protected void onSafeCreate(@Nullable Bundle savedInstanceState)
  {
    super.onSafeCreate(savedInstanceState);
    setContentView(R.layout.mn_org_map_activity_map_placeholder);

    mDisplayManager = DisplayManager.from(this);
    mDisplayManager.addListener(DisplayType.Device, this);

    findViewById(R.id.btn_continue).setOnClickListener((unused) -> mDisplayManager.changeDisplay(DisplayType.Device));
  }

  @Override
  public void onDisplayChangedToDevice(@NonNull Runnable onTaskFinishedCallback)
  {
    mRemoveDisplayListener = false;
    startActivity(new Intent(this, MwmActivity.class)
        .putExtra(MwmActivity.EXTRA_UPDATE_THEME, true));
    finish();
    onTaskFinishedCallback.run();
  }

  @Override
  protected void onSafeDestroy()
  {
    super.onSafeDestroy();
    if (mRemoveDisplayListener)
      mDisplayManager.removeListener(DisplayType.Device);
  }
}
