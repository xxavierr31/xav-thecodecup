package com.example.personalmidterm

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.personalmidterm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            android.widget.Toast.makeText(this, "Notifications enabled for rewards!", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        requestNotificationPermission()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        setupCustomNavbar(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateNavbarSelection(destination.id)
            
            // Hide navbar for certain screens
            val stackOnlyDestinations = setOf(
                R.id.loadingScreenFragment,
                R.id.detailsFragment,
                R.id.cartFragment,
                R.id.orderSuccessFragment,
                R.id.redeemFragment
            )
            binding.bottomNavCustom.root.visibility = if (destination.id in stackOnlyDestinations) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    private fun setupCustomNavbar(navController: androidx.navigation.NavController) {
        binding.bottomNavCustom.nav1Group.setOnClickListener {
            navController.navigate(R.id.menuFragment)
        }
        binding.bottomNavCustom.nav2Group.setOnClickListener {
            navController.navigate(R.id.rewardsFragment)
        }
        binding.bottomNavCustom.nav3Group.setOnClickListener {
            navController.navigate(R.id.homeFragment)
        }
        binding.bottomNavCustom.nav4Group.setOnClickListener {
            navController.navigate(R.id.profileFragment)
        }
        binding.bottomNavCustom.nav5Group.setOnClickListener {
            navController.navigate(R.id.myOrdersFragment)
        }
    }

    private fun updateNavbarSelection(destinationId: Int) {
        val brown = getColor(R.color.theme_brown)
        val grey = getColor(R.color.text_secondary)
        val brownList = android.content.res.ColorStateList.valueOf(brown)
        val greyList = android.content.res.ColorStateList.valueOf(grey)

        binding.bottomNavCustom.apply {
            nav1Label.setTextColor(if (destinationId == R.id.menuFragment) brown else grey)
            nav1.imageTintList = if (destinationId == R.id.menuFragment) brownList else greyList

            nav2Label.setTextColor(if (destinationId == R.id.rewardsFragment) brown else grey)
            nav2.imageTintList = if (destinationId == R.id.rewardsFragment) brownList else greyList

            nav3Label.setTextColor(if (destinationId == R.id.homeFragment) brown else grey)
            nav3.imageTintList = if (destinationId == R.id.homeFragment) brownList else greyList

            nav4Label.setTextColor(if (destinationId == R.id.profileFragment) brown else grey)
            nav4.imageTintList = if (destinationId == R.id.profileFragment) brownList else greyList

            nav5Label.setTextColor(if (destinationId == R.id.myOrdersFragment) brown else grey)
            nav5.imageTintList = if (destinationId == R.id.myOrdersFragment) brownList else greyList
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Rewards"
            val descriptionText = "Notifications for rank ups and rewards"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("REWARDS_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != 
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun showRankUpNotification(newRank: String) {
        val builder = NotificationCompat.Builder(this, "REWARDS_CHANNEL")
            .setSmallIcon(R.drawable.ic_leaf)
            .setContentTitle("New Rank Unlocked!")
            .setContentText("Congratulations! You've reached the $newRank rank!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(this)) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    notify(1001, builder.build())
                }
            }
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }
}
