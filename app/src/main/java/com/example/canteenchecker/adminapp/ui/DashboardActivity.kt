package com.example.canteenchecker.adminapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.canteenchecker.adminapp.CanteenCheckerApplication
import com.example.canteenchecker.adminapp.R
import com.example.canteenchecker.adminapp.api.AdminApiFactory
import com.example.canteenchecker.adminapp.core.CanteenChangedBroadcastReceiver
import com.example.canteenchecker.adminapp.core.Dish
import com.example.canteenchecker.adminapp.core.registerCanteenChangedBroadcastReceiver
import com.example.canteenchecker.adminapp.core.unregisterCanteenChangedBroadcastReceiver
import com.example.canteenchecker.adminapp.databinding.ActivityDashboardBinding
import com.example.canteenchecker.adminapp.ui.ReviewsFragment.Companion.addReviewsFragment
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

        binding.btnChangeWaitingTime.setOnClickListener{ changeWaitingTime() }
        binding.btnChangeDishOfTheDay.setOnClickListener{changeDishOfTheDay()}

        supportFragmentManager.beginTransaction().addReviewsFragment(R.id.fcvReviews).commitNow()

        registerCanteenChangedBroadcastReceiver(receiver)

        updateCanteen()

    }

    private fun updateCanteen() = lifecycleScope.launch {
        var authenticationToken = (application as CanteenCheckerApplication).authenticationToken
        if (authenticationToken == null) {
            Toast.makeText(this@DashboardActivity, "Something went wrong...", Toast.LENGTH_SHORT).show()
        }
        else {
            AdminApiFactory.createAdminApi().getCanteen(authenticationToken)
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

                    binding.txvDish.text = it.dish
                    binding.txvDishPrice.text = "${it.dishPrice}€"

                }
        }
    }

    private val canteenId get() = "28185962-6e44-44a5-9707-f5f7bf398a37"

    private val receiver = object : CanteenChangedBroadcastReceiver(){
        override fun onReceiveCanteenChanged(canteenId: String) {
            Log.w("H", "HERE")
            if(canteenId == this@DashboardActivity.canteenId){

                // updateCanteenDetails()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterCanteenChangedBroadcastReceiver(receiver)
    }


    private fun changeWaitingTime() {
        var authenticationToken = (application as CanteenCheckerApplication).authenticationToken
        if (authenticationToken == null) {
            authenticationToken = ""
//            Toast.makeText(this@DashboardActivity, "Something went wrong...", Toast.LENGTH_SHORT).show()
//            return
        }

        val context = this

        val view = layoutInflater.inflate(R.layout.dialog_change_waitingtime, null)


        AlertDialog.Builder(context)
            .setTitle(R.string.text_waiting_time)
            .setView(view)
            .setPositiveButton(R.string.text_send){ dialog, _ ->
                dialog.dismiss()
                lifecycleScope.launch {
                    AdminApiFactory.createAdminApi().updateWaitingTime(
                        authenticationToken,
                        view.findViewById<EditText>(R.id.edtWaitingTime).text.toString().toInt()
                    ).onFailure {
                        Toast.makeText(context, R.string.message_waitingtime_not_updated, Toast.LENGTH_SHORT).show()
                    }
                    .onSuccess {
                        Toast.makeText(context, R.string.message_waitingtime_updated, Toast.LENGTH_SHORT).show()
                    }
                }
            }.create().show()
    }


    private fun dishOfTheDayIsValid(name: String, price: Double): Boolean {
        val validName = name != ""
        val validPrice = !price.equals(0.0)

        if (!validName) {
            Toast.makeText(this, getString(R.string.error_dish_name), Toast.LENGTH_SHORT).show()
        }

        if (!validPrice) {
            Toast.makeText(this, getString(R.string.error_dish_price), Toast.LENGTH_SHORT).show()
        }

        return validName && validPrice
    }


    private fun changeDishOfTheDay() {
        var authenticationToken = (application as CanteenCheckerApplication).authenticationToken
        if (authenticationToken == null) {
            authenticationToken = ""
//            Toast.makeText(this@DashboardActivity, "Something went wrong...", Toast.LENGTH_SHORT).show()
//            return
        }

        val context = this

        val view = layoutInflater.inflate(R.layout.dialog_change_dish_of_the_day, null)

        AlertDialog.Builder(context)
            .setTitle(R.string.text_change_dish_of_the_day)
            .setView(view)
            .setPositiveButton(R.string.text_send){ dialog, _ ->
                dialog.dismiss()
                val dishOfTheDayName = view.findViewById<EditText>(R.id.edtDishOfTheDayName).text.toString()
                val dishOfTheDayPrice = view.findViewById<EditText>(R.id.edtDishOfTheDayPrice).text.toString().toDoubleOrNull() ?: 0.00

                if (dishOfTheDayIsValid(dishOfTheDayName, dishOfTheDayPrice)) {
                    val dish = Dish(dishOfTheDayName, dishOfTheDayPrice)
                    lifecycleScope.launch {
                        AdminApiFactory.createAdminApi().updateDish(
                            authenticationToken, dish).onFailure {
                            Toast.makeText(context, R.string.message_waitingtime_not_updated, Toast.LENGTH_SHORT).show()
                        }
                        .onSuccess {
                            Toast.makeText(context, R.string.message_waitingtime_updated, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }.create().show()
    }
}