package com.newsscraper.scraper.model

import java.sql.Date

import com.newsscraper.table.{Link, Article}

/**
 * Created by dmcquill on 3/17/15.
 */
trait WebsitePageData {
    def adaptToArticles: Seq[ Article ]
    def count: Int
}

trait WebsiteLinkData {
}
