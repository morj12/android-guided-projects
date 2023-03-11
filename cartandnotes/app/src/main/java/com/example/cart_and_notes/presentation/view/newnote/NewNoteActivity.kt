package com.example.cart_and_notes.presentation.view.newnote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.cart_and_notes.R
import com.example.cart_and_notes.databinding.ActivityNewNoteBinding
import com.example.cart_and_notes.domain.entity.Note
import com.example.cart_and_notes.presentation.view.main.NoteFragment
import com.example.cart_and_notes.util.TimeHelper

class NewNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewNoteBinding

    private var note: Note? = null

    private var validTitle = true
    private var validDescription = true

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

    private fun setMainResult() {
        if (isInputValid()) {
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
        } else if (!validTitle) {
            binding.edTitle.error = getString(R.string.empty_title)
        } else if (!validDescription) {
            binding.edTitle.error = getString(R.string.empty_desc)
        }

    }

    private fun isInputValid(): Boolean {
        validTitle = binding.edTitle.text.toString().isNotBlank()
        validDescription = binding.edDescription.text.toString().isNotBlank()
        return validTitle && validDescription
    }

    private fun createNote(): Note {
        return Note(
            binding.edTitle.text.toString(),
            binding.edDescription.text.toString(),
            TimeHelper.getCurrentTime(),
            ""
        )
    }

    private fun updateNote(): Note? {
        with(binding) {
            return note?.copy(
                title = edTitle.text.toString(),
                content = edDescription.text.toString()
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_save -> setMainResult()
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}