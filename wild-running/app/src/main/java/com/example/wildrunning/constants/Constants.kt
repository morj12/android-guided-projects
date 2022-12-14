package com.example.wildrunning.constants

import android.Manifest

object Constants {

    const val SLOW_ANIMATION_DURATION = 1000L
    const val STANDARD_ANIMATION_DURATION = 500L
    const val FAST_ANIMATION_DURATION = 200L

    const val STOPWATCH_INTERVAL = 1000L
    const val LOCATION_INTERVAL = 3
    const val DEFAULT_ROUND_INTERVAL = 300

    const val LAYOUT_HEIGHT_1 = 200f
    const val LAYOUT_HEIGHT_2 = 300f
    const val LAYOUT_HEIGHT_3 = 400f
    const val LAYOUT_HEIGHT_4 = 500f
    const val LAYOUT_HEIGHT_5 = 600f

    const val LOCATION_PERMISSION_ID = 42
    const val CAMERA_REQUEST_CODE = 10

    const val RATIO_4_3 = 4.0 / 3.0
    const val RATIO_16_9 = 16.0 / 9.0

    const val DISTANCE_LIMIT_RUN = 0.12 * LOCATION_INTERVAL
    const val DISTANCE_LIMIT_ROLLERSKATE = 0.35 * LOCATION_INTERVAL
    const val DISTANCE_LIMIT_BIKE = 0.4 * LOCATION_INTERVAL

    val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val CAMERA_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA
    )

    // shared preferences keys
    const val PREFS_USER_EMAIL = "userEmail"
    const val PREFS_USER_PROVIDER = "provider"
    const val PREFS_SELECTED_SPORT = "selectedSport"
    const val PREFS_IS_INTERVAL_MODE = "isIntervalMode"
    const val PREFS_HARD_INTERVAL_TIME = "hardIntervalTime"
    const val PREFS_TOTAL_INTERVAL_TIME = "totalIntervalTime"
    const val PREFS_RUNNING_TIME = "runningTime"
    const val PREFS_WALKING_TIME = "walkingTime"
    const val PREFS_HARD_VOLUME = "hardVolume"
    const val PREFS_SOFT_VOLUME = "softVolume"
    const val PREFS_NOTIFY_VOLUME = "notifyVolume"
}