package com.newsscraper.scraper.strategy

import com.newsscraper.scraper.model.{WebsitePageData}

trait ScraperStrategy {
    def crawl: WebsitePageData
}