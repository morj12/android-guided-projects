package com.example.cart_and_notes.presentation.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cart_and_notes.databinding.FragmentCartBinding
import com.example.cart_and_notes.domain.entity.Cart
import com.example.cart_and_notes.presentation.adapter.CartAdapter
import com.example.cart_and_notes.presentation.dialog.NewCartDialog
import com.example.cart_and_notes.presentation.view.cart.CartActivity
import com.example.cart_and_notes.presentation.view.AdditionFragment
import com.example.cart_and_notes.MyApp
import com.example.cart_and_notes.util.TimeHelper

class CartFragment : AdditionFragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding: FragmentCartBinding
        get() = _binding ?: throw RuntimeException("FragmentCartBinding is null")

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MyApp).database)
    }

    private lateinit var adapter: CartAdapter

    private val onNewCartClicked: (String) -> Unit = {
        val cart = Cart(
            it,
            TimeHelper.getCurrentTime(),
            0,
            0
        )
        mainViewModel.insertCart(cart)
    }

    override fun onClickNew() {
        NewCartDialog.showDialog(activity as AppCompatActivity, "", onNewCartClicked)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observe()
        setupSwipeListener()
        setupClickListener()
        setupEditClickListener()
    }

    private fun initRecyclerView() {
        with(binding) {
            rcCarts.layoutManager = LinearLayoutManager(activity)
            adapter = CartAdapter()
            rcCarts.adapter = adapter
        }
    }

    private fun observe() {
        mainViewModel.allCarts.observe(viewLifecycleOwner) {
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
                mainViewModel.deleteCart(item)
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rcCarts)
    }

    private fun setupClickListener() {
        adapter.onCartClickListener = {
            val intent = Intent(activity, CartActivity::class.java).apply {
                putExtra(CartActivity.CART_KEY, it)
            }
            startActivity(intent)
        }
    }

    private fun setupEditClickListener() {
        adapter.onEditCartClickListener = { cart ->
            NewCartDialog.showDialog(activity as AppCompatActivity, cart.name) {
                mainViewModel.updateCart(cart.copy(name = it))
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CartFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}