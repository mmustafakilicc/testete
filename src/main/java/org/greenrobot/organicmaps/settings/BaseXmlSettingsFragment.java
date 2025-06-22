package org.greenrobot.organicmaps.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.organicmaps.R;
import org.greenrobot.organicmaps.util.ThemeUtils;
import org.greenrobot.organicmaps.util.Utils;
import org.greenrobot.organicmaps.util.WindowInsetUtils.ScrollableContentInsetsListener;

abstract class BaseXmlSettingsFragment extends PreferenceFragmentCompat
{
  protected abstract @XmlRes int getXmlResources();

  @NonNull
  public <T extends Preference> T getPreference(@NonNull CharSequence key)
  {
    final T pref = findPreference(key);
    if (pref == null)
      throw new RuntimeException("Can't get preference by key: "+key);
    return pref;
  }
  @Override
  public void onCreatePreferences(Bundle bundle, String root)
  {
    setPreferencesFromResource(getXmlResources(), root);
  }

  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);
    Utils.detachFragmentIfCoreNotInitialized(context, this);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);

    int color;
    if (ThemeUtils.isDefaultTheme(requireContext()))
      color = ContextCompat.getColor(requireContext(), R.color.mn_org_map_bg_cards);
    else
      color = ContextCompat.getColor(requireContext(), R.color.mn_org_map_bg_cards_night);
    view.setBackgroundColor(color);

    RecyclerView recyclerView = getListView();
    ViewCompat.setOnApplyWindowInsetsListener(recyclerView, new ScrollableContentInsetsListener(recyclerView));
  }

  protected SettingsActivity getSettingsActivity()
  {
    return (SettingsActivity) requireActivity();
  }
}
