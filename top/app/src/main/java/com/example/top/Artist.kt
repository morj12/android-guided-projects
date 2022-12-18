package com.example.top

data class Artist(
    var id: Long = 0,
    var name: String = "",
    var surname: String = "",
    var birthDate: Long = 0,
    var birthPlace: String= "",
    var height: Short = 0,
    var order: Int = 0,
    var notes: String = "",
    var photoUrl: String = ""
)
{
    companion object {
        const val NAME = "name"
        const val SURNAME = "surname"
        const val BIRTH_DATE = "birthDate"
        const val BIRTH_PLACE = "birthPlace"
        const val HEIGHT = "height"
        const val ORDER = "order"
        const val NOTES = "notes"
        const val PHOTO_URL = "photoUrl"
    }
}
