package org.greenrobot.organicmaps.bookmarks;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.greenrobot.organicmaps.R;
import org.greenrobot.organicmaps.base.BaseMwmFragmentActivity;
import org.greenrobot.organicmaps.bookmarks.data.BookmarkCategory;

public class BookmarkCategorySettingsActivity extends BaseMwmFragmentActivity
{
  public static final String EXTRA_BOOKMARK_CATEGORY = "bookmark_category";

  @Override
  protected int getContentLayoutResId()
  {
    return R.layout.mn_org_map_fragment_container_layout;
  }

  @Override
  protected int getFragmentContentResId()
  {
    return R.id.fragment_container;
  }

  @Override
  protected Class<? extends Fragment> getFragmentClass()
  {
    return BookmarkCategorySettingsFragment.class;
  }

  public static void startForResult(@NonNull Fragment fragment, ActivityResultLauncher<Intent> startBookmarkSettingsForResult,
                                           @NonNull BookmarkCategory category)
  {
    android.content.Intent intent = new Intent(fragment.requireActivity(), BookmarkCategorySettingsActivity.class)
        .putExtra(EXTRA_BOOKMARK_CATEGORY, category);
    startBookmarkSettingsForResult.launch(intent);
  }
}
