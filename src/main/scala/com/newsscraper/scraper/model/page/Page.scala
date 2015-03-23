package com.newsscraper.scraper.model.page

import java.sql.Date

import com.newsscraper.table.Article

/**
 * Created by dmcquill on 3/18/15.
 */
trait Page {
    def tuple: Article
}
