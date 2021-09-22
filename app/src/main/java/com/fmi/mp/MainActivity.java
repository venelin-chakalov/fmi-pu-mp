package com.fmi.mp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView myList;
    private Button addContact;
    private Button deleteContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CountryHelper.init();
        myList = findViewById(R.id.listView);
        addContact = findViewById(R.id.addContact);
        deleteContact = findViewById(R.id.deleteAllContacts);
        List<PhoneRecord> myContacts = getMyContacts();
        myList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myContacts));
        Helper.getListViewSize(myList);
        myList.setOnItemClickListener(this::listener);
        addContact.setOnClickListener((view) -> {
            Intent myIntent = new Intent(view.getContext(), AddContact.class);
            startActivityForResult(myIntent, 0);
        });

        deleteContact.setOnClickListener(this::deleteAll);

    }
    public void deleteAll(View v) {
        PhoneDBHelper helper = new PhoneDBHelper(this);
        helper.purgeAll(helper.getWritableDatabase());

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public List<PhoneRecord> getMyContacts() {
        PhoneDBHelper helper = new PhoneDBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                PhoneContract.ContactEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        List<PhoneRecord> records = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(PhoneContract.ContactEntry._ID));
            String code = cursor.getString(
                    cursor.getColumnIndexOrThrow(PhoneContract.ContactEntry.COLUMN_NAME_COUNTRY_CODE)
            );

            String firstName = cursor.getString(
                    cursor.getColumnIndexOrThrow(PhoneContract.ContactEntry.COLUMN_NAME_FIRST_NAME)
            );

            String lastName = cursor.getString(
                    cursor.getColumnIndexOrThrow(PhoneContract.ContactEntry.COLUMN_NAME_LAST_NAME)
            );

            String phoneNumber = cursor.getString(
                    cursor.getColumnIndexOrThrow(PhoneContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER)
            );

            String company = cursor.getString(
                    cursor.getColumnIndexOrThrow(PhoneContract.ContactEntry.COLUMN_NAME_COMPANY)
            );

            records.add(new PhoneRecord(id, firstName, lastName, phoneNumber, code, company));
        }
        cursor.close();
        return records;
    }

    public void listener(AdapterView<?> adapter, View view, int position, long arg) {
        PhoneRecord item = (PhoneRecord) myList.getItemAtPosition(position);

        Intent myIntent = new Intent(view.getContext(), AddContact.class);
        myIntent.putExtra("record", item);
        startActivityForResult(myIntent, 0);

    }
}