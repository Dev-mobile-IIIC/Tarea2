package com.example.myfi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myfi.databinding.ActivityLocationPickerBinding
import java.util.Locale

class LocationPickerActivity : AppCompatActivity() {
    companion object {
        const val RESULT_LATITUDE = "latitude"
        const val RESULT_LONGITUDE = "longitude"
        const val RESULT_NAME = "location_name"
        const val RESULT_ADDRESS = "location_address"
    }

    private lateinit var binding: ActivityLocationPickerBinding
    private var selectedLocation: Location? = null
    private var selectedAddress: String? = null
    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
        if (grants[Manifest.permission.ACCESS_FINE_LOCATION] == true || grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
            captureCurrentLocation()
        else binding.tvLocationStatus.setText(R.string.location_permission_denied)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mapContainer.contentDescription = getString(R.string.location_search_hint)
        binding.tvLocationStatus.setText(R.string.location_search_hint)
        binding.tilLocationSearch.isEnabled = false
        binding.etLocationSearch.isEnabled = false
        binding.btnUseCurrentLocation.setOnClickListener { requestCurrentLocation() }
        binding.btnConfirmLocation.setOnClickListener { confirmSelection() }
        binding.btnCancelLocation.setOnClickListener { setResult(RESULT_CANCELED); finish() }
        binding.btnConfirmLocation.isEnabled = false
    }

    private fun requestCurrentLocation() {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fine || coarse) captureCurrentLocation()
        else permissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    @Suppress("MissingPermission")
    private fun captureCurrentLocation() {
        if (!lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) return
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        val provider = when {
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            else -> null
        }
        if (provider == null) {
            binding.tvLocationStatus.setText(R.string.location_unavailable)
            return
        }
        binding.tvLocationStatus.setText(R.string.location_selected)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            manager.getCurrentLocation(provider, null, mainExecutor) { location -> displayLocation(location) }
        } else {
            displayLocation(manager.getLastKnownLocation(provider))
        }
    }

    private fun displayLocation(location: Location?) {
        if (!lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) return
        if (location == null) {
            binding.tvLocationStatus.setText(R.string.location_unavailable)
            return
        }
        selectedLocation = location
        selectedAddress = runCatching {
            @Suppress("DEPRECATION")
            Geocoder(this, Locale.getDefault()).getFromLocation(location.latitude, location.longitude, 1)
                ?.firstOrNull()?.getAddressLine(0)
        }.getOrNull()
        binding.tvLocationName.text = getString(R.string.location_selected)
        binding.tvLocationAddress.text = selectedAddress ?: getString(R.string.location_not_set)
        binding.tvLocationStatus.setText(R.string.location_selected)
        binding.btnConfirmLocation.isEnabled = true
    }

    private fun confirmSelection() {
        val location = selectedLocation ?: return
        setResult(RESULT_OK, Intent().apply {
            putExtra(RESULT_LATITUDE, location.latitude)
            putExtra(RESULT_LONGITUDE, location.longitude)
            putExtra(RESULT_NAME, getString(R.string.location_selected))
            putExtra(RESULT_ADDRESS, selectedAddress)
        })
        finish()
    }
}
