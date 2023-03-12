package com.example.cart_and_notes.presentation.view.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cart_and_notes.databinding.FragmentNoteBinding
import com.example.cart_and_notes.domain.entity.Note
import com.example.cart_and_notes.presentation.adapter.NoteAdapter
import com.example.cart_and_notes.presentation.view.AdditionFragment
import com.example.cart_and_notes.MyApp
import com.example.cart_and_notes.presentation.view.newnote.NewNoteActivity

class NoteFragment : AdditionFragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding: FragmentNoteBinding
        get() = _binding ?: throw RuntimeException("FragmentNoteBinding is null")

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MyApp).database)
    }

    private lateinit var editLauncher: ActivityResultLauncher<Intent>

    private lateinit var adapter: NoteAdapter

    override fun onClickNew() {
        editLauncher.launch(Intent(activity, NewNoteActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observe()
        setupSwipeListener()
        setupClickListener()
    }

    private fun initRecyclerView() {
        with(binding) {
            rcNotes.layoutManager = LinearLayoutManager(activity)
            adapter = NoteAdapter()
            rcNotes.adapter = adapter
        }
    }

    private fun observe() {
        mainViewModel.allNotes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun setupSwipeListener() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.currentList[viewHolder.adapterPosition]
                mainViewModel.deleteNote(item)
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rcNotes)
    }

    private fun setupClickListener() {
        adapter.onNoteClickListener = {
            val intent = Intent(activity, NewNoteActivity::class.java).apply {
                putExtra(NEw_NOTE_KEY, it)
            }
            editLauncher.launch(intent)
        }
    }

    private fun onEditResult() {
        editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val isUpdate = it.data?.getBooleanExtra(IS_UPDATE_KEY, false)
                    ?: throw RuntimeException("Update indicator is null")
                val note = it.data?.getParcelableExtra<Note>(NEw_NOTE_KEY)
                    ?: throw RuntimeException("Note is null")
                if (isUpdate) mainViewModel.updateNote(note) else mainViewModel.insertNote(note)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {

        const val NEw_NOTE_KEY = "new_note"
        const val IS_UPDATE_KEY = "is_update"

        @JvmStatic
        fun newInstance() = NoteFragment()
    }
}