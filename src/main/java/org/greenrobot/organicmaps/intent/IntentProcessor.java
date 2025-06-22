package org.greenrobot.organicmaps.intent;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.organicmaps.MwmActivity;

public interface IntentProcessor
{
  @Nullable
  boolean process(@NonNull Intent intent, @NonNull MwmActivity activity);
}
