package com.newsscraper.scraper.strategy.page

import java.net.URL
import java.text.SimpleDateFormat

import com.newsscraper.scraper.model.page.{NewsMaxPage, Page, PageData}
import com.newsscraper.xml.adapter.TagSoupFactoryAdapter

/**
 * Created by dmcquill on 3/17/15.
 */
class PageScraperStrategy {

    var dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy hh:mm aaa")

    def crawl(pages: Array[String]): PageData = {

        var pageData = List[Page]()

        for(page <- pages) {
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

            pageData = pageData :+ NewsMaxPage(title = headText, body = bodyText, date = date)
        }

        PageData(pages = pageData)
    }

}
