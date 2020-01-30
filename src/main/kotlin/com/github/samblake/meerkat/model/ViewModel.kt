package com.github.samblake.meerkat.model

abstract class ViewModel(val id: Int, val name: String, val description: String) {

    abstract val baseUrl: String

    fun getInstanceUrl() = "${baseUrl}/${id}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ViewModel

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

}