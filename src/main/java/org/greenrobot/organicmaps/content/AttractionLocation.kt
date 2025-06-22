package org.greenrobot.organicmaps.content

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import org.greenrobot.organicmaps.bookmarks.data.Icon

@Keep
@Parcelize
data class AttractionLocation(
    val categoryName: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    @Icon.PredefinedColor val color: Int = Icon.PREDEFINED_COLOR_PURPLE
) : Parcelable