package com.example.proj_mad;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterDonor extends AppCompatActivity implements View.OnClickListener{
    EditText name, age, hemo, weight, city, bp,emailreg;
    TextView showdate,termsc;
    RadioGroup genderGroup, dateGroup;
    RadioButton male, female, yes, no;
    Spinner bloodGroup;
    Button register, dateBtn;
    String selectedGender = "",donatedBefore = "No", donatedDate = "NA";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    boolean isEligible = true,termschecked=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_donor);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Donors");
        emailreg=findViewById(R.id.regemail);
        name = findViewById(R.id.full_name);
        age = findViewById(R.id.age);
        hemo = findViewById(R.id.hemoglobin);
        weight = findViewById(R.id.weight);
        city = findViewById(R.id.city);
        bp = findViewById(R.id.bp);
        showdate = findViewById(R.id.tvSelectedDate);
        genderGroup = findViewById(R.id.gender_group);
        dateGroup = findViewById(R.id.dates);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        yes = findViewById(R.id.yesd);
        no = findViewById(R.id.nod);
        bloodGroup = findViewById(R.id.blood_group);
        register = findViewById(R.id.register);
        dateBtn = findViewById(R.id.btnSelectDate);
        termsc=findViewById(R.id.TC);
        termsc.setOnClickListener(this);
        dateBtn.setOnClickListener(this);
        register.setOnClickListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.BG, R.layout.spinner_item); // Use custom layout
        adapter.setDropDownViewResource(R.layout.spinner_item); // Apply the same layout for dropdown items
        bloodGroup.setAdapter(adapter);//loads blood group option from xml array
        dateBtn.setVisibility(View.GONE);
        showdate.setVisibility(View.GONE);

        dateGroup.setOnCheckedChangeListener((group, checkedId) -> {

            //if user selects yes for donated before date picker appears else is hidden
            if (checkedId == R.id.yesd) {
                dateBtn.setVisibility(View.VISIBLE);
                showdate.setVisibility(View.VISIBLE);
                donatedBefore = "Yes";
            } else if (checkedId == R.id.nod) {
                dateBtn.setVisibility(View.GONE);
                showdate.setVisibility(View.GONE);
                donatedBefore = "No";
                donatedDate = "NA";
            }
        });
        genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.male) {
                selectedGender = "Male";
            } else if (checkedId == R.id.female) {
                selectedGender = "Female";
            }
        });

    }


    private void calculate(){
        String donorName = name.getText().toString().trim();
        String donorAge = age.getText().toString().trim();
        String donorHemo = hemo.getText().toString().trim();
        String donorWeight = weight.getText().toString().trim();
        String donorCity = city.getText().toString().trim();
        String donorBP = bp.getText().toString().trim();
        String donorBloodGroup = bloodGroup.getSelectedItem().toString();
        String regmail1=emailreg.getText().toString().trim();
        String lastDonation = showdate.getText().toString().trim();

        String result ="";
        // Use selected donation date if available


        if (donorName.isEmpty() || donorAge.isEmpty() || donorHemo.isEmpty() || donorWeight.isEmpty() ||
                donorCity.isEmpty() || donorBP.isEmpty() || regmail1.isEmpty()|| donorBloodGroup.equals("Select Blood Group")||termschecked==false){
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check last donation date eligibility
        if (donatedBefore.equals("Yes")) {
            if (lastDonation.equals("Selected Date")) {
                Toast.makeText(this, "Please select a valid donation date!", Toast.LENGTH_SHORT).show();
                return;
            }


            if (selectedGender.equals("Male") && !lastDonation.isEmpty()) {
                Calendar selectedDate = Calendar.getInstance();
                String[] parts = lastDonation.split("/");
                selectedDate.set(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[0]));

                Calendar minValidDate = Calendar.getInstance();
                minValidDate.add(Calendar.MONTH, -3);

                donatedDate = lastDonation;

                if (selectedDate.after(minValidDate)) {
                    isEligible = false;
                    result+=("■ Donate only if 3+ months since last donation\n");}

            }
            if (selectedGender.equals("Female") && !lastDonation.isEmpty()) {
                Calendar selectedDate = Calendar.getInstance();
                String[] parts = lastDonation.split("/");
                selectedDate.set(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[0]));

                Calendar minValidDate = Calendar.getInstance();
                minValidDate.add(Calendar.MONTH, -4);

                donatedDate = lastDonation;
                if (selectedDate.after(minValidDate)) {
                    isEligible = false;
                    result+=("■ Donate only if 4+ months since last donation!\n");}
            }


        }




        double hemoLevel = Double.parseDouble(donorHemo);
        int ageValue = Integer.parseInt(donorAge);
        double weightValue = Double.parseDouble(donorWeight);
        int bpValue = Integer.parseInt(donorBP);


        if (selectedGender.equals("Male") && hemoLevel < 13.0) {
            isEligible = false;
            result+=("■ Low Hemoglobin (Should be >13)\n");
        }
        if (selectedGender.equals("Female") && hemoLevel < 12.5) {
            isEligible = false;
            result+=("■ Low Hemoglobin (Should be >12.5)\n");
        }
        if (ageValue < 18 || ageValue > 65) {
            isEligible = false;
            result+=("■ Age must be between 18 and 65\n");
        }
        if (weightValue < 50) {
            isEligible = false;
            result+=("■ Underweight (Min 50 kg)\n");
        }
        if (bpValue < 90 || bpValue > 140) {
            isEligible = false;
            result+=("■ Blood Pressure should be between 90 and 140\n");
        }
        // if eleigible saves data to firebase and sends data to eligible activity
        //if not still goes to elegibility screen but skips firebase save


        // Store data in Bundle
        Bundle bundle = new Bundle();
        bundle.putString("Name", donorName);
        bundle.putString("Age", donorAge);
        bundle.putString("Hemoglobin", donorHemo);
        bundle.putString("Weight", donorWeight);
        bundle.putString("City", donorCity);
        bundle.putString("BP", donorBP);
        bundle.putString("Email",regmail1);
        bundle.putString("BloodGroup", donorBloodGroup);
        bundle.putString("Gender", selectedGender);
        bundle.putString("DonatedBefore", donatedBefore);
        bundle.putString("LastDonationDate", donatedDate);
        bundle.putBoolean("Eligible", isEligible);
        bundle.putString("Reason", result.toString());
        saveDonorToFirebase(bundle);
        Intent intent = new Intent(getApplicationContext(), Eligibility.class);
        intent.putExtras(bundle);
        startActivity(intent);


    }

    private void saveDonorToFirebase(Bundle bundle) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            mAuth.signInAnonymously() //if user isnt logged in signs in anonymously
                    .addOnSuccessListener(authResult -> {
                        Log.d("Firebase", "Anonymous Sign-in Success");
                        saveDonorData(bundle); // Call method to save after login only if eligible
                    })
                    .addOnFailureListener(e -> Log.e("Firebase", "Sign-in Failed: " + e.getMessage()));
            return;
        }
        if(!isEligible){
            return;
        }
        saveDonorData(bundle);
    }

    //converts all bundle values into hashmap,saves under current user id in donors in firebase
    private void saveDonorData(Bundle bundle) {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        HashMap<String, Object> donorData = new HashMap<>();
        for (String key : bundle.keySet()) {
            donorData.put(key, bundle.get(key));
        }

        mDatabase.child(user.getUid()).setValue(donorData)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Data Saved Successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }



    @Override
    public void onClick(View v) {
        int id=v.getId();
        if (id==R.id.btnSelectDate){
            showDate();
        } else if (id==R.id.register) {
            calculate();
        }
        else if (id==R.id.TC) {
            showTerms();
        }
    }

    private void showTerms() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions");
        //displays terms in scrollable alertdialog

        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        TextView message = new TextView(this);
        message.setText("1. Eligibility Criteria: The donor must be between 18-65 years old and weigh at least 50 kg.\n" +
                "2. Health Condition: The donor should be in good health, free from infections, and should not have cold, fever, or flu symptoms.\n" +
                "3. No Recent Surgery/Vaccination: Donors should not have had any major surgery, dental procedure, or vaccination in the last 6 months.\n" +
                "4. No Alcohol or Smoking: Donors should avoid alcohol for at least 24 hours before donation and not smoke for at least 1 hour before donating.\n" +
                "5. No Chronic Diseases: Donors with diabetes, hypertension (uncontrolled), heart disease, or kidney issues should consult a doctor before donating.\n" +
                "6. No Recent Travel or High-Risk Activities: If the donor has recently traveled to malaria-prone areas or engaged in high-risk activities (e.g., tattoos or piercings in the last 6 months), they must inform the medical team.\n" +
                "7. Medication Restriction: Donors on antibiotics, steroids, or blood thinners may not be eligible.\n" +
                "8. No Recent Blood Donation: The donor should not have donated plasma, platelets, or whole blood in the last 90 days.\n" +
                "9. Pregnancy & Menstruation: Pregnant women, lactating mothers, and those on their menstrual cycle should avoid donating.\n" +
                "10. Consent & Honesty: The donor must provide accurate information about their health and consent to the donation process.\n");
        message.setPadding(10, 10, 10, 10);
        message.setTextSize(16);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setText("I ACCEPT THE T&C");

        layout.addView(message);
        layout.addView(checkBox);
        scrollView.addView(layout);
        builder.setView(scrollView);

        //sets flag termschecked to prevent continuing without agreement
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Save state only if checkbox is checked
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
            editor.putBoolean("terms_accepted", checkBox.isChecked());
            editor.apply();
            termschecked = checkBox.isChecked(); // Update global variable
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Disable OK button initially
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        // Enable OK button only when checkbox is checked
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(isChecked)
        );
    }


    private void showDate(){
        // opens date picker,sets date on show date when selected

        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog=new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String sd=dayOfMonth+"/"+(month1 +1)+"/"+ year1;
            showdate.setText(sd);

        },year,month,day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        //restricts past dates
        datePickerDialog.show();
    }
}