package com.example.mqttkotlinsample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.txtRegisterHere


class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        var signInHelper = DBHelper(applicationContext)
        var signInDB = signInHelper.readableDatabase

        // Sign in Button
        btnSignIn.setOnClickListener {

            if (txtUsername.text.trim().isNotEmpty() && txtPassword.text.trim().isNotEmpty()) {
                //validate data from database
                var args = listOf<String>(txtUsername.text.toString(), txtPassword.text.toString()).toTypedArray()
                var queryData = signInDB.rawQuery("SELECT Username, Password FROM Users WHERE Username = ? AND Password = ?", args)

                if(queryData.moveToNext()){
                    Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent);
                } else{
                    Toast.makeText(this, "Invalid Credential", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "Input required", Toast.LENGTH_LONG).show()
            }
        }

        // Clickable plain text to go to register page
        txtRegisterHere.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent);
        }
    }
}