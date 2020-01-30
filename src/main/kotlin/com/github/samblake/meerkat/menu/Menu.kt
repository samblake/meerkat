package com.github.samblake.meerkat.menu

import com.github.samblake.meerkat.model.NamedEntityClass

class Menu(val sections: List<Section>) {

    fun findSelectedItem(crumbs: List<String>): Item? {
        return sections.map { it.findSelectedItem(crumbs) }.filterNotNull().firstOrNull()
            ?: sections.firstOrNull()?.items?.firstOrNull()
    }

}

class Section(val name: String, val items: List<Item>) {

    fun findSelectedItem(crumbs: List<String>) : Item? = items.firstOrNull { it.isSelected(crumbs) }

}

class Item(val name: String, val url: String, val icon: String) {

    constructor(namedEntityClass: NamedEntityClass<*>, icon: String) : this(namedEntityClass, "/", icon)

    constructor(namedEntityClass: NamedEntityClass<*>, baseUrl: String, icon: String)
            : this(namedEntityClass.name, baseUrl + namedEntityClass.urlSegment, icon)

    fun isSelected(crumbs: List<String>): Boolean = crumbs.any { it == name }

}

class ViewMenu(val menu: Menu, val selected: Item?) {

    fun isSelected(item: Item): Boolean = item == selected

    val sections = menu.sections

}