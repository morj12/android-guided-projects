package com.example.top.activity

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
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.top.R
import com.example.top.database.artist.Artist
import com.example.top.database.artist.ArtistRepository
import com.example.top.databinding.ActivityAddArtistBinding
import com.example.top.util.ArtistValidator
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
        initActionBar()
        initArtist(intent)
        initCalendar()
        initImageListeners()
    }

    private fun initActionBar() =
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    private fun initArtist(intent: Intent) {
        artist = Artist(0)
        artist.birthDate = System.currentTimeMillis()
        artist.order = intent.getIntExtra(Artist.ORDER, 0)
    }

    private fun initCalendar() {
        calendar = Calendar.getInstance(Locale.ROOT)
        binding.etBirthdate.setText(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.ROOT
            ).format(System.currentTimeMillis())
        )
        binding.etBirthdate.setOnTouchListener(::onBirthDateTouched)
    }

    private fun initImageListeners() {
        binding.imgFromUrl.setOnClickListener(::onImgButtonClicked)
        binding.imgDeletePhoto.setOnClickListener(::onImgButtonClicked)
    }

    private fun onImgButtonClicked(view: View) {
        when (view.id) {
            R.id.imgDeletePhoto -> deletePhoto()
            R.id.imgFromUrl -> showAddPhotoDialog()
        }
    }

    private fun deletePhoto() {
        AlertDialog.Builder(this)
            .setTitle(R.string.details_dialog_delete_title)
            .setMessage(getString(R.string.details_dialog_delete_message, artist.name))
            .setPositiveButton(R.string.details_dialog_delete_delete)
            { _, _ -> initImageView("") }
            .setNegativeButton(R.string.label_dialog_cancel, null)
            .show()
    }

    private fun showAddPhotoDialog() {
        val etPhotoUrl = EditText(this)
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.addArtist_dialogUrl_title)
            .setPositiveButton(
                R.string.label_dialog_data
            ) { _, _ -> initImageView(etPhotoUrl.text.toString().trim()) }
            .setNegativeButton(R.string.label_dialog_cancel, null)
        builder.setView(etPhotoUrl)
        builder.show()
    }

    private fun initImageView(url: String) {
        if (url != "") {
            val options = RequestOptions().also {
                it.diskCacheStrategy(DiskCacheStrategy.ALL)
                it.centerCrop()
            }
            Glide.with(this)
                .load(url)
                .apply(options)
                .into(binding.imgPhotoArtist)
        } else {
            binding.imgPhotoArtist.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_photo_size_select_actual
                )
            )
        }
        artist.photoUrl = url
    }

    private fun onBirthDateTouched(view: View, motionEvent: MotionEvent): Boolean {
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
        if (ArtistValidator.validate(
                binding.etHeight,
                (getString(R.string.addArtist_error_minimumHeight)),
                binding.etSurname,
                getString(R.string.addArtist_error_required),
                binding.etName,
                getString(R.string.addArtist_error_required)
            )
        ) {
            artist.name = binding.etName.text.toString().trim()
            artist.surname = binding.etSurname.text.toString().trim()
            artist.height = binding.etHeight.text.toString().trim().toShort()
            artist.birthPlace = binding.etBirthPlace.text.toString().trim()
            artist.notes = binding.notes.text.toString().trim()
            ArtistRepository.addArtist(artist)
            finish()
        }
    }

    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
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