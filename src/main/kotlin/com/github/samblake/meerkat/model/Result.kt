package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.EntityID

object Results : NamedTable("results") {
    val name = varchar("name", length = 100)
    val description = text("description")
    val run = reference("run", Runs)
    val case = reference("case", Cases)
}

class Result(id: EntityID<Int>) : NamedEntity<ViewResult>(id) {
    companion object : NamedEntityClass<Result>(Results)

    override var name by Results.name
    var description by Results.description
    var run by Results.run
    var case by Results.case

    override fun asViewModel(baseUrl: String) = ViewResult.from(this, baseUrl)

}

class ViewResult(id: Int, name: String, description: String,
        override val baseUrl: String) : ViewModel(id, name, description) {

    companion object : ViewType<ViewResult>("Results","results", "mdi-thumbs-up-down") {

        fun from(result: Result, baseUrl: String): ViewResult = ViewResult(
            result.id.value,
            result.name,
            result.description,
            baseUrl
        )

    }

    override val icon = ViewResult.icon

}