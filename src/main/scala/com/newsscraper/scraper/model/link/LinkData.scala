package com.newsscraper.scraper.model.link

import com.newsscraper.scraper.model.{WebsiteLinkData}
import com.newsscraper.table.Link

/**
 * Created by dmcquill on 3/17/15.
 */
case class ArchiveLinkData(archives: Map[String, Array[String]]) extends WebsiteLinkData {
};
