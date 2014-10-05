package cz.mencik.flickr.exif2geo

import com.flickr4java.flickr.Flickr

import scala.math.BigDecimal.RoundingMode

/**
 * Created by Vlastimil on 5. 10. 2014.
 */
class ExifCoords(implicit flickr: Flickr) {

  import ExifCoords._

  def getCoords(photoId: String): Option[BetterGeoData] = {
    val exif = flickr.getPhotosInterface.getExif(photoId, null)
    import collection.JavaConverters._
    val gpsData = (for {
      edata <- exif.asScala if edata.getTagspace == "GPS"
      v <- Option(edata.getRaw) if v.length > 0
    } yield edata.getTag -> v).toMap

    for {
      latStr <- gpsData.get(TagLatitude)
      lonStr <- gpsData.get(TagLongitude)
      lat <- parseCoord(latStr)
      lon <- parseCoord(lonStr)
    } yield BetterGeoData(lat = lat, lon = lon)
  }

  private def parseCoord(str: String): Option[BigDecimal] = {
    val degB = new StringBuilder
    val minB = new StringBuilder
    val secB = new StringBuilder

    var i = 0
    while (i < str.length && str.charAt(i).isDigit) {
      degB += str.charAt(i)
      i += 1
    }
    while (i < str.length && !str.charAt(i).isDigit) {
      i += 1
    }
    while (i < str.length && str.charAt(i).isDigit) {
      minB += str.charAt(i)
      i += 1
    }
    while (i < str.length && !str.charAt(i).isDigit) {
      i += 1
    }
    while (i < str.length && (str.charAt(i).isDigit || str.charAt(i) == '.')) {
      secB += str.charAt(i)
      i += 1
    }

    try {
      val deg = BigDecimal(degB.toString())
      val min = BigDecimal(minB.toString())
      val sec = BigDecimal(secB.toString())

      val exact = deg + (min / BigDecimal(60)) + (sec / BigDecimal(3600))
      Some(exact.setScale(6, RoundingMode.HALF_UP))
    } catch {
      case e: NumberFormatException =>
        e.printStackTrace()
        None
    }
  }


}

object ExifCoords {

  private val TagLatitude = "GPSLatitude"
  private val TagLongitude = "GPSLongitude"


}

