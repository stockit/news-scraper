package com.newsscraper.scraper.model.page

import java.util.Date
import javax.sql.rowset.serial.SerialBlob

import com.newsscraper.table.Article

/**
 * Created by dmcquill on 3/18/15.
 */
case class NewsMaxPage(title: String, body: String, date: Date, url: String) extends Page {
    def tuple: Article = {
        Article(None, this.title, this.body, new java.sql.Date(date.getTime()))
    }
}
