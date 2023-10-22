package com.reanimator.rickormorty.data.filters

data class EpisodeFilter(
    override var _searchQuery: String? = null,
    private var _season: String? = null
) : Filterable {
    override val searchQuery
        get() = _searchQuery
    val season
        get() = _season

    companion object {
        const val SEASON_1 = "%S01%"
        const val SEASON_2 = "%S02%"
        const val SEASON_3 = "%S03%"
        const val SEASON_4 = "%S04%"
        const val SEASON_5 = "%S05%"
    }

    sealed class EpisodeSeason {
        data object SeasonOne : EpisodeSeason()
        data object SeasonTwo : EpisodeSeason()
        data object SeasonThree : EpisodeSeason()
        data object SeasonFour : EpisodeSeason()
        data object SeasonFive : EpisodeSeason()
    }

    fun updateSeasonFilter(season: EpisodeSeason?): EpisodeFilter {
        _season = when (season) {
            EpisodeSeason.SeasonOne -> SEASON_1
            EpisodeSeason.SeasonTwo -> SEASON_2
            EpisodeSeason.SeasonThree -> SEASON_3
            EpisodeSeason.SeasonFour -> SEASON_4
            EpisodeSeason.SeasonFive -> SEASON_5
            else -> null
        }
        return this.copy(_season = this._season)
    }

    override fun updateSearchQuery(query: String?): EpisodeFilter {
        return this.copy(_searchQuery = query)
    }
}