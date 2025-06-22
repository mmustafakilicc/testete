package org.greenrobot.organicmaps.bookmarks.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;

import com.google.common.base.Objects;

import org.greenrobot.organicmaps.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Icon implements Parcelable
{
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ PREDEFINED_COLOR_NONE, PREDEFINED_COLOR_RED, PREDEFINED_COLOR_BLUE,
            PREDEFINED_COLOR_PURPLE, PREDEFINED_COLOR_YELLOW, PREDEFINED_COLOR_PINK,
            PREDEFINED_COLOR_BROWN, PREDEFINED_COLOR_GREEN, PREDEFINED_COLOR_ORANGE,
            PREDEFINED_COLOR_DEEPPURPLE, PREDEFINED_COLOR_LIGHTBLUE, PREDEFINED_COLOR_CYAN,
            PREDEFINED_COLOR_TEAL, PREDEFINED_COLOR_LIME, PREDEFINED_COLOR_DEEPORANGE,
            PREDEFINED_COLOR_GRAY, PREDEFINED_COLOR_BLUEGRAY})
  public @interface PredefinedColor {}

  public static final int PREDEFINED_COLOR_NONE = 0;
  public static final int PREDEFINED_COLOR_RED = 1;
  public static final int PREDEFINED_COLOR_BLUE = 2;
  public static final int PREDEFINED_COLOR_PURPLE = 3;
  public static final int PREDEFINED_COLOR_YELLOW = 4;
  public static final int PREDEFINED_COLOR_PINK = 5;
  public static final int PREDEFINED_COLOR_BROWN = 6;
  public static final int PREDEFINED_COLOR_GREEN = 7;
  public static final int PREDEFINED_COLOR_ORANGE = 8;
  public static final int PREDEFINED_COLOR_DEEPPURPLE = 9;
  public static final int PREDEFINED_COLOR_LIGHTBLUE = 10;
  public static final int PREDEFINED_COLOR_CYAN = 11;
  public static final int PREDEFINED_COLOR_TEAL = 12;
  public static final int PREDEFINED_COLOR_LIME = 13;
  public static final int PREDEFINED_COLOR_DEEPORANGE = 14;
  public static final int PREDEFINED_COLOR_GRAY = 15;
  public static final int PREDEFINED_COLOR_BLUEGRAY = 16;

  private static int shift(int v, int bitCount) { return v << bitCount; }
  private static int toARGB(int r, int g, int b)
  {
    return shift(255, 24) + shift(r, 16) + shift(g, 8) + b;
  }

  /// @note Important! Should be synced with kml/types.hpp/PredefinedColor
  /// @todo Values can be taken from Core.
  private static final int[] ARGB_COLORS = { toARGB(229, 27, 35), // none
                                             toARGB(229, 27, 35), // red
                                             toARGB(0, 110, 199), // blue
                                             toARGB(156, 39, 176), // purple
                                             toARGB(255, 200, 0), // yellow
                                             toARGB(255, 65, 130), // pink
                                             toARGB(121, 85, 72), // brown
                                             toARGB(56, 142, 60), // green
                                             toARGB(255, 160, 0), // orange
                                             toARGB(102, 57, 191), // deeppurple
                                             toARGB(36, 156, 242), // lightblue
                                             toARGB(20, 190, 205), // cyan
                                             toARGB(0, 165, 140), // teal
                                             toARGB(147, 191, 57), // lime
                                             toARGB(240, 100, 50), // deeporange
                                             toARGB(115, 115, 115), // gray
                                             toARGB(89, 115, 128) }; // bluegray

  static final int BOOKMARK_ICON_TYPE_NONE = 0;

  /// @note Important! Should be synced with kml/types.hpp/BookmarkIcon
  /// @todo Can make better: take name-by-type from Core and make a concat: "R.drawable.ic_bookmark_" + name.
  // First icon should be "none" <-> BOOKMARK_ICON_TYPE_NONE.
  @DrawableRes
  private static final int[] TYPE_ICONS = { R.drawable.mn_org_map_ic_bookmark_none,
                                            R.drawable.mn_org_map_ic_bookmark_hotel,
                                            R.drawable.mn_org_map_ic_bookmark_animals,
                                            R.drawable.mn_org_map_ic_bookmark_buddhism,
                                            R.drawable.mn_org_map_ic_bookmark_building,
                                            R.drawable.mn_org_map_ic_bookmark_christianity,
                                            R.drawable.mn_org_map_ic_bookmark_entertainment,
                                            R.drawable.mn_org_map_ic_bookmark_money,
                                            R.drawable.mn_org_map_ic_bookmark_food,
                                            R.drawable.mn_org_map_ic_bookmark_gas,
                                            R.drawable.mn_org_map_ic_bookmark_judaism,
                                            R.drawable.mn_org_map_ic_bookmark_medicine,
                                            R.drawable.mn_org_map_ic_bookmark_mountain,
                                            R.drawable.mn_org_map_ic_bookmark_museum,
                                            R.drawable.mn_org_map_ic_bookmark_islam,
                                            R.drawable.mn_org_map_ic_bookmark_park,
                                            R.drawable.mn_org_map_ic_bookmark_parking,
                                            R.drawable.mn_org_map_ic_bookmark_shop,
                                            R.drawable.mn_org_map_ic_bookmark_sights,
                                            R.drawable.mn_org_map_ic_bookmark_swim,
                                            R.drawable.mn_org_map_ic_bookmark_water,
                                            R.drawable.mn_org_map_ic_bookmark_bar,
                                            R.drawable.mn_org_map_ic_bookmark_transport,
                                            R.drawable.mn_org_map_ic_bookmark_viewpoint,
                                            R.drawable.mn_org_map_ic_bookmark_sport,
                                            R.drawable.mn_org_map_ic_bookmark_none,  // pub
                                            R.drawable.mn_org_map_ic_bookmark_none,  // art
                                            R.drawable.mn_org_map_ic_bookmark_none,  // bank
                                            R.drawable.mn_org_map_ic_bookmark_none,  // cafe
                                            R.drawable.mn_org_map_ic_bookmark_none,  // pharmacy
                                            R.drawable.mn_org_map_ic_bookmark_none,  // stadium
                                            R.drawable.mn_org_map_ic_bookmark_none,  // theatre
                                            R.drawable.mn_org_map_ic_bookmark_none   // information
  };

  @PredefinedColor
  private final int mColor;
  private final int mType;

  public Icon(@PredefinedColor int color, int type)
  {
    mColor = color;
    mType = type;
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeInt(mColor);
    dest.writeInt(mType);
  }

  private Icon(Parcel in)
  {
    mColor = in.readInt();
    mType = in.readInt();
  }

  @PredefinedColor
  public int getColor()
  {
    return mColor;
  }

  public int argb()
  {
    return ARGB_COLORS[mColor];
  }

  public static int getColorPosition(int color)
  {
    for (int index = 1; index < ARGB_COLORS.length; index++)
    {
      if (ARGB_COLORS[index] == color)
        return index;
    }
    return -1;
  }

  @DrawableRes
  public int getResId()
  {
    return TYPE_ICONS[mType];
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o instanceof Icon comparedIcon)
      return mColor == comparedIcon.mColor && mType == comparedIcon.mType;
    return false;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(mColor, mType);
  }

  public static final Parcelable.Creator<Icon> CREATOR = new Parcelable.Creator<>()
  {
    public Icon createFromParcel(Parcel in)
    {
      return new Icon(in);
    }

    public Icon[] newArray(int size)
    {
      return new Icon[size];
    }
  };
}
