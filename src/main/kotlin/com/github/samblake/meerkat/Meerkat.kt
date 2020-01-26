package com.github.samblake.meerkat

import com.github.samblake.meerkat.edge.Configuration
import com.github.samblake.meerkat.edge.Database
import com.github.samblake.meerkat.services.BrowserService
import com.github.samblake.meerkat.services.ProjectService
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.thymeleaf.Thymeleaf
import io.ktor.thymeleaf.ThymeleafContent
import nz.net.ultraq.thymeleaf.LayoutDialect
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.text.DateFormat.LONG

fun main() {

    val staticDir = Configuration.static()

    Database.init()

    embeddedServer(Netty, port = 7000) {

        install(DefaultHeaders)
        install(Compression)
        install(CallLogging)

        install(ContentNegotiation) {
            gson {
                setDateFormat(LONG)
                setPrettyPrinting()
            }
        }

        install(Thymeleaf) {
            setTemplateResolver(ClassLoaderTemplateResolver().apply {
                prefix = "templates/"
                suffix = ".html"
                characterEncoding = "utf-8"
                addDialect(LayoutDialect())
            })
        }

        routing {
            static("css") {
                files("${staticDir}/css")
            }
            static("js") {
                files("${staticDir}/js")
            }

            get("/") {
                call.respond(ThymeleafContent("index", mapOf()))
            }
            get("/projects") {
                call.respond( ProjectService.all() )
            }
            get("/browsers") {
                call.respond( BrowserService.all() )
            }

        }

    }.start(wait = true)
}