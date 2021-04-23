package com.example.mqttkotlinsample

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Database variables
        var helper = DBHelper(applicationContext)
        var db = helper.readableDatabase
        var rs = db.rawQuery("SELECT * FROM Users", null)

        if (rs.moveToNext())
            Toast.makeText(applicationContext,rs.getString(1), Toast.LENGTH_LONG)

        // register button
        btnRegister.setOnClickListener {
            if (txtFirstname.text.trim().isNotEmpty() && txtLastname.text.trim().isNotEmpty() && txtNewUsername.text.trim().isNotEmpty() && txtNewPassword.text.trim().isNotEmpty()) {
                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent);
            } else {
                Toast.makeText(this, "Input required", Toast.LENGTH_LONG).show()
            }

            // Inserting username and password to database
            var cv = ContentValues()
            cv.put("Username", txtNewUsername.text.toString())
            cv.put("Password", txtNewPassword.text.toString())
            cv.put("First_Name", txtFirstname.text.toString())
            cv.put("Last_Name", txtLastname.text.toString())
            db.insert("Users", null,cv)
        }

        // sign-in text button
        txtSignInHere.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    }
}