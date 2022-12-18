package com.example.top

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.top.databinding.ActivityDetailsBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

class DetailsActivity : AppCompatActivity() {

    private var artist: Artist = Artist()
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadArtist()
        configArtist()
        configActionBar()
        configImageView(artist.photoUrl)
        configCalendar()
    }

    private fun loadArtist() {
        val extras = intent.extras
        artist.name = extras?.getString(Artist.NAME)!!
        artist.surname = extras.getString(Artist.SURNAME)!!
        artist.height = extras.getShort(Artist.HEIGHT)
        artist.birthPlace = extras.getString(Artist.BIRTH_PLACE)!!
        artist.notes = extras.getString(Artist.NOTES)!!
        artist.order = extras.getInt(Artist.ORDER)
        artist.photoUrl = extras.getString(Artist.PHOTO_URL)!!
        artist.birthDate = extras.getLong(Artist.BIRTH_DATE)
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

    private fun configCalendar() {}
}