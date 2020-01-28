package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.EntityID

object Projects : NamedTable("projects") {
    val name = varchar("name", length = 100)
    val description = varchar("description", length = 500)
}

class Project(id: EntityID<Int>) : NamedEntity<ProjectDto>(id) {
    companion object : NamedEntityClass<Project>(Projects)

    override var name by Projects.name

    var description by Projects.description

    override fun toDto() = ProjectDto.from(this)

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