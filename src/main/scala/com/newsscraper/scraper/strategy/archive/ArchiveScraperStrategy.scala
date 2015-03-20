package com.newsscraper.scraper.strategy.archive

import com.newsscraper.scraper.model.WebsiteData
import com.newsscraper.scraper.strategy.ScraperStrategy
import com.newsscraper.scraper.strategy.link.LinkScraperStrategy
import com.newsscraper.scraper.strategy.page.PageScraperStrategy

/**
 * Created by dmcquill on 3/17/15.
 */
class ArchiveScraperStrategy extends ScraperStrategy {

    var linkScraper: LinkScraperStrategy = new LinkScraperStrategy()
    var pageScraper: PageScraperStrategy = new PageScraperStrategy()

    def crawl: WebsiteData = {

        val linkData = linkScraper.crawl()

        pageScraper.crawl(linkData.links)
    }
}
