package com.example.canteenchecker.adminapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.canteenchecker.adminapp.api.AdminApiFactory
import com.example.canteenchecker.adminapp.databinding.ActivityDashboardBinding
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    companion object {

        fun intent(context: Context) =  Intent(context, DashboardActivity::class.java)
    }

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Dashboard"

        supportFragmentManager.beginTransaction().addReviewsFragment()

        updateCanteen()

    }

    private fun updateCanteen() = lifecycleScope.launch {
        AdminApiFactory.createAdminApi().getCanteen()
            .onFailure {
                Toast.makeText(this@DashboardActivity, "Something went wrong...", Toast.LENGTH_SHORT).show()
            }
            .onSuccess {
                binding.txvCanteenName.text = it.name
                binding.txvCanteenAddress.text = it.address
                binding.txvWebsite.text = it.website
                binding.txvPhoneNumber.text = it.phoneNumber
                binding.txvWaitingTime.text = it.waitingTime.toString()
                binding.prbWaitingTime.progress = it.waitingTime

            }
    }
}