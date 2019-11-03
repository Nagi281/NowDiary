package com.example.naginowdiary;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEdtEmail, mEdtPassword, mEdtConfirm, mEdtNickname;
    private Button mBtnSignUp, mBtnCancel;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        addComponents();
        addEventListener();
    }

    private void addComponents() {
        mEdtEmail = findViewById(R.id.edt_email_register);
        mEdtPassword = findViewById(R.id.edt_password_register);
        mEdtConfirm = findViewById(R.id.edt_confirm_password);
        mEdtNickname = findViewById(R.id.edt_nickname_register);
        mBtnSignUp = findViewById(R.id.btn_signUp);
        mBtnCancel = findViewById(R.id.btn_back_to_login);
        progressBar = findViewById(R.id.pb_register);
        auth = FirebaseAuth.getInstance();
    }

    private void addEventListener() {
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEdtEmail.getText().toString();
                String password = mEdtPassword.getText().toString();
                String confirm = mEdtConfirm.getText().toString();
                String nickname = mEdtNickname.getText().toString();
                if (validation(email, password, confirm, nickname)) {
                    createUser(email, password, nickname);
                }
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean validation(String email, String password, String confirm, String nickname) {
        boolean result = false;
        String error = getResources().getString(R.string.inputBlank);
        if (email.equals("")) {
            mEdtEmail.setError(error);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEdtPassword.setError("Please input email");
        } else if (password.equals("")) {
            mEdtPassword.setError(error);
        } else if (confirm.equals("")) {
            mEdtConfirm.setError(error);
        } else if (nickname.equals("")) {
            mEdtNickname.setError(error);
        } else if (!confirm.equals(password)) {
            mEdtConfirm.setError("Password confirm doesn't match");
        } else {
            result = true;
        }
        return result;
    }

    public void createUser(String email, String password, final String nickname) {
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    createUserNode(auth.getCurrentUser().getUid(), nickname);
                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                String status = "Register Successfully. Please check your email for verification!";
                                Toast.makeText(RegisterActivity.this, status, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this,
                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this,
                            task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createUserNode(final String userId, final String nickname) {
        final DatabaseReference mDbRoot = FirebaseDatabase.getInstance().getReference();
        mDbRoot.child(userId)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    mDbRoot.child(userId).child("userInfo").child("username").setValue(nickname)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this,
                                                                "Wrong", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
    }
}
