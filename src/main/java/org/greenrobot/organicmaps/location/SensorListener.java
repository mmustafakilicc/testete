package org.greenrobot.organicmaps.location;

public interface SensorListener
{
  void onCompassUpdated(double north);

  default void onCompassCalibrationRecommended()
  {
    // No op.
  }

  default void onCompassCalibrationRequired()
  {
    // No op.
  }
}
