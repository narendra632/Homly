package com.example.homly

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homly.databinding.ActivityMainBinding
import com.example.homly.fragments.ChatsListFragment
import com.example.homly.fragments.FavouriteListFragment
import com.example.homly.fragments.HomeFragment
import com.example.homly.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    //view Binding

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // activity_main.xml = ActivityMainBinding
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        // By default (when app open) show HomeFragment
        showHomeFragment()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //handle bottomNv item clicks to navigate between fragments
        binding.bottomNavigationView.setOnItemSelectedListener {menuItem ->

            val itemId = menuItem.itemId

            when(itemId){
                R.id.item_home -> {
                    showHomeFragment()
                }
                R.id.item_chats -> {
                    showChatsListFragment()
                }
                R.id.item_favourite -> {
                    showFavouriteListFragment()
                }
                R.id.item_profile -> {
                    showProfileFragment()
                }
            }
            true
        }

    }

    private fun showHomeFragment(){
        binding.toolbarTitleTv.text = "Home"
        // show HomeFragment
        val homeFragment = HomeFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, homeFragment,"HomeFragment")
        fragmentTransaction.commit()
    }

    private fun showChatsListFragment(){
        binding.toolbarTitleTv.text = "Chats"
        // show ChatsListFragment
        val chatsListFragment = ChatsListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, chatsListFragment, "ChatsListFragment")
        fragmentTransaction.commit()
    }

    private fun showFavouriteListFragment(){
        binding.toolbarTitleTv.text = "Favourites"
        // show FavouriteListFragment
        val favouriteListFragment = FavouriteListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, favouriteListFragment, "FavouriteListFragment")
        fragmentTransaction.commit()
    }

    private fun showProfileFragment(){
        binding.toolbarTitleTv.text = "Chats"
        // show ProfileFragment
        val profileFragment = ProfileFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, profileFragment, "ProfileFragment")
        fragmentTransaction.commit()
    }
}