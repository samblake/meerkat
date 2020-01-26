package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Projects : IntIdTable("projects") {
    val name = com.github.samblake.meerkat.model.Projects.varchar("name", length = 100)
    val description = com.github.samblake.meerkat.model.Projects.varchar("description", length = 500)
}

class Project(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Project>(Projects)

    var name by Projects.name
    var description by Projects.description
}

data class ProjectDto(val id: Int, val name: String, val description: String) {

    companion object {
        fun from(project: Project): ProjectDto {
            return ProjectDto(
                project.id.value,
                project.name,
                project.description
            )
        }
    }

}