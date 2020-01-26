package com.github.samblake.meerkat

import com.github.samblake.meerkat.edge.Database
import com.github.samblake.meerkat.edge.Database.query
import com.github.samblake.meerkat.model.Project
import com.github.samblake.meerkat.model.ProjectDto
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.text.DateFormat

fun main() {

    Database.init()

    embeddedServer(Netty, port = 7000) {

        install(DefaultHeaders)
        install(Compression)
        install(CallLogging)
        install(ContentNegotiation) {
            gson {
                setDateFormat(DateFormat.LONG)
                setPrettyPrinting()
            }
        }

        routing {
            static("css") {
                files("static/css")
            }
            static("js") {
                files("static/js")
            }
            static("home") {
                default("static/index.html")
            }

            get("/") {
                call.respondText("Hello World!", ContentType.Text.Plain)
            }
            get("/projects") {
                val projects = query {
                    Project.all().toList().map { p -> ProjectDto.from(p) }
                }
                call.respond( projects )
            }
        }

    }.start(wait = true)
}