package cz.mencik.flickr.exif2geo

import com.flickr4java.flickr.{FlickrException, Flickr}

/**
 * Created by Vlastimil on 5. 10. 2014.
 */
class GeoUpdate(implicit flickr: Flickr) {

  def setLocation(photoId: String, loc: BetterGeoData, overwrite: Boolean = false): Boolean = {
    val geo = new BetterGeoInterface(apiKey = flickr.getApiKey, secret = flickr.getSharedSecret, transport = flickr.getTransport)

    val canSet =
      if (!overwrite) {
        try {
          geo.getLocation(photoId)
          false
        } catch {
          case e: FlickrException if e.getErrorCode == "2" => true
        }
      } else true

    if (canSet) {
      geo.setLocationBetter(photoId, loc)
      true
    }
    else false

  }

}
