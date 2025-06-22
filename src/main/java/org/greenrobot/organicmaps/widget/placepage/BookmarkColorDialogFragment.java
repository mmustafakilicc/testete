package org.greenrobot.organicmaps.widget.placepage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.organicmaps.R;
import org.greenrobot.organicmaps.base.BaseMwmDialogFragment;
import org.greenrobot.organicmaps.bookmarks.IconsAdapter;
import org.greenrobot.organicmaps.bookmarks.data.BookmarkManager;
import org.greenrobot.organicmaps.bookmarks.data.Icon;

import java.util.List;

public class BookmarkColorDialogFragment extends BaseMwmDialogFragment
{
  public static final String ICON_TYPE = "ExtraIconType";

  private int mIconColor;

  public interface OnBookmarkColorChangeListener
  {
    void onBookmarkColorSet(int colorPos);
  }

  private OnBookmarkColorChangeListener mColorSetListener;

  public BookmarkColorDialogFragment() {}

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    if (getArguments() != null)
      mIconColor = getArguments().getInt(ICON_TYPE);

    return new MaterialAlertDialogBuilder(requireActivity(), R.style.MwmTheme_AlertDialog)
        .setView(buildView())
        .setTitle(R.string.choose_color)
        .setNegativeButton(R.string.cancel, null)
        .create();
  }

  public void setOnColorSetListener(OnBookmarkColorChangeListener listener)
  {
    mColorSetListener = listener;
  }

  private View buildView()
  {
    final List<Icon> icons = BookmarkManager.ICONS;
    final IconsAdapter adapter = new IconsAdapter(requireActivity(), icons);
    adapter.chooseItem(mIconColor);

    @SuppressLint("InflateParams")
    final GridView gView = (GridView) LayoutInflater.from(requireActivity()).inflate(R.layout.mn_org_map_fragment_color_grid, null);
    gView.setAdapter(adapter);
    gView.setOnItemClickListener((arg0, who, pos, id) -> {
      if (mColorSetListener != null)
        mColorSetListener.onBookmarkColorSet(pos);
      dismiss();
    });

    return gView;
  }

}
