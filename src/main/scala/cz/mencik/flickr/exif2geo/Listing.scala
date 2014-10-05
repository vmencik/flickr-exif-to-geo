package cz.mencik.flickr.exif2geo

import com.flickr4java.flickr.Flickr

import scala.annotation.tailrec

/**
 * Created by Vlastimil on 5. 10. 2014.
 */
class Listing(implicit flickr: Flickr) {

  def photoIdsInAlbum(albumId: String): Seq[String] = {
    val albums = flickr.getPhotosetsInterface

    @tailrec
    def loop(ids: Seq[String], page: Int): Seq[String] = {
      val resp = albums.getPhotos(albumId, Listing.PerPage, page)
      import collection.JavaConverters._
      val idsOnPage = resp.asScala.map(_.getId).toIndexedSeq
      if (resp.getPages > resp.getPage)
        loop(ids ++ idsOnPage, resp.getPage + 1)
      else
        ids ++ idsOnPage
    }

    loop(IndexedSeq.empty, 1)
  }

}

object Listing {

  private val PerPage = 500

}