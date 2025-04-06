package com.example.proj_mad;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class CreateAcc extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth; //signup and login with firebasse
    private DatabaseReference mDatabase; //to store user info in
    EditText name, pass, cpass, mail;
    CheckBox tc;
    Button signup;
    TextView login;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

        // initialize firebase authentication
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        name = findViewById(R.id.full_name);
        pass = findViewById(R.id.passwords);
        cpass = findViewById(R.id.retype_password);
        mail = findViewById(R.id.emails);
        tc = findViewById(R.id.terms_checkbox);
        signup = findViewById(R.id.signup_button);
        login = findViewById(R.id.sign_in_text);
        tc.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showTerms();
            }
        });
        signup.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signup_button) {
            registerUser();
        } else if (v.getId() == R.id.sign_in_text) {
            startActivity(new Intent(CreateAcc.this, Login.class));
        }
    }

    private void registerUser() {
        String Name = name.getText().toString().trim();
        String Mail = mail.getText().toString().trim();
        String Pass = pass.getText().toString().trim();
        String Cpass = cpass.getText().toString().trim();

        if (Name.isEmpty() || Mail.isEmpty() || Pass.isEmpty() || Cpass.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Pass.equals(Cpass)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!tc.isChecked()) {
            Toast.makeText(this, "Agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        // create user (email n pass) -firebase
        mAuth.createUserWithEmailAndPassword(Mail, Pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid(); // get unique user id

                    // store user details in firebase realtime database
                    writeNewUser(userId, Name, Mail, Pass, Cpass);

                    // update firebase authentication profile with display name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            //.setDisplayName(Name)
                            .build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(CreateAcc.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateAcc.this, Login.class));
                            finish(); // Close CreateAcc Activity
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showTerms() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //pop up
        builder.setTitle("Terms and Conditions");

        ScrollView scrollView = new ScrollView(this); // to scroll
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL); //to stack things vertically
        layout.setPadding(50, 20, 50, 20);

        TextView message = new TextView(this);
        message.setText("1. User Responsibility: You are responsible for maintaining the confidentiality of your login credentials and for any activity occurring under your account.\n\n" +
                "2. Data Privacy: Your personal data (name, email, etc.) will be securely stored and used only for authentication purposes.\n\n" +
                "3. Security Measures: We take reasonable precautions to protect your data, but we cannot guarantee complete security against cyber threats.\n\n" +
                "4. Usage Restrictions: You agree not to misuse the platform for illegal, fraudulent, or unauthorized purposes.\n\n" +
                "5. Account Termination: If suspicious activity is detected, we reserve the right to suspend or terminate your account.\n\n" +
                "6. Third-Party Services: Some features may require third-party integrations. By logging in, you consent to these services as per their terms.\n\n" +
                "7. Updates & Changes: We may update these terms from time to time. Continued use of the app means you accept the latest terms.");
        message.setPadding(10, 10, 10, 10);
        message.setTextSize(16);

        layout.addView(message);
        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setPositiveButton("OK", (dialog, which) -> {
            // No checkbox validation, just dismiss the dialog
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void writeNewUser(String userId, String name, String email, String pass, String cpass) {
        // create a hashmap to store all user details
        HashMap<String, String> userData = new HashMap<>();
        userData.put("fullName", name);
        userData.put("email", email);
        userData.put("password", pass);
        userData.put("cpass", cpass);

        // save user data to firebase database under "Users/{userId}"
        mDatabase.child(userId).setValue(userData) //generate unique id
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateAcc.this, "User data saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateAcc.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}