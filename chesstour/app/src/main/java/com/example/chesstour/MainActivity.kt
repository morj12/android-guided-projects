package com.example.chesstour

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getColor
import androidx.test.runner.screenshot.ScreenCapture
import androidx.test.runner.screenshot.Screenshot.capture
import com.example.chesstour.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    companion object {
        const val DIMENSION = 8
    }

    // Game vars
    private lateinit var board: Board
    private var isPlaying = true
    private var mHandler: Handler? = null
    private var level = 1
    private var time = -1
    private var lastSelectedCellX: Int by Delegates.notNull()
    private var lastSelectedCellY: Int by Delegates.notNull()

    private var moves = DIMENSION.pow(2)
    private var options = 0

    // Share run vars
    private var bitmap: Bitmap? = null
    private lateinit var shareMessage: String

    // Colors
    private var blackColor: Int by Delegates.notNull()
    private var whiteColor: Int by Delegates.notNull()
    private var lastCellColor: Int by Delegates.notNull()
    private var selectedCellColor: Int by Delegates.notNull()

    // Shared prefs
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var binding: ActivityMainBinding
    private lateinit var tableLayout: TableLayout


    private var chronometer = object : Runnable {
        override fun run() {
            try {
                if (isPlaying) {
                    time++
                    updateTimeView(time)
                }
            } finally {
                mHandler!!.postDelayed(this, 1000L)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGame()
        startGame()
    }

    private fun initGame() {
        initColors()
        initBoardSize()
        initPreferences()
        initLevel()
    }

    private fun initPreferences() {
        preferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        editor = preferences.edit()
    }

    private fun initLevel() {
        val localLevel = preferences.getInt("level", -1)
        if (localLevel != -1) level = localLevel
    }

    private fun initColors() {
        blackColor = getColor(this, R.color.black_cell)
        whiteColor = getColor(this, R.color.white_cell)
        lastCellColor = getColor(this, R.color.last_cell)
        selectedCellColor = getColor(this, R.color.selected_cell)
    }

    private fun initBoardSize() {
        tableLayout = binding.boardView

        // Get cell size
        val width = windowManager.defaultDisplay.width
        val dpWidth = (width / resources.displayMetrics.density)
        val cellSize = (dpWidth / 8)

        // Create cells
        for (i in 0..7) {
            val tableRow = TableRow(this)
            for (j in 0..7) {
                val cell = ImageView(this)
                val typedSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    cellSize,
                    resources.displayMetrics
                ).toInt()
                cell.layoutParams = TableRow.LayoutParams(typedSize, typedSize)
                cell.tag = "c$i$j"
                cell.setBackgroundColor(if ((i + j) % 2 == 0) whiteColor else (blackColor))
                cell.setOnClickListener(::onCellClicked)
                tableRow.addView(cell)
            }
            tableLayout.addView(tableRow)
        }
    }

    private fun startGame() {
        moves = DIMENSION.pow(2)
        isPlaying = true
        setMessageVisible(false)

        findViewById<TextView>(R.id.levelNumberView).text = level.toString()

        clearLogicBoard()
        clearGraphicBoard()

        setFirstPosition()

        resetTime()
        startTime()
    }

    private fun setMessage(firstMessage: String, secondMessage: String, victory: Boolean) {
        isPlaying = false
        val score: String

        findViewById<TextView>(R.id.mainMessageView).text = firstMessage

        val time = findViewById<TextView>(R.id.timerView)

        score = if (victory) {
            shareMessage = "I won! https://kurilkaeao.com/"
            time.text.toString()
        } else {
            shareMessage = "I lost! https://kurilkaeao.com/"
            "Score: ${DIMENSION.pow(2) - moves} / ${DIMENSION.pow(2)}"
        }

        binding.secondaryMessageView.text = score

        binding.actionView.text = secondMessage

    }

    private fun setMessageVisible(visible: Boolean) {
        binding.messageView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    private fun clearLogicBoard() {
        board = Board(DIMENSION)
    }

    private fun clearGraphicBoard() {
        var cell: ImageView

        for (i in 0..7) {
            for (j in 0..7) {
                cell = tableLayout.findViewWithTag("c$i$j")
                cell.setImageResource(0)
                if (board.isBlackCell(i, j)) cell.setBackgroundColor(blackColor)
                else cell.setBackgroundColor(whiteColor)
            }
        }
    }

    private fun setFirstPosition() {
        val r = Random()
        val x = r.nextInt(8)
        val y = r.nextInt(8)

        lastSelectedCellX = x
        lastSelectedCellY = y

        selectCell(x, y)
    }


    private fun paintPiece(x: Int, y: Int, color: Int) {
        val cell = tableLayout.findViewWithTag<ImageView>("c$x$y")
        cell.setBackgroundColor(color)
        cell.setImageResource(R.drawable.knight)
    }

    private fun clearOptions() {
        options = 0

        for (i in 0..7) {
            for (j in 0..7) {
                if (board.isOption(i, j)) {
                    board.setEmpty(i, j)
                    clearGraphicOption(i, j)
                }
            }
        }
    }

    private fun clearGraphicOption(x: Int, y: Int) {
        val cell = tableLayout.findViewWithTag<ImageView>("c$x$y")
        if (board.isPiece(x, y)) {
            cell.setBackgroundColor(lastCellColor)
        } else if (board.isBlackCell(x, y)) {
            cell.setBackgroundColor(blackColor)
        } else {
            cell.setBackgroundColor(whiteColor)
        }

    }

    private fun setOptions(x: Int, y: Int) {
        val options = board.getOptions(x, y)

        options.forEach {
            board.setOption(it.first, it.second)
            paintOption(it.first, it.second)
            this.options++
        }

        findViewById<TextView>(R.id.optionsCountView).text = this.options.toString()
    }

    private fun isGameOver() {
        if (moves > 0) {
            if (options == 0) {
                setMessageVisible(true)
                setMessage("Game over", "Try again!", false)
            }
        } else {
            setMessageVisible(true)
            level++
            editor.apply {
                putInt("level", level)
            }.apply()
            setMessage("You win!", "Next level", true)
        }
    }

    private fun paintOption(x: Int, y: Int) {
        val cell = tableLayout.findViewWithTag<ImageView>("c$x$y")
        if (board.isBlackCell(x, y)) {
            cell.setBackgroundResource(R.drawable.option_black)
        } else {
            cell.setBackgroundResource(R.drawable.option_white)
        }
    }

    /*********
     * ONCLICK
     *********/

    private fun onCellClicked(view: View) {
        val name = view.tag.toString()
        val x = name[1].digitToInt()
        val y = name[2].digitToInt()
        if (board.isValidMove(lastSelectedCellX, lastSelectedCellY, x, y)) selectCell(x, y)
    }

    private fun selectCell(x: Int, y: Int) {
        moves--
        binding.movesCountView.text = moves.toString()

        board.setPiece(x, y)
        paintPiece(lastSelectedCellX, lastSelectedCellY, lastCellColor)
        paintPiece(x, y, selectedCellColor)
        lastSelectedCellX = x
        lastSelectedCellY = y
        clearOptions()
        setOptions(x, y)

        isGameOver()
    }

    fun onActionClicked(view: View) {
        setMessageVisible(false)
        startGame()
    }

    fun onShareClicked(view: View) {
        shareGame()
    }

    /******************
     * Share functions
     ******************/

    private fun shareGame() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        val capture: ScreenCapture = capture(this)
        bitmap = capture.bitmap

        if (bitmap != null) {
            var name = SimpleDateFormat("yyyy/MM/dd").format(Date())
            name = name.replace(":", "").replace("/", "")

            val path = saveImage(bitmap!!, "$name.jpg")
            val bmpUri = Uri.parse(path)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            shareIntent.type = "image/jpg"

            val finalShareIntent =
                Intent.createChooser(shareIntent, "Select the app you want to share the game to")
            finalShareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(finalShareIntent)
        }
    }

    private fun saveImage(bitmap: Bitmap, fileName: String): String? {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/scr")
            }

            val uri = this.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            if (uri != null) {
                this.contentResolver.openOutputStream(uri).use {
                    if (it == null) return@use

                    bitmap.compress(Bitmap.CompressFormat.PNG, 85, it)
                    it.flush()
                    it.close()
                    MediaScannerConnection.scanFile(
                        this,
                        arrayOf(uri.toString()),
                        null,
                        null
                    )
                }
            }

            return uri.toString()
        }

        val filePath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES + "/scr"
        ).absolutePath

        val dir = File(filePath)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        val fOut = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut)
        fOut.flush()
        fOut.close()

        MediaScannerConnection.scanFile(
            this,
            arrayOf(file.toString()),
            null,
            null
        )

        return filePath
    }

    private fun resetTime() {
        mHandler?.removeCallbacks(chronometer)
        time = -1

        binding.timerView.text = "00:00"
    }

    private fun startTime() {
        mHandler = Handler(Looper.getMainLooper())
        chronometer.run()
    }

    private fun updateTimeView(time: Int) {
        val formattedTime = getFormattedTime((time * 1000L))
        binding.timerView.text = formattedTime
    }

    private fun getFormattedTime(ms: Long): String {
        var millis = ms
        val min = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(min)
        val sec = TimeUnit.MILLISECONDS.toSeconds(millis)

        return "${if (min < 10) "0" else ""}$min:" +
                "${if (sec < 10) "0" else ""}$sec"
    }
}

private fun Int.pow(i: Int) = this.toFloat().pow(i).toInt()
