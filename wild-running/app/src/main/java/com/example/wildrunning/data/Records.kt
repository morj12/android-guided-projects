package com.example.wildrunning.data

data class Records(
    var distanceRecord: RecordMedal = RecordMedal.NONE,
    var avgSpeedRecord: RecordMedal = RecordMedal.NONE,
    var maxSpeedRecord: RecordMedal = RecordMedal.NONE
) {

    enum class RecordMedal {
        GOLD, BRONZE, SILVER, NONE
    }
}
