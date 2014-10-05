name := "flickr-exif-to-geo"

version := "1.0"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "com.flickr4java" % "flickr4java" % "2.11",
  "commons-codec" % "commons-codec" % "1.9"
//  "org.scribe" % "scribe" % "1.3.6"
)

//resolvers += "Scribe Repo" at "https://raw.github.com/fernandezpablo85/scribe-java/mvn-repo/"
