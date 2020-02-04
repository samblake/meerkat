package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.EntityID

object Cases : NamedTable("cases") {
    val name = varchar("name", length = 100)
    val description = text("description")
    val project = reference("project", Projects)
    val path = text("path")
}

class Case(id: EntityID<Int>) : NamedEntity<ViewCase>(id) {
    companion object : NamedEntityClass<Case>(Cases)

    override var name by Cases.name
    var description by Cases.description
    var project by Project referencedOn Cases.project
    var path by Cases.path

    override fun asViewModel(baseUrl: String) = ViewCase.from(this, baseUrl)

}

class ViewCase(id: Int, name: String, description: String,
        @Listing("Path") val path: String, val project: Project,
        override val baseUrl: String) : ViewModel(id, name, description) {

    companion object : ViewType<ViewCase>("Cases","cases", "mdi-bookmark-check") {

        fun from(case: Case, baseUrl: String): ViewCase {
            return ViewCase(
                case.id.value,
                case.name,
                case.description,
                case.path,
                case.project,
                baseUrl
            )
        }

    }

    override val icon = ViewCase.icon

}