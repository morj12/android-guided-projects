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
import com.example.top.database.actor.Actor
import com.example.top.database.actor.ActorRepository
import com.example.top.databinding.ActivityAddActorBinding
import com.example.top.util.ActorValidator
import java.text.SimpleDateFormat
import java.util.*

class AddActorActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityAddActorBinding
    private lateinit var actor: Actor
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddActorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etName.requestFocus()
        initActionBar()
        initActor(intent)
        initCalendar()
        initImageListeners()
    }

    private fun initActionBar() =
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    private fun initActor(intent: Intent) {
        actor = Actor(0)
        actor.birthDate = System.currentTimeMillis()
        actor.order = intent.getIntExtra(Actor.ORDER, 0)
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
            .setMessage(getString(R.string.details_dialog_delete_message, actor.name))
            .setPositiveButton(R.string.details_dialog_delete_delete)
            { _, _ -> initImageView("") }
            .setNegativeButton(R.string.label_dialog_cancel, null)
            .show()
    }

    private fun showAddPhotoDialog() {
        val etPhotoUrl = EditText(this)
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.addActor_dialogUrl_title)
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
                .into(binding.imgPhotoActor)
        } else {
            binding.imgPhotoActor.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_photo_size_select_actual
                )
            )
        }
        actor.photoUrl = url
    }

    private fun onBirthDateTouched(view: View, motionEvent: MotionEvent): Boolean {
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            val selectorDate = DialogSelectorDate()
            selectorDate.listener = this
            val args = Bundle()
            args.putLong(DialogSelectorDate.DATE, actor.birthDate)
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
            saveActor()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun saveActor() {
        if (ActorValidator.validate(
                binding.etHeight,
                (getString(R.string.addActor_error_minimumHeight)),
                binding.etSurname,
                getString(R.string.addActor_error_required),
                binding.etName,
                getString(R.string.addActor_error_required)
            )
        ) {
            actor.name = binding.etName.text.toString().trim()
            actor.surname = binding.etSurname.text.toString().trim()
            actor.height = binding.etHeight.text.toString().trim().toShort()
            actor.birthPlace = binding.etBirthPlace.text.toString().trim()
            actor.notes = binding.notes.text.toString().trim()
            ActorRepository.addActor(actor)
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
        actor.birthDate = calendar.timeInMillis
    }
}