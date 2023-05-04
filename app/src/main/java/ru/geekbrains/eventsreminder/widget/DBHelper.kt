package ru.geekbrains.eventsreminder.widget

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DbHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        SQL_CREATE_TABLE = "CREATE TABLE " +
                Contract.TABLE_NAME + "(" +
                Contract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.COL_EVENT_TITLE + " TEXT NOT NULL, " +
                Contract.COL_EVENT_DATE + " INTEGER NOT NULL, " +
                Contract.COL_EVENT_TIME + " TEXT NOT NULL, " +
                Contract.COL_EVENT_TYPE + " TEXT NOT NULL" +
                ")"
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + Contract.TABLE_NAME
        db.execSQL(SQL_DROP_TABLE)
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "eventItems.db"
        private var SQL_DROP_TABLE: String? = null
        private var SQL_CREATE_TABLE: String? = null
    }
}