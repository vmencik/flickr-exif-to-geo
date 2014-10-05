package cz.mencik.flickr.exif2geo

import com.flickr4java.flickr.{FlickrException, Response, Transport}
import com.flickr4java.flickr.photos.geo.GeoInterface

/**
 * Created by Vlastimil on 5. 10. 2014.
 */
class BetterGeoInterface(apiKey: String, secret: String, transport: Transport) extends GeoInterface(apiKey, secret, transport) {

  def setLocationBetter(photoId: String, geo: BetterGeoData): Unit = {
    val parameters: Map[String, AnyRef] = Map(
      "method" -> GeoInterface.METHOD_SET_LOCATION,
      "photo_id" -> photoId,
      "lat" -> geo.lat.underlying().toPlainString,
      "lon" -> geo.lon.underlying().toPlainString
    ) ++ geo.maybeAccuracy.map("accuracy" -> Int.box(_))

    import collection.JavaConverters._

    // Note: This method requires an HTTP POST request.
    val response: Response = transport.post(transport.getPath(), parameters.asJava, apiKey, secret)


    // This method has no specific response - It returns an empty sucess response
    // if it completes without error.
    if (response.isError) {
      throw new FlickrException(response.getErrorCode, response.getErrorMessage)
    }
  }

}

case class BetterGeoData(lat: BigDecimal, lon: BigDecimal, maybeAccuracy: Option[Int] = None)
