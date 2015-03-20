package com.newsscraper.xml.adapter

import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl

import scala.xml.factory.XMLLoader
import scala.xml.{Elem, XML}

object TagSoupFactoryAdapter
{
    private val factory=new SAXFactoryImpl()

    def get(): XMLLoader[Elem]=
    {
        XML.withSAXParser(factory.newSAXParser())
    }
}