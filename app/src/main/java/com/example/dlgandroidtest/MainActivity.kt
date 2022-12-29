package com.example.dlgandroidtest

import android.app.Instrumentation
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    var activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            if (intent != null && intent.getBooleanExtra("registered", false)) {
                Snackbar.make(
                    findViewById(R.id.MainActivityLayout),
                    "Account Registered",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
    /**
     * Initialization of all views in the resource/xml
     */
    //private lateinit var createAccountBtn: Button

    val navigateDetails = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        /**
         * Reference to all views in the resource/xml
         */
       // createAccountBtn = findViewById(R.id.create_account_btn)


    }
    fun createAccount(view: View?) {
        val intent = Intent(this, SecondActivity::class.java)
        activityResultLauncher.launch(intent)

    }
}