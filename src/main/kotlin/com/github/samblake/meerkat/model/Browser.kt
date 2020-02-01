package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.EntityID

object Browsers : NamedTable("browsers") {
    val name = varchar("name", length = 100)
    val description = text("description")
    val client = enumerationByName("client", 10, Clients::class)
    val width = integer("width")
    val height = integer("height")
    val additionalConfig = text("additional_config").nullable()
}

class Browser(id: EntityID<Int>) : NamedEntity<ViewBrowser>(id) {
    companion object : NamedEntityClass<Browser>(Browsers)

    override var name by Browsers.name

    var description by Browsers.description
    val client by Browsers.client
    val width by Browsers.width
    val height by Browsers.height
    val additionalConfig by Browsers.additionalConfig

    override fun asViewModel(baseUrl: String) = ViewBrowser.from(this, baseUrl)

}

class ViewBrowser(id: Int, name: String, description: String, val client: Clients, val width: Int, val height: Int,
        val additionalConfig: String?, override val baseUrl: String) : ViewModel(id, name, description) {

    companion object : ViewType<ViewBrowser>("Browsers", "browsers", "mdi-cellphone-link")  {

        fun from(browser: Browser, baseUrl: String): ViewBrowser {
            return ViewBrowser(
                browser.id.value,
                browser.name,
                browser.description,
                browser.client,
                browser.width,
                browser.height,
                browser.additionalConfig,
                baseUrl
            )
        }

    }

    override val icon = ViewBrowser.icon

}

enum class Clients {
    Chrome, Firefox, Safari
}