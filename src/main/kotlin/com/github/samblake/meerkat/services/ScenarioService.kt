package com.github.samblake.meerkat.services

import com.github.samblake.meerkat.edge.Database
import com.github.samblake.meerkat.model.Project
import com.github.samblake.meerkat.model.Scenario
import com.github.samblake.meerkat.model.Scenarios
import com.github.samblake.meerkat.model.ViewScenario

object ScenarioService {

    suspend fun all(project: Project, baseUrl: String): List<ViewScenario> = Database.query {
        Scenario.find{ Scenarios.project eq project.id }.toList().map { it.asViewModel(baseUrl) }
    }

}