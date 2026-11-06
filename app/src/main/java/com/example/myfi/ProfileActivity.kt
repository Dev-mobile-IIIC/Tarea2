package com.example.myfi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myfi.databinding.ActivityProfileBinding
import com.example.myfi.ui.AppNavigation

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppNavigation.bindBottomNavigation(this, binding.bottomNavigation)
        binding.btnManageCategories.setOnClickListener { startActivity(Intent(this, CategoriesActivity::class.java)) }
        binding.tvPrivacyNote.setText(R.string.privacy_note)
        binding.tvAppVersion.text = getString(R.string.app_version, packageManager.getPackageInfo(packageName, 0).versionName.orEmpty())
    }
}
