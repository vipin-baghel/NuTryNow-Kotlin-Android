package com.king.nutrynow

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val isNetworkConnected: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Hide the action bar
        val actionBar = supportActionBar
        actionBar?.hide()
        val sharedPreferences:SharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        sharedPreferences.edit().putBoolean("isFirstTime", false).apply()

        //loading the default fragment
        loadFragment(FragmentHome())

        //getting bottom navigation view and attaching the listener
        val navigation = findViewById<BottomNavigationView>(R.id.nav_view)
        navigation.setOnNavigationItemSelectedListener(this)

        if (!isNetworkConnected) {
            Toast.makeText(this, "No Internet !!", Toast.LENGTH_LONG).show()
        }
    }


    private fun loadFragment(fragment: Fragment?): Boolean {
        //switching fragment
        return if (fragment != null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
            true

        } else {
            Toast.makeText(this, "Error loading Fragment", Toast.LENGTH_SHORT).show()
            false
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        var fragment: Fragment? = null

        when (menuItem.itemId) {
            R.id.navigation_home -> fragment = FragmentHome()

            R.id.navigation_food -> fragment = FragmentFood()

            R.id.navigation_settings -> fragment = FragmentSettings()
        }

        return loadFragment(fragment)
    }
}



