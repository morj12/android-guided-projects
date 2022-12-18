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
        var ORDER = "order"
    }
}
