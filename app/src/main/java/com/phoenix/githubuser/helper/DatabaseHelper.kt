package com.phoenix.githubuser.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.phoenix.githubuser.db.UserContract
import com.phoenix.githubuser.db.UserContract.UserColumns.Companion.TABLE_NAME

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "dbfavuser"
        private const val DATABASE_VERSION = 1
        private val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME" +
                " (${UserContract.UserColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${UserContract.UserColumns.COL_USERNAME} TEXT NOT NULL," +
                " ${UserContract.UserColumns.COL_AVATAR} TEXT NOT NULL," +
                " ${UserContract.UserColumns.COL_NAME} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

}