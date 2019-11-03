package com.example.naginowdiary;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activty);
        final EditText mEdtResetEmail = findViewById(R.id.edt_email_reset);
        Button mBtnReset = findViewById(R.id.btn_reset);
        mBtnReset.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String resetEmail = mEdtResetEmail.getText().toString();
                        if (resetEmail.equals("") || !Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
                            mEdtResetEmail.setError("Please enter your email!");
                        } else {
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            auth.sendPasswordResetEmail(resetEmail)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("reset password", "Email sent.");
                                                String result = "Reset Email sent! " +
                                                        "Please check your Email!";
                                                Toast.makeText(ResetPasswordActivity.this,
                                                        result, Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(ResetPasswordActivity.this,
                                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }
        );
    }
}
