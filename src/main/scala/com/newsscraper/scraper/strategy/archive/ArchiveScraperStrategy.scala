package com.newsscraper.scraper.strategy.archive

import com.newsscraper.scraper.model.page.{Page, PageData}
import com.newsscraper.scraper.model.{WebsitePageData, WebsiteLinkData}
import com.newsscraper.scraper.strategy.ScraperStrategy
import com.newsscraper.scraper.strategy.link.{NewsMaxLinkScraperStrategy, LinkScraperStrategy}
import com.newsscraper.scraper.strategy.page.{NewsMaxPageScraperStrategy, PageScraperStrategy}
import org.slf4j.LoggerFactory

/**
 * Created by dmcquill on 3/17/15.
 */
class ArchiveScraperStrategy extends ScraperStrategy {

    val logger = LoggerFactory.getLogger(classOf[ArchiveScraperStrategy])

    var linkScraper: LinkScraperStrategy = new NewsMaxLinkScraperStrategy()
    var pageScraper: PageScraperStrategy = new NewsMaxPageScraperStrategy()

    def crawl: WebsitePageData = {

        val linkData: WebsiteLinkData = linkScraper.crawl()

//        logger.info(s"Finished processing link data: ${linkData.count} pages found")

//        val websiteData = pageScraper.crawl(linkData.adaptToLinks.map({ _.url }).toArray)

//        logger.info(s"Finished processing page data: ${websiteData.count} pages found")
//
//        websiteData
        new PageData(pages = List[Page]())
    }
}
