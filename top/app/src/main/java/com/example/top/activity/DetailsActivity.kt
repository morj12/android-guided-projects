package com.example.top.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.icu.util.Calendar
import android.os.Handler
import android.os.Looper
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
import com.example.top.R
import com.example.top.database.actor.Actor
import com.example.top.database.actor.ActorRepository
import com.example.top.databinding.ActivityDetailsBinding
import com.example.top.util.ActorValidator
import com.example.top.util.Message
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.roundToLong

class DetailsActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var actor: Actor
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var calendar: Calendar
    private lateinit var menuItem: MenuItem

    private var executor = Executors.newSingleThreadExecutor()

    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executor.execute {
            loadActor()
            Handler(Looper.getMainLooper()).post {
                initActorInfo()
                initActionBar()
                initImageView(actor.photoUrl)
                initCalendar()
                initFloatingActionButton()
                initImageButtons()
            }
        }
    }

    private fun loadActor() {
        val extras = intent.extras
        actor = ActorRepository.getActor(extras?.getLong(Actor.ID)!!)
    }

    private fun initActorInfo() {
        binding.etNameDetail.setText(actor.name)
        binding.etSurnameDetail.setText(actor.surname)
        binding.etBirthDateDetail.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
                .format(actor.birthDate)
        )
        binding.etAgeDetail.setText(getAge(actor.birthDate))
        binding.etHeightDetail.setText((actor.height).toString())
        binding.etOrderDetail.setText((actor.order).toString())
        binding.etBirthPlaceDetail.setText(actor.birthPlace)
        binding.etNotesDetail.setText(actor.notes)
    }

    private fun initActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        configTitle()
    }

    private fun configTitle() {
        binding.toolbarLayout.title = "${actor.name} ${actor.surname}"
    }

    private fun initImageView(url: String) {
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
        actor.photoUrl = url
    }

    private fun initCalendar() {
        calendar = Calendar.getInstance(Locale.ROOT)
        binding.etBirthDateDetail.setOnTouchListener(::onBirthDateClicked)
    }

    private fun initFloatingActionButton() {
        binding.fab.setOnClickListener {
            if (isEditing) {
                if (ActorValidator.validate(
                        binding.etHeightDetail,
                        (getString(R.string.addActor_error_minimumHeight)),
                        binding.etSurnameDetail,
                        getString(R.string.addActor_error_required),
                        binding.etNameDetail,
                        getString(R.string.addActor_error_required)
                    )
                ) {
                    actor.name = binding.etNameDetail.text.toString().trim()
                    actor.surname = binding.etSurnameDetail.text.toString().trim()
                    actor.height = binding.etHeightDetail.text.toString().trim().toShort()
                    actor.birthPlace = binding.etBirthPlaceDetail.text.toString().trim()
                    actor.notes = binding.etNotesDetail.text.toString().trim()

                    executor.execute {
                        ActorRepository.updateActor(actor)
                        Handler(Looper.getMainLooper()).post {
                            configTitle()
                            showMessage(R.string.details_message_update_success)
                            binding.fab.setImageDrawable(getDrawable(R.drawable.ic_account_edit))
                            enableUIElements(false)
                            isEditing = false
                        }
                    }
                }
            } else {
                isEditing = true
                binding.fab.setImageDrawable(getDrawable(R.drawable.ic_account_check))
                enableUIElements(true)
            }
        }
    }

    private fun initImageButtons() {
        binding.imgDeleteDetail.setOnClickListener(::onPhotoOptionClicked)
        binding.imgFromUrlDetail.setOnClickListener(::onPhotoOptionClicked)
    }

    private fun getAge(birthDate: Long): String {
        val time = Calendar.getInstance().timeInMillis / 1000 - birthDate / 1000
        val years = time.toFloat().roundToLong() / 31536000
        return years.toString()
    }

    private fun onPhotoOptionClicked(view: View) {
        when (view.id) {
            R.id.imgDeleteDetail -> deleteImage()
            R.id.imgFromUrlDetail -> loadImageFromUrl()
        }
    }

    private fun deleteImage() {
        AlertDialog.Builder(this)
            .setTitle(R.string.details_dialog_delete_title)
            .setMessage(getString(R.string.details_dialog_delete_message, actor.name))
            .setPositiveButton(R.string.details_dialog_delete_delete)
            { _, _ -> savePhotoUrlActor("") }
            .setNegativeButton(R.string.label_dialog_cancel, null)
            .show()
    }

    private fun loadImageFromUrl() {
        val etPhotoUrl = EditText(this)
        val builder = android.app.AlertDialog.Builder(this)
            .setTitle(R.string.addActor_dialogUrl_title)
            .setPositiveButton(
                R.string.label_dialog_data
            ) { _, _ -> savePhotoUrlActor(etPhotoUrl.text.toString().trim()) }
            .setNegativeButton(R.string.label_dialog_cancel, null)
        builder.setView(etPhotoUrl)
        builder.show()
    }

    private fun savePhotoUrlActor(photoUrl: String) {
        actor.photoUrl = photoUrl
        executor.execute {
            ActorRepository.updateActor(actor)
            Handler(Looper.getMainLooper()).post {
                initImageView(photoUrl)
                Message.showMessage(binding.containerMain, R.string.details_message_update_success)
            }
        }
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
        actor.birthDate = calendar.timeInMillis
    }

    private fun onBirthDateClicked(view: View?, motionEvent: MotionEvent): Boolean {
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

    private fun showMessage(message: Int) =
        Snackbar.make(binding.containerMain, message, Snackbar.LENGTH_SHORT).show()
}