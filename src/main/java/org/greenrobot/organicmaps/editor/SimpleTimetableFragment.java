package org.greenrobot.organicmaps.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.organicmaps.R;
import org.greenrobot.organicmaps.base.BaseMwmRecyclerFragment;
import org.greenrobot.organicmaps.editor.data.HoursMinutes;

public class SimpleTimetableFragment extends BaseMwmRecyclerFragment<SimpleTimetableAdapter>
                                  implements TimetableProvider,
                                             HoursMinutesPickerFragment.OnPickListener
{
  private SimpleTimetableAdapter mAdapter;
  @Nullable
  private String mInitTimetables;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
  }

  @NonNull
  @Override
  protected SimpleTimetableAdapter createAdapter()
  {
    mAdapter = new SimpleTimetableAdapter(this);
    mAdapter.setTimetables(mInitTimetables);
    return mAdapter;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    return inflater.inflate(R.layout.mn_org_map_fragment_timetable_simple, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);
  }

  @Nullable
  @Override
  public String getTimetables()
  {
    return mAdapter.getTimetables();
  }

  @Override
  public void setTimetables(@Nullable String timetables)
  {
    mInitTimetables = timetables;
  }

  @Override
  public void onHoursMinutesPicked(HoursMinutes from, HoursMinutes to, int id)
  {
    mAdapter.onHoursMinutesPicked(from, to, id);
  }
}
