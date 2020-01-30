package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.EntityID

object Projects : NamedTable("projects") {
    val name = varchar("name", length = 100)
    val description = varchar("description", length = 500)
}

class Project(id: EntityID<Int>) : NamedEntity<ViewProject>(id) {
    companion object : NamedEntityClass<Project>("Projects","projects", Projects)

    override var name by Projects.name

    var description by Projects.description

    override fun asViewModel(baseUrl: String) = ViewProject.from(this, baseUrl)

}

class ViewProject(id: Int, name: String, description: String,
        override val baseUrl: String) : ViewModel(id, name, description) {

    companion object {
        fun from(project: Project, baseUrl: String): ViewProject {
            return ViewProject(
                project.id.value,
                project.name,
                project.description,
                baseUrl
            )
        }
    }

}