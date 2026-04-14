package com.example.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    EditText name, email, password, phone;
    TextView logintxt;
    MaterialButton signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        signupBtn = findViewById(R.id.signupBtn);
        logintxt = findViewById(R.id.logintxt);


        logintxt.setOnClickListener(v ->
                startActivity(new Intent(SignupActivity.this, LoginActivity.class))
        );

        signupBtn.setOnClickListener(v -> {

            String userName = name.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String userPass = password.getText().toString().trim();
            String userPhone = phone.getText().toString().trim();


            if (userName.isEmpty() || userEmail.isEmpty() || userPass.isEmpty() || userPhone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userPass.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userPhone.length() < 10) {
                Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }


            mAuth.createUserWithEmailAndPassword(userEmail, userPass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            String userId = mAuth.getCurrentUser().getUid();


                            Map<String, Object> user = new HashMap<>();
                            user.put("name", userName);
                            user.put("email", userEmail);
                            user.put("phone", userPhone);

                            db.collection("users").document(userId)
                                    .set(user)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Signup Successful 🎉", Toast.LENGTH_SHORT).show();


                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });

                        } else {

                            Toast.makeText(this,
                                    "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}