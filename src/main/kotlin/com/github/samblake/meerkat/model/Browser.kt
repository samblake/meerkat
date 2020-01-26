package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Browsers : IntIdTable("browsers") {
    val name = varchar("name", length = 100)
    val description = text("description")
    val client = varchar("client", length = 10)
    val width = integer("width")
    val height = integer("width")
    val additionConfig = text("addition_config")
}

class Browser(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Project>(Projects)

    var name by Browsers.name
    var description by Browsers.description
    val client by Browsers.client
    val width by Browsers.width
    val height by Browsers.height
    val additionConfig by Browsers.additionConfig
}

data class BrowserDto(val id: Int, val name: String, val description: String,
                 val client: String, val width: Int, val height: Int, val additionConfig: String) {

    companion object {
        fun from(browser: Browser): BrowserDto {
            return BrowserDto(
                browser.id.value,
                browser.name,
                browser.description,
                browser.client,
                browser.width,
                browser.height,
                browser.additionConfig
            )
        }
    }

}