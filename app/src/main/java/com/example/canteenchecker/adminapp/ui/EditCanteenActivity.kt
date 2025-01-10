package com.example.canteenchecker.adminapp.ui

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.canteenchecker.adminapp.CanteenCheckerApplication
import com.example.canteenchecker.adminapp.R
import com.example.canteenchecker.adminapp.api.AdminApiFactory
import com.example.canteenchecker.adminapp.core.CanteenData
import com.example.canteenchecker.adminapp.databinding.ActivityEditCanteenBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class EditCanteenActivity : AppCompatActivity() {

    companion object {
        fun intent(context: Context) = Intent(context, EditCanteenActivity::class.java)

        private const val DEFAULT_ZOOM_FACTOR = 15f;
    }

    private lateinit var binding: ActivityEditCanteenBinding
    private lateinit var mapFragment: SupportMapFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditCanteenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Edit Canteen"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        mapFragment = supportFragmentManager.findFragmentByTag(getString(R.string.tag_map_fragment)) as SupportMapFragment
        mapFragment.getMapAsync{
            it.uiSettings.apply {
                setAllGesturesEnabled(true)
                isZoomControlsEnabled = true
            }

            it.setOnMapClickListener { latitudeLongitude ->
                val address = try {
                    val geocoder = Geocoder(this)
                    val results = geocoder.getFromLocation(
                        latitudeLongitude.latitude,
                        latitudeLongitude.longitude,
                        1
                    )
                    if (results.isNullOrEmpty()) {
                        null
                    } else {
                        results.first().getAddressLine(0) // The full address
                    }
                } catch (e: Exception) {
                    null
                }

                if (address != null) {
                    binding.edtCanteenAddress.setText(address)
                    it.clear()
                    it.addMarker(MarkerOptions()
                        .position(latitudeLongitude)
                        .title(address))
                    it.animateCamera(CameraUpdateFactory.newLatLngZoom(latitudeLongitude, 15f))
                }
                binding.edtCanteenAddress.setText(address ?: getString(R.string.invalid_location))
            }
        }

        binding.edtCanteenAddress.setOnEditorActionListener {
            v, _, _ ->
                val rawAddress = v.text.toString().trim()
                if (rawAddress.isNotBlank()) {
                    updateMap(rawAddress)
                }
                false

        }


        updateCanteen()
    }

    private fun updateMap(address: String) {
        val addressCoords = Geocoder(this@EditCanteenActivity)
            .getFromLocationName(address, 1)
            ?.firstOrNull()
            ?.run{ LatLng(latitude, longitude)}
        mapFragment.getMapAsync{ map ->
            map.apply {
                clear()
                if(addressCoords != null){
                    addMarker(MarkerOptions().position(addressCoords))
                    animateCamera(
                        CameraUpdateFactory.newLatLngZoom(addressCoords, DEFAULT_ZOOM_FACTOR)
                    )
                }else{
                    animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(0.0, 0.0), 0f)
                    )
                }

            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_canteen, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.mniSaveCanteen -> {
                saveCanteenData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun saveCanteenData() = lifecycleScope.launch {
        val authenticationToken = (application as CanteenCheckerApplication).authenticationToken?: ""

        val canteenData = CanteenData(
            binding.edtCanteenName.text.toString(),
            binding.edtCanteenAddress.text.toString(),
            binding.edtCanteenWebsite.text.toString(),
            binding.edtPhoneNumber.text.toString()
        )

        AdminApiFactory.createAdminApi().updateCanteenData(authenticationToken, canteenData)
            .onFailure {
                Toast.makeText(this@EditCanteenActivity,
                    getString(R.string.error_updating_canteen), Toast.LENGTH_SHORT).show()
            }
            .onSuccess {
                Toast.makeText(this@EditCanteenActivity,
                    getString(R.string.successfully_updated_canteen), Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun updateCanteen() = lifecycleScope.launch {
        val authenticationToken = (application as CanteenCheckerApplication).authenticationToken?: ""

        AdminApiFactory.createAdminApi().getCanteen(authenticationToken)
            .onFailure {
                Toast.makeText(this@EditCanteenActivity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            }
            .onSuccess {
                binding.edtCanteenName.setText(it.name)
                binding.edtCanteenWebsite.setText(it.website)
                binding.edtPhoneNumber.setText(it.phoneNumber)
                binding.edtCanteenAddress.setText(it.address)

                updateMap(it.address)
            }
    }
}