package com.github.samblake.meerkat.services

import com.github.samblake.meerkat.edge.Database.query
import com.github.samblake.meerkat.model.Browser
import com.github.samblake.meerkat.model.ViewBrowser

object BrowserService {

    suspend fun all(baseUrl: String): List<ViewBrowser> = query {
        Browser.all().toList().map { it.asViewModel(baseUrl) }
    }

}