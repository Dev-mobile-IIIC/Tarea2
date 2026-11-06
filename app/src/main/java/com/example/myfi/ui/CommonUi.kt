package com.example.myfi.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.myfi.MainActivity
import com.example.myfi.ProfileActivity
import com.example.myfi.SavingsGoalsActivity
import com.example.myfi.TransactionListActivity

object AppNavigation {
    fun bindBottomNavigation(activity: AppCompatActivity, navigation: com.google.android.material.bottomnavigation.BottomNavigationView) {
        navigation.setOnItemSelectedListener { item ->
            val target = when (item.itemId) {
                com.example.myfi.R.id.navigation_home -> MainActivity::class.java
                com.example.myfi.R.id.navigation_transactions -> TransactionListActivity::class.java
                com.example.myfi.R.id.navigation_goals -> SavingsGoalsActivity::class.java
                com.example.myfi.R.id.navigation_profile -> ProfileActivity::class.java
                else -> null
            }
            if (target != null && target != activity::class.java) {
                activity.startActivity(Intent(activity, target).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
            }
            true
        }
    }
}
