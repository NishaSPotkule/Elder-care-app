package com.example.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText email,password;
    TextView signuptxt;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        signuptxt=findViewById(R.id.signuptxt);
        loginBtn=findViewById(R.id.loginBtn);
        auth=FirebaseAuth.getInstance();



            loginBtn.setOnClickListener(v -> {

                String userEmail = email.getText().toString().trim();
                String userPass = password.getText().toString().trim();

                if(userEmail.isEmpty() || userPass.isEmpty()){
                    Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(userEmail, userPass)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
            signuptxt.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));

        }

    }
