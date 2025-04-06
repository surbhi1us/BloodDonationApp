package com.example.proj_mad;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth; //used to sign users in
    private DatabaseReference mDatabase; //used to get users full profile
    Button loginn, creatacc;
    EditText emaill, pwd;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        loginn = findViewById(R.id.login_button);
        emaill = findViewById(R.id.email);
        pwd = findViewById(R.id.password);
        creatacc = findViewById(R.id.create_account);
        ImageView togglePassword = findViewById(R.id.togglePassword);
        togglePassword.setOnClickListener(v -> { //lets user to see or hide pass by clicking eye icon
            if (isPasswordVisible) {
                pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                togglePassword.setImageResource(R.drawable.ic_eye_closed); // Change to closed eye icon
            } else {
                pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                togglePassword.setImageResource(R.drawable.ic_eye_open); // Change to open eye icon
            }
            isPasswordVisible = !isPasswordVisible;
            pwd.setSelection(pwd.length()); // Keep cursor at end
        });
        loginn.setOnClickListener(this);
        creatacc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_button) {
            loginUser();
        } else if (v.getId() == R.id.create_account) {
            startActivity(new Intent(Login.this, CreateAcc.class));
        }
    }


    private void loginUser() {
        String email = emaill.getText().toString().trim();
        String password = pwd.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        //tries to log user in with firebase
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser(); //gets current logged in user
                if (user != null) {
                    String userId = user.getUid(); // Get Firebase User ID

                    // Fetch User Data from Firebase Realtime Database
                    mDatabase.child(userId).get().addOnCompleteListener(dataTask -> {
                        if (dataTask.isSuccessful()) {
                            DataSnapshot snapshot = dataTask.getResult();
                            if (snapshot.exists()) { //retrives users name and email from firebase
                                String name1 = snapshot.child("fullName").getValue(String.class);
                                String email1 = snapshot.child("email").getValue(String.class);

                                Toast.makeText(Login.this, "Welcome " + name1 + "!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("fullName", name1);
                                intent.putExtra("email", email1);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Login.this, "User data not found!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Login.this, "Database error: " + dataTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_LONG).show();
            }
        });
    }
}

