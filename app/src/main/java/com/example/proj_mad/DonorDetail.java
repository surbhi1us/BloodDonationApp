package com.example.proj_mad;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DonorDetail extends AppCompatActivity {

    private RecyclerView recyclerView;//ui scrollable list
    private DonorAdapter donorAdapter; //fills data into list
    private List<MainModel> donorList; //list of donors to show

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_detail);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //one card below other

        donorList = getIntent().getParcelableArrayListExtra("donorList"); //when called list is sent

        if (donorList == null || donorList.isEmpty()) {
            Toast.makeText(this, "No donors found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        donorAdapter = new DonorAdapter(donorList); //adapter is created using donor list
        recyclerView.setAdapter(donorAdapter); //shows it
    }

}
