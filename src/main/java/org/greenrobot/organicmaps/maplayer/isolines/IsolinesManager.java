package org.greenrobot.organicmaps.maplayer.isolines;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import org.greenrobot.organicmaps.Framework;
import org.greenrobot.organicmaps.OrganicMapsModule;

public class IsolinesManager
{
  @NonNull
  private final OnIsolinesChangedListener mListener;

  public IsolinesManager(@NonNull Application application)
  {
    mListener = new OnIsolinesChangedListener(application);
  }

  static public boolean isEnabled()
  {
    return Framework.nativeIsIsolinesLayerEnabled();
  }

  private void registerListener()
  {
    nativeAddListener(mListener);
  }

  static public void setEnabled(boolean isEnabled)
  {
    if (isEnabled == isEnabled())
      return;

    Framework.nativeSetIsolinesLayerEnabled(isEnabled);
  }

  public void initialize()
  {
    registerListener();
  }

  @NonNull
  public static IsolinesManager from(@NonNull Context context)
  {
    return OrganicMapsModule.get().getIsoLinesManager();
  }

  private static native void nativeAddListener(@NonNull OnIsolinesChangedListener listener);
  private static native void nativeRemoveListener(@NonNull OnIsolinesChangedListener listener);
  private static native boolean nativeShouldShowNotification();

  public void attach(@NonNull IsolinesErrorDialogListener listener)
  {
    mListener.attach(listener);
  }

  public void detach()
  {
    mListener.detach();
  }

  public boolean shouldShowNotification()
  {
    return nativeShouldShowNotification();
  }
}
