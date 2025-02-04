package com.example.guru2024

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper private constructor(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "appDB"
        private const val DB_VERSION = 2

        @Volatile
        private var instance: MyDBHelper? = null

        fun getInstance(context: Context): MyDBHelper {
            return instance ?: synchronized(this) {
                instance ?: MyDBHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE memberTBL (mId TEXT PRIMARY KEY, mPW TEXT, mName TEXT, mCat TEXT);")
        db!!.execSQL("CREATE TABLE timeTBL (mId TEXT, tTime TEXT, tLoc TEXT, tCat TEXT);")
        db!!.execSQL("CREATE TABLE recTBL (mId TEXT, rTime TEXT, rLoc TEXT, rCat TEXT, rContent TEXT);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS memberTBL")
        db!!.execSQL("DROP TABLE IF EXISTS timeTBL")
        db!!.execSQL("DROP TABLE IF EXISTS recTBL")
        onCreate(db)
    }
}