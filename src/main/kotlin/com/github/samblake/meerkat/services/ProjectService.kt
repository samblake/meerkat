package com.github.samblake.meerkat.services

import com.github.samblake.meerkat.edge.Database
import com.github.samblake.meerkat.model.Project
import com.github.samblake.meerkat.model.ProjectDto

object ProjectService {

    suspend fun all(): List<ProjectDto> = Database.query {
        Project.all().toList().map { it.toDto() }
    }

}