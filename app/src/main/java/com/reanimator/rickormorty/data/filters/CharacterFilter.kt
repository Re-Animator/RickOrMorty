package com.reanimator.rickormorty.data.filters

data class CharacterFilter(
    override var _searchQuery: String? = null,
    private var _status: String? = null,
    private var _gender: String? = null
) : Filterable {
    override val searchQuery
        get() = _searchQuery
    val status
        get() = _status
    val gender
        get() = _gender

    companion object {
        const val STATUS_ALIVE = "alive"
        const val STATUS_DEAD = "dead"
        const val STATUS_UNKNOWN = "unknown"

        const val GENDER_MALE = "male"
        const val GENDER_FEMALE = "female"
        const val GENDER_GENDERLESS = "genderless"
        const val GENDER_UNKNOWN = "unknown"
    }

    sealed class CharacterStatus {
        data object Alive : CharacterStatus()
        data object Dead : CharacterStatus()
        data object Unknown : CharacterStatus()
    }

    sealed class CharacterGender {
        data object Male : CharacterGender()
        data object Female : CharacterGender()
        data object Genderless : CharacterGender()
        data object Unknown : CharacterGender()
    }

    fun updateGender(gender: CharacterGender?): CharacterFilter {
        _gender = when (gender) {
            CharacterGender.Male -> GENDER_MALE
            CharacterGender.Female -> GENDER_FEMALE
            CharacterGender.Genderless -> GENDER_GENDERLESS
            CharacterGender.Unknown -> GENDER_UNKNOWN
            else -> null
        }
        return this.copy(_gender = this._gender)
    }

    fun updateStatus(status: CharacterStatus?): CharacterFilter {
        _status = when (status) {
            CharacterStatus.Dead -> STATUS_DEAD
            CharacterStatus.Alive -> STATUS_ALIVE
            CharacterStatus.Unknown -> STATUS_UNKNOWN
            else -> null
        }
        return this.copy(_status = this._status)
    }

    override fun updateSearchQuery(query: String?): CharacterFilter {
        return this.copy(_searchQuery = query)
    }
}