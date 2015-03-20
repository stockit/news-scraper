package com.newsscraper.scraper.strategy

import com.newsscraper.scraper.model.WebsiteData

trait ScraperStrategy {
    def crawl: WebsiteData
}