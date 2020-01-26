package com.github.samblake.meerkat.services

import com.github.samblake.meerkat.edge.Database.query
import com.github.samblake.meerkat.model.Browser
import com.github.samblake.meerkat.model.BrowserDto

object BrowserService {

    suspend fun all(): List<BrowserDto> = query {
        Browser.all().toList().map { b -> BrowserDto.from(b) }
    }

}