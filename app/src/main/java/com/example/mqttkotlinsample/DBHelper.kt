package com.example.mqttkotlinsample

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "Smart Trash Bin Data", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE Users(Username text primary key, Password TEXT, First_Name TEXT, Last_Name TEXT)")
        db?.execSQL("CREATE TABLE BinHistory(HistoryID INTEGER primary key autoincrement, Date TEXT, Content INTEGER)")
        db?.execSQL("CREATE TABLE BinContent(ContentID INTEGER primary key autoincrement, Content INTEGER)")

        // Insert data for bin history and content
        var cv = ContentValues()
        cv.put("Username", "admin")
        cv.put("Password", "admin")
        cv.put("First_Name", "Admin")
        cv.put("Last_Name", "Admin")
        db?.insert("Users", null, cv)

        // Insert data for bin history and content (temp)
        var cv2 = ContentValues()
        cv2.put("Date", "Monday")
        cv2.put("Content", 0)
        db?.insert("BinHistory", null, cv2)
        cv2.put("Date", "Tuesday")
        cv2.put("Content", 15)
        db?.insert("BinHistory", null, cv2)
        cv2.put("Date", "Wednesday")
        cv2.put("Content", 25)
        db?.insert("BinHistory", null, cv2)

        var cv3 = ContentValues()
        cv3.put("Content", 0)
        db?.insert("BinContent", null, cv3)
        cv3.put("Content", 15)
        db?.insert("BinContent", null, cv3)
        cv3.put("Content", 25)
        db?.insert("BinContent", null, cv3)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}