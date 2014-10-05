package cz.mencik.flickr.exif2geo

import java.util.{Scanner, Collections}

import com.flickr4java.flickr.auth.Permission
import com.flickr4java.flickr.{RequestContext, REST, Flickr}
import org.apache.log4j.{Level, Logger, BasicConfigurator}
import org.scribe.model.{Token, Verifier}

/**
 * Created by Vlastimil on 5. 10. 2014.
 */
object ProofOfConcept extends App {

  val apiKey = "YOUR_API_KEY"
  val secret = "SECRET"
  implicit val flickr = new Flickr(apiKey, secret, new REST())

  BasicConfigurator.configure()
  Logger.getRootLogger.setLevel(Level.DEBUG)

  val auth = flickr.getAuthInterface
  // see https://github.com/callmeal/Flickr4Java/blob/master/Flickr4Java/src/examples/java/AuthExample.java
//  getNewAccessToken()
  val accessToken = new Token("TOKEN", "SECRET")

  val authUser = auth.checkToken(accessToken)
  println(authUser.getUser.getUsername)
  println(authUser.getPermission)
  RequestContext.getRequestContext.setAuth(authUser)

  val listing = new Listing
  val exifCoords = new ExifCoords
  val update = new GeoUpdate
  //  Flickr.debugRequest = true

  processAlbum("FLICKR_ALBUM/PHOTOSET_ID")

  private def processAlbum(albumId: String): Unit = {
    var cnt = 0
    val ids = listing.photoIdsInAlbum(albumId)
    println(s"Processing ${ids.size} photos in album $albumId")
    if (ids.nonEmpty) {
      val line = Array.fill(ids.size - 1)(' ') :+ '|'
      println(new String(line))
    }
    for (photoId <- ids) {
      if (processPhoto(photoId)) {
        cnt += 1
        print('.')
      } else {
        print('0')
      }
    }
    println()
    println(s"$cnt photos updated with Geo location in album $albumId")
  }

  private def processPhoto(id: String): Boolean = exifCoords.getCoords(id) match {
    case Some(geo) =>
      if (update.setLocation(id, geo)) {
//        println(s"$id updated to $geo")
        true
      } else {
        println(s"$id skipped")
        false
      }
    case None =>
      println(s"No coords for $id")
      false
  }

  private def getNewAccessToken(): Token = {
    val token = auth.getRequestToken
    println(token)
    val url = auth.getAuthorizationUrl(token, Permission.WRITE)
    println(url)

    val scanner = new Scanner(System.in)
    val tokenKey = scanner.nextLine()
    scanner.close()

    val newToken = auth.getAccessToken(token, new Verifier(tokenKey))
    println("succcess: " + newToken)
    newToken
  }
}
