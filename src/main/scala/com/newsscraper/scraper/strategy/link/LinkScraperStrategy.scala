package com.newsscraper.scraper.strategy.link

import java.net.URL

import com.newsscraper.scraper.model.link.LinkData
import com.newsscraper.scraper.util.ScraperUtil
import com.newsscraper.xml.adapter.TagSoupFactoryAdapter

import scala.annotation.tailrec
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by dmcquill on 3/17/15.
 */
class LinkScraperStrategy {

    private def lookupArchiveLinks(links: Array[String]): Array[String] = {

        @tailrec
        def lookupArchiveLinksInner(links: Array[String], current: Array[String]): Array[String] = {
            if(links.isEmpty) current
            else {
                val url = new URL(links.head)

                val element = TagSoupFactoryAdapter.get().load(url)
                val archiveElements = ( element \\ "ul" ) (ScraperUtil.attribute(_, "class").matches(".*archiveRepeaterUL.*")) \\ "a"

                val foundLinks: Array[String] = archiveElements
                    .map(_.attribute("href"))
                    .map({
                    case Some(href) => href.text
                    case None => ""
                }).toArray

                lookupArchiveLinksInner(links.tail, current ++ foundLinks)
            }
        }

        lookupArchiveLinksInner(links, Array[String]())
    }

    def crawl(): LinkData = {
        val url = new URL("http://www.newsmax.com/Archives/Markets/7/2007/8/");

        val element = TagSoupFactoryAdapter.get().load(url)
        val archiveElements = ( element \\ "table" \\ "a" ) (ScraperUtil.attribute(_, "href").matches("/Archives/.*"))

        val archiveLinks: Array[String] = archiveElements.map(_.attribute("href")).map({
            case Some(href) => s"http://www.newsmax.com${href.text}"
            case None => null
        }).toArray

        val articleLinks = lookupArchiveLinks(Array[String](archiveLinks.head))

        LinkData(links = Array[String](articleLinks.head))
    }

}
