package com.newsscraper.scraper.model.page

import java.sql.Date

import com.newsscraper.scraper.model.WebsitePageData
import com.newsscraper.table.Article

/**
 * Created by dmcquill on 3/18/15.
 */
case class PageData(pages: List[Page]) extends WebsitePageData {

    def adaptToArticles: Seq[ Article ] = {
        pages.map({
            _.tuple
        })
    }

    def count: Int = {
        pages.size
    }
}

