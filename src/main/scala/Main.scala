
import com.newsscraper.NewsScraper
import com.newsscraper.scraper.model.link.ArchiveLinkData
import com.newsscraper.scraper.model.page.NewsMaxPage
import com.newsscraper.scraper.model.{WebsiteLinkData, WebsitePageData}
import com.newsscraper.scraper.strategy.archive.ArchiveScraperStrategy
import com.newsscraper.scraper.strategy.link.NewsMaxLinkScraperStrategy
import com.newsscraper.scraper.strategy.page.NewsMaxPageScraperStrategy
import com.newsscraper.table._
import org.slf4j.LoggerFactory
import scala.slick.jdbc.meta.MTable
import slick.driver.MySQLDriver.simple._


/**
 * Created by dmcquill on 3/13/15.
 */
object Main {

    val sites: TableQuery[Sites] = TableQuery[Sites]
    val articles: TableQuery[Articles] = TableQuery[Articles]
    val links: TableQuery[Links] = TableQuery[Links]
    val archives: TableQuery[Archives] = TableQuery[Archives]
    val linkArchive: TableQuery[LinkArchive] = TableQuery[LinkArchive]
    val linkArticle: TableQuery[LinkArticle] = TableQuery[LinkArticle]

    val pageScraper = new NewsMaxPageScraperStrategy()

    val logger = LoggerFactory.getLogger("Main")

    def dropDatabases(): Unit = {
        val db = NewsScraper.stockitDatabase

        db.withSession { implicit session =>
            if(!MTable.getTables(linkArchive.baseTableRow.tableName).list.isEmpty) linkArchive.ddl.drop
            if(!MTable.getTables(linkArticle.baseTableRow.tableName).list.isEmpty) linkArticle.ddl.drop
        }

        db.withSession { implicit session =>
            if(!MTable.getTables(archives.baseTableRow.tableName).list.isEmpty) archives.ddl.drop
        }

        db.withSession { implicit session =>
            if(!MTable.getTables(sites.baseTableRow.tableName).list.isEmpty) sites.ddl.drop
            if(!MTable.getTables(articles.baseTableRow.tableName).list.isEmpty) articles.ddl.drop
            if(!MTable.getTables(links.baseTableRow.tableName).list.isEmpty) links.ddl.drop
        }

    }

    def createDatabases(): Unit = {
        val db = NewsScraper.stockitDatabase

        db.withSession { implicit session =>
            if(MTable.getTables(sites.baseTableRow.tableName).list.isEmpty) sites.ddl.create
            if(MTable.getTables(articles.baseTableRow.tableName).list.isEmpty) articles.ddl.create
            if(MTable.getTables(links.baseTableRow.tableName).list.isEmpty) links.ddl.create
            if(MTable.getTables(archives.baseTableRow.tableName).list.isEmpty) archives.ddl.create
            if(MTable.getTables(linkArchive.baseTableRow.tableName).list.isEmpty) linkArchive.ddl.create
            if(MTable.getTables(linkArticle.baseTableRow.tableName).list.isEmpty) linkArticle.ddl.create
        }
    }

    def retrieveArchiveLinks(): ArchiveLinkData = {
        val linkScraperStrategy = new NewsMaxLinkScraperStrategy()

        val linkData: ArchiveLinkData = linkScraperStrategy.crawl()

        linkData
    }

    def storeArchiveLinks(website: Site, linkData: ArchiveLinkData): Unit = {
        val db = NewsScraper.stockitDatabase

        db.withSession { implicit session =>
            sites += website

            val websiteId = (sites returning sites.map(_.id)) += website

            linkData.archives foreach { each =>
                val url = each._1
                val archiveLinks = each._2

                val archiveId = (archives returning archives.map(_.id)) += Archive(None, url, websiteId)

                archiveLinks foreach { link =>
                    val linkId = (links returning links.map(_.id)) += Link(None, link)

                    linkArchive += (linkId, archiveId)
                }
            }
        }
    }

    def fetchArchiveLinksFromDB(siteName: String): Seq[Archive] = {
        val db = NewsScraper.stockitDatabase

        val session: Session = db.createSession()

        val foundArchives = sites
            .filter({ _.name === siteName })
            .run(session)
            .map({ site =>
                archives.filter({ _.siteId === site.id }).run(session)
            })
            .flatten

        session.close()

        foundArchives
    }

    def fetchLinks(archive: Archive): Seq[Link] = {
        val db = NewsScraper.stockitDatabase

        val session: Session = db.createSession()

        val archiveLinks = linkArchive
            .filter({
                _.archiveId === archive.id
            })
            .run(session)
            .map({ link =>
                links.filter({ _.id === link._1 }).run(session)
            })
            .flatten

        session.close()

        archiveLinks
    }

    def processStoredArchiveLinks(siteName: String): Unit = {
        val fetchedArchives = fetchArchiveLinksFromDB(siteName)

        var numProcessed = 0

        fetchedArchives foreach { archive =>
            val fetchedLinks = fetchLinks(archive)

            val pageData = pageScraper.crawl(fetchedLinks.map({ _.url }).toArray)

            val db = NewsScraper.stockitDatabase

            pageData.pages foreach { page =>
                try {
                    db.withSession { implicit session =>
                        if( page.isInstanceOf[NewsMaxPage]) {
                            val newsPage = page.asInstanceOf[NewsMaxPage]
                            val articleId = (articles returning articles.map(_.id)) += ( if(newsPage == null) Article(None, null, null, null) else newsPage.tuple )

                            val foundLinks: Seq[Link] = links.filter({ _.url === newsPage.url}).run
                            if(foundLinks.size > 0) {
                                val linkId = foundLinks(0).id.get

                                linkArticle += (linkId, articleId)
                            }
                        }
                    }
                } catch {
                    case e: Exception => {
                        logger.error("Error saving page to db")
                    }
                }
            }

            logger.info(s"Stored archive page [$numProcessed] of [${fetchedArchives.size}]")

            numProcessed += 1
        }
    }

    def main(args: Array[String]): Unit = {

        dropDatabases()
        createDatabases()

        val website = Site(None, name = "NewsMax")
        storeArchiveLinks(website, retrieveArchiveLinks())

        processStoredArchiveLinks("NewsMax")
    }

}
