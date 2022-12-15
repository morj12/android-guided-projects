package com.example.wildrunning.utils

import android.graphics.Color
import android.location.Location
import android.util.Log
import android.widget.LinearLayout
import com.example.wildrunning.activity.*
import com.example.wildrunning.activity.LoginActivity.Companion.userEmail
import com.example.wildrunning.activity.MainActivity.Companion.lastImage
import com.example.wildrunning.activity.MainActivity.Companion.locationEnabled
import com.example.wildrunning.activity.MainActivity.Companion.photoNumber
import com.example.wildrunning.activity.MainActivity.Companion.selectedSport
import com.example.wildrunning.activity.MainActivity.Companion.totalsMap
import com.example.wildrunning.adapter.RunAdapter
import com.example.wildrunning.data.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

object FirestoreClient {

    /**
     * Loads medals from database for a sport type and stores the data in a map
     */
    fun loadSportMedals(sport: SportType, medalsMap: MedalsMap) {
        // Get distance records
        val db = FirebaseFirestore.getInstance()
        db.collection("runs$sport")
            .whereEqualTo("user", userEmail)
            .orderBy("distance", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { doc ->
                doc.forEach {
                    medalsMap[sport]!!["distance"]!!.add(
                        it["distance"].toString().toDouble()
                    )
                }
                while (medalsMap[sport]!!["distance"]!!.size < 3) medalsMap[sport]!!["distance"]!!.add(
                    0.0
                )
            }
            .addOnFailureListener {
                Log.e(this.javaClass.name, this::loadSportMedals.name, it)
            }

        // Get average speed records
        db.collection("runs$sport")
            .whereEqualTo("user", userEmail)
            .orderBy("avgSpeed", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { doc ->
                doc.forEach {
                    medalsMap[sport]!!["avgSpeed"]!!.add(
                        it["avgSpeed"].toString().toDouble()
                    )
                }
                while (medalsMap[sport]!!["avgSpeed"]!!.size < 3) medalsMap[sport]!!["avgSpeed"]!!.add(
                    0.0
                )

            }
            .addOnFailureListener {
                Log.e(this.javaClass.name, this::loadSportMedals.name, it)
            }

        // Get maximum speed records
        db.collection("runs$sport")
            .whereEqualTo("user", userEmail)
            .orderBy("maxSpeed", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { doc ->
                doc.forEach {
                    medalsMap[sport]!!["maxSpeed"]!!.add(
                        it["maxSpeed"].toString().toDouble()
                    )
                }
                while (medalsMap[sport]!!["maxSpeed"]!!.size < 3) medalsMap[sport]!!["maxSpeed"]!!.add(
                    0.0
                )

            }
            .addOnFailureListener {
                Log.e(this.javaClass.name, this::loadSportMedals.name, it)
            }
    }

    /**
     * Loads sport type totals from database for a sport type and stores the data in a map
     */
    fun loadSportTotals(sport: SportType, callback: (SportType) -> Unit) {
        val collection = "totals$sport"
        val db = FirebaseFirestore.getInstance()
        db.collection(collection).document(userEmail)
            .get()
            .addOnSuccessListener {
                if (it.data?.size != null) {
                    val totals = it.toObject(Totals::class.java)
                    totalsMap[sport] = totals!!
                } else {
                    db.collection(collection).document(userEmail).set(
                        mutableMapOf(
                            "recordAvgSpeed" to 0.0,
                            "recordDistance" to 0.0,
                            "recordSpeed" to 0.0,
                            "totalDistance" to 0.0,
                            "totalRuns" to 0,
                            "totalTime" to 0
                        )
                    )
                }

                callback.invoke(sport)
            }
            .addOnFailureListener {
                Log.e(this.javaClass.name, this::loadSportTotals.name, it)
            }
    }

    /**
     * Removes a run and related data
     */
    fun deleteRun(runId: String, sport: SportType, layout: LinearLayout, run: Run) {
        if (locationEnabled) deleteLocations(runId)
        if (photoNumber > 0) StorageClient.deletePicture(runId)
        updateTotalsAfterRunRemove(run)
        updateRecords(run, sport)
        mDeleteRun(runId, sport, layout)
    }


    /**
     * Deletes all location of a run
     */
    private fun deleteLocations(runId: String) {
        val id = runId.subSequence(userEmail.length, runId.length).toString()
        val db = FirebaseFirestore.getInstance()
        db.collection("locations/$userEmail/id")
            .get()
            .addOnSuccessListener { doc ->
                doc.forEach {
                    db.collection("location/$userEmail/$id").document(it.id).delete()
                }
            }
            .addOnFailureListener {
                Log.e(this.javaClass.name, this::deleteLocations.name, it)
            }
    }

    /**
     * Update distance, average speed and maximum speed records
     */
    private fun updateRecords(run: Run, sport: SportType) {
        updateDistanceRecord(run, sport)
        updateAvgSpeedRecord(run, sport)
        updateMaxSpeedRecord(run, sport)
    }

    /**
     * Updates distance records
     */
    private fun updateDistanceRecord(run: Run, sport: SportType) {
        if (run.distance!! == totalsMap[sport]!!.recordDistance) {
            val db = FirebaseFirestore.getInstance()
            db.collection("runs$sport")
                .whereEqualTo("user", userEmail)
                .orderBy("distance", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    totalsMap[sport]!!.recordDistance = if (it.size() == 1) 0.0
                    else it.documents[1].get("distance").toString().toDouble()

                    val collection = "totals$sport"
                    db.collection(collection).document(userEmail)
                        .update("recordDistance", totalsMap[sport]!!.recordDistance)
                }
        }
    }

    /**
     * Updates average speed records
     */
    private fun updateAvgSpeedRecord(run: Run, sport: SportType) {
        if (run.avgSpeed!! == totalsMap[sport]!!.recordAvgSpeed) {
            val db = FirebaseFirestore.getInstance()
            db.collection("runs$sport")
                .whereEqualTo("user", userEmail)
                .orderBy("avgSpeed", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    totalsMap[sport]!!.recordAvgSpeed = if (it.size() == 1) 0.0
                    else it.documents[1].get("avgSpeed").toString().toDouble()

                    val collection = "totals$sport"
                    db.collection(collection).document(userEmail)
                        .update("recordAvgSpeed", totalsMap[sport]!!.recordAvgSpeed)
                }
        }
    }

    /**
     * Updates maximum speed records
     */
    private fun updateMaxSpeedRecord(run: Run, sport: SportType) {
        if (run.maxSpeed!! == totalsMap[sport]!!.recordSpeed) {
            val db = FirebaseFirestore.getInstance()
            db.collection("runs$sport")
                .whereEqualTo("user", userEmail)
                .orderBy("maxSpeed", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    totalsMap[sport]!!.recordSpeed = if (it.size() == 1) 0.0
                    else it.documents[1].get("maxSpeed").toString().toDouble()

                    val collection = "totals$sport"
                    db.collection(collection).document(userEmail)
                        .update("recordSpeed", totalsMap[sport]!!.recordSpeed)
                }
        }
    }

    /**
     * Decreases totals after a run has been removed
     */
    private fun updateTotalsAfterRunRemove(run: Run) {
        totalsMap[selectedSport]!!.totalDistance =
            totalsMap[selectedSport]!!.totalDistance!! - run.distance!!
        totalsMap[selectedSport]!!.totalRuns = totalsMap[selectedSport]!!.totalRuns!! - 1
        totalsMap[selectedSport]!!.totalTime =
            totalsMap[selectedSport]!!.totalTime!! - TimeFormatter.timeToSeconds(run.duration!!)
    }

    private fun mDeleteRun(runId: String, sport: SportType, layout: LinearLayout) {
        val db = FirebaseFirestore.getInstance()
        db.collection("runs$sport").document(runId).delete()
            .addOnSuccessListener {
                Snackbar.make(layout, "Run removed", Snackbar.LENGTH_LONG)
                    .setAction("OK") {
                        layout.setBackgroundColor(Color.CYAN)
                    }
                    .show()
            }
    }

    /**
     * Saves finished run data in the database
     */
    fun saveRunData(collection: String, run: Run) {
        val db = FirebaseFirestore.getInstance()
        db.collection(collection).document(run.user!!).set(
            mutableMapOf(
                "user" to userEmail,
                "date" to run.date,
                "startTime" to run.startTime,
                "sport" to selectedSport,
                "locationEnabled" to locationEnabled,
                "duration" to run.duration,
                "distance" to run.distance,
                "avgSpeed" to run.avgSpeed,
                "maxSpeed" to run.maxSpeed,
                "minAltitude" to run.minAltitude,
                "maxAltitude" to run.maxAltitude,
                "minLatitude" to run.minLatitude,
                "maxLatitude" to run.maxLatitude,
                "minLongitude" to run.minLongitude,
                "maxLongitude" to run.maxLongitude,
                "latitudeCenter" to run.latitudeCenter,
                "longitudeCenter" to run.longitudeCenter,
                "medalDistance" to run.medalDistance,
                "medalAvgSpeed" to run.medalAvgSpeed,
                "medalMaxSpeed" to run.medalMaxSpeed,
                "photoNumber" to photoNumber,
                "lastImage" to lastImage
            )
        )

        if (run.intervalMode == true) {
            db.collection(collection).document(run.user!!).update("intervalMode", true)
            db.collection(collection).document(run.user!!)
                .update("intervalDuration", run.intervalDuration)
            db.collection(collection).document(run.user!!)
                .update("runningTime", run.runningTime)
            db.collection(collection).document(run.user!!)
                .update("walkingTime", run.walkingTime)
        }
    }

    /**
     * Sets the level for a sport and stores it in a map
     */
    fun loadSportLevel(
        sport: SportType,
        levelConditionsMap: LevelConditionsMap,
        callback: (SportType) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("levels$sport")
            .get()
            .addOnSuccessListener { document ->

                document.forEach {
                    levelConditionsMap[sport]!!.add(it.toObject(Level::class.java))
                }

                callback.invoke(sport)

            }
            .addOnFailureListener {
                Log.e(this.javaClass.name, this::loadSportMedals.name, it)
            }
    }

    /**
     * Updates totals after a run has been finished
     */
    fun updateTotalsAfterRunFinish(collection: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection(collection).document(userEmail)
            .update("recordAvgSpeed", totalsMap[selectedSport]!!.recordAvgSpeed)
        db.collection(collection).document(userEmail)
            .update("recordDistance", totalsMap[selectedSport]!!.recordDistance)
        db.collection(collection).document(userEmail)
            .update("recordSpeed", totalsMap[selectedSport]!!.recordSpeed)
        db.collection(collection).document(userEmail)
            .update("totalDistance", totalsMap[selectedSport]!!.totalDistance)
        db.collection(collection).document(userEmail)
            .update("totalRuns", totalsMap[selectedSport]!!.totalRuns)
        db.collection(collection).document(userEmail)
            .update("totalTime", totalsMap[selectedSport]!!.totalTime)
    }

    /**
     * Saves a location in the database
     */
    fun saveLocation(
        path: String,
        doc: String,
        location: Location,
        speed: Double,
        maxSpeed: Boolean,
        color: Int
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("locations/$userEmail/$path").document(doc).set(
            mutableMapOf(
                "time" to SimpleDateFormat("HH:mm:ss").format(Date()),
                "latitude" to location.latitude,
                "longitude" to location.longitude,
                "altitude" to location.altitude,
                "hasAltitude" to location.hasAltitude(),
                "speedFromGoogle" to location.speed,
                "customSpeed" to speed,
                "maxSpeed" to maxSpeed,
                "color" to color
            )
        )
    }

    /**
     * Loads runs from the database and add them to a list
     */
    fun loadRuns(
        field: String,
        order: Query.Direction,
        selectedSport: SportType,
        runsList: MutableList<Run>,
        mAdapter: RunAdapter
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("runs$selectedSport")
            .orderBy(field, order)
            .whereEqualTo("user", userEmail)
            .get()
            .addOnSuccessListener { run ->
                run.forEach {
                    runsList.add(it.toObject(Run::class.java))
                }

                mAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.e(this.javaClass.name, this::loadSportMedals.name, it)
            }
    }

}