package com.github.samblake.meerkat

import com.github.samblake.meerkat.crumbs.Crumb.crumbs
import com.github.samblake.meerkat.crumbs.Crumb.entity
import com.github.samblake.meerkat.crumbs.Crumb.title
import com.github.samblake.meerkat.crumbs.crumb
import com.github.samblake.meerkat.edge.Configuration
import com.github.samblake.meerkat.edge.Database
import com.github.samblake.meerkat.menu.Item
import com.github.samblake.meerkat.menu.Menu
import com.github.samblake.meerkat.menu.Section
import com.github.samblake.meerkat.menu.ViewMenu
import com.github.samblake.meerkat.model.*
import com.github.samblake.meerkat.services.BrowserService
import com.github.samblake.meerkat.services.ProjectService
import com.github.samblake.meerkat.services.ScenarioService
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

            route("") { crumb("Meerkat") {
                get {
                    call.respond(ThymeleafContent("index", mapOf(
                        attrTo(title), attrTo(crumbs), "menu" to generateMenu()
                    )))
                }

                with (ViewProject) { route(urlSegment) { crumb(name) {
                    get {
                        val projects = ProjectService.all(url())
                        listCall(projects)
                    }

                    route("{id}") { crumb(Project) {
                        get {
                            val project = attr(entity).asViewModel(url())
                            viewCall(project)
                        }

                        with (ViewScenario) { route(urlSegment) { crumb(name) {
                            get {
                                val project = attr(entity) as Project
                                val scenarios = ScenarioService.all(project, url())
                                listCall(scenarios)
                            }

                            route("{id}") { crumb(Scenario) {
                                get {
                                    val scenario = attr(entity).asViewModel(url())
                                    viewCall(scenario)
                                }
                            }
                        }}}}
                    }

                }}}}

                with (ViewBrowser) { route(urlSegment) { crumb(name) {
                    get {
                        val browsers = BrowserService.all(url())
                        listCall(browsers)
                    }

                    route("{id}") { crumb(Browser) {
                        get {
                            val browser = attr(entity).asViewModel(url())
                            viewCall(browser)
                        }
                    }}
                }}}
            }}
        }

    }.start(wait = true)

}

private fun PipelineContext<Unit, ApplicationCall>.url() = call.request.uri

private suspend fun PipelineContext<Unit, ApplicationCall>.viewCall(browser: Any) {
    when (call.request.contentType()) {
        ContentType.Application.Json -> call.respond(browser)
        else -> call.respond(ThymeleafContent("generic/view", viewMap(browser)))
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.listCall(projects: List<Any>) {
    when (call.request.contentType()) {
        ContentType.Application.Json -> call.respond(projects)
        else -> call.respond(ThymeleafContent("generic/list", listMap(projects)))
    }
}

private fun PipelineContext<Unit, ApplicationCall>.listMap(entities: List<Any>): Map<String, Any> = mapOf(
    attrTo(title),
    attrTo(crumbs),
    "entities" to entities,
    "menu" to generateMenu()
)

private fun PipelineContext<Unit, ApplicationCall>.viewMap(entity: Any): Map<String, Any> = mapOf(
    attrTo(title),
    attrTo(crumbs),
    "entity" to entity,
    "menu" to generateMenu()
)

val menu = Menu(listOf(
    Section("General", listOf(Item("Home", "/", "mdi-home"))),
    Section("Setup", listOf(Item(ViewProject), Item(ViewBrowser))),
    Section("Runs", listOf())
))

private fun PipelineContext<Unit, ApplicationCall>.generateMenu(): ViewMenu {
    val crumbs = attr(crumbs)
    val selectedItem = menu.findSelectedItem(crumbs)
    return ViewMenu(menu, selectedItem)
}

private fun <T:Any>PipelineContext<Unit, ApplicationCall>.attr(key: AttributeKey<T>): T =
    context.request.call.attributes.get(key)

private fun <T:Any>PipelineContext<Unit, ApplicationCall>.attrTo(key: AttributeKey<T>): Pair<String, T> =
    key.name to attr(key)
