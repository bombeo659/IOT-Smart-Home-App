package com.iot.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private CheckBox checkbox;
    private FirebaseAuth auth;

    private long backPressedTime;
    private Toast backToast;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        TextView textViewRegister = (TextView) findViewById(R.id.register);
        textViewRegister.setOnClickListener(this);

        TextView textViewForgot = (TextView) findViewById(R.id.forgotPassword);
        textViewForgot.setOnClickListener(this);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        ImageView facebookButton = (ImageView) findViewById(R.id.facebook);
        facebookButton.setOnClickListener(this);

        ImageView googleButton = (ImageView) findViewById(R.id.google);
        googleButton.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();
        checkbox = (CheckBox) findViewById(R.id.checkBox);
        Paper.init(this);

        String UserEmailKey = Paper.book().read(CurrentUser.UserEmailKey);
        String UserPasswordKey = Paper.book().read(CurrentUser.UserPasswordKey);
        if (UserEmailKey != "" && UserPasswordKey != "") {
            if (!TextUtils.isEmpty(UserEmailKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                progressBar.setVisibility(View.VISIBLE);
                AllowAccess(UserEmailKey, UserPasswordKey);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            finishAffinity();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
        case R.id.forgotPassword:
            startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            break;
        case R.id.register:
            startActivity(new Intent(MainActivity.this, RegisterUser.class));
            break;
        case R.id.loginButton:
            loginUser();
            break;
        case R.id.facebook:
            startActivity(new Intent(MainActivity.this, FacebookLogin.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            break;
        case R.id.google:
            startActivity(new Intent(MainActivity.this, GoogleLogin.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            break;
        }
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns. EMAIL_ADDRESS.matcher(email).matches ()) {
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6){
            editTextPassword.setError("Min password length should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()){
                        if(checkbox.isChecked()) {
                            Paper.book().write(CurrentUser.UserEmailKey, email);
                            Paper.book().write(CurrentUser.UserPasswordKey, password);
                        }
                        startActivity(new Intent(MainActivity.this, HomeNavigation.class));
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials!", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void AllowAccess(final String userEmailKey, final String userPasswordKey) {
        auth.signInWithEmailAndPassword(userEmailKey, userPasswordKey).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()){
                        startActivity(new Intent(MainActivity.this, HomeNavigation.class));
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials!", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}