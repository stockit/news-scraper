package com.newsscraper.table

import java.sql.{Blob, Date}

import slick.driver.MySQLDriver.simple._
import slick.lifted.{ProvenShape}

/**
 * Created by dmcquill on 3/19/15.
 */
case class Article(id: Option[Int], title: String, content: String, date: Date)

class Articles(tag: Tag) extends Table[Article](tag, "st_art") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("art_ttl")
    def content = column[String]("art_ctnt", O.DBType("LONGTEXT"))
    def date = column[Date]("art_dt")

    def * = (id.?, title, content, date) <> (Article.tupled, Article.unapply)
}

