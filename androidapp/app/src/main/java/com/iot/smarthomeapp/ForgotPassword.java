package com.iot.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailReset;
    private Button resetPasswordButton;
    private ProgressBar progressBar;
    private TextView loginText;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailReset = (EditText) findViewById(R.id.email);
        resetPasswordButton = (Button) findViewById(R.id.resetPasswordButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginText = (TextView) findViewById(R.id.loginText);
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPassword.this, MainActivity.class));
            }
        });

        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword(){
        String email = emailReset.getText().toString().trim();

        if (email.isEmpty()){
            emailReset.setError("Email is required!");
            emailReset.requestFocus();
            return;
        }
        if (!Patterns. EMAIL_ADDRESS.matcher(email).matches ()) {
            emailReset.setError("Please provide valid email");
            emailReset.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, "Check your email to reset your password!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                }else {
                    Toast.makeText(ForgotPassword.this, "Try again! Something wrong happened!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}