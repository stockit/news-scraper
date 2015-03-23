package com.newsscraper.scraper.strategy.page

import com.newsscraper.scraper.model.WebsitePageData

trait PageScraperStrategy {

    def crawl(pages: Array[String]): WebsitePageData

}
