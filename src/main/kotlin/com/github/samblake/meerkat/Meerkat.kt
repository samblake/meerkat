package com.github.samblake.meerkat

import com.github.samblake.meerkat.edge.Configuration
import com.github.samblake.meerkat.edge.Database
import com.github.samblake.meerkat.services.BrowserService
import com.github.samblake.meerkat.services.ProjectService
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.contentType
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.thymeleaf.Thymeleaf
import io.ktor.thymeleaf.ThymeleafContent
import io.ktor.util.AttributeKey
import nz.net.ultraq.thymeleaf.LayoutDialect
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.text.DateFormat.LONG

val crumbKey = AttributeKey<MutableList<String>>("crumbs")
val titleKey = AttributeKey<String>("title")

fun Route.withCrumb(name: String, callback: Route.() -> Unit): Route {

    val routeWithCrumb = this.createChild(object : RouteSelector(1.0) {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Constant
    })

    // Intercepts calls from this route at the features step
    routeWithCrumb.intercept(ApplicationCallPipeline.Features) {
        var att = context.request.call.attributes.getOrNull(crumbKey)
        if (att == null) {
            att = ArrayList()
        }
        att.add(name)
        context.request.call.attributes.put(crumbKey, att)
        context.request.call.attributes.put(titleKey, name)
    }

    // Configure this route with the block provided by the user
    callback(routeWithCrumb)

    return routeWithCrumb
}

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

            route("/") {
                withCrumb("Meerkat") {
                    get {
                        call.respond(ThymeleafContent("index", mapOf()))
                    }
                    route("projects") {
                        withCrumb("Projects") {
                            get {
                                val projects = ProjectService.all()
                                when (call.request.contentType()) {
                                    ContentType.Application.Json -> call.respond(projects)
                                    else -> {
                                        val crumbs = context.request.call.attributes.get(crumbKey)
                                        val title = context.request.call.attributes.get(titleKey)
                                        call.respond(ThymeleafContent("projects/list", mapOf(
                                            "projects" to projects,
                                            "crumbs" to crumbs,
                                            "title" to title
                                        )))
                                    }
                                }
                            }
                        }
                    }
                    route("browsers") {
                        withCrumb("Browsers") {
                            get {
                                val browsers = BrowserService.all()
                                when (call.request.contentType()) {
                                    ContentType.Application.Json -> call.respond(browsers)
                                    else -> {
                                        val crumbs = context.request.call.attributes.get(crumbKey)
                                        call.respond(
                                            ThymeleafContent(
                                                "browser/list",
                                                mapOf("browsers" to browsers, "crumbs" to crumbs)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }.start(wait = true)



}