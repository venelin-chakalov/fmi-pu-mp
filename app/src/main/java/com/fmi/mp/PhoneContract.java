package com.fmi.mp;

import android.provider.BaseColumns;

public final class PhoneContract {

    private PhoneContract() {}

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + ContactEntry.TABLE_NAME + " (" +
                    ContactEntry._ID + " INTEGER PRIMARY KEY," +
                    ContactEntry.COLUMN_NAME_COUNTRY_CODE + " INTEGER," +
                    ContactEntry.COLUMN_NAME_PHONE_NUMBER + " INTEGER," +
                    ContactEntry.COLUMN_NAME_COMPANY + " TEXT," +
                    ContactEntry.COLUMN_NAME_FIRST_NAME + " TEXT," +
                    ContactEntry.COLUMN_NAME_LAST_NAME + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME;

    /* Inner class that defines the table contents */
    public static class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "Contact";

        public static final String _ID = "id";
        public static final String COLUMN_NAME_COUNTRY_CODE = "country_code";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_NAME_COMPANY = "company";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
    }
}