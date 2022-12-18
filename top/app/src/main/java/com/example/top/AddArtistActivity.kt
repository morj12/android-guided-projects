package com.example.top

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.top.databinding.ActivityAddArtistBinding
import java.text.SimpleDateFormat
import java.util.*

class AddArtistActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityAddArtistBinding
    private lateinit var artist: Artist
    private lateinit var calendar: Calendar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArtistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etName.requestFocus()
        configActionBar()
        configArtist(intent)
        configCalendar()
        configImageButtons()
    }

    private fun configImageButtons() {
        binding.imgFromUrl.setOnClickListener(::onImgButtonClicked)
        binding.imgFromGallery.setOnClickListener(::onImgButtonClicked)
        binding.imgDeletePhoto.setOnClickListener(::onImgButtonClicked)
    }

    private fun onImgButtonClicked(view: View) {
        when (view.id) {
            R.id.imgDeletePhoto -> println()
            R.id.imgFromGallery -> println()
            R.id.imgFromUrl -> showAddPhotoDialog()
        }

    }

    private fun showAddPhotoDialog() {
        val etPhotoUrl = EditText(this)
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.addArtist_dialogUrl_title)
            .setPositiveButton(
                R.string.label_dialog_data
            ) { _, _ -> configureView(etPhotoUrl.text.toString().trim()) }
            .setNegativeButton(R.string.label_dialog_cancel) { _, _ -> }
        builder.setView(etPhotoUrl)
        builder.show()
    }

    private fun configureView(url: String) {
        if (url != "") {
            val options = RequestOptions().also {
                it.diskCacheStrategy(DiskCacheStrategy.ALL)
                it.centerCrop()
            }
            Glide.with(this)
                .load(url)
                .apply(options)
                .into(binding.imgPhoto)
        }

        artist.photoUrl = url
    }


    private fun configActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun configArtist(intent: Intent) {
        artist = Artist()
        artist.birthDate = System.currentTimeMillis()
        artist.order = intent.getIntExtra(Artist.ORDER, 0)
    }

    private fun configCalendar() {
        calendar = Calendar.getInstance(Locale.ROOT)
        binding.etBirthdate.setText(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.ROOT
            ).format(System.currentTimeMillis())
        )
        binding.etBirthdate.setOnTouchListener(::onBirthdateTouched)

    }

    private fun onBirthdateTouched(view: View, motionEvent: MotionEvent): Boolean {
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            val selectorDate = DialogSelectorDate()
            selectorDate.listener = this
            val args = Bundle()
            args.putLong(DialogSelectorDate.DATE, artist.birthDate)
            selectorDate.arguments = args
            selectorDate.show(fragmentManager, DialogSelectorDate.SELECTED_DATE)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.action_save -> {
            saveArtist()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun saveArtist() {
        if (fieldsValidated()) {
            artist.name = binding.etName.text.toString().trim()
            artist.surname = binding.etSurname.text.toString().trim()
            artist.height = binding.etHeight.text.toString().trim().toShort()
            artist.birthPlace = binding.etBirthPlace.text.toString().trim()
            artist.notes = binding.notes.text.toString().trim()
            artist.order = artist.order
            artist.photoUrl = artist.photoUrl
            artist.birthDate = artist.birthDate

            val intent = Intent()
            intent.putExtra(Artist.NAME, artist.name)
            intent.putExtra(Artist.SURNAME, artist.surname)
            intent.putExtra(Artist.HEIGHT, artist.height)
            intent.putExtra(Artist.BIRTH_PLACE, artist.birthPlace)
            intent.putExtra(Artist.NOTES, artist.notes)
            intent.putExtra(Artist.ORDER, artist.order)
            intent.putExtra(Artist.PHOTO_URL, artist.photoUrl)
            intent.putExtra(Artist.BIRTH_DATE, artist.birthDate)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun fieldsValidated(): Boolean {
        var isValid = true

        val height = binding.etHeight.text.toString()

        val incorrectHeight = height.isEmpty()      // not null
                || height.any { !it.isDigit() }     // not number
                || height.toInt() <= 0              // negative number

        if (incorrectHeight) {
            binding.etHeight.error = (getString(R.string.addArtist_error_minimumHeight))
            binding.etHeight.requestFocus()
            isValid = false
        }

        val nullSurname = binding.etSurname.text.toString().trim().isEmpty()

        if (nullSurname) {
            binding.etSurname.error = getString(R.string.addArtist_error_required)
            binding.etSurname.requestFocus()
            isValid = false
        }

        val nullName = binding.etName.text.toString().trim().isEmpty()

        if (nullName) {
            binding.etName.error = getString(R.string.addArtist_error_required)
            binding.etName.requestFocus()
            isValid = false
        }

        return isValid
    }

    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
        /**
         * Called twice for some reason
         */
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        binding.etBirthdate.setText(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.ROOT
            ).format(calendar.timeInMillis)
        )
        artist.birthDate = calendar.timeInMillis
    }
}