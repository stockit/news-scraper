package com.newsscraper.scraper.util

import xml.Node

/**
 * Created by dmcquill on 3/17/15.
 */
object ScraperUtil {
    def attribute(node: Node, label: String) =
        node.attribute(label) match {
            case Some(res) => res.head.toString()
            case None => ""
        }
}
