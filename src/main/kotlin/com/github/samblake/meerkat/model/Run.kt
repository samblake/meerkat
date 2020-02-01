package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.EntityID

object Runs : NamedTable("runs") {
    val name = varchar("name", length = 100)
    val description = text("description")
}

class Run(id: EntityID<Int>) : NamedEntity<ViewRun>(id) {
    companion object : NamedEntityClass<Run>(Runs)

    override var name by Runs.name

    var description by Runs.description

    override fun asViewModel(baseUrl: String) = ViewRun.from(this, baseUrl)

}

class ViewRun(id: Int, name: String, description: String,
        override val baseUrl: String) : ViewModel(id, name, description) {

    companion object : ViewType<ViewRun>("Runs","runs", "mdi-play-circle") {

        fun from(run: Run, baseUrl: String): ViewRun = ViewRun(
            run.id.value,
            run.name,
            run.description,
            baseUrl
        )

    }

    override val icon = ViewRun.icon

}