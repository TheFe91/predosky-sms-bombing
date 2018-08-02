package com.rollercoders.predoskysmsbombing;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SmsManager smsManager;
    private static int REQUEST_SMS = 0;
    private static int PHONE_STATE = 1;
    private static int READ_CONTACTS = 2;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this;

        smsManager = SmsManager.getDefault();
        Button go = findViewById(R.id.go);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            bombSMS();
                        }
                        else {
                            Toast.makeText(ctx, "Need Contacts Permissions!", Toast.LENGTH_SHORT).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);
                        }
                    }
                    else {
                        Toast.makeText(ctx, "Need Phone-State Permissions!", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE);
                    }
                }
                else {
                    Toast.makeText(ctx, "Need SMS Send Permissions!", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS);
                }
            }
        });
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
        String[] characters = message.split("");
        number = number.trim();
        number = "+39" + number; //per ora hardcodato codice Italia, poi gestiamo
        for (String character : characters) {
            if (!character.equals("")) {
                smsManager.sendTextMessage(number, null, character, null, null);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /*
    TODO: IMPLEMENTARE METODO PER FARE IL BOMBING VERO E PROPRIO
    TODO: CAPIRE COME CHIAMARE IL SERVIZIO SMS DI ANDROID
    TODO: SPAMMARE PREDO COME SE NON CI FOSSE 1 DOMANI
    */

}
