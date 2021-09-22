package com.fmi.mp;

import static android.content.Intent.EXTRA_PHONE_NUMBER;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

public class AddContact extends Activity {
    private EditText countryCode;
    private EditText country;
    private EditText phoneNumber;
    private EditText firstName;
    private EditText lastName;
    private EditText currency;
    private EditText company;
    private Button saveContact;
    private Button call;
    private Button deleteContact;
    private Button sendSMS;
    private ImageView image;
    private SQLiteDatabase db;
    private PhoneRecord pr;
    private boolean isPopulated = false;
    private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        countryCode = findViewById(R.id.countryCode);
        country = findViewById(R.id.country);
        currency = findViewById(R.id.currency);
        phoneNumber = findViewById(R.id.number);
        firstName = findViewById(R.id.name);
        lastName = findViewById(R.id.surname);
        company = findViewById(R.id.company);
        saveContact = findViewById(R.id.saveContact);
        deleteContact = findViewById(R.id.delete);
        sendSMS = findViewById(R.id.sms);
        call = findViewById(R.id.call);
        image = findViewById(R.id.imageView2);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Serializable record = extras.getSerializable("record");
            if (record != null) {
                isPopulated = true;
                countryCode.setText(((PhoneRecord) record).getCode());
                phoneNumber.setText(((PhoneRecord) record).getPhone());
                firstName.setText(((PhoneRecord) record).getFirstName());
                lastName.setText(((PhoneRecord) record).getLastName());
                company.setText(((PhoneRecord) record).getCompany());
                pr = (PhoneRecord) record;
            }
        }


        PhoneDBHelper helper = new PhoneDBHelper(this);
        db = helper.getWritableDatabase();

        this.requestBuilder = Glide.with(this)
                .using(Glide.buildStreamModelLoader(Uri.class, this), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .listener(new SvgSoftwareLayerSetter<>());

        CountryCodeWatcher watcher = new CountryCodeWatcher(countryCode, this.country, requestBuilder);
        countryCode.addTextChangedListener(watcher);
        saveContact.setOnClickListener(this::addData);

        if (isPopulated) {
            watcher.onTextChanged(pr.getCode(), 1, 1, 1);
        }

        call.setOnClickListener(this::call);
        deleteContact.setOnClickListener(this::deleteContact);
        sendSMS.setOnClickListener(this::sendSMS);
    }


    private void deleteContact(View v) {
        Intent myIntent = new Intent(v.getContext(), MainActivity.class);
        if (!isPopulated) {
            startActivityForResult(myIntent, 0);
            return;
        }
        PhoneDBHelper helper = new PhoneDBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        String selection = PhoneContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER + " LIKE ?";
        String[] selectionArgs = { pr.getPhone() };
        db.delete(PhoneContract.ContactEntry.TABLE_NAME, selection, selectionArgs);
        startActivityForResult(myIntent, 0);
    }

    public void sendSMS(View v) {
        String phone = "+" + countryCode.getText().toString() + phoneNumber.getText().toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this);

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra("address",phone);
            sendIntent.putExtra(EXTRA_PHONE_NUMBER,phone);


            if (defaultSmsPackageName != null) {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            startActivity(sendIntent);

        } else {
            Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address",phone);
            startActivity(smsIntent);
        }


    }

    public void call(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL);

        String phone = "+" + countryCode.getText().toString() + phoneNumber.getText().toString();
        intent.setData(Uri.parse("tel:" + phone));
        this.startActivity(intent);
    }

    public void addData(View data) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(PhoneContract.ContactEntry.COLUMN_NAME_COUNTRY_CODE, countryCode.getText().toString());
        values.put(PhoneContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER, Integer.parseInt(phoneNumber.getText().toString()));
        values.put(PhoneContract.ContactEntry.COLUMN_NAME_FIRST_NAME, firstName.getText().toString());
        values.put(PhoneContract.ContactEntry.COLUMN_NAME_LAST_NAME, lastName.getText().toString());
        values.put(PhoneContract.ContactEntry.COLUMN_NAME_COMPANY, company.getText().toString());

        if (isPopulated) {
            String selection = PhoneContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER + " LIKE ?";
            String[] selectionArgs = {pr.getPhone()};
            db.update(PhoneContract.ContactEntry.TABLE_NAME, values, selection, selectionArgs);
        } else {
            db.insert(PhoneContract.ContactEntry.TABLE_NAME, null, values);
        }
        Intent myIntent = new Intent(data.getContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public class CountryCodeWatcher implements TextWatcher {
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final EditText countryCode;
        private final EditText country;
        private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

        public CountryCodeWatcher(EditText countryCode, EditText country, GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder) {
            this.countryCode = countryCode;
            this.country = country;
            this.requestBuilder = requestBuilder;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            CountryContainer container = CountryHelper.get(countryCode.getText().toString());
            if (container == null) {
                country.setText("Няма такава държава");
                currency.setText("ВАЛУТА");
                image.setImageBitmap(null);
                return;
            }
            country.setText(container.getName());
            currency.setText(container.getCurrency());

            Thread imageFetcher = new Thread(() -> runOnUiThread(() -> {
                Uri uri = Uri.parse(container.getFlag());
                requestBuilder
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .load(uri)
                        .into(image);
            }));

            imageFetcher.start();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
