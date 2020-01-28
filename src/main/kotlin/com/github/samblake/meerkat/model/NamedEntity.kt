package com.github.samblake.meerkat.model

import org.jetbrains.exposed.dao.*

abstract class NamedEntity(id: EntityID<Int>) : IntEntity(id) {
    abstract var name:String
}

abstract class NamedTable(name: String = "", columnName: String = "id") : IntIdTable(name, columnName) {
}

abstract class NamedEntityClass<out E:NamedEntity>(table: IdTable<Int>, entityType: Class<E>? = null) : IntEntityClass<E>(table, entityType)
