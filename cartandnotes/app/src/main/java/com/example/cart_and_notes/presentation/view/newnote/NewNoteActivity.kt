package com.example.cart_and_notes.presentation.view.newnote

import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.cart_and_notes.R
import com.example.cart_and_notes.databinding.ActivityNewNoteBinding
import com.example.cart_and_notes.entity.NoteDbModel
import com.example.cart_and_notes.presentation.view.main.NoteFragment
import java.util.*

class NewNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewNoteBinding

    private var note: NoteDbModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getNote()
    }

    private fun getNote() {
        note = intent.getParcelableExtra(NoteFragment.NEw_NOTE_KEY)
        fillNote()
    }

    private fun fillNote() = with(binding) {
        if (note != null) {
            edTitle.setText(note?.title)
            edDescription.setText(note?.content)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    // TODO: add check for incorrect and null values
    private fun setMainResult() {
        var isUpdate = false
        val tempNote = if (note == null) {
            createNote()
        } else {
            isUpdate = true
            updateNote()
        }
        val intent = Intent().apply {
            putExtra(NoteFragment.NEw_NOTE_KEY, tempNote)
            putExtra(NoteFragment.IS_UPDATE_KEY, isUpdate)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun createNote(): NoteDbModel {
        return NoteDbModel(
            binding.edTitle.text.toString(),
            binding.edDescription.text.toString(),
            getCurrentTime(),
            ""
        )
    }

    private fun updateNote(): NoteDbModel? {
        with(binding) {
            return note?.copy(
                title = edTitle.text.toString(),
                content = edDescription.text.toString()
            )
        }
    }

    private fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("hh:mm:ss - yyyy/MM/dd", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_save -> setMainResult()
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}