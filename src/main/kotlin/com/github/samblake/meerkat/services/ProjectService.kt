package com.github.samblake.meerkat.services

import com.github.samblake.meerkat.edge.Database
import com.github.samblake.meerkat.model.Project
import com.github.samblake.meerkat.model.ViewProject

object ProjectService {

    suspend fun all(baseUrl: String): List<ViewProject> = Database.query {
        Project.all().toList().map { it.asViewModel(baseUrl) }
    }

}