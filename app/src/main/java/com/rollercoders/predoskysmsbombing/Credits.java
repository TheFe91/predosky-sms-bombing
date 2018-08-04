package com.rollercoders.predoskysmsbombing;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class Credits extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        getActionBar();

        Button contact = findViewById(R.id.contact_us);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mailClient = new Intent(Intent.ACTION_SEND);
                mailClient.setData(Uri.parse("mailto:"));
                mailClient.setType("text/plain");
                mailClient.putExtra(Intent.EXTRA_EMAIL, new String[] {"rollercodersteam@gmail.com"});

                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(mailClient, 0);

                if (activities.size() > 0) {
                    String title = "Completa azione con...";
                    Intent chooser = Intent.createChooser(mailClient, title);
                    startActivity(chooser);
                }
                else {
                    Toast.makeText(getApplicationContext(), "There are no email clients installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("interstitial", true);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra("interstitial", true);
        startActivity(i);
        finish();
    }
}
