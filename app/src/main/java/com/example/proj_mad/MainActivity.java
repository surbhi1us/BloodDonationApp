package com.example.proj_mad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button donateNow, findDonor;
    TextView name;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        donateNow = findViewById(R.id.donate_now);
        findDonor = findViewById(R.id.find_donor);
        name = findViewById(R.id.name1); // This is your TextView

        // Get the name from the Login activity and set it to the TextView
        String fullName = getIntent().getStringExtra("fullName");
        if (fullName != null) {
            name.setText(fullName);
        }

        donateNow.setOnClickListener(v -> { //when clicked navigates to registration for donation
            Intent intent = new Intent(MainActivity.this, RegisterDonor.class);
            startActivity(intent);
        });

        findDonor.setOnClickListener(v -> { //opens find donor to search for donor by city and blood group
            Intent intent = new Intent(MainActivity.this, FindDonor.class);
            startActivity(intent);
        });
    }
}
