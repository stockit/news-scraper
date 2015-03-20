package com.newsscraper

import com.typesafe.config.ConfigFactory
import slick.jdbc.JdbcBackend._

/**
 * Created by dmcquill on 3/18/15.
 */
object NewsScraper extends App {

    lazy val stockitDBName = "mysql-stockit"
    lazy val databaseConfig = ConfigFactory.load("database")

    def stockitDatabase: Database = {
        Database.forConfig(
            stockitDBName,
            config = databaseConfig
        )
    }
}
