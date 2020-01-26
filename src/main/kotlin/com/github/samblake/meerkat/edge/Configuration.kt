package com.github.samblake.meerkat.edge

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.Feature.OPTIONAL_SOURCE_BY_DEFAULT

object Configuration {

    private val SCHEMA = "meerkat"
    private val DEFAULT_DRIVER_CLASS_NAME = "org.h2.Driver"
    private val DEFAULT_JDBC_URL = "jdbc:h2:mem:test;INIT=" +
            "RUNSCRIPT FROM './sql/init-h2.sql'\\;" +
            "RUNSCRIPT FROM './sql/init.sql'\\;" +
            "RUNSCRIPT FROM './sql/example.sql'\\;"

    object MeerkatSpec : ConfigSpec() {
        object WebSpec : ConfigSpec() {
            val static by optional("static")
        }
        object JdbcSpec : ConfigSpec() {
            val driver by optional(DEFAULT_DRIVER_CLASS_NAME)
            val url by optional(DEFAULT_JDBC_URL)
            val username by optional(SCHEMA)
            val password by optional(SCHEMA)
        }
    }

    val config = Config { addSpec(MeerkatSpec) }
        .enable(OPTIONAL_SOURCE_BY_DEFAULT)
        .from.properties.resource("meerkat.properties")
        .from.env()
        .from.systemProperties()


    fun static() = config[MeerkatSpec.WebSpec.static]

    fun jdbcDriver() = config[MeerkatSpec.JdbcSpec.driver]
    fun jdbcUrl() = config[MeerkatSpec.JdbcSpec.url]
    fun jdbcUsername() = config[MeerkatSpec.JdbcSpec.username]
    fun jdbcPassword() = config[MeerkatSpec.JdbcSpec.password]

}