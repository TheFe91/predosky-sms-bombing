package com.rollercoders.predoskysmsbombing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private SmsManager smsManager;
    private static int REQUEST_SMS = 0;
    private static int PHONE_STATE = 1;
    private static int READ_CONTACTS = 2;
    private final int PICK_CONTACT = 3;
    private Context ctx;
    private ProgressBar progressBar;
    private TextView counter, total, splitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this;
        progressBar = findViewById(R.id.progress_bar);
        counter = findViewById(R.id.counter_pt1);
        splitter = findViewById(R.id.splitter);
        total = findViewById(R.id.counter_pt2);

        smsManager = SmsManager.getDefault();
        Button go = findViewById(R.id.go);
        ImageButton chooseContact = findViewById(R.id.contact);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        bombSMS();
                    }
                    else {
                        Toast.makeText(ctx, "Need Phone-State Permissions!", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE);
                    }
                }
                else {
                    Toast.makeText(ctx, R.string.need_sms_permission, Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS);
                }
            }
        });

        chooseContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                }
                else {
                    Toast.makeText(ctx, R.string.need_contacts_permission, Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            try {
                Uri contactUri = data.getData();
                assert contactUri != null;
                Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                EditText numberObj = findViewById(R.id.smsNumber);
                numberObj.setText(cursor.getString(column));
                cursor.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bombSMS();
            }
            else {
                Toast.makeText(this, "Permission wasn't granted!", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == PHONE_STATE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bombSMS();
            }
            else {
                Toast.makeText(this, "Permission wasn't granted!", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bombSMS();
            }
            else {
                Toast.makeText(this, "Permission wasn't granted!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void bombSMS() {
        EditText messageObj = findViewById(R.id.smsText);
        EditText numberObj = findViewById(R.id.smsNumber);
        String number = numberObj.getText().toString();
        String message = messageObj.getText().toString().replace(" ", "");

        if (number.equals("") || message.equals(""))
            return;

        number = number.trim();

        Bomber bomber = new Bomber(this);
        bomber.execute(number, message);
    }

    private static class Bomber extends AsyncTask<String, Integer, String> {
        private WeakReference<MainActivity> activityWeakReference;

        Bomber (MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.progressBar.setVisibility(View.VISIBLE);
            activity.counter.setVisibility(View.VISIBLE);
            activity.splitter.setVisibility(View.VISIBLE);
            activity.total.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) return "";

            String number = strings[0], message = strings[1];

            String[] characters = message.split("");
            int baseProgressUnit = 100 / message.length();
            int i = baseProgressUnit;
            int messageCounter = 0;
            for (String character : characters) {
                if (!character.equals("")) {
                    activity.smsManager.sendTextMessage(number, null, character, null, null);
                    messageCounter++;
                    try {
                        Thread.sleep(750);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finally {
                        publishProgress(i, messageCounter, message.length());
                        i += baseProgressUnit;
                    }
                }
            }

            return "SPAMMED! ^^";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) return;

            super.onProgressUpdate(values);
            if (activity.total.getText().equals(""))
                activity.total.setText(String.valueOf(values[2]));
            activity.progressBar.setProgress(values[0]);
            activity.counter.setText(String.valueOf(values[1]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.progressBar.setProgress(0);
            activity.progressBar.setVisibility(View.INVISIBLE);
            activity.total.setVisibility(View.INVISIBLE);
            activity.splitter.setVisibility(View.INVISIBLE);
            activity.counter.setVisibility(View.INVISIBLE);

            Toast.makeText(activity, s, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.credits:
                Intent i = new Intent(this, Credits.class);
                this.startActivity(i);
                onPause();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /*
     * TODO: SPAMMARE PREDO COME SE NON CI FOSSE 1 DOMANI
     */

}
