package com.iot.smarthomeapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.iot.smarthomeapp.ForgotPassword;
import com.iot.smarthomeapp.MainActivity;
import com.iot.smarthomeapp.R;


public class ChangePasswordFragment extends Fragment {

    private EditText emailReset;
    private ProgressBar progressBar;

    private FirebaseAuth auth;

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

       emailReset = (EditText) view.findViewById(R.id.resetEmail);
       progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

       Button resetPasswordButton = (Button) view.findViewById(R.id.resetPasswordButton);
       resetPasswordButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               resetPassword();
           }
       });

       auth = FirebaseAuth.getInstance();

       return  view;
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
                    Toast.makeText(getContext(), "Check your email to reset your password!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getContext(), MainActivity.class));
                }else {
                    Toast.makeText(getContext(), "Try again! Something wrong happened!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}