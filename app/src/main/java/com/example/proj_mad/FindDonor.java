package com.example.proj_mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindDonor extends AppCompatActivity {

    private EditText cityEditText;
    private Spinner bloodGroupSpinner;
    private Button findDonorButton;
    private DatabaseReference databaseReference;
    private ArrayList<MainModel> donorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_donor);

        cityEditText = findViewById(R.id.city);
        bloodGroupSpinner = findViewById(R.id.blood_group);
        findDonorButton = findViewById(R.id.find_donor_button);
        databaseReference = FirebaseDatabase.getInstance().getReference("Donors"); //connects to donors section of firebase realtime database

        donorList = new ArrayList<>();

        // Load blood groups from strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.BG, R.layout.spinner_item); // Use custom layout
        adapter.setDropDownViewResource(R.layout.spinner_item); // Apply the same layout for dropdown items
        bloodGroupSpinner.setAdapter(adapter);


        findDonorButton.setOnClickListener(v -> {
            String city = cityEditText.getText().toString().trim();
            String bloodGroup = bloodGroupSpinner.getSelectedItem().toString(); //gets user input

            if (city.isEmpty() || bloodGroup.equals("Select Blood Group")) {
                Toast.makeText(FindDonor.this, "Please enter city and select a blood group!", Toast.LENGTH_SHORT).show();
                return;
            }

            findDonor(bloodGroup, city);
        });
    }

    private void findDonor(String bloodGroup, String city) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() { //reads donor data from firebase only once not live updating
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donorList.clear();  // Clear previous data

                for (DataSnapshot donorSnapshot : snapshot.getChildren()) { //extracts each donors details from firebase
                    String name = donorSnapshot.child("Name").getValue(String.class);
                    String email = donorSnapshot.child("Email").getValue(String.class);
                    String blood = donorSnapshot.child("BloodGroup").getValue(String.class);
                    String donorCity = donorSnapshot.child("City").getValue(String.class);

                    if (blood != null && donorCity != null &&
                            blood.equalsIgnoreCase(bloodGroup) &&
                            donorCity.equalsIgnoreCase(city)) {
                        donorList.add(new MainModel(name, email, blood));
                    } //compares user input with donor city and blood group not case sensitive
                }

                if (donorList.isEmpty()) {
                    Toast.makeText(FindDonor.this, "No donors found", Toast.LENGTH_SHORT).show();
                } else {
                    // pass data to DonorDetail
                    Intent intent = new Intent(FindDonor.this, DonorDetail.class);
                    intent.putParcelableArrayListExtra("donorList", donorList);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FindDonor.this, "Error fetching donors: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
