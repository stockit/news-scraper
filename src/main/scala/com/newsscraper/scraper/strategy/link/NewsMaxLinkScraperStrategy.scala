package com.newsscraper.scraper.strategy.link

import java.net.URL

import com.newsscraper.concurrent.ConcurrentReducer
import com.newsscraper.scraper.model.link.{ArchiveLinkData}
import com.newsscraper.scraper.util.ScraperUtil
import com.newsscraper.xml.adapter.TagSoupFactoryAdapter
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global

class NewsMaxLinkScraperStrategy extends LinkScraperStrategy {

    val logger = LoggerFactory.getLogger(classOf[NewsMaxLinkScraperStrategy])

    private def lookupArchiveLinks(links: Array[String]): Map[String, Array[String]] = {

        var numProcessed = 0

        def incrementProcessed(numPages: Int) = {
            this.synchronized {
                numProcessed += 1

                logger.info(s"Processed archive link [$numProcessed] of [${links.size}]]; found: [$numPages] pages")
            }
        }

        var concurrentReducer = new ConcurrentReducer()
        concurrentReducer.threads = 100

        var futureCalls = Future.sequence(concurrentReducer.map[String, Map[String, Array[String]]](links.toList, { _url =>
            val url = new URL(_url)

            val element = TagSoupFactoryAdapter.get().load(url)
            val archiveElements = ( element \\ "ul" ) (ScraperUtil.attribute(_, "class").matches(".*archiveRepeaterUL.*")) \\ "a"

            val foundLinks: Array[String] = archiveElements
                .map(_.attribute("href"))
                .map({
                case Some(href) => href.text
                case None => ""
            }).toArray

            incrementProcessed(foundLinks.size)

            Map[String, Array[String]]( _url -> foundLinks )
        }))

        var result: Seq[Seq[Map[String, Array[String]]]] = Await.result(futureCalls, Duration.Inf)

        var mappedResults = Map[String, Array[String]]()

        for( sequence <- result ) {
            if( sequence.isInstanceOf[Seq[Map[String, Array[String]]]] && sequence.size > 0) {
                mappedResults ++= sequence(0)
            }
        }

        mappedResults
    }

    def crawl(): ArchiveLinkData = {
        val url = new URL("http://www.newsmax.com/Archives/Markets/7/2007/8/");

        val element = TagSoupFactoryAdapter.get().load(url)
        val archiveElements = ( element \\ "table" \\ "a" ) (ScraperUtil.attribute(_, "href").matches("/Archives/.*"))

        val archiveLinks: Array[String] = archiveElements.map(_.attribute("href")).map({
            case Some(href) => s"http://www.newsmax.com${href.text}"
            case None => null
        }).toArray

        logger.info(s"Found archive links: [${archiveLinks.size}]")

        ArchiveLinkData(archives = lookupArchiveLinks(archiveLinks))
    }

}
