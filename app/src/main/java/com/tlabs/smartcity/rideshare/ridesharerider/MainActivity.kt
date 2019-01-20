package com.tlabs.smartcity.rideshare.ridesharerider

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout

    private lateinit var binding: com.tlabs.smartcity.rideshare.ridesharerider.databinding.ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        val navController = Navigation.findNavController(this, R.id.nav_fragment)
        drawerLayout = binding.drawerLayout
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mapFragment,
                R.id.settingsFragment
            ), drawerLayout
        )

        // Set up navigation menu
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.nav_fragment), drawerLayout
        )
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        supportFragmentManager.findFragmentById(R.id.nav_fragment)?.childFragmentManager
            ?.fragments?.get(0)?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
