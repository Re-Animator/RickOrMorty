package com.reanimator.rickormorty.data.filters

data class LocationFilter(
    override var _searchQuery: String? = null,
    private var _typeSearchQuery: String? = null
) : Filterable {
    override val searchQuery
        get() = _searchQuery
    val typeSearchQuery
        get() = _typeSearchQuery

    override fun updateSearchQuery(query: String?): LocationFilter {
        return this.copy(_searchQuery = query)
    }

    fun updateTypeSearchQuery(query: String?): LocationFilter {
        return this.copy(_typeSearchQuery = query)
    }
}