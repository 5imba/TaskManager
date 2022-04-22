package com.wildraion.taskmanager

import android.app.*
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.wildraion.taskmanager.databinding.ActivityMainBinding
import com.wildraion.taskmanager.fragments.list.ListFragment
import com.wildraion.taskmanager.notification.channelID


class MainActivity : AppCompatActivity() {

    private lateinit var mToggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mToggle = ActionBarDrawerToggle(this, binding.navDrawer,
            R.string.open_menu, R.string.close_menu)


        binding.navDrawer.addDrawerListener(mToggle)
        mToggle.syncState()

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener{ _, destination, _ ->
            binding.navDrawer.setDrawerLockMode(
                if(destination.displayName.contains("listFragment")) DrawerLayout.LOCK_MODE_UNLOCKED
                else DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }

        val navView = binding.navView
        navView.menu.getItem(0).isChecked = true
        navView.menu.getItem(1).isCheckable = false

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_weather -> {
                    if(navHostFragment.childFragmentManager.backStackEntryCount == 0){
                        navController.navigate(R.id.action_listFragment_to_weatherFragment)
                    }
                }
            }

            NavigationUI.onNavDestinationSelected(it, navController)
            binding.navDrawer.closeDrawer(GravityCompat.START)
            true
        }

        setupActionBarWithNavController(navController, binding.navDrawer)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)

        createNotificationChannel()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private var num: Int = 0

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(navHostFragment.childFragmentManager.backStackEntryCount == 0){
            if(mToggle.onOptionsItemSelected(item))
                return true
        }

        num += 1
        return super.onOptionsItemSelected(item)
    }

    private fun createNotificationChannel(){
        val name = "Task Manager"
        val description = "The title of the task whose deadline is expiring"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = description
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}