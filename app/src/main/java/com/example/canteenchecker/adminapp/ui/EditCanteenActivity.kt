package com.example.canteenchecker.adminapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.canteenchecker.adminapp.CanteenCheckerApplication
import com.example.canteenchecker.adminapp.R
import com.example.canteenchecker.adminapp.api.AdminApiFactory
import com.example.canteenchecker.adminapp.databinding.ActivityEditCanteenBinding
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.coroutines.launch

class EditCanteenActivity : AppCompatActivity() {

    companion object {
        fun intent(context: Context) = Intent(context, EditCanteenActivity::class.java)
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
        }

        updateCanteen()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateCanteen() = lifecycleScope.launch {
        var authenticationToken = (application as CanteenCheckerApplication).authenticationToken
        if (authenticationToken == null) {
            Toast.makeText(this@EditCanteenActivity, "Something went wrong...", Toast.LENGTH_SHORT).show()
        }
        else {
            AdminApiFactory.createAdminApi().getCanteen(authenticationToken)
                .onFailure {
                    Toast.makeText(this@EditCanteenActivity, "Something went wrong...", Toast.LENGTH_SHORT).show()
                }
                .onSuccess {
//                    binding.txvCanteenName.text = it.name
//                    binding.txvCanteenAddress.text = it.address
//                    binding.txvWebsite.text = it.website
//                    binding.txvPhoneNumber.text = it.phoneNumber
//                    binding.txvWaitingTime.text = it.waitingTime.toString()
//                    binding.prbWaitingTime.progress = it.waitingTime
//
//                    binding.txvDish.text = it.dish
//                    binding.txvDishPrice.text = "${it.dishPrice}€"
//
//                    canteen = it

                }
        }
    }
}