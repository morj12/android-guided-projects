package com.example.cart_and_notes.presentation.view.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.widget.EditText
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cart_and_notes.R
import com.example.cart_and_notes.databinding.ActivityCartBinding
import com.example.cart_and_notes.domain.entity.Cart
import com.example.cart_and_notes.domain.entity.CartItem
import com.example.cart_and_notes.presentation.view.main.MainViewModel
import com.example.cart_and_notes.MyApp
import com.example.cart_and_notes.presentation.adapter.CartItemAdapter
import com.example.cart_and_notes.presentation.dialog.EditCartItemDialog

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var saveCartItem: MenuItem
    private var newItemName: EditText? = null

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as MyApp).database)
    }

    private var cart: Cart? = null

    private lateinit var adapter: CartItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCart()
        initRecyclerView()
        observe()
        initClickListener()
        setupSwipeListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_menu, menu)
        saveCartItem =
            menu?.findItem(R.id.save_cart_item) ?: throw RuntimeException("Null menu item")
        val newItem = menu.findItem(R.id.new_cart_item)
        newItemName = newItem.actionView?.findViewById(R.id.ed_new_cart_item) as EditText?
        newItem.setOnActionExpandListener(expandActionView())
        saveCartItem.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_cart_item -> addNewCartItem()
            R.id.delete_cart -> {
                mainViewModel.deleteCart(cart!!)
                finish()
            }
            R.id.clear_cart -> mainViewModel.clearCart(cart!!.id)
        }
        return super.onOptionsItemSelected(item)
    }

    // TODO: also update cart parameters
    // TODO: clean text after opening
    // TODO: check for null name
    private fun addNewCartItem() {
        if (newItemName?.text.toString().isEmpty()) return
        val item = CartItem(
            newItemName?.text.toString(),
            "",
            false,
            cart?.id!!,
            "item"
        )
        newItemName?.setText("")
        mainViewModel.insertCartItem(item)
    }


    private fun expandActionView(): OnActionExpandListener {
        return object : OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                saveCartItem.isVisible = true
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                saveCartItem.isVisible = false
                invalidateOptionsMenu()
                return true
            }

        }
    }


    private fun initCart() {
        cart = intent.getParcelableExtra(CART_KEY)
    }

    private fun initRecyclerView() = with(binding) {
        rcCartItems.layoutManager = LinearLayoutManager(this@CartActivity)
        adapter = CartItemAdapter()
        rcCartItems.adapter = adapter
    }

    private fun observe() {
        mainViewModel.allCartItems(cart?.id!!).observe(this) {
            adapter.submitList(it)
        }
    }

    private fun initClickListener() {
        adapter.onCartItemEditClickListener = {
            EditCartItemDialog.showDialog(this, it) { updatedItem ->
                mainViewModel.updateCartItem(updatedItem)
            }
        }
        adapter.onCartItemCheckBoxClickListener = {
            mainViewModel.updateCartItem(it)
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
                mainViewModel.deleteCartItem(item)
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rcCartItems)
    }

    companion object {

        const val CART_KEY = "cart"

    }
}