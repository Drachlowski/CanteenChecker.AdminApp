package com.example.canteenchecker.adminapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.canteenchecker.adminapp.CanteenCheckerApplication
import com.example.canteenchecker.adminapp.R
import com.example.canteenchecker.adminapp.api.AdminApiFactory
import com.example.canteenchecker.adminapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    companion object {
        fun intent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener{authenticate()}
    }

    private fun authenticate() {
        val context: Context = this
        lifecycleScope.launch {
            setUIEnabled(false)
            AdminApiFactory.createAdminApi().authenticate(binding.edtUserName.text.toString(), binding.edtPassword.text.toString())
                .onFailure {
                    setUIEnabled(true)
                    binding.edtPassword.text.clear()
                    Toast.makeText(context, getString(R.string.error_login), Toast.LENGTH_SHORT).show()
                }
                .onSuccess {
                    binding.edtUserName.text.clear()
                    binding.edtPassword.text.clear()
                    setUIEnabled(true)
                    (application as CanteenCheckerApplication).authenticationToken = it
                    startActivity(DashboardActivity.intent(context))
                }
        }
    }


    private fun setUIEnabled(enabled: Boolean) {
        binding.btnLogin.isEnabled = enabled
        binding.edtUserName.isEnabled = enabled
        binding.edtPassword.isEnabled = enabled
    }

}