package com.example.dlgandroidtest


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class SecondActivity : AppCompatActivity() {

    /**
     * Initialization of all views in the resource/xml
     */

    private lateinit var backArrow: ImageButton
    private lateinit var emailInput: AutoCompleteTextView
    private lateinit var passwordInput: EditText
    private lateinit var passwordConfirmInput: EditText
    private lateinit var registerBtn: Button
    private lateinit var wipeServerDataBtn: RadioButton

    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    //private val navigateDetails = Intent()

    private var emailIsGood = false
    private var passwordIsGood = false
    private var passwordMatch = false
    private var server: MockServer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        /**
         * Hides action bar
         */
        supportActionBar?.hide()

        server = MockServer()
        pref = getPreferences(MODE_PRIVATE)
        editor = pref?.edit()


        /**
         * Reference to all views in the resource/xml
         */

        backArrow = findViewById(R.id.back_arrow)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        passwordConfirmInput = findViewById(R.id.password_confirm_input)
        registerBtn = findViewById(R.id.register_btn)

        emailInput.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int) {
                }

                override fun onTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int) {
                }

                override fun afterTextChanged(
                    p0: Editable?) {

                    emailIsGood = validateEmail()
                    emailInput.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.tick,
                        0
                    )
                    if (validateEmail()) {
                        emailInput.setBackgroundResource(R.drawable.rectangle_green)

                    }
                    else {
                        emailInput.setBackgroundResource(R.drawable.rectangle_red)
                    }
                    checkGoodInfo()
                }
            }

        )

        passwordInput.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int) {
                }

                override fun onTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int) {
                }

                override fun afterTextChanged(
                    p0: Editable?) {
                    passwordIsGood = validatePassword()

                    passwordInput.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.tick,
                        0
                    )
                    if(validatePassword()) {
                        passwordInput.setBackgroundResource(R.drawable.rectangle_green)

                    }
                    else {
                        passwordInput.setBackgroundResource(R.drawable.rectangle_red)
                    }
                    checkGoodInfo()
                }

            }
        )

        passwordConfirmInput.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int) {
                }

                override fun onTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int) {
                }

                override fun afterTextChanged(
                    p0: Editable?) {

                    passwordMatch = confirmPassword()
                    passwordConfirmInput.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.tick,
                        0
                    )

                    if(confirmPassword()) {
                        passwordConfirmInput.setBackgroundResource(R.drawable.rectangle_green)


                    }
                    else {
                        passwordConfirmInput.setBackgroundResource(R.drawable.rectangle_red)
                    }
                    checkGoodInfo()
                }

            }
        )

        val emailHistory: Set<String> = HashSet (
            pref?.getStringSet("email_history", HashSet())!!
                )
        if (emailHistory.isNotEmpty()) {
            val adapter = ArrayAdapter(
                this, androidx.appcompat.R.layout.select_dialog_item_material,
                emailHistory.toTypedArray()
            )
            emailInput.setAdapter(adapter)
        }
        registerBtn.background.setColorFilter(
            Color.GRAY, PorterDuff.Mode.MULTIPLY
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == androidx.appcompat.R.id.home) {
            finishCustom()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun finishCustom() {
        registerBtn.background.clearColorFilter()
        finish()
    }

    fun validateEmail(): Boolean {
        val email = emailInput.text.toString()
        if (email.isEmpty()) {
            emailInput.error = "Email is required."
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Provide a valid email address."
            return false
        }
        if (server?.let {
                getPacketData(
                    it.sendMessage("GET, verifyEmail, $email")
                )
            }
            == "1"
        ) {
            emailInput.error = "Email already used."
            return false
        }
        emailInput.error = null
        return true
    }
    
    fun validatePassword() : Boolean {
        val password = passwordInput.text.toString()
        if (password.isEmpty()) {
            passwordInput.error = "Password is required"
            return false
        }
        if (password.length < 8) {
            passwordInput.error = "Password must have at least 8 characters."

            return false
        }
        var hasNumber = false
        var hasUpper = false
        var hasLower = false
        for (char in password.toCharArray()) {
            if (Character.isDigit(char)) hasNumber = true
            else if (Character.isUpperCase(char)) hasUpper = true
            else if (Character.isLowerCase(char)) hasLower = true
            if (hasNumber && hasUpper && hasLower)

                return true
        }
        var missing = " "
        if (!hasNumber) missing += "number" + ", "
        if (!hasUpper) missing += "uppercase" + ", "
        if (!hasLower) missing += "lowercase" + ", "

        if (missing.isNotEmpty()) {
            missing = missing.substring(0, missing.length - 2) + "."
            passwordInput.error = "Password is missing: $missing"
            return true
        }
        passwordInput.error
        return false
    }

    fun checkGoodInfo() {
        if (emailIsGood && passwordIsGood && passwordMatch) {
            if (!registerBtn.isEnabled) registerBtn.background.colorFilter = null
            registerBtn.isEnabled = true
        } else {
            if (registerBtn.isEnabled) registerBtn.background.setColorFilter(
                Color.GRAY, PorterDuff.Mode.MULTIPLY
            )
            registerBtn.isEnabled = false
        }
    }

    fun confirmPassword() : Boolean {
        if (passwordInput.text.toString() != passwordConfirmInput.text.toString()) {
            passwordConfirmInput.error = "Password do not match"
            return false

        }
        passwordConfirmInput.error = null
        return passwordConfirmInput.text.toString().isNotEmpty()
    }

    private fun registerEmail(email: String) {
        val emailList: MutableSet<String> = HashSet(
            pref?.getStringSet("email_list", HashSet())!!
        )
        emailList.add(email)
        editor?.putStringSet("email_list", emailList)?.commit()
    }


    fun backArrow(view: View) {
        finish()
    }

    companion object {
        fun getPacketData(msg: String): String {
            val msgSplit = msg.split(",", 3.toString()).toTypedArray()
            return if (msgSplit.size > 2) msgSplit[2] else ""
        }
    }
    fun registerInfo(view: View) {
        val email = emailInput.text.toString()
        val emailHistory: MutableSet<String> = HashSet(
            pref?.getStringSet("email_history", HashSet())!!

        )
        emailHistory.add(email)
        editor?.putStringSet("email_history", emailHistory)?.commit()

        /**
         * While it is suppose to wait for response, no need to go that far for now
         */
        server?.sendMessage("POST, registerEmail, $email")
        val intent = Intent()
        intent.putExtra("registered", true)
        setResult(RESULT_OK, intent)
        finishCustom()
        //Toast.makeText(this, "To the next page", Toast.LENGTH_SHORT).show()
    }

    fun wipeData(view: View) {
        server!!.clearData()
        wipeServerDataBtn.isChecked = false
        Snackbar.make(
            findViewById(R.id.SecondActivityLayout),
            "Data wiped",
            Snackbar.LENGTH_LONG
        ).show()
    }

    /**
     * Creates internal class
     * Sensitive information are stored in the client. If we are to mock email verification.
     * This mocks client-server relationship as well.
     */
    internal  inner class MockServer {
        private val pref = getPreferences(MODE_PRIVATE)
        private val editor = pref.edit()

        /**
         * Request Format: mode, event, data
         * Response Format: code, event, data
         */

        fun sendMessage(msg: String): String {
            var msg = msg
            var msgSplit = msg.split(",", 3.toString()).toTypedArray()
            val msgMode = msgSplit[0]
            msg = msgSplit[0]
            msgSplit = msg.split(",", 3.toString()).toTypedArray()
            val msgEvent = msgSplit[0]
            msg = msgSplit[0]
            var  result = ""

            if (msgMode == "GET") {
                when (msgEvent) {
                    "verifyEmail" -> result = if (verifyEmail(msg) == true) "1" else
                        "0"
                    "Something else" -> {}
                        else -> return "400,,"
                    }
                } else if (msgMode == "POST") {
                    when (msgEvent) {
                        "registerEmail else" -> registerEmail(msg)
                        "Something else" -> {}
                    }
                } else return "400,,"
            return "200, $msgEvent, $result"
            }

    private fun verifyEmail(email: String): Boolean? {
        val emailList = pref?.getStringSet("email_list", HashSet())
        return emailList?.contains(email)
        }
        
        /**
         * Just for testing purposes, here's an option to wipe the stored email list.
         */
        fun clearData() {
            editor?.clear()?.commit()
        }
    }
}