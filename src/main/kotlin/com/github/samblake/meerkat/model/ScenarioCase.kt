package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object ScenarioCases : IntIdTable("scenario_cases") {
    val scenario = reference("scenario", Scenarios)
    val case = reference("case", Cases)
}

class ScenarioCase(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ScenarioCase>(ScenarioCases)

    var scenario by Scenario referencedOn ScenarioCases.scenario
    var case by Case referencedOn ScenarioCases.case

}