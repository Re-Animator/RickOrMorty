package com.reanimator.rickormorty.data.filters

interface Filterable {
    val _searchQuery: String?

    val searchQuery
        get() = _searchQuery

    fun updateSearchQuery(query: String?): Filterable
}