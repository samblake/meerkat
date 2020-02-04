package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.EntityID

object Projects : NamedTable("projects") {
    val name = varchar("name", length = 100)
    val description = text("description")
    val base = varchar("base", length = 255)
}

class Project(id: EntityID<Int>) : NamedEntity<ViewProject>(id) {
    companion object : NamedEntityClass<Project>(Projects)

    override var name by Projects.name

    var description by Projects.description
    var base by Projects.base
    val scenarios by Scenario referrersOn Scenarios.project
    val cases by Case referrersOn Cases.project

    override fun asViewModel(baseUrl: String) = ViewProject.from(this, baseUrl)

}

class ViewProject(id: Int, name: String, description: String, @Listing("Base") val base: String,
        override val baseUrl: String) : ViewModel(id, name, description) {

    companion object : ViewType<ViewProject>("Projects","projects", "mdi-clipboard-outline") {

        fun from(project: Project, baseUrl: String): ViewProject {
            return ViewProject(
                project.id.value,
                project.name,
                project.description,
                project.base,
                baseUrl
            )
        }

    }

    override val icon = ViewProject.icon

}