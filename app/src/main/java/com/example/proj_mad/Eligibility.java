package com.example.proj_mad;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Eligibility extends AppCompatActivity {
TextView elig,res,reasonn;
ImageView okay;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eligibility);
        elig=findViewById(R.id.eligiblee);
        res=findViewById(R.id.resultss);
        reasonn=findViewById(R.id.reason);
        okay=findViewById(R.id.imgokay);
        Bundle b=getIntent().getExtras(); //get data tht was sent from other activity using intent
        if (b!= null){
            Boolean b1=b.getBoolean("Eligible"); //b1 is true /false
            String s=b.getString("Reason"); //why not elig
            if (b1) {
                elig.setText("You are Eligible to donate blood");
                okay.setImageResource(R.drawable.okay);
            } else{
                reasonn.setText("Reason");
                elig.setText("You are not Eligible to donate blood");
                res.setText(s);
                okay.setImageResource(R.drawable.notokay);
            }
        }

    }
}