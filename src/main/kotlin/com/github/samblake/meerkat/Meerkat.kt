package com.github.samblake.meerkat

import com.github.samblake.meerkat.crumbs.Crumb
import com.github.samblake.meerkat.crumbs.crumb
import com.github.samblake.meerkat.edge.Configuration
import com.github.samblake.meerkat.edge.Database
import com.github.samblake.meerkat.model.Browser
import com.github.samblake.meerkat.model.Project
import com.github.samblake.meerkat.services.BrowserService
import com.github.samblake.meerkat.services.ProjectService
import io.ktor.application.ApplicationCall
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
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.thymeleaf.Thymeleaf
import io.ktor.thymeleaf.ThymeleafContent
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
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

            route("/") {
                crumb("Meerkat") {
                    get {
                        call.respond(ThymeleafContent("index", mapOf(
                            "menu" to generateMenu()
                        )))
                    }

                    route("projects") {
                        crumb("Projects") {
                            get {
                                val projects = ProjectService.all()
                                when (call.request.contentType()) {
                                    ContentType.Application.Json -> call.respond(projects)
                                    else -> call.respond(ThymeleafContent("projects/list", mapOf(
                                        attrTo(Crumb.title),
                                        attrTo(Crumb.crumbs),
                                        "projects" to projects,
                                        "url" to call.request.uri,
                                        "menu" to generateMenu()
                                    )))
                                }
                            }

                            route("{id}") {
                                crumb(Project) {
                                    get {
                                        val project = attr(Crumb.entity).toDto()
                                        when (call.request.contentType()) {
                                            ContentType.Application.Json -> call.respond(project)
                                            else -> call.respond(ThymeleafContent("projects/view", mapOf(
                                                attrTo(Crumb.title),
                                                attrTo(Crumb.crumbs),
                                                "project" to project,
                                                "url" to call.request.uri,
                                                "menu" to generateMenu()
                                            )))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    route("browsers") {
                        crumb("Browsers") {
                            get {
                                val browsers = BrowserService.all()
                                when (call.request.contentType()) {
                                    ContentType.Application.Json -> call.respond(browsers)
                                    else -> call.respond(ThymeleafContent("browsers/list", mapOf(
                                        attrTo(Crumb.title),
                                        attrTo(Crumb.crumbs),
                                        "browsers" to browsers,
                                        "url" to call.request.uri,
                                        "menu" to generateMenu()
                                    )))
                                }
                            }

                            route("{id}") {
                                crumb(Browser) {
                                    get {
                                        val browser = attr(Crumb.entity).toDto()
                                        when (call.request.contentType()) {
                                            ContentType.Application.Json -> call.respond(browser)
                                            else -> call.respond(ThymeleafContent("browsers/view", mapOf(
                                                attrTo(Crumb.title),
                                                attrTo(Crumb.crumbs),
                                                "browser" to browser,
                                                "url" to call.request.uri,
                                                "menu" to generateMenu()
                                            )))
                                        }
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

data class Link(val name: String, val href: String)
val menu = listOf(
    Link("Projects", "/projects"),
    Link("Browsers", "/browsers")
)

data class MenuLink(val link: Link, val selected: Boolean)

private fun PipelineContext<Unit, ApplicationCall>.generateMenu(): List<MenuLink> {
    val crumbs = attr(Crumb.crumbs)
    return menu.map { link ->
        val matching = crumbs.filter { crumb -> crumb == link.name }
        MenuLink(link, matching.isNotEmpty())
    }
}

private fun <T:Any>PipelineContext<Unit, ApplicationCall>.attr(key: AttributeKey<T>): T =
    context.request.call.attributes.get(key)

private fun <T:Any>PipelineContext<Unit, ApplicationCall>.attrTo(key: AttributeKey<T>): Pair<String, T> =
    key.name to attr(key)
