package org.greenrobot.organicmaps.content;

import androidx.annotation.NonNull;

public interface DataSource<D>
{
  @NonNull
  D getData();

  void invalidate();
}
