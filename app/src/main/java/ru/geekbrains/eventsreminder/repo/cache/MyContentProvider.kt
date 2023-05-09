package ru.geekbrains.eventsreminder.repo.cache

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri


class MyContentProvider : ContentProvider() {
    private var mDbHelper: DbHelper? = null
    override fun onCreate(): Boolean {
        val context: Context? = context
        mDbHelper = DbHelper(context)
        return true
    }


    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db: SQLiteDatabase? = mDbHelper?.readableDatabase
        val match = sUriMatcher.match(uri)
        var retCursor: Cursor? = null
        when (match) {
            EVENTS_CODE -> retCursor = db?.query(
                Contract.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )
            else -> {}
        }
        context?.let {
            retCursor?.setNotificationUri(it.contentResolver, uri)
        }
        return retCursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        var returnUri: Uri? = null
        mDbHelper?.let {
            val db: SQLiteDatabase = it.writableDatabase
            val match = sUriMatcher.match(uri)
            when (match) {
                EVENTS_CODE -> {
                    val id = db.insert(Contract.TABLE_NAME, null, values)
                    if (id > 0) {
                        returnUri = ContentUris.withAppendedId(Contract.PATH_EVENTS_URI, id)
                    }
                }
                else -> {}
            }
            context?.contentResolver?.notifyChange(uri, null)
        }
        return returnUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        var res = 0

        mDbHelper?.let {
            val db: SQLiteDatabase = it.writableDatabase
            val match = sUriMatcher.match(uri)
            when (match) {
                EVENTS_CODE -> {
                    res = db.delete(Contract.TABLE_NAME, "1", null);
                }
                else -> {}
            }
            context?.contentResolver?.notifyChange(uri, null)
        }
        return res
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    companion object {
        const val EVENTS_CODE = 100
        val sUriMatcher = buildUriMatcher()
        fun buildUriMatcher(): UriMatcher {
            val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH_EVENTS, EVENTS_CODE)
            return uriMatcher
        }
    }
}