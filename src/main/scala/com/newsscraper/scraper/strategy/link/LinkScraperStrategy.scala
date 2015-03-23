package com.newsscraper.scraper.strategy.link

import com.newsscraper.scraper.model.WebsiteLinkData

trait LinkScraperStrategy {

    def crawl(): WebsiteLinkData

}
