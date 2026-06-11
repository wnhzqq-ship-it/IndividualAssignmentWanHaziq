package com.example.individiualassignment_wanhaziq;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    TextView textGithubUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textGithubUrl = findViewById(R.id.textGithubUrl);

        textGithubUrl.setOnClickListener(v -> {
            String url = textGithubUrl.getText().toString();

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);

            Toast.makeText(this, "Opening application website.", Toast.LENGTH_SHORT).show();
        });
    }
}