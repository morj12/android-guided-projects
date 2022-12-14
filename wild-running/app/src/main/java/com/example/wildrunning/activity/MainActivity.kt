package com.example.wildrunning.activity

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.example.wildrunning.R
import com.example.wildrunning.activity.LoginActivity.Companion.providerSession
import com.example.wildrunning.activity.LoginActivity.Companion.userEmail
import com.example.wildrunning.constants.Constants.LAYOUT_HEIGHT_5
import com.example.wildrunning.constants.Constants.DEFAULT_ROUND_INTERVAL
import com.example.wildrunning.constants.Constants.DISTANCE_LIMIT_BIKE
import com.example.wildrunning.constants.Constants.DISTANCE_LIMIT_ROLLERSKATE
import com.example.wildrunning.constants.Constants.DISTANCE_LIMIT_RUN
import com.example.wildrunning.constants.Constants.FAST_ANIMATION_DURATION
import com.example.wildrunning.constants.Constants.LOCATION_INTERVAL
import com.example.wildrunning.constants.Constants.LOCATION_PERMISSION_ID
import com.example.wildrunning.constants.Constants.LAYOUT_HEIGHT_3
import com.example.wildrunning.constants.Constants.PREFS_HARD_INTERVAL_TIME
import com.example.wildrunning.constants.Constants.PREFS_HARD_VOLUME
import com.example.wildrunning.constants.Constants.PREFS_IS_INTERVAL_MODE
import com.example.wildrunning.constants.Constants.PREFS_NOTIFY_VOLUME
import com.example.wildrunning.constants.Constants.PREFS_RUNNING_TIME
import com.example.wildrunning.constants.Constants.PREFS_SELECTED_SPORT
import com.example.wildrunning.constants.Constants.PREFS_SOFT_VOLUME
import com.example.wildrunning.constants.Constants.PREFS_TOTAL_INTERVAL_TIME
import com.example.wildrunning.constants.Constants.PREFS_USER_EMAIL
import com.example.wildrunning.constants.Constants.PREFS_USER_PROVIDER
import com.example.wildrunning.constants.Constants.PREFS_WALKING_TIME
import com.example.wildrunning.constants.Constants.SLOW_ANIMATION_DURATION
import com.example.wildrunning.constants.Constants.LAYOUT_HEIGHT_1
import com.example.wildrunning.constants.Constants.STANDARD_ANIMATION_DURATION
import com.example.wildrunning.constants.Constants.LAYOUT_HEIGHT_2
import com.example.wildrunning.constants.Constants.LAYOUT_HEIGHT_4
import com.example.wildrunning.constants.Constants.STOPWATCH_INTERVAL
import com.example.wildrunning.constants.Constants.LOCATION_PERMISSIONS
import com.example.wildrunning.data.*
import com.example.wildrunning.databinding.ActivityMainBinding
import com.example.wildrunning.utils.*
import com.example.wildrunning.widget.Widget
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import me.tankery.lib.circularseekbar.CircularSeekBar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*
import kotlin.properties.Delegates

typealias MedalsMap = MutableMap<SportType, MutableMap<String, MutableList<Double>>>
typealias LevelConditionsMap = MutableMap<SportType, MutableList<Level>>

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        lateinit var mainContext: MainActivity

        lateinit var totalsMap: MutableMap<SportType, Totals>

        var locationEnabled = true
        var selectedSport = SportType.Running
        var records = Records()

        var photoNumber = 0
        var lastImage = ""

        lateinit var widgetTime: String
        lateinit var widgetDistance: String
    }

    // Shared preferences
    private lateinit var preferences: SharedPreferences
    private lateinit var prefsEditor: SharedPreferences.Editor
    private var noSetMiddlePoint = false

    // Run loop variables
    private var mHandler: Handler? = null
    private var timeInSeconds = 0L
    private var rounds = 1
    private var isRunStarted = false
    private var isHardInterval = true
    private var roundInterval = DEFAULT_ROUND_INTERVAL
    private var runningTime = 0
    private var limitDistance = 1.0 * LOCATION_INTERVAL

    // Location & Maps variables
    private lateinit var locationClient: FusedLocationProviderClient
    private var flagSavedLocation = false
    private var actLatitude = 0.0
    private var actLongitude = 0.0
    private var lastLat = 0.0
    private var lastLong = 0.0
    private var distance = 0.0
    private var maxSpeed = 0.0
    private var avgSpeed = 0.0
    private var speed = 0.0

    // save as Extrema object
    var extrema = Extrema()

    // Device screen variables
    private var screenWidthInPixels = 0
    private var screenHeightInPixels = 0
    private var animationsWidth = 0

    // Sounds variables
    private var notifyMediaPlayer: MediaPlayer? = null
    private var hardMediaPlayer: MediaPlayer? = null
    private var softMediaPlayer: MediaPlayer? = null
    private var recordMediaPlayer: MediaPlayer? = null

    // Database variables
    private lateinit var currentLevelMap: MutableMap<SportType, Level>
    private lateinit var levelConditionsMap: LevelConditionsMap
    private lateinit var medalsMap: MedalsMap
    var sportsLoaded = 0
    private lateinit var runDate: String
    private lateinit var runStartTime: String

    // Used colors
    private var whiteColor: Int by Delegates.notNull()
    private var orangeColor: Int by Delegates.notNull()
    private var grayMediumColor: Int by Delegates.notNull()
    private var chronoRunningColor: Int by Delegates.notNull()
    private var chronoWalkingColor: Int by Delegates.notNull()
    private var grayColor: Int by Delegates.notNull()
    private var grayDarkColor: Int by Delegates.notNull()
    private var salmonDarkColor: Int by Delegates.notNull()
    private var greenColor: Int by Delegates.notNull()

    // Binding
    private lateinit var binding: ActivityMainBinding

    // Widget
    private lateinit var widget: Widget
    private lateinit var mAppWidgetManager: AppWidgetManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainContext = this

        initToolBar()
        initNavigationView()
        initColors()
        initViews()
        hidePopup()
        initMusic()
        initLocationPermissions()
        initTotals()
        initLevels()
        initMedals()
        hideMedals()
        initPreferences()
        initWidget()
        loadDatabase()
    }

    override fun onBackPressed() {
        if (binding.popupOuterContainer.isVisible) findViewById<ImageView>(R.id.ivClosePopUp).callOnClick()
        else if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        else
            alertLogOut()
    }

    /***********************************************
     *            INITIALIZATION BLOCK             *
     **********************************************/

    private fun initToolBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.bar_title,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initNavigationView() {
        binding.navView.setNavigationItemSelectedListener(this)

        val headerView = LayoutInflater.from(this).inflate(
            R.layout.nav_header_main,
            binding.navView,
            false
        )
        binding.navView.removeHeaderView(headerView)
        binding.navView.addHeaderView(headerView)

        headerView.findViewById<TextView>(R.id.tvUser).text = userEmail
    }

    private fun initColors() {
        whiteColor = getColor(this, R.color.white)
        orangeColor = getColor(this, R.color.orange)
        grayMediumColor = getColor(this, R.color.gray_medium)
        chronoRunningColor = getColor(this, R.color.chrono_running)
        chronoWalkingColor = getColor(this, R.color.chrono_walking)
        grayColor = getColor(this, R.color.gray)
        grayDarkColor = getColor(this, R.color.gray_dark)
        salmonDarkColor = getColor(this, R.color.salmon_dark)
        greenColor = getColor(this, R.color.green)
    }

    private fun initViews() {
        binding.cameraButton.isVisible = false
        binding.stopwatchTextView.setTextColor(whiteColor)
        hideSettingsLayouts()
        initActualRunData()
        initIntervalsSettings()
    }

    private fun hideSettingsLayouts() {
        /**
         * Hide layouts by default
         */
        MyLayoutManager.let {
            it.setLinearLayoutHeight(binding.intervalModeOuterContainer, 0)
            it.setLinearLayoutHeight(binding.volumeSettingsOuterContainer, 0)
            it.setLinearLayoutHeight(binding.softTrackContainer, 0)
            it.setLinearLayoutHeight(binding.softVolumeContainer, 0)
        }

        /**
         * Move inner containers
         */
        binding.intervalModeInnerContainer.translationY = -LAYOUT_HEIGHT_2
        binding.volumeSettingsInnerContainer.translationY = -LAYOUT_HEIGHT_2
    }

    private fun initActualRunData() {
        /**
         * Init seekbars
         */
        binding.currentDistanceSeekBar.progress = 0f
        binding.currentAvgSpeedSeekBar.progress = 0f
        binding.currentSpeedSeekBar.progress = 0f
        binding.currentMaxSpeedSeekBar.progress = 0f

        /**
         * Init records
         */
        binding.distanceRecordTextView.text = ""
        binding.avgSpeedRecordTextView.text = ""
        binding.maxSpeedRecordTextView.text = ""

        /**
         * Init progress bars
         */
        screenWidthInPixels = resources.displayMetrics.widthPixels
        screenHeightInPixels = resources.displayMetrics.heightPixels
        animationsWidth = screenWidthInPixels
        binding.stopwatchProgressBar.translationX = -animationsWidth.toFloat()
        binding.roundProgressBar.translationX = -animationsWidth.toFloat()
    }

    private fun initIntervalsSettings() {
        initSeekBar()
        initIntervalPicker()
    }

    private fun initSeekBar() {
        /**
         * Hard/soft duration seekbar settings
         */
        val runWalkSeekBar = binding.runWalkSeekBar
        runWalkSeekBar.max = DEFAULT_ROUND_INTERVAL.toFloat()
        runWalkSeekBar.progress = runWalkSeekBar.max / 2
        runWalkSeekBar.setOnSeekBarChangeListener(object :
            CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Float,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    val stepOffset = (runWalkSeekBar.max / 4).toInt()
                    var step = progress.toInt() / stepOffset
                    if (progress.toInt() % stepOffset > stepOffset / 2) {
                        step++
                    }
                    noSetMiddlePoint = true
                    runWalkSeekBar.progress = (step * stepOffset).toFloat()

                } else if (!noSetMiddlePoint) {
                    runWalkSeekBar.progress = runWalkSeekBar.max / 2
                    setButtonsState(false)
                } else {
                    runWalkSeekBar.progress = progress
                    setButtonsState(false)
                    noSetMiddlePoint = false
                }

                binding.runningTimeTextView.text =
                    TimeFormatter.secondsToString(runWalkSeekBar.progress.toLong())
                        .subSequence(3, 8)
                binding.walkingTimeTextView.text =
                    TimeFormatter.secondsToString((roundInterval - runWalkSeekBar.progress.toInt()).toLong())
                        .subSequence(3, 8)

                runningTime =
                    TimeFormatter.timeToSeconds(binding.runningTimeTextView.text.toString())
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {}

            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {}
        })
    }

    private fun initIntervalPicker() {
        /**
         * Init interval picker
         */
        val durationIntervalNumberPicker = binding.durationIntervalNumberPicker
        with(durationIntervalNumberPicker)
        {
            minValue = 1
            maxValue = 60
            value = 5
            wrapSelectorWheel = true
            setFormatter { String.format("%02d", it) }
            setOnValueChangedListener { _, _, new ->
                binding.runWalkSeekBar.max = (new * 60).toFloat()
                binding.runWalkSeekBar.progress = binding.runWalkSeekBar.max / 2
                binding.runningTimeTextView.text =
                    TimeFormatter.secondsToString((new * 60 / 2).toLong()).subSequence(3, 8)
                binding.walkingTimeTextView.text = binding.runningTimeTextView.text

                roundInterval = new * 60
                runningTime = roundInterval / 2
            }

        }
    }

    private fun hidePopup() {
        binding.popupInnerContainer.translationX = LAYOUT_HEIGHT_3
        binding.popupOuterContainer.isVisible = false
    }

    private fun initStopwatch() {
        binding.stopwatchTextView.text = getString(R.string.chrono_start)
        timeInSeconds = 0
        rounds = 1
    }

    private fun initMusic() {
        notifyMediaPlayer = MediaPlayer.create(this, R.raw.notification)
        hardMediaPlayer = MediaPlayer.create(this, R.raw.hard)
        softMediaPlayer = MediaPlayer.create(this, R.raw.soft)
        recordMediaPlayer = MediaPlayer.create(this, R.raw.record)

        hardMediaPlayer?.isLooping = true
        softMediaPlayer?.isLooping = true
        notifyMediaPlayer?.isLooping = false
        recordMediaPlayer?.isLooping = false

        binding.hardMusicSeekBar.isEnabled = false
        binding.softMusicSeekBar.isEnabled = false

        hardMediaPlayer?.setVolume(
            binding.hardVolumeSeekBar.progress.toFloat() / 100,
            binding.hardVolumeSeekBar.progress.toFloat() / 100
        )

        softMediaPlayer?.setVolume(
            binding.softVolumeSeekBar.progress.toFloat() / 100,
            binding.softVolumeSeekBar.progress.toFloat() / 100
        )

        initVolumeSeekBarListeners()
        setTrackProgress()
    }

    private fun initVolumeSeekBarListeners() {
        binding.hardVolumeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                hardMediaPlayer?.setVolume(p1 / 100f, p1 / 100f)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.softVolumeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                softMediaPlayer?.setVolume(p1 / 100f, p1 / 100f)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.notificationVolumeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                notifyMediaPlayer?.setVolume(p1 / 100f, p1 / 100f)
                recordMediaPlayer?.setVolume(p1 / 100f, p1 / 100f)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    private fun setTrackProgress() {
        binding.hardMusicSeekBar.max = hardMediaPlayer!!.duration / 1000
        binding.softMusicSeekBar.max = softMediaPlayer!!.duration / 1000
        updateTracksTime()
    }

    private fun updateTracksTime() {
        binding.hardMusicElapseTime.text =
            TimeFormatter.secondsToString(binding.hardMusicSeekBar.progress.toLong())
        binding.hardMusicRemainingTime.text = getString(
            R.string.remaining_time_placeholder,
            TimeFormatter.secondsToString(hardMediaPlayer!!.duration / 1000 - binding.hardMusicSeekBar.progress.toLong())
        )

        binding.softMusicElapseTime.text =
            TimeFormatter.secondsToString(binding.softMusicSeekBar.progress.toLong())
        binding.softMusicRemainingTime.text = getString(
            R.string.remaining_time_placeholder,
            TimeFormatter.secondsToString(softMediaPlayer!!.duration / 1000 - binding.softMusicSeekBar.progress.toLong())
        )
    }

    private fun initLocationPermissions() =
        if (checkPermission())
            locationClient = LocationServices.getFusedLocationProviderClient(this)
        else
            requestLocationPermissions()

    private fun initTotals() {
        totalsMap = mutableMapOf(
            SportType.Bike to Totals(),
            SportType.RollerSkate to Totals(),
            SportType.Running to Totals()
        )

        totalsMap.values.forEach {
            it.recordAvgSpeed = 0.0
            it.recordDistance = 0.0
            it.recordSpeed = 0.0
            it.totalTime = 0
            it.totalDistance = 0.0
            it.totalRuns = 0
        }
    }

    private fun initLevels() {
        levelConditionsMap = mutableMapOf(
            SportType.Bike to mutableListOf(),
            SportType.RollerSkate to mutableListOf(),
            SportType.Running to mutableListOf()
        )

        currentLevelMap = mutableMapOf(
            SportType.Bike to Level(),
            SportType.RollerSkate to Level(),
            SportType.Running to Level()
        )
    }

    private fun initMedals() {
        medalsMap = mutableMapOf(
            SportType.Running to mutableMapOf(
                "distance" to mutableListOf(),
                "avgSpeed" to mutableListOf(),
                "maxSpeed" to mutableListOf()
            ),
            SportType.RollerSkate to mutableMapOf(
                "distance" to mutableListOf(),
                "avgSpeed" to mutableListOf(),
                "maxSpeed" to mutableListOf()
            ),
            SportType.Bike to mutableMapOf(
                "distance" to mutableListOf(),
                "avgSpeed" to mutableListOf(),
                "maxSpeed" to mutableListOf()
            )
        )
    }

    private fun hideMedals() = with(binding) {
        ivMedalDistance.setImageResource(0)
        ivMedalAvgSpeed.setImageResource(0)
        ivMedalMaxSpeed.setImageResource(0)
        tvMedalDistanceTitle.text = ""
        tvMedalAvgSpeedTitle.text = ""
        tvMedalMaxSpeedTitle.text = ""
    }

    private fun initPreferences() {
        preferences =
            getSharedPreferences("sharedPrefs_${userEmail}_$providerSession", MODE_PRIVATE)
        prefsEditor = preferences.edit()
        loadPreferences()
    }

    private fun loadPreferences() {
        if (preferences.getString(PREFS_USER_EMAIL, "") == userEmail) {
            val sport = preferences.getString(PREFS_SELECTED_SPORT, SportType.Running.name)!!
            selectedSport = SportType.valueOf(sport)
            selectSport(selectedSport)

            binding.intervalModeSwitch.isChecked =
                preferences.getBoolean(PREFS_IS_INTERVAL_MODE, false)
            if (binding.intervalModeSwitch.isChecked) {
                binding.durationIntervalNumberPicker.value =
                    preferences.getInt(PREFS_TOTAL_INTERVAL_TIME, 5)
                roundInterval = binding.durationIntervalNumberPicker.value * 60
                binding.runWalkSeekBar.max = roundInterval.toFloat()
                noSetMiddlePoint = true
                binding.runWalkSeekBar.progress =
                    preferences.getFloat(
                        PREFS_HARD_INTERVAL_TIME,
                        (DEFAULT_ROUND_INTERVAL / 2).toFloat()
                    )
                binding.runningTimeTextView.text =
                    preferences.getString(PREFS_RUNNING_TIME, "2: 30")
                binding.walkingTimeTextView.text =
                    preferences.getString(PREFS_WALKING_TIME, "2: 30")
                binding.intervalModeSwitch.callOnClick()
            }

            binding.hardVolumeSeekBar.progress = preferences.getInt(PREFS_HARD_VOLUME, 100)
            binding.softVolumeSeekBar.progress = preferences.getInt(PREFS_SOFT_VOLUME, 100)
            binding.notificationVolumeSeekBar.progress =
                preferences.getInt(PREFS_NOTIFY_VOLUME, 100)

        } else selectSport(SportType.Running)
    }

    private fun initWidget() {
        widget = Widget()
        mAppWidgetManager = AppWidgetManager.getInstance(mainContext)!!
        updateWidget()
    }

    private fun updateWidget() {
        widgetDistance = NumberHelper.round(distance, 1)
        widgetTime = binding.stopwatchTextView.text.toString()

        val intent = Intent(application, Widget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val ids = mAppWidgetManager.getAppWidgetIds(ComponentName(application, Widget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }

    private fun loadDatabase() {
        loadUserTotals()
        loadUserMedals()
    }

    private fun loadUserTotals() = SportType.values().forEach(::loadSportTotals)

    private fun loadUserMedals() = SportType.values().forEach { loadSportMedals(it, medalsMap) }

    private fun loadSportMedals(sport: SportType, medalsMap: MedalsMap) =
        FirestoreClient.loadSportMedals(sport, medalsMap)

    private fun loadSportTotals(sport: SportType) =
        FirestoreClient.loadSportTotals(sport, ::onSportTotalsLoaded)

    private fun loadSportLevel(sport: SportType) =
        FirestoreClient.loadSportLevel(sport, levelConditionsMap, ::onSetSportLevelLoaded)

    /***********************************************
     *           ONCLICK LISTENERS BLOCK           *
     **********************************************/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_record -> goRecordActivity()
            R.id.nav_item_signout -> logOut()
            R.id.nav_item_clearprefs -> alertClearPreferences()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun onStartOrStopClicked(view: View) {
        if (timeInSeconds == 0L && !isLocationEnabled()) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.alertGPSTitle))
                .setMessage(R.string.alertGPSDescription)
                .setPositiveButton(R.string.allowGPS)
                { _, _ -> enableLocation() }
                .setNegativeButton(R.string.cancelGPS)
                { _, _ ->
                    locationEnabled = false
                    manageRun()
                }
                .setCancelable(true)
                .show()

        } else {
            manageRun()
        }
    }

    fun onTakePictureClicked(view: View) {
        val intent = Intent(this, CameraActivity::class.java)
        val inParameter = intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        inParameter.putExtra("runDate", runDate)
        inParameter.putExtra("runStartTime", runStartTime)
        startActivity(intent)
    }

    fun onSelectBikeClicked(view: View) {
        if (timeInSeconds == 0L) selectSport(SportType.Bike)
    }

    fun onSelectRollerSkateClicked(view: View) {
        if (timeInSeconds == 0L) selectSport(SportType.RollerSkate)
    }

    fun onSelectRunningClicked(view: View) {
        if (timeInSeconds == 0L) selectSport(SportType.Running)
    }

    fun onInflateIntervalModeClicked(view: View) {
        if (binding.intervalModeSwitch.isChecked) {
            Animator.let {
                it.animateViewOfInt(
                    binding.intervalModeSwitch,
                    "textColor",
                    orangeColor,
                    STANDARD_ANIMATION_DURATION
                )
                MyLayoutManager.setLinearLayoutHeight(
                    binding.intervalModeOuterContainer,
                    LAYOUT_HEIGHT_4.toInt()
                )
                it.animateViewOfFloat(
                    binding.intervalModeInnerContainer,
                    "translationY",
                    0f,
                    STANDARD_ANIMATION_DURATION
                )
                it.animateViewOfFloat(
                    binding.stopwatchTextView,
                    "translationX",
                    -110f,
                    STANDARD_ANIMATION_DURATION
                )
                it.animateViewOfInt(
                    binding.roundsTextView,
                    "textColor",
                    whiteColor,
                    STANDARD_ANIMATION_DURATION
                )
                MyLayoutManager.setLinearLayoutHeight(
                    binding.softTrackContainer,
                    LAYOUT_HEIGHT_1.toInt()
                )
                MyLayoutManager.setLinearLayoutHeight(
                    binding.softVolumeContainer,
                    LAYOUT_HEIGHT_1.toInt()
                )
            }

            binding.roundsTextView.setText(R.string.rounds)

            if (binding.volumeSwitch.isChecked) {
                MyLayoutManager.setLinearLayoutHeight(
                    binding.volumeSettingsOuterContainer,
                    LAYOUT_HEIGHT_5.toInt()
                )
            }

            runningTime = TimeFormatter.timeToSeconds(binding.runningTimeTextView.text.toString())

        } else {

            MyLayoutManager.let {
                it.setLinearLayoutHeight(binding.intervalModeOuterContainer, 0)
                it.setLinearLayoutHeight(binding.softTrackContainer, 0)
                it.setLinearLayoutHeight(binding.softVolumeContainer, 0)
            }
            Animator.animateViewOfFloat(
                binding.stopwatchTextView,
                "translationX",
                0f,
                STANDARD_ANIMATION_DURATION
            )

            binding.intervalModeSwitch.setTextColor(whiteColor)
            binding.intervalModeInnerContainer.translationY = -LAYOUT_HEIGHT_1
            binding.roundsTextView.text = ""

            if (binding.volumeSwitch.isChecked) {
                MyLayoutManager.setLinearLayoutHeight(
                    binding.volumeSettingsOuterContainer,
                    LAYOUT_HEIGHT_3.toInt()
                )
            }
        }
    }

    fun onInflateVolumesClicked(view: View) {
        if (binding.volumeSwitch.isChecked) {
            val height =
                if (!binding.intervalModeSwitch.isChecked) LAYOUT_HEIGHT_3.toInt() else LAYOUT_HEIGHT_5.toInt()

            Animator.let {
                it.animateViewOfInt(
                    binding.volumeSwitch,
                    "textColor",
                    orangeColor,
                    STANDARD_ANIMATION_DURATION
                )
                MyLayoutManager.setLinearLayoutHeight(binding.volumeSettingsOuterContainer, height)
                it.animateViewOfFloat(
                    binding.volumeSettingsInnerContainer,
                    "translationY",
                    0f,
                    STANDARD_ANIMATION_DURATION
                )
            }

        } else {
            binding.volumeSwitch.setTextColor(whiteColor)
            MyLayoutManager.setLinearLayoutHeight(binding.volumeSettingsOuterContainer, 0)
            binding.volumeSettingsInnerContainer.translationY = -LAYOUT_HEIGHT_2
        }
    }

    fun onClosePopupClicked(view: View) {
        resetMedals()
        hidePopup()
        binding.mainLayout.isEnabled = true

        isHardInterval = true
        locationEnabled = true
        flagSavedLocation = false
        distance = 0.0
        maxSpeed = 0.0
        avgSpeed = 0.0
        speed = 0.0
        extrema = Extrema()
    }

    fun onRemoveRunClicked(view: View) {
        val doc = (userEmail + runDate + runStartTime)
            .replace("/", "")
            .replace(":", "")

        val currentRun = Run()
        currentRun.distance = NumberHelper.round(distance, 1).toDouble()
        currentRun.avgSpeed = NumberHelper.round(avgSpeed, 1).toDouble()
        currentRun.maxSpeed = NumberHelper.round(maxSpeed, 1).toDouble()
        currentRun.duration = binding.stopwatchTextView.text.toString()

        FirestoreClient.deleteRun(doc, selectedSport, binding.popupOuterContainer, currentRun)
        loadUserMedals()
        loadSportLevel(selectedSport)

        findViewById<ImageView>(R.id.ivClosePopUp).callOnClick()
    }

    fun onResetClicked(view: View) {
        savePreferences()
        saveRunData()
        updateUserTotals()
        loadSportLevel(selectedSport)
        showPopup()
        initStopwatch()
        setButtonsState(false)
        binding.stopwatchTextView.setTextColor(whiteColor)
        resetInterface()
        resetMedals()
        initMusic()
    }

    /***********************************************
     *               CALLBACKS BLOCK               *
     **********************************************/

    private fun onSetSportLevelLoaded(sport: SportType) {
        val lyNavLevel: LinearLayout
        val ivLevel: ImageView
        val tvTotalTime: TextView
        val tvTotalRuns: TextView
        val tvTotalDistance: TextView
        val tvNumberLevel: TextView
        val csbDistance: CircularSeekBar
        val csbRuns: CircularSeekBar

        when (sport) {
            SportType.Running -> {
                lyNavLevel = findViewById(R.id.lyNavLevelRun)
                ivLevel = findViewById(R.id.ivLevelRun)
                tvTotalTime = findViewById(R.id.tvTotalTimeRun)
                tvTotalRuns = findViewById(R.id.tvTotalRunsRun)
                tvTotalDistance = findViewById(R.id.tvTotalDistanceRun)
                tvNumberLevel = findViewById(R.id.tvNumberLevelRun)
                csbDistance = findViewById(R.id.csbDistanceRun)
                csbRuns = findViewById(R.id.csbRunsRun)
            }
            SportType.RollerSkate -> {
                lyNavLevel = findViewById(R.id.lyNavLevelRollerSkate)
                ivLevel = findViewById(R.id.ivLevelRollerSkate)
                tvTotalTime = findViewById(R.id.tvTotalTimeRollerSkate)
                tvTotalRuns = findViewById(R.id.tvTotalRunsRollerSkate)
                tvTotalDistance = findViewById(R.id.tvTotalDistanceRollerSkate)
                tvNumberLevel = findViewById(R.id.tvNumberLevelRollerSkate)
                csbDistance = findViewById(R.id.csbDistanceRollerSkate)
                csbRuns = findViewById(R.id.csbRunsRollerSkate)
            }
            SportType.Bike -> {
                lyNavLevel = findViewById(R.id.lyNavLevelBike)
                ivLevel = findViewById(R.id.ivLevelBike)
                tvTotalTime = findViewById(R.id.tvTotalTimeBike)
                tvTotalRuns = findViewById(R.id.tvTotalRunsBike)
                tvTotalDistance = findViewById(R.id.tvTotalDistanceBike)
                tvNumberLevel = findViewById(R.id.tvNumberLevelBike)
                csbDistance = findViewById(R.id.csbDistanceBike)
                csbRuns = findViewById(R.id.csbRunsBike)
            }
        }

        if (totalsMap[sport]!!.totalTime!! == 0) MyLayoutManager.setLinearLayoutHeight(
            lyNavLevel,
            0
        )
        else {
            MyLayoutManager.setLinearLayoutHeight(lyNavLevel, LAYOUT_HEIGHT_2.toInt())
            for (level in levelConditionsMap[sport]!!) {
                if ((totalsMap[sport]!!.totalRuns!! <= level.RunsTarget!!)
                    || (totalsMap[sport]!!.totalDistance!! <= level.DistanceTarget!!)
                ) {
                    currentLevelMap[sport]!!.name = level.name
                    currentLevelMap[sport]!!.image = level.image
                    currentLevelMap[sport]!!.RunsTarget = level.RunsTarget
                    currentLevelMap[sport]!!.DistanceTarget = level.DistanceTarget

                    break
                }
            }

            val levelText = "${getString(R.string.level)} ${
                currentLevelMap[sport]!!.image!!.subSequence(
                    6,
                    7
                )
            }"
            tvNumberLevel.text = levelText

            val totalTime =
                TimeFormatter.secondsToStringWithDate(totalsMap[sport]!!.totalTime!!.toLong())
            tvTotalTime.text = totalTime

            ivLevel.setImageResource(
                resources.getIdentifier(
                    currentLevelMap[sport]!!.image,
                    "drawable",
                    packageName
                )
            )

            tvTotalRuns.text =
                getString(
                    R.string.total_runs_placeholder, totalsMap[sport]!!.totalRuns,
                    currentLevelMap[sport]!!.RunsTarget
                )
            val percent =
                (totalsMap[sport]!!.totalDistance!! * 100 / currentLevelMap[sport]!!.DistanceTarget!!).toInt()
            tvTotalDistance.text = getString(R.string.percentage_placeholder, percent)

            csbDistance.max = currentLevelMap[sport]!!.DistanceTarget!!.toFloat()
            csbDistance.progress =
                if (totalsMap[sport]!!.totalDistance!! >= currentLevelMap[sport]!!.DistanceTarget!!.toDouble())
                    csbDistance.max
                else totalsMap[sport]!!.totalDistance!!.toFloat()

            csbRuns.max = currentLevelMap[sport]!!.RunsTarget!!.toFloat()
            csbRuns.progress =
                if (totalsMap[sport]!!.totalRuns!! >= currentLevelMap[sport]!!.RunsTarget!!)
                    csbRuns.max
                else totalsMap[sport]!!.totalRuns!!.toFloat()
        }
    }

    private fun onSportTotalsLoaded(sport: SportType) {
        sportsLoaded++
        loadSportLevel(sport)
        if (sportsLoaded == 3) selectSport(selectedSport)
    }

    /***********************************************
     *            OTHER FUNCTIONS BLOCK            *
     **********************************************/

    private fun resetMedals() = with(records) {
        distanceRecord = Records.RecordMedal.NONE
        avgSpeedRecord = Records.RecordMedal.NONE
        maxSpeedRecord = Records.RecordMedal.NONE
    }

    private fun saveRunData() {
        val id = (userEmail + runDate + runStartTime)
            .replace("/", "")
            .replace(":", "")

        val durationString = binding.stopwatchTextView.text.toString()
        val dist = NumberHelper.round(distance, 1).toDouble()
        val avgSp = NumberHelper.round(avgSpeed, 1).toDouble()
        val maxSp = NumberHelper.round(maxSpeed, 1).toDouble()
        val latitudeCenter = (extrema.minLatitude!! + extrema.maxLatitude!!) / 2
        val longitudeCenter = (extrema.minLongitude!! + extrema.maxLongitude!!) / 2

        val collection = "runs${selectedSport}"

        val medalDistance = records.distanceRecord
        val medalAvgSpeed = records.avgSpeedRecord
        val medalMaxSpeed = records.maxSpeedRecord

        FirestoreClient.saveRunData(
            collection, id, runDate, runStartTime,
            durationString, dist, avgSp, maxSp, extrema, latitudeCenter, longitudeCenter,
            binding.intervalModeSwitch.isChecked,
            binding.durationIntervalNumberPicker.value,
            binding.runningTimeTextView.text.toString(),
            binding.walkingTimeTextView.text.toString(),
            medalDistance, medalAvgSpeed, medalMaxSpeed
        )
    }

    private fun updateUserTotals() {
        totalsMap[selectedSport]!!.totalRuns = totalsMap[selectedSport]!!.totalRuns!! + 1
        totalsMap[selectedSport]!!.totalDistance =
            totalsMap[selectedSport]!!.totalDistance!! + distance
        totalsMap[selectedSport]!!.totalTime =
            totalsMap[selectedSport]!!.totalTime!! + timeInSeconds.toInt()

        if (distance > totalsMap[selectedSport]!!.recordDistance!!) {
            totalsMap[selectedSport]!!.recordDistance = distance
        }
        if (maxSpeed > totalsMap[selectedSport]!!.recordSpeed!!) {
            totalsMap[selectedSport]!!.recordSpeed = maxSpeed
        }
        if (avgSpeed > totalsMap[selectedSport]!!.recordAvgSpeed!!) {
            totalsMap[selectedSport]!!.recordAvgSpeed = avgSpeed
        }

        totalsMap[selectedSport]!!.totalDistance =
            NumberHelper.round(totalsMap[selectedSport]!!.totalDistance!!, 1).toDouble()
        totalsMap[selectedSport]!!.recordDistance =
            NumberHelper.round(totalsMap[selectedSport]!!.recordDistance!!, 1).toDouble()
        totalsMap[selectedSport]!!.recordSpeed =
            NumberHelper.round(totalsMap[selectedSport]!!.recordSpeed!!, 1).toDouble()
        totalsMap[selectedSport]!!.recordAvgSpeed =
            NumberHelper.round(totalsMap[selectedSport]!!.recordAvgSpeed!!, 1).toDouble()

        val collection = "totals${selectedSport}"

        FirestoreClient.updateTotalsAfterRunFinish(collection)
    }

    private fun showPopup() {
        binding.mainLayout.isEnabled = false
        binding.popupOuterContainer.isVisible = true
        Animator.animateViewOfFloat(
            binding.popupInnerContainer,
            "translationX",
            0f,
            FAST_ANIMATION_DURATION
        )

        showPopupData()
    }

    private fun showPopupData() {
        showPopupHeader()
        showPopupMedals()
        showPopupRunData()
    }

    private fun showPopupHeader() {
        onSetSportLevelLoaded(selectedSport)

        binding.selectedSportImageView.setImageResource(
            resources.getIdentifier(
                selectedSport.name.lowercase(),
                "mipmap",
                packageName
            )
        )

        val levelText =
            "${getString(R.string.level)} ${
                currentLevelMap[selectedSport]!!.image!!.subSequence(
                    6,
                    7
                )
            }"
        binding.numberLevelPopupTextView.text = levelText


        binding.runsLevelPopupCircularSeekBar.max =
            currentLevelMap[selectedSport]!!.RunsTarget!!.toFloat()
        binding.runsLevelPopupCircularSeekBar.progress =
            totalsMap[selectedSport]!!.totalRuns!!.toFloat()
        if (totalsMap[selectedSport]!!.totalRuns!! > currentLevelMap[selectedSport]!!.RunsTarget!!) {
            binding.runsLevelPopupCircularSeekBar.max =
                currentLevelMap[selectedSport]!!.RunsTarget!!.toFloat()
            binding.runsLevelPopupCircularSeekBar.progress =
                binding.runsLevelPopupCircularSeekBar.max
        }


        binding.distanceLevelPopupCircularSeekBar.max =
            currentLevelMap[selectedSport]!!.DistanceTarget!!.toFloat()
        binding.distanceLevelPopupCircularSeekBar.progress =
            totalsMap[selectedSport]!!.totalDistance!!.toFloat()
        if (totalsMap[selectedSport]!!.totalDistance!! > currentLevelMap[selectedSport]!!.DistanceTarget!!) {
            binding.distanceLevelPopupCircularSeekBar.max =
                currentLevelMap[selectedSport]!!.DistanceTarget!!.toFloat()
            binding.distanceLevelPopupCircularSeekBar.progress =
                binding.distanceLevelPopupCircularSeekBar.max
        }


        binding.totalRunsLevelPopupTextView.text =
            getString(
                R.string.total_runs_placeholder,
                totalsMap[selectedSport]!!.totalRuns!!,
                currentLevelMap[selectedSport]!!.RunsTarget!!
            )

        val td = totalsMap[selectedSport]!!.totalDistance!!
        var tdK = td.toString()
        if (td > 1000) tdK = (td / 1000).toInt().toString() + "K"
        val ld = currentLevelMap[selectedSport]!!.DistanceTarget!!.toDouble()
        var ldK = ld.toInt().toString()
        if (ld > 1000) ldK = (ld / 1000).toInt().toString() + "K"
        binding.totalDistancePopupTextView.text =
            getString(R.string.distance_percentage_placeholder, tdK, ldK)

        val percent =
            (totalsMap[selectedSport]!!.totalDistance!! * 100 / currentLevelMap[selectedSport]!!.DistanceTarget!!).toInt()
        binding.totalDistanceLevelPopupTextView.text =
            getString(R.string.percentage_placeholder, percent)

        binding.currentLevelImageView.setImageResource(
            resources.getIdentifier(
                currentLevelMap[selectedSport]!!.image,
                "drawable",
                packageName
            )
        )

        val time = TimeFormatter.secondsToString(totalsMap[selectedSport]!!.totalTime!!.toLong())
        binding.totalTimePopupTextView.text = getString(R.string.total_time_placeholder, time)

    }

    private fun showPopupMedals() {
        when (records.distanceRecord) {
            Records.RecordMedal.GOLD -> binding.ivMedalDistance.setImageResource(R.drawable.medalgold)
            Records.RecordMedal.BRONZE -> binding.ivMedalDistance.setImageResource(R.drawable.medalsilver)
            Records.RecordMedal.SILVER -> binding.ivMedalDistance.setImageResource(R.drawable.medalbronze)
            Records.RecordMedal.NONE -> {}
        }
        if (records.distanceRecord != Records.RecordMedal.NONE) {
            binding.tvMedalDistanceTitle.setText(R.string.medalDistanceDescription)
        }

        when (records.avgSpeedRecord) {
            Records.RecordMedal.GOLD -> binding.ivMedalAvgSpeed.setImageResource(R.drawable.medalgold)
            Records.RecordMedal.BRONZE -> binding.ivMedalAvgSpeed.setImageResource(R.drawable.medalsilver)
            Records.RecordMedal.SILVER -> binding.ivMedalAvgSpeed.setImageResource(R.drawable.medalbronze)
            Records.RecordMedal.NONE -> {}
        }
        if (records.avgSpeedRecord != Records.RecordMedal.NONE) {
            binding.tvMedalAvgSpeedTitle.setText(R.string.medalAvgSpeedDescription)
        }

        when (records.maxSpeedRecord) {
            Records.RecordMedal.GOLD -> binding.ivMedalMaxSpeed.setImageResource(R.drawable.medalgold)
            Records.RecordMedal.BRONZE -> binding.ivMedalMaxSpeed.setImageResource(R.drawable.medalsilver)
            Records.RecordMedal.SILVER -> binding.ivMedalMaxSpeed.setImageResource(R.drawable.medalbronze)
            Records.RecordMedal.NONE -> {}
        }
        if (records.maxSpeedRecord != Records.RecordMedal.NONE) {
            binding.tvMedalMaxSpeedTitle.setText(R.string.medalMaxSpeedDescription)
        }
    }

    private fun showPopupRunData() {
        binding.durationRunPopupTextView.text = binding.stopwatchTextView.text

        if (binding.intervalModeSwitch.isChecked) {
            MyLayoutManager.setLinearLayoutHeight(
                binding.intervalRunPopupContainer,
                LAYOUT_HEIGHT_1.toInt()
            )
            val details = "${binding.durationIntervalNumberPicker.value} min. (" +
                    "${binding.runningTimeTextView.text} / ${binding.walkingTimeTextView.text})"
            binding.intervalRunPopupTextView.text = details
        } else MyLayoutManager.setLinearLayoutHeight(binding.intervalRunPopupContainer, 0)

        binding.distanceRunPopupTextView.text = NumberHelper.round(distance, 2)

        if (extrema.maxAltitude == null)
            MyLayoutManager.setLinearLayoutHeight(
                binding.unevennessPopupContainer,
                LAYOUT_HEIGHT_1.toInt()
            )
        else {
            binding.maxUnevennessPopupTextView.text = extrema.maxAltitude!!.toInt().toString()
            binding.minUnevennessPopupTextView.text = extrema.minAltitude!!.toInt().toString()
        }

        binding.avgSpeedPopupTextView.text = NumberHelper.round(avgSpeed, 1)
        binding.maxSpeedPopupTextView.text = NumberHelper.round(maxSpeed, 1)

    }

    private fun alertClearPreferences() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.alertPrefsTitle))
            .setMessage(getString(R.string.alertPrefsDescription))
            .setPositiveButton(R.string.removePrefs)
            { _, _ -> clearPreferences() }
            .setNegativeButton(R.string.cancelPrefs) { _, _ -> }
            .setCancelable(true)
            .show()
    }

    private fun clearPreferences() = prefsEditor.clear().apply()

    private fun savePreferences() {
        prefsEditor.clear()
        prefsEditor.apply {
            putString(PREFS_USER_EMAIL, userEmail)
            putString(PREFS_USER_PROVIDER, providerSession)
            putString(PREFS_SELECTED_SPORT, selectedSport.name)
            putBoolean(PREFS_IS_INTERVAL_MODE, binding.intervalModeSwitch.isChecked)
            putInt(PREFS_TOTAL_INTERVAL_TIME, binding.durationIntervalNumberPicker.value)
            putFloat(PREFS_HARD_INTERVAL_TIME, binding.runWalkSeekBar.progress)
            putString(PREFS_RUNNING_TIME, binding.runningTimeTextView.text.toString())
            putString(PREFS_WALKING_TIME, binding.walkingTimeTextView.text.toString())
            putInt(PREFS_HARD_VOLUME, binding.hardVolumeSeekBar.progress)
            putInt(PREFS_SOFT_VOLUME, binding.softVolumeSeekBar.progress)
            putInt(PREFS_NOTIFY_VOLUME, binding.notificationVolumeSeekBar.progress)
        }.apply()
    }

    private fun selectSport(sport: SportType) {
        selectedSport = sport
        when (selectedSport) {
            SportType.Bike -> {
                limitDistance = DISTANCE_LIMIT_BIKE
                binding.sportBikeButton.setBackgroundColor(orangeColor)
                binding.sportRollerSkateButton.setBackgroundColor(
                    grayMediumColor
                )
                binding.sportRunButton.setBackgroundColor(grayMediumColor)
            }
            SportType.RollerSkate -> {
                limitDistance = DISTANCE_LIMIT_ROLLERSKATE
                binding.sportBikeButton.setBackgroundColor(grayMediumColor)
                binding.sportRollerSkateButton.setBackgroundColor(orangeColor)
                binding.sportRunButton.setBackgroundColor(grayMediumColor)
            }
            SportType.Running -> {
                limitDistance = DISTANCE_LIMIT_RUN
                binding.sportBikeButton.setBackgroundColor(grayMediumColor)
                binding.sportRollerSkateButton.setBackgroundColor(
                    grayMediumColor
                )
                binding.sportRunButton.setBackgroundColor(orangeColor)
            }
        }

        refreshSportSeekBars()
        refreshRecords()
    }

    private fun refreshSportSeekBars() = with(binding) {
        recordDistanceSeekBar.max = totalsMap[selectedSport]!!.recordDistance?.toFloat()!!
        recordDistanceSeekBar.progress =
            totalsMap[selectedSport]!!.recordDistance?.toFloat()!!

        recordAvgSpeedSeekBar.max = totalsMap[selectedSport]!!.recordAvgSpeed?.toFloat()!!
        recordAvgSpeedSeekBar.progress =
            totalsMap[selectedSport]!!.recordAvgSpeed?.toFloat()!!

        recordSpeedSeekBar.max = totalsMap[selectedSport]!!.recordSpeed?.toFloat()!!
        recordSpeedSeekBar.progress = totalsMap[selectedSport]!!.recordSpeed?.toFloat()!!

        currentDistanceSeekBar.max = recordDistanceSeekBar.max
        currentAvgSpeedSeekBar.max = recordAvgSpeedSeekBar.max
        currentSpeedSeekBar.max = recordSpeedSeekBar.max
        currentMaxSpeedSeekBar.max = recordSpeedSeekBar.max
        currentMaxSpeedSeekBar.progress = 0f
    }

    private fun refreshRecords() {
        binding.distanceRecordTextView.text = if (totalsMap[selectedSport]!!.recordDistance!! > 0)
            totalsMap[selectedSport]!!.recordDistance.toString()
        else ""
        binding.avgSpeedRecordTextView.text = if (totalsMap[selectedSport]!!.recordAvgSpeed!! > 0)
            totalsMap[selectedSport]!!.recordAvgSpeed.toString()
        else ""
        binding.maxSpeedRecordTextView.text = if (totalsMap[selectedSport]!!.recordSpeed!! > 0)
            totalsMap[selectedSport]!!.recordSpeed.toString()
        else ""
    }

    private fun checkPermission() =
        LOCATION_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun requestLocationPermissions() =
        ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOCATION_PERMISSION_ID)

    private fun enableLocation() =
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

    private fun manageRun() {
        if (timeInSeconds == 0L) { // First start

            runDate = SimpleDateFormat("yyyy/MM/dd").format(Date())
            runStartTime = SimpleDateFormat("HH:mm:ss").format(Date())

            binding.cameraButton.isVisible = true
            setConfigsEnabled(false)
            binding.stopwatchTextView.setTextColor(chronoRunningColor)
            hardMediaPlayer?.start()

            if (locationEnabled) {
                // The manageLocation call is double to reset the last location data
                flagSavedLocation = false
                manageLocation()
                manageLocation()
            }
        }

        if (!isRunStarted) {
            unpause()
            // anti null pointer exception system
        } else if (timeInSeconds > LOCATION_INTERVAL + 1) {
            pause()
        }
    }

    private fun pause() {
        isRunStarted = false
        stopStopwatch()
        setButtonsState(true)
        if (isHardInterval) hardMediaPlayer?.pause() else softMediaPlayer?.pause()
    }

    private fun unpause() {
        isRunStarted = true
        startStopwatch()
        setButtonsState(false)
        if (isHardInterval) hardMediaPlayer?.start() else softMediaPlayer?.start()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun resetInterface() {
        setConfigsEnabled(true)

        with(binding) {
            cameraButton.isVisible = false

            currentDistanceTextView.text = "0.0"
            currentAvgSpeedTextView.text = "0.0"
            currentSpeedTextView.text = "0.0"

            distanceRecordTextView.setTextColor(grayColor)
            maxSpeedRecordTextView.setTextColor(grayColor)
            avgSpeedRecordTextView.setTextColor(grayColor)

            currentDistanceSeekBar.progress = 0f
            currentAvgSpeedSeekBar.progress = 0f
            currentMaxSpeedSeekBar.progress = 0f
            currentSpeedSeekBar.progress = 0f

            stopwatchProgressBar.translationX = -animationsWidth.toFloat()
            roundProgressBar.translationX = -animationsWidth.toFloat()

            roundsTextView.text = getString(R.string.rounds)

            hardMusicSeekBar.progress = 0
            softMusicSeekBar.progress = 0
            hardMusicElapseTime.text = getString(R.string.hard_position)
            softMusicElapseTime.text = getString(R.string.soft_position)
        }
    }

    private fun logOut() {
        pause()
        userEmail = ""
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun goRecordActivity() {
        if (isRunStarted) {
            findViewById<LinearLayout>(R.id.startButton).callOnClick()
        }
        startActivity(Intent(this, RecordActivity::class.java))
    }

    private fun startStopwatch() {
        mHandler = Handler(Looper.getMainLooper())
        stopwatchRunnable.run()
    }

    private fun stopStopwatch() = mHandler?.removeCallbacks(stopwatchRunnable)

    private var stopwatchRunnable = object : Runnable {
        override fun run() {
            try {
                if (hardMediaPlayer!!.isPlaying) {
                    binding.hardMusicSeekBar.progress++
                }

                if (softMediaPlayer!!.isPlaying)
                    binding.softMusicSeekBar.progress++

                updateTracksTime()

                if (locationEnabled && timeInSeconds % LOCATION_INTERVAL == 0L) {
                    manageLocation()
                }

                if (binding.intervalModeSwitch.isChecked) {
                    checkStopRun()
                    checkNewRound()
                }

                timeInSeconds++
                updateStopwatchView()
                updateWidget()

            } finally {
                mHandler!!.postDelayed(this, STOPWATCH_INTERVAL)
            }
        }
    }

    private fun manageLocation() {
        if (checkPermission()) {
            locationClient.lastLocation.addOnSuccessListener {
                requestNewLocationData()
            }
        } else {
            requestLocationPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val request = LocationRequest().also {
            it.priority = PRIORITY_HIGH_ACCURACY
            it.interval = 0
            it.fastestInterval = 0
            it.numUpdates = 1
        }
        locationClient.requestLocationUpdates(request, locationCallBack, Looper.myLooper())
    }

    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val lastLocation = result.lastLocation
            lastLat = lastLocation!!.latitude
            lastLong = lastLocation.longitude

            if (timeInSeconds > 0L) {
                saveNewLocation(lastLocation)
            }
        }
    }

    private fun saveNewLocation(location: Location) {
        val newLatitude = location.latitude
        val newLongitude = location.longitude

        if (flagSavedLocation) {
            if (timeInSeconds >= LOCATION_INTERVAL) {
                val intervalDistance = calculateDistance(newLatitude, newLongitude)
                updateRunSpeed(intervalDistance)
                refreshCurrentRunInterface()
                saveLocationToDatabase(location)
                checkMedals()
            }
        }

        flagSavedLocation = true

        actLatitude = newLatitude
        actLongitude = newLongitude

        if (extrema.minLatitude == null) {
            extrema.minLatitude = actLatitude
            extrema.maxLatitude = actLatitude
            extrema.minLongitude = actLongitude
            extrema.maxLongitude = actLongitude
        }
        if (actLatitude < extrema.minLatitude!!) extrema.minLatitude = actLatitude
        if (actLatitude > extrema.maxLatitude!!) extrema.maxLatitude = actLatitude
        if (actLongitude < extrema.minLongitude!!) extrema.minLongitude = actLongitude
        if (actLongitude > extrema.maxLongitude!!) extrema.maxLongitude = actLongitude

        if (location.hasAltitude()) {
            if (extrema.maxAltitude == null) {
                extrema.maxAltitude = location.altitude
                extrema.minAltitude = location.altitude
            }
            if (location.altitude > extrema.maxAltitude!!) extrema.maxAltitude = location.altitude
            if (location.altitude < extrema.minAltitude!!) extrema.minAltitude = location.altitude
        }

    }

    private fun checkMedals() {
        if (medalsMap[selectedSport]!!["distance"]!!.size > 0
            || medalsMap[selectedSport]!!["avgSpeed"]!!.size > 0
            || medalsMap[selectedSport]!!["maxSpeed"]!!.size > 0
        ) {
            if (distance > 0) {
                if (distance >= medalsMap[selectedSport]!!["distance"]!![0]) {
                    records.distanceRecord = Records.RecordMedal.GOLD
                } else if (distance >= medalsMap[selectedSport]!!["distance"]!![1]) {
                    records.distanceRecord = Records.RecordMedal.SILVER
                } else if (distance >= medalsMap[selectedSport]!!["distance"]!![2]) {
                    records.distanceRecord = Records.RecordMedal.BRONZE
                }
            }

            if (avgSpeed > 0) {
                if (avgSpeed >= medalsMap[selectedSport]!!["avgSpeed"]!![0]) {
                    records.distanceRecord = Records.RecordMedal.GOLD
                } else if (avgSpeed >= medalsMap[selectedSport]!!["avgSpeed"]!![1]) {
                    records.avgSpeedRecord = Records.RecordMedal.SILVER
                } else if (avgSpeed >= medalsMap[selectedSport]!!["avgSpeed"]!![2]) {
                    records.avgSpeedRecord = Records.RecordMedal.BRONZE
                }
            }

            if (maxSpeed > 0) {
                if (maxSpeed >= medalsMap[selectedSport]!!["maxSpeed"]!![0]) {
                    records.maxSpeedRecord = Records.RecordMedal.GOLD
                } else if (maxSpeed >= medalsMap[selectedSport]!!["maxSpeed"]!![1]) {
                    records.maxSpeedRecord = Records.RecordMedal.SILVER
                } else if (maxSpeed >= medalsMap[selectedSport]!!["maxSpeed"]!![2]) {
                    records.maxSpeedRecord = Records.RecordMedal.BRONZE
                }
            }
        }
    }

    private fun saveLocationToDatabase(location: Location) {
        val path = (runDate + runStartTime)
            .replace("/", "")
            .replace(":", "")
        val doc = String.format("%04d", timeInSeconds)
        val maxSpeed = speed == maxSpeed && speed > 0

        FirestoreClient.saveLocation(
            path,
            doc,
            location,
            speed,
            maxSpeed,
            binding.stopwatchTextView.currentTextColor
        )
    }

    private fun refreshCurrentRunInterface() {
        binding.currentDistanceTextView.text = NumberHelper.round(distance, 2)
        binding.currentSpeedTextView.text = NumberHelper.round(speed, 1)
        binding.currentAvgSpeedTextView.text = NumberHelper.round(avgSpeed, 1)

        binding.currentDistanceSeekBar.progress = distance.toFloat()

        if (distance > totalsMap[selectedSport]!!.recordDistance!!) {
            binding.distanceRecordTextView.text = NumberHelper.round(distance, 1)
            binding.distanceRecordTextView.setTextColor(salmonDarkColor)
            totalsMap[selectedSport]!!.recordDistance = distance
            binding.currentDistanceSeekBar.max = distance.toFloat()
            binding.currentDistanceSeekBar.progress = distance.toFloat()
            recordMediaPlayer?.start()
        }

        binding.currentAvgSpeedSeekBar.progress = avgSpeed.toFloat()

        if (avgSpeed > totalsMap[selectedSport]!!.recordAvgSpeed!!) {
            binding.avgSpeedRecordTextView.text = NumberHelper.round(avgSpeed, 1)
            binding.avgSpeedRecordTextView.setTextColor(salmonDarkColor)
            totalsMap[selectedSport]!!.recordAvgSpeed = avgSpeed
            binding.recordAvgSpeedSeekBar.max = avgSpeed.toFloat()
            binding.recordAvgSpeedSeekBar.progress = avgSpeed.toFloat()
            binding.currentAvgSpeedSeekBar.max = avgSpeed.toFloat()
            binding.currentAvgSpeedSeekBar.progress = avgSpeed.toFloat()
            recordMediaPlayer?.start()
        }

        binding.currentSpeedSeekBar.progress = speed.toFloat()

        if (speed > totalsMap[selectedSport]!!.recordSpeed!!) {
            binding.maxSpeedRecordTextView.text = NumberHelper.round(speed, 1)
            binding.maxSpeedRecordTextView.setTextColor(salmonDarkColor)
            totalsMap[selectedSport]!!.recordSpeed = speed
            binding.recordSpeedSeekBar.max = speed.toFloat()
            binding.recordSpeedSeekBar.progress = speed.toFloat()
            binding.currentMaxSpeedSeekBar.max = speed.toFloat()
            binding.currentMaxSpeedSeekBar.progress = speed.toFloat()
            binding.currentSpeedSeekBar.max = speed.toFloat()
            binding.currentSpeedSeekBar.progress = speed.toFloat()
            recordMediaPlayer?.start()

        } else if (speed == maxSpeed) {
            binding.currentMaxSpeedSeekBar.max = speed.toFloat()
            binding.currentMaxSpeedSeekBar.progress = speed.toFloat()
            binding.currentSpeedSeekBar.max = binding.recordSpeedSeekBar.max
        }

    }

    private fun updateRunSpeed(d: Double) {
        speed = ((d * 3600) / LOCATION_INTERVAL)
        if (speed > maxSpeed) maxSpeed = speed
        avgSpeed = ((distance * 3600) / timeInSeconds)
    }

    private fun calculateDistance(newLat: Double, newLong: Double): Double {
        val earthRadius = 6371.0

        val dLat = Math.toRadians(newLat - actLatitude)
        val dLng = Math.toRadians(newLong - actLongitude)
        val sinDLat = sin(dLat / 2.0)
        val sinDLng = sin(dLng / 2.0)
        val va1 =
            sinDLat.pow(2.0) + (sinDLng.pow(2.0)
                    * cos(Math.toRadians(actLatitude)) * cos(
                Math.toRadians(newLat)
            ))
        val va2 = 2 * atan2(sqrt(va1), sqrt(1 - va1))
        val newDistance = earthRadius * va2

        if (newDistance < limitDistance) distance += newDistance

        return newDistance
    }

    private fun checkStopRun() {
        var seconds = timeInSeconds
        while (seconds > roundInterval) seconds -= roundInterval

        if (seconds.toInt() == runningTime) {
            binding.stopwatchTextView.setTextColor(chronoWalkingColor)
            binding.roundProgressBar.setBackgroundColor(chronoWalkingColor)
            binding.roundProgressBar.translationX = -animationsWidth.toFloat()

            hardMediaPlayer?.pause()
            notifyMediaPlayer?.start()
            softMediaPlayer?.start()
            isHardInterval = false

        } else {
            updateRoundProgressBar()
        }
    }

    private fun updateRoundProgressBar() {
        var seconds = timeInSeconds.toInt()
        while (seconds >= roundInterval) seconds -= roundInterval
        seconds++

        if (binding.stopwatchTextView.currentTextColor == chronoRunningColor) {
            val movement =
                -1 * (animationsWidth - (seconds * animationsWidth / runningTime)).toFloat()
            Animator.animateViewOfFloat(
                binding.roundProgressBar,
                "translationX",
                movement,
                SLOW_ANIMATION_DURATION
            )
        } else if (binding.stopwatchTextView.currentTextColor == chronoWalkingColor) {
            seconds -= runningTime
            val movement =
                -1 * (animationsWidth - (seconds * animationsWidth / (roundInterval - runningTime))).toFloat()
            Animator.animateViewOfFloat(
                binding.roundProgressBar,
                "translationX",
                movement,
                SLOW_ANIMATION_DURATION
            )
        }
    }

    private fun checkNewRound() {
        if (timeInSeconds % roundInterval == 0L && timeInSeconds > 0L) {
            rounds++
            binding.roundsTextView.text = getString(R.string.round_placeholder, rounds)

            binding.stopwatchTextView.setTextColor(chronoRunningColor)
            binding.roundProgressBar.setBackgroundColor(chronoRunningColor)
            binding.roundProgressBar.translationX = -animationsWidth.toFloat()

            softMediaPlayer?.pause()
            notifyMediaPlayer?.start()
            hardMediaPlayer?.start()
            isHardInterval = true

        } else updateRoundProgressBar()
    }

    private fun setButtonsState(isReset: Boolean) {
        binding.resetTextView.isEnabled = isReset
        binding.startButton.isEnabled = true

        if (isReset) {
            binding.resetTextView.setBackgroundColor(greenColor)
            Animator.animateViewOfFloat(
                binding.resetTextView,
                "translationY",
                0f,
                STANDARD_ANIMATION_DURATION
            )
        } else {
            binding.resetTextView.setBackgroundColor(grayColor)
            Animator.animateViewOfFloat(
                binding.resetTextView,
                "translationY",
                150f,
                STANDARD_ANIMATION_DURATION
            )
        }

        if (isRunStarted) {
            binding.startButton.background =
                AppCompatResources.getDrawable(this, R.drawable.circle_background_topause)
            binding.startButtonTextView.text = getString(R.string.stop)
        } else {
            binding.startButton.background =
                AppCompatResources.getDrawable(this, R.drawable.circle_background_toplay)
            binding.startButtonTextView.text = getString(R.string.start)
        }
    }

    private fun setConfigsEnabled(state: Boolean) {
        with(binding) {
            intervalModeSwitch.isClickable = state
            durationIntervalNumberPicker.isEnabled = state
            runWalkSeekBar.isEnabled = state
        }
    }

    private fun updateStopwatchView() {
        binding.stopwatchTextView.text = TimeFormatter.secondsToString(timeInSeconds)
    }

    private fun alertLogOut() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.alertLogoutTitle))
            .setMessage(getString(R.string.alertLogoutDescription))
            .setPositiveButton(R.string.logoutOk) { _, _ ->
                logOut()
            }
            .setNegativeButton(R.string.cancelGPS) { _, _ ->
            }
            .setCancelable(true)
            .show()
    }
}

enum class SportType {
    Running, RollerSkate, Bike
}