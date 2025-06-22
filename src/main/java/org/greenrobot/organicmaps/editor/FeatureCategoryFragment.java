package org.greenrobot.organicmaps.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.organicmaps.R;
import org.greenrobot.organicmaps.base.BaseMwmRecyclerFragment;
import org.greenrobot.organicmaps.editor.data.FeatureCategory;
import org.greenrobot.organicmaps.util.Language;
import org.greenrobot.organicmaps.util.Utils;
import org.greenrobot.organicmaps.widget.SearchToolbarController;
import org.greenrobot.organicmaps.widget.ToolbarController;

import java.util.Arrays;
import java.util.Comparator;

public class FeatureCategoryFragment extends BaseMwmRecyclerFragment<FeatureCategoryAdapter>
{
  private FeatureCategory mSelectedCategory;
  protected ToolbarController mToolbarController;

  public interface FeatureCategoryListener
  {
    void onFeatureCategorySelected(FeatureCategory category);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    return inflater.inflate(R.layout.mn_org_map_fragment_categories, container, false);
  }

  @CallSuper
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);

    final Bundle args = getArguments();
    if (args != null)
    {
      mSelectedCategory = Utils.getParcelable(args, FeatureCategoryActivity.EXTRA_FEATURE_CATEGORY,
          FeatureCategory.class);
    }
    mToolbarController = new SearchToolbarController(view, requireActivity())
    {
      @Override
      protected void onTextChanged(String query)
      {
        setFilter(query);
      }
    };

  }

  private void setFilter(String query)
  {
    String locale = Language.getDefaultLocale();
    String[] creatableTypes = query.isEmpty()
                              ? Editor.nativeGetAllCreatableFeatureTypes(locale)
                              : Editor.nativeSearchCreatableFeatureTypes(query, locale);

    FeatureCategory[] categories = makeFeatureCategoriesFromTypes(creatableTypes);

    getAdapter().setCategories(categories);
    getRecyclerView().scrollToPosition(0);
  }

  @NonNull
  @Override
  protected FeatureCategoryAdapter createAdapter()
  {
    String locale = Language.getDefaultLocale();
    String[] creatableTypes = Editor.nativeGetAllCreatableFeatureTypes(locale);

    FeatureCategory[] categories = makeFeatureCategoriesFromTypes(creatableTypes);

    return new FeatureCategoryAdapter(this, categories, mSelectedCategory);
  }

  @NonNull
  private FeatureCategory[] makeFeatureCategoriesFromTypes(@NonNull String[] creatableTypes)
  {
    FeatureCategory[] categories = new FeatureCategory[creatableTypes.length];

    for (int i = 0; i < creatableTypes.length; ++i)
    {
      String localizedType = Utils.getLocalizedFeatureType(requireContext(), creatableTypes[i]);
      categories[i] = new FeatureCategory(creatableTypes[i], localizedType);
    }

    Arrays.sort(categories, Comparator.comparing(FeatureCategory::getLocalizedTypeName));

    return categories;
  }

  public void selectCategory(FeatureCategory category)
  {
    if (requireActivity() instanceof FeatureCategoryListener)
      ((FeatureCategoryListener) requireActivity()).onFeatureCategorySelected(category);
    else if (getParentFragment() instanceof FeatureCategoryListener)
      ((FeatureCategoryListener) getParentFragment()).onFeatureCategorySelected(category);
  }
}
