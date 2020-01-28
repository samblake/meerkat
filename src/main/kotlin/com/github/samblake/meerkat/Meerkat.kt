package com.github.samblake.meerkat

import com.github.samblake.meerkat.crumbs.Crumb
import com.github.samblake.meerkat.crumbs.crumb
import com.github.samblake.meerkat.edge.Configuration
import com.github.samblake.meerkat.edge.Database
import com.github.samblake.meerkat.model.Browser
import com.github.samblake.meerkat.model.BrowserDto
import com.github.samblake.meerkat.model.Project
import com.github.samblake.meerkat.model.ProjectDto
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
                        call.respond(ThymeleafContent("index", mapOf()))
                    }

                    route("projects") {
                        crumb("Projects") {
                            get {
                                val projects = ProjectService.all()
                                when (call.request.contentType()) {
                                    ContentType.Application.Json -> call.respond(projects)
                                    else -> call.respond(ThymeleafContent("projects/list", mapOf(
                                        "projects" to projects,
                                        "crumbs" to attr(Crumb.crumbs),
                                        "title" to attr(Crumb.title),
                                        "url" to call.request.uri
                                    )))
                                }
                            }

                            route("{id}") {
                                crumb(Project) {
                                    get {
                                        val project = ProjectDto.from(attr(Crumb.entity) as Project)
                                        when (call.request.contentType()) {
                                            ContentType.Application.Json -> call.respond(project)
                                            else -> call.respond(ThymeleafContent("projects/view", mapOf(
                                                "project" to project,
                                                "crumbs" to attr(Crumb.crumbs),
                                                "title" to attr(Crumb.title),
                                                "url" to call.request.uri
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
                                        "browsers" to browsers,
                                        "crumbs" to attr(Crumb.crumbs),
                                        "title" to attr(Crumb.title),
                                        "url" to call.request.uri
                                    )))
                                }
                            }

                            route("{id}") {
                                crumb(Browser) {
                                    get {
                                        val browser = BrowserDto.from(attr(Crumb.entity) as Browser)
                                        when (call.request.contentType()) {
                                            ContentType.Application.Json -> call.respond(browser)
                                            else -> call.respond(ThymeleafContent("browsers/view", mapOf(
                                                "browser" to browser,
                                                "crumbs" to attr(Crumb.crumbs),
                                                "title" to attr(Crumb.title),
                                                "url" to call.request.uri
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

private fun <T:Any>PipelineContext<Unit, ApplicationCall>.attr(key: AttributeKey<T>): T =
    context.request.call.attributes.get(key)

