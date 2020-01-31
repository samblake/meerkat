package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.*

abstract class NamedEntity<T:Any>(id: EntityID<Int>) : IntEntity(id) {

    abstract var name:String

    abstract fun asViewModel(baseUrl: String):T

}

abstract class NamedTable(name: String = "", columnName: String = "id") : IntIdTable(name, columnName)

abstract class NamedEntityClass<out E:NamedEntity<out Any>>(val name: String, val urlSegment: String,
        table: IdTable<Int>, entityType: Class<E>? = null) : IntEntityClass<E>(table, entityType)