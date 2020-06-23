package com.pyo.safe_guard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pyo.safe_guard.navigation.ChatFragment
import com.pyo.safe_guard.navigation.DogFragment
import com.pyo.safe_guard.navigation.MyInfoFragment
import com.pyo.safe_guard.navigation.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.action_search -> {
                var searchFragment = SearchFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, searchFragment).commit()
                return true
            }
            R.id.action_chat -> {
                var chatFragment = ChatFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, chatFragment).commit()
                return true
            }
            R.id.action_dog -> {
                var dogFragment = DogFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, dogFragment).commit()
                return true
            }
            R.id.action_my_info -> {
                var myInfoFragment = MyInfoFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, myInfoFragment).commit()
                return true
            }
        }

        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
    }


}

