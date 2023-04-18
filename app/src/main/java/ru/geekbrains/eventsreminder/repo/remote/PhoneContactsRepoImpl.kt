package ru.geekbrains.eventsreminder.repo.remote

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import androidx.annotation.RequiresApi
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.usecases.addBirthDayEventFromContactPhone
import ru.geekbrains.eventsreminder.usecases.getLocalDateFromBirthDay
import java.time.LocalDate

class PhoneContactsRepoImpl(val context: Context):PhoneContactsRepo {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    override fun loadBirthDayEvents(endDay: Int): List<EventData> {
        val listBirthDayEvents = arrayListOf<EventData>()
        val contentResolver: ContentResolver? =
            context.contentResolver
        contentResolver?.let {
            val cursorWithContacts: Cursor? = it.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )
            cursorWithContacts?.let { cursor ->
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val selection =
                        ContactsContract.Data.RAW_CONTACT_ID + " = " + id + " and " +
                                ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' and " +
                                ContactsContract.CommonDataKinds.Event.TYPE + "='" +
                                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + "'";
                    val cursorWithBD: Cursor? = it.query(
                        ContactsContract.Data.CONTENT_URI, null, selection, null, null
                    )
                    cursorWithBD?.let { cursorBD ->
                        while (cursorBD.moveToNext()) {
                            val birthDay =
                                cursorBD.getString(cursorBD.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE))
                            if (getLocalDateFromBirthDay(birthDay)<= LocalDate.now().plusDays(endDay.toLong())) {
                                listBirthDayEvents.add(
                                    addBirthDayEventFromContactPhone(
                                        name,
                                        birthDay
                                    )
                                )
                            }
                        }
                    }
                    cursorWithBD?.close()
                }
            }
            cursorWithContacts?.close()
        }
        return listBirthDayEvents
    }
}