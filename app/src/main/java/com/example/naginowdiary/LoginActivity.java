package com.example.naginowdiary;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private EditText mEdtEmail, mEdtPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button mBtnLogin, mBtnSignUp, mBtnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanced) {
        super.onCreate(savedInstanced);
        setContentView(R.layout.gooole_login_activity);
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        mEdtEmail = findViewById(R.id.edt_email);
        mEdtPassword = findViewById(R.id.edt_password);
        progressBar = findViewById(R.id.progress_bar);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnSignUp = findViewById(R.id.btn_signUp);
        mBtnResetPassword = findViewById(R.id.btn_reset_password);
        auth = FirebaseAuth.getInstance();
        addEventsListener();
    }

    private void addEventsListener() {
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        mBtnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
            }
        });
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEdtEmail.getText().toString();
                Log.d("verifyLogin","1");
                final String password = mEdtPassword.getText().toString();
                if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), "Enter Email address!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter Password address!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("verifyLogin","2");
                    progressBar.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (!task.isSuccessful()) {
                                        if (password.length() < 6) {
                                            mEdtPassword.setError("Login Failed! Password too short");
                                        }
                                        Toast.makeText(getApplicationContext(),
                                                "Invalid email or Password", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (auth.getCurrentUser().isEmailVerified()) {
                                            Log.d("verify","Email verified");
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            //auth.signOut();
                                            Log.d("verify","Email not verified");
                                            Toast.makeText(LoginActivity.this, "Please verify your account first", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
}
