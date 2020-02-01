package com.github.samblake.meerkat.menu

import com.github.samblake.meerkat.model.ViewType

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

    constructor(viewType: ViewType<*>) : this(viewType, "/")

    constructor(viewType: ViewType<*>, baseUrl: String)
            : this(viewType.name, baseUrl + viewType.urlSegment, viewType.icon)

    fun isSelected(crumbs: List<String>): Boolean = crumbs.any { it == name }

}

class ViewMenu(val menu: Menu, val selected: Item?) {

    fun isSelected(item: Item): Boolean = item == selected

    val sections = menu.sections

}