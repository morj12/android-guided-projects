package com.example.top

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.icu.util.Calendar
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.top.databinding.ActivityDetailsBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

class DetailsActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var artist: Artist
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var calendar: Calendar
    private lateinit var menuItem: MenuItem

    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadArtist()
        configArtist()
        configActionBar()
        configImageView(artist.photoUrl)
        configCalendar()
        configFloatingActionButton()
        binding.etBirthDateDetail.setOnTouchListener(::onBirthDateClicked)
        configImageButtons()
    }

    private fun loadArtist() {
        val extras = intent.extras
        artist = ArtistRepository.getArtist(extras?.getLong(Artist.ID)!!)
    }

    private fun configArtist() {
        binding.etNameDetail.setText(artist.name)
        binding.etSurnameDetail.setText(artist.surname)
        binding.etBirthDateDetail.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
                .format(artist.birthDate)
        )
        binding.etAgeDetail.setText(getAge(artist.birthDate))
        binding.etHeightDetail.setText((artist.height).toString())
        binding.etOrderDetail.setText((artist.order).toString())
        binding.etBirthPlaceDetail.setText(artist.birthPlace)
        binding.etNotesDetail.setText(artist.notes)
    }

    private fun getAge(birthDate: Long): String {
        val time = Calendar.getInstance().timeInMillis / 1000 - birthDate / 1000
        val years = time.toFloat().roundToLong() / 31536000
        return years.toString()
    }

    private fun configImageButtons() {
        binding.imgDeleteDetail.setOnClickListener(::onPhotoOptionClicked)
        binding.imgFromUrlDetail.setOnClickListener(::onPhotoOptionClicked)
    }

    private fun onPhotoOptionClicked(view: View) {
        when (view.id) {
            R.id.imgDeleteDetail -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.details_dialog_delete_title)
                    .setMessage(getString(R.string.details_dialog_delete_message, artist.name))
                    .setPositiveButton(R.string.details_dialog_delete_delete)
                    {_, _ -> savePhotoUrlArtist("")}
                    .setNegativeButton(R.string.label_dialog_cancel, null)
                    .show()
            }
            R.id.imgFromUrlDetail -> {
                val etPhotoUrl = EditText(this)
                val builder = android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.addArtist_dialogUrl_title)
                    .setPositiveButton(
                        R.string.label_dialog_data
                    ) { _, _ -> savePhotoUrlArtist(etPhotoUrl.text.toString().trim()) }
                    .setNegativeButton(R.string.label_dialog_cancel) { _, _ -> }
                builder.setView(etPhotoUrl)
                builder.show()
            }
        }
    }

    private fun savePhotoUrlArtist(photoUrl: String) {
        artist.photoUrl = photoUrl
        ArtistRepository.updateArtist(artist)
        configImageView(photoUrl)
        showMessage(R.string.details_message_update_success)
    }


    private fun configActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        configTitle()
    }

    private fun configTitle() {
        binding.toolbarLayout.title = "${artist.name} ${artist.surname}"
    }

    private fun configImageView(url: String) {
        if (url != "") {
            val options = RequestOptions()
                .also {
                    it.diskCacheStrategy(DiskCacheStrategy.ALL)
                    it.centerCrop()
                }

            Glide.with(this)
                .load(url)
                .apply(options)
                .into(binding.ivImage)
        } else {
            binding.ivImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_photo_size_select_actual
                )
            )
        }
        artist.photoUrl = url
    }

    private fun configFloatingActionButton() {
        binding.fab.setOnClickListener {
            if (isEditing) {
                if (fieldsValidated()) {
                    artist.name = binding.etNameDetail.text.toString().trim()
                    artist.surname = binding.etSurnameDetail.text.toString().trim()
                    artist.height = binding.etHeightDetail.text.toString().trim().toShort()
                    artist.birthPlace = binding.etBirthPlaceDetail.text.toString().trim()
                    artist.notes = binding.etNotesDetail.text.toString().trim()
                    ArtistRepository.updateArtist(artist)
                    configTitle()
                    showMessage(R.string.details_message_update_success)
                }
                binding.fab.setImageDrawable(getDrawable(R.drawable.ic_account_edit))
                enableUIElements(false)
                isEditing = false
            } else {
                isEditing = true
                binding.fab.setImageDrawable(getDrawable(R.drawable.ic_account_check))
                enableUIElements(true)
            }
        }
    }

    private fun fieldsValidated(): Boolean {
        var isValid = true

        val height = binding.etHeightDetail.text.toString()

        val incorrectHeight = height.isEmpty()      // not null
                || height.any { !it.isDigit() }     // not number
                || height.toInt() <= 0              // negative number

        if (incorrectHeight) {
            binding.etHeightDetail.error = (getString(R.string.addArtist_error_minimumHeight))
            binding.etHeightDetail.requestFocus()
            isValid = false
        }

        val nullSurname = binding.etSurnameDetail.text.toString().trim().isEmpty()

        if (nullSurname) {
            binding.etSurnameDetail.error = getString(R.string.addArtist_error_required)
            binding.etSurnameDetail.requestFocus()
            isValid = false
        }

        val nullName = binding.etNameDetail.text.toString().trim().isEmpty()

        if (nullName) {
            binding.etNameDetail.error = getString(R.string.addArtist_error_required)
            binding.etNameDetail.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun enableUIElements(enable: Boolean) {
        binding.etNameDetail.isEnabled = enable
        binding.etSurnameDetail.isEnabled = enable
        binding.etBirthDateDetail.isEnabled = enable
        binding.etHeightDetail.isEnabled = enable
        binding.etBirthPlaceDetail.isEnabled = enable
        binding.etNotesDetail.isEnabled = enable
        menuItem.isVisible = enable
        binding.appBar.setExpanded(!enable)
        binding.containerMain.isNestedScrollingEnabled = !enable
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        menuItem = menu.findItem(R.id.action_save)
        menuItem.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                binding.fab.callOnClick()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configCalendar() {
        calendar = Calendar.getInstance(Locale.ROOT)
    }

    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        binding.etBirthDateDetail.setText(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.ROOT
            ).format(calendar.timeInMillis)
        )
        binding.etAgeDetail.setText(getAge(calendar.timeInMillis))
        artist.birthDate = calendar.timeInMillis
    }

    private fun onBirthDateClicked(view: View?, motionEvent: MotionEvent): Boolean {
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

    private fun showMessage(message: Int) =
        Snackbar.make(binding.containerMain, message, Snackbar.LENGTH_SHORT).show()
}