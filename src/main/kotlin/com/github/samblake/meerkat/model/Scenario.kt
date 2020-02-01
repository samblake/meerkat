package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.EntityID

object Scenarios : NamedTable("scenarios") {
    val name = varchar("name", length = 100)
    val description = text("description")
    val project = reference("project", Projects)
}

class Scenario(id: EntityID<Int>) : NamedEntity<ViewScenario>(id) {
    companion object : NamedEntityClass<Scenario>(Scenarios)

    override var name by Scenarios.name
    var description by Scenarios.description
    var project by Project referencedOn Scenarios.project
    var cases by Case via ScenarioCases

    override fun asViewModel(baseUrl: String) = ViewScenario.from(this, baseUrl)

}

class ViewScenario(id: Int, name: String, description: String,
        override val baseUrl: String) : ViewModel(id, name, description) {

    companion object : ViewType<ViewScenario>("Scenarios","scenarios", "mdi-playlist-check") {

        fun from(scenario: Scenario, baseUrl: String): ViewScenario {
            return ViewScenario(
                scenario.id.value,
                scenario.name,
                scenario.description,
                baseUrl
            )
        }

    }

    override val icon = ViewScenario.icon

}