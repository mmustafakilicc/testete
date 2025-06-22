package org.greenrobot.organicmaps.util

import android.content.Context
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import org.greenrobot.organicmaps.bookmarks.data.BookmarkManager
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object KmlUtils {

    fun saveKmlToCache(context: Context, kmlContent: String, fileName: String): String? {
        try {
            val cacheDir: File = context.cacheDir
            val kmlFile = File(cacheDir, "$fileName.kml")
            kmlFile.writeText(kmlContent)
            return kmlFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun generateStyleBlock(): String {
        return KmlColorStyle.entries.joinToString("\n") { colorStyle ->
            val iconName = colorStyle.styleId.replace("placemark-", "")
            """
        <Style id="${colorStyle.styleId}">
          <IconStyle>
            <Icon>
              <href>/placemarks/placemark-$iconName.png</href>
            </Icon>
          </IconStyle>
        </Style>
        """.trimIndent()
        }
    }

    fun createFormattedOrganicMapsKmlString(docData: KmlDocumentData): String {

        val placeMarksXml = docData.placeMarks.joinToString(separator = "\n") { placemark ->
            val extendedDataContent = listOfNotNull(
                placemark.extendedData.featureTypes?.let { types ->
                    val values = types.joinToString("") { "<mwm:value>$it</mwm:value>" }
                    "  <mwm:featureTypes>$values</mwm:featureTypes>"
                },
                placemark.extendedData.name?.let {
                    "  <mwm:name><mwm:lang code=\"default\">$it</mwm:lang></mwm:name>"
                },
                placemark.extendedData.scale?.let { "  <mwm:scale>$it</mwm:scale>" },
                placemark.extendedData.icon?.let { "  <mwm:icon>$it</mwm:icon>" },
                placemark.extendedData.visibility?.let { "  <mwm:visibility>$it</mwm:visibility>" }
            ).joinToString("\n")

            val extendedDataBlock = if (extendedDataContent.isNotBlank()) {
                """
            <ExtendedData xmlns:mwm="">
            $extendedDataContent
            </ExtendedData>
            """.trimIndent()
            } else {
                ""
            }

            """
        <Placemark>
          <name>${placemark.name}</name>
          <TimeStamp>
            <when>${getUtcTimestamp()}</when>
          </TimeStamp>
          <styleUrl>#${placemark.style.styleId}</styleUrl>
          <Point>
            <coordinates>${placemark.longitude},${placemark.latitude},0</coordinates>
          </Point>
          ${extendedDataBlock.lines().joinToString("\n") { "  $it" }}
        </Placemark>
        """.trimIndent()
        }.lines().joinToString("\n") { "  $it" }

        val xmlDeclaration = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"

        val kmlBody = """
        <kml xmlns="http://www.opengis.net/kml/2.2" xmlns:gx="http://www.google.com/kml/ext/2.2">
          <Document>
            ${generateStyleBlock().lines().joinToString("\n") { "  $it" }}
            <name>${docData.documentName}</name>
            <visibility>1</visibility>
            <ExtendedData xmlns:mwm="">
              <mwm:name>
                <mwm:lang code="default">${docData.documentName}</mwm:lang>
              </mwm:name>
              <mwm:annotation></mwm:annotation>
              <mwm:description>${docData.description}</mwm:description>
              <mwm:lastModified>${getUtcTimestamp()}</mwm:lastModified>
              <mwm:accessRules>Local</mwm:accessRules>
            </ExtendedData>
            $placeMarksXml
          </Document>
        </kml>
    """.trimIndent()

        return "$xmlDeclaration\n$kmlBody"
    }

    private fun getUtcTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

//    fun saveCustomBookmarks(context: Context, kmlDocumentData: KmlDocumentData) {
//        val kmlContent = createFormattedOrganicMapsKmlString(kmlDocumentData)
//        val kmlPath = saveKmlToCache(context, kmlContent, kmlDocumentData.documentName) ?: return
//        BookmarkManager.INSTANCE.loadBookmarksFile(kmlPath, true)
//    }

    fun saveCustomBookmarks(context: Context, kmlDocumentData: KmlDocumentData) {
        val categoryExists = BookmarkManager.INSTANCE.categories.find { it.name == kmlDocumentData.documentName }
        if(categoryExists != null){
            if(categoryExists.bookmarksCount == kmlDocumentData.placeMarks.size){
                BookmarkManager.INSTANCE.showBookmarkCategoryOnMap(categoryExists.id)
                return
            }
            BookmarkManager.INSTANCE.deleteCategory(categoryExists.id)
        }
        val kmlContent = createFormattedOrganicMapsKmlString(kmlDocumentData)
        val kmlPath = saveKmlToCache(context, kmlContent, kmlDocumentData.documentName) ?: return
        BookmarkManager.INSTANCE.loadBookmarksFile(kmlPath, true)
    }

    //This is for test
    fun generateAndSaveComplexKml(context: Context): String? {
        val myPlaceMarks = listOf(
            KmlPlaceMarkData(
                name = "Forest",
                latitude = 39.990565,
                longitude = 32.62273,
                style = KmlColorStyle.RED,
                extendedData = PlaceMarkExtendedData(
                    featureTypes = listOf("landuse-forest"),
                    icon = "Park",
                    name = null
                )
            ),
            KmlPlaceMarkData(
                name = "A Blue Point",
                latitude = 40.003712,
                longitude = 32.615691,
                style = KmlColorStyle.BLUE,
                extendedData = PlaceMarkExtendedData(
                    featureTypes = listOf("amenity-theatre"),
                    icon = "Theatre",
                    name = null
                )
            ),

            KmlPlaceMarkData(
                name = "A Cyan Point",
                latitude = 40.003712,
                longitude = 32.615691,
                style = KmlColorStyle.CYAN,
                extendedData = PlaceMarkExtendedData(
                    featureTypes = listOf("historic-monument"),
                    icon = "Sights",
                    name = null
                )
            ),
            KmlPlaceMarkData(
                name = "A Green Point",
                latitude = 40.008914,
                longitude = 32.624812,
                style = KmlColorStyle.GREEN,
                extendedData = PlaceMarkExtendedData()
            )
        )
        val myDocument = KmlDocumentData(
            documentName = "lalalala",
            placeMarks = myPlaceMarks
        )
        val kmlContent = createFormattedOrganicMapsKmlString(myDocument)
        val savedFile = saveKmlToCache(context, kmlContent, myDocument.documentName)
        return savedFile
    }
}

@Keep
enum class KmlColorStyle(val styleId: String) {
    RED("placemark-red"),
    BLUE("placemark-blue"),
    PURPLE("placemark-purple"),
    YELLOW("placemark-yellow"),
    PINK("placemark-pink"),
    BROWN("placemark-brown"),
    GREEN("placemark-green"),
    ORANGE("placemark-orange"),
    DEEP_PURPLE("placemark-deeppurple"),
    LIGHT_BLUE("placemark-lightblue"),
    CYAN("placemark-cyan"),
    TEAL("placemark-teal"),
    LIME("placemark-lime"),
    DEEP_ORANGE("placemark-deeporange"),
    GRAY("placemark-gray"),
    BLUE_GRAY("placemark-bluegray");
}

@Parcelize
@Keep
data class PlaceMarkExtendedData(
    val name: String? = "Map Point",
    val featureTypes: List<String>? = null,
    val scale: Int? = 13,
    val icon: String? = null,
    val visibility: Int? = 1
) : Parcelable

@Parcelize
@Keep
data class KmlPlaceMarkData(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val style: KmlColorStyle,
    val extendedData: PlaceMarkExtendedData
) : Parcelable

@Parcelize
@Keep
data class KmlDocumentData(
    val documentName: String,
    val description: String = "",
    val placeMarks: List<KmlPlaceMarkData>
) : Parcelable