name := "stock-news-scraper"

version := "1.0"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

// scalaVersion := "2.11.6"
scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.6.4",
    "org.slf4j" % "slf4j-simple" % "1.6.4",
    "mysql" % "mysql-connector-java" % "5.1.12",
    "com.typesafe" % "config" % "1.2.1",
    "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1",
    "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.3",
    "com.typesafe.slick" %% "slick" % "2.1.0"
)
