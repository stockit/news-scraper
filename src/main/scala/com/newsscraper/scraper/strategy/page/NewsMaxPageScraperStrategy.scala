package com.newsscraper.scraper.strategy.page

import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date

import com.newsscraper.concurrent.ConcurrentReducer
import com.newsscraper.scraper.model.page.{NewsMaxPage, Page, PageData}
import com.newsscraper.xml.adapter.TagSoupFactoryAdapter
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class NewsMaxPageScraperStrategy extends PageScraperStrategy {

    val logger = LoggerFactory.getLogger(classOf[NewsMaxPageScraperStrategy])
    val dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy hh:mm aaa")

    def crawl(pages: Array[String]): PageData = {

        var numProcessed = 0

        def incrementProcessed() = {
            this.synchronized {
                numProcessed += 1

//                logger.info(s"Processed archive link [$numProcessed] of [${pages.size}]]")
            }
        }

        def incrementProcessedWithError() = {
            this.synchronized {
                numProcessed += 1

                logger.error(s"Error on archive link [$numProcessed] of [${pages.size}]]")
            }
        }

        var concurrentReducer = new ConcurrentReducer()
        concurrentReducer.threads = 4

        var futureCalls = Future.sequence(concurrentReducer.map[String, Page](list = pages.toList, { page: String =>
            try {
                val url = new URL(page)
                val element = TagSoupFactoryAdapter.get().load(url)

                val headEl = element \\ "h1"

                val dateEl = for {
                    div <- element \\ "div"
                    p <- div \ "p"
                    if (p \ "@class").text == "artPgDate"
                    date <- p
                } yield date

                val paragraphEls = for {
                    div <- element \\ "div"
                    if (div \ "@id").text == "mainArticleDiv"
                    p <- div \ "p"
                } yield p

                val headText = headEl.text
                val dateText = dateEl.text
                val bodyText = paragraphEls.map({ _.text }).mkString(" ")

                val date = dateFormat.parse(dateText.trim())

                incrementProcessed()

                NewsMaxPage(title = headText, body = bodyText, date = date, url = page)
            } catch {
                case e: Exception => {
                    e.printStackTrace()

                    incrementProcessedWithError()

                    null
                }
            }
        }))

        val pageData = Await.result(futureCalls, Duration.Inf)(0)

        PageData(pages = pageData.toList)
    }

}
