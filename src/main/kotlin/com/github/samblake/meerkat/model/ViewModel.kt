package com.github.samblake.meerkat.model

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

abstract class ViewModel(val id: Int, val name: String, val description: String) {

    abstract val baseUrl: String
    abstract val icon: String

    fun getInstanceUrl() = "${baseUrl}/${id}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ViewModel
        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }

    fun getListings(): Map<String, String> {
        val fields = (this::class as KClass<ViewModel>).memberProperties.filter { it.annotations.any { it is Listing } }
        return fields.map {
            Pair((it.annotations.first { it is Listing } as Listing).name, it.get(this).toString())
        }.toMap()
    }

}

abstract class ViewType<T: ViewModel>(val name: String, val urlSegment: String, val icon: String)


@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Listing(val name: String)