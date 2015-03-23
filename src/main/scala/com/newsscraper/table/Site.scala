package com.newsscraper.table

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.lifted.ProvenShape

/**
 * Created by dmcquill on 3/22/15.
 */
case class Site(id: Option[Int], name: String)

class Sites(tag: Tag) extends Table[Site](tag, "st_site") {

    def id: Column[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name", O.NotNull)

    def * = (id.?, name) <> (Site.tupled, Site.unapply)
    def idx = index("st_site_idx", (id, name), unique = true)
}
