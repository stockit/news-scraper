package com.newsscraper

import com.newsscraper.concurrent.ConcurrentReducer
import com.newsscraper.scraper.strategy.link.{NewsMaxLinkScraperStrategy, LinkScraperStrategy}

/**
 * Created by dmcquill on 3/21/15.
 */
object MainFuture {

//    def flow[A](noOfRecipients: Int, opsPerClient: Int, keyPrefix: String,
//                fn: (Int, String) => A) = {
//        (1 to noOfRecipients) map {i =>
//            Future {
//                fn(opsPerClient, "list_" + i)
//            }
//        }
//    }

    def main(args: Array[String]): Unit = {
        val linkScraperStrategy = new NewsMaxLinkScraperStrategy()

        val links = linkScraperStrategy.crawl()

        println(links)
    }
}
