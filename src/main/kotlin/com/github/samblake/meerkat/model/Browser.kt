package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.EntityID

object Browsers : NamedTable("browsers") {
    val name = varchar("name", length = 100)
    val description = text("description")
    val client = enumerationByName("client", 10, Clients::class)
    val width = integer("width")
    val height = integer("width")
    val additionalConfig = text("additional_config").nullable()
}

class Browser(id: EntityID<Int>) : NamedEntity<BrowserDto>(id) {
    companion object : NamedEntityClass<Browser>(Browsers)

    override var name by Browsers.name

    var description by Browsers.description
    val client by Browsers.client
    val width by Browsers.width
    val height by Browsers.height
    val additionalConfig by Browsers.additionalConfig

    override fun toDto() = BrowserDto.from(this)

}

data class BrowserDto(val id: Int, val name: String, val description: String,
    val client: Clients, val width: Int, val height: Int, val additionalConfig: String?) {

    companion object {
        fun from(browser: Browser): BrowserDto {
            return BrowserDto(
                browser.id.value,
                browser.name,
                browser.description,
                browser.client,
                browser.width,
                browser.height,
                browser.additionalConfig
            )
        }
    }

}

enum class Clients {
    Chrome, Firefox, Safari
}