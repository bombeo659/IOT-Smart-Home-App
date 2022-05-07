package com.iot.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iot.smarthomeapp.Prevalent;
import com.iot.smarthomeapp.User;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextEmail, editTextPassword;
    private TextView textViewRegister, textViewForgot;
    private Button loginButton;
    private CheckBox checkbox;
    private ProgressBar progressBar;
    private ProgressDialog loadingBar;
    private FirebaseAuth auth;
    private String parentDbName = "Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        textViewRegister = (TextView) findViewById(R.id.register);
        textViewRegister.setOnClickListener(this);
        textViewForgot = (TextView) findViewById(R.id.forgotPassword);
        textViewForgot.setOnClickListener(this);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        loadingBar = new ProgressDialog(this);
        checkbox = (CheckBox) findViewById(R.id.checkBox);
        Paper.init(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

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
            case R.id.checkBox:
                autoLoginUser();
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
        else if (!Patterns. EMAIL_ADDRESS.matcher(email).matches ()) {
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return;
        }

        else if (password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        else if (password.length() < 6){
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
                        startActivity(new Intent(MainActivity.this, HomeScreen.class));
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
    private void autoLoginUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            AllowAccessToAccount(email, password);
        }
    }
    private void AllowAccessToAccount (final String p_email, final String p_password) {
        if(checkbox.isChecked()) {
            Paper.book().write(Prevalent.UserEmail, p_email);
            Paper.book().write(Prevalent.UserPassword, p_password);
        }
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbName).child(p_email).exists())
                {
                    User usersData = dataSnapshot.child(parentDbName).child(p_email).getValue(User.class);

                    if (usersData.getEmail().equals(p_email))
                    {
                        if (usersData.getPassword().equals(p_password))
                        {
                            if (parentDbName.equals("Admins"))
                            {
                                Toast.makeText(MainActivity.this, "Welcome Admin, you are logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users"))
                            {
                                Toast.makeText(MainActivity.this, "logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Account with this " + p_email + " do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
//        InitMapping();
//        StartMQTT();
//        //Start Login
//        signIn.setOnClickListener(v -> {
//            Editable username = user.getText();
//            Editable pass = password.getText();
//            if(username.length() == 0){
//                Toast.makeText(MainActivity.this, "Please enter your Username", Toast.LENGTH_SHORT).show();
//            }
//            else if(pass.length() == 0){
//                Toast.makeText(MainActivity.this, "Please enter your Password", Toast.LENGTH_SHORT).show();
//            }
//            else{
//                if(cbRemember.isChecked()){
//                    saveLoginData(username.toString(), pass.toString());
//                }
//                else{
//                    clearLoginData();
//                }
//                if(username.toString().equals(userLogin) && pass.toString().equals(passLogin)){
//                    getLastValue_Temp();
//                    getLastValue_Humi();
//                    getLastValue_Led();
//                    getLastValue_Pump();
//                    Home.setVisibility(View.VISIBLE);
//                    Login.setVisibility(View.GONE);
//                    Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(MainActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        signUp.setOnClickListener(v -> {
//            Editable username = user.getText();
//            Editable pass = password.getText();
//            Editable phoneNumber = phone.getText();
//            if(username.length() <= 3){
//                Toast.makeText(MainActivity.this, "Please enter your Username above 3 character", Toast.LENGTH_SHORT).show();
//            }
//            else if(pass.length() <= 3){
//                Toast.makeText(MainActivity.this, "Please enter your Password above 3 character", Toast.LENGTH_SHORT).show();
//            }
//            else if(phoneNumber.length() != 10){
//                Toast.makeText(MainActivity.this, "Phone is not correct", Toast.LENGTH_SHORT).show();
//            }
//            else{
//                userLogin = username.toString();
//                passLogin = pass.toString();
//                switchCompat.setChecked(false);
//                Toast.makeText(MainActivity.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
//            }
//        });
//        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if(isChecked){
//                startActivity(new Intent(MainActivity.this, RegisterUser.class));
////                user.setText("");
////                password.setText("");
////                cbRemember.setChecked(false);
////                phone.setText("");
////                signIn.setVisibility(View.INVISIBLE);
////                cbRemember.setVisibility(View.INVISIBLE);
////                textForgot.setVisibility(View.INVISIBLE);
////                imgPhone.setVisibility(View.VISIBLE);
////                textPhone.setVisibility(View.VISIBLE);
////                phone.setVisibility(View.VISIBLE);
////                signUp.setVisibility(View.VISIBLE);
//            } else{
//                user.setText("");
//                password.setText("");
//                cbRemember.setChecked(false);
//                phone.setText("");
//                signIn.setVisibility(View.VISIBLE);
//                cbRemember.setVisibility(View.VISIBLE);
//                textForgot.setVisibility(View.VISIBLE);
//                imgPhone.setVisibility(View.INVISIBLE);
//                textPhone.setVisibility(View.INVISIBLE);
//                phone.setVisibility(View.INVISIBLE);
//                signUp.setVisibility(View.INVISIBLE);
//            }
//        });
//        //create shared preferences
//        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        // load username and password were saved
//        loadLoginData();
//        //End Login
//
//        // Start Home
//        logout.setOnClickListener(v -> {
//            Home.setVisibility(View.GONE);
//            Login.setVisibility(View.VISIBLE);
//        });
//
//        switch_led.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if(isChecked){
//                mqttHelper.publishToTopic("iotg06/feeds/bk-iot-led", "1");
//                led_image.setImageDrawable(getDrawable(R.drawable.ic_led_on));
//            }
//            else{
//                mqttHelper.publishToTopic("iotg06/feeds/bk-iot-led", "0");
//                led_image.setImageDrawable(getDrawable(R.drawable.ic_led_off));
//            }
//        });
//        switch_pump.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if(isChecked){
//                mqttHelper.publishToTopic("iotg06/feeds/bk-iot-pump", "1");
//                pump_image.setImageDrawable(getDrawable(R.drawable.ic_motor_on));
//            }
//            else{
//                mqttHelper.publishToTopic("iotg06/feeds/bk-iot-pump", "0");
//                pump_image.setImageDrawable(getDrawable(R.drawable.ic_motor_off));
//            }
//        });
        // End Home



//    void InitMapping() {
//        Login = findViewById(R.id.Login);
//        Home = findViewById(R.id.Home);
//        //Start Login
//        user = findViewById(R.id.userName);
//        password = findViewById(R.id.passWord);
//        phone = findViewById(R.id.login_phoneNum);
//        cbRemember = findViewById(R.id.cbRemember);
//        textForgot = findViewById(R.id.text_forgot);
//        signIn = findViewById(R.id.btnSubmit);
//        switchCompat = findViewById(R.id.btnSign);
//        signUp = findViewById(R.id.btnSub);
//        textPhone = findViewById(R.id.text_phone);
//        imgPhone = findViewById(R.id.image_phone);
//        //End Login
//
//        // Start Home
//        logout = findViewById(R.id.logout_btn);
//        data_temp = findViewById(R.id.data_temp);
//        data_temp_check = findViewById(R.id.data_temp_check);
//        data_humi = findViewById(R.id.data_humidity);
//        data_humi_check = findViewById(R.id.data_humidity_check);
//        switch_led = findViewById(R.id.switch_led);
//        switch_pump = findViewById(R.id.switch_pump);
//        led_image = findViewById(R.id.led_image);
//        pump_image = findViewById(R.id.pump_image);
//        // End Home
//    }

//    private void clearLoginData() {
//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.clear();
//        editor.apply();
//    }
//    private void saveLoginData(String username, String Pass) {
//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.putString(USERNAME, username);
//        editor.putString(PASS, Pass);
//        editor.putBoolean(REMEMBER,cbRemember.isChecked());
//        editor.apply();
//    }
//    private void loadLoginData() {
//        if(sharedpreferences.getBoolean(REMEMBER,false)) {
//            user.setText(sharedpreferences.getString(USERNAME, ""));
//            password.setText(sharedpreferences.getString(PASS, ""));
//            cbRemember.setChecked(true);
//        } else
//            cbRemember.setChecked(false);
//    }
//
//    private void StartMQTT(){
//        mqttHelper = new MQTTHelper(getApplicationContext(), "nqt");
//        mqttHelper.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {}
//
//            @Override
//            public void connectionLost(Throwable cause) {}
//
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void messageArrived(String topic, MqttMessage message){
//                if(topic.contains("bk-iot-temp")){
//                    data_temp.setText(message.toString());
//                    int temp = Integer.parseInt(message.toString());
//                    if(temp > 28){
//                        data_temp_check.setText("Hot");
//                        data_temp_check.setTextColor(Color.RED);
//                    }
//                    else if(temp < 24){
//                        data_temp_check.setText("Cold");
//                        data_temp_check.setTextColor(Color.RED);
//                    }else{
//                        data_temp_check.setText("Normal");
//                        data_temp_check.setTextColor(Color.BLACK);
//                    }
//                }
//
//                if(topic.contains("bk-iot-humi")){
//                    data_humi.setText(message.toString());
//                    int humidity = Integer.parseInt(message.toString());
//                    if(humidity > 70){
//                        data_humi_check.setText("Humid air");
//                        data_humi_check.setTextColor(Color.RED);
//                    }
//                    else if(humidity < 40){
//                        data_humi_check.setText("Dry air");
//                        data_humi_check.setTextColor(Color.RED);
//                    }else{
//                        data_humi_check.setText("Normal");
//                        data_humi_check.setTextColor(Color.BLACK);
//                    }
//                }
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {}
//        });
//    }
//
//    private void getLastValue_Temp(){
//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-temp/data?limit=1";
//
//        @SuppressLint("SetTextI18n")
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
//            try {
//                JSONObject info = response.getJSONObject(0);
//                data_temp.setText(info.getString("value"));
//                int temp = Integer.parseInt(info.getString("value"));
//                if(temp > 28){
//                    data_temp_check.setText("Hot");
//                    data_temp_check.setTextColor(Color.RED);
//                }
//                else if(temp < 24){
//                    data_temp_check.setText("Cold");
//                    data_temp_check.setTextColor(Color.RED);
//                }else{
//                    data_temp_check.setText("Normal");
//                    data_temp_check.setTextColor(Color.BLACK);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show());
//        queue.add(request);
//    }
//
//    private void getLastValue_Humi(){
//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-humi/data?limit=1";
//
//        @SuppressLint("SetTextI18n")
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
//            try {
//                JSONObject info = response.getJSONObject(0);
//                data_humi.setText(info.getString("value"));
//                int humidity = Integer.parseInt(info.getString("value"));
//                if(humidity > 70){
//                    data_humi_check.setText("Humid air");
//                    data_humi_check.setTextColor(Color.RED);
//                }
//                else if(humidity < 40){
//                    data_humi_check.setText("Dry air");
//                    data_humi_check.setTextColor(Color.RED);
//                }else{
//                    data_humi_check.setText("Normal");
//                    data_humi_check.setTextColor(Color.BLACK);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show());
//        queue.add(request);
//    }
//
//    private void getLastValue_Led(){
//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-led/data?limit=1";
//        @SuppressLint("UseCompatLoadingForDrawables")
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
//            try {
//                JSONObject info = response.getJSONObject(0);
//                if(info.getString("value").equals("1")){
//                    switch_led.setChecked(true);
//                    led_image.setImageDrawable(getDrawable(R.drawable.ic_led_on));
//                } else{
//                    switch_led.setChecked(false);
//                    led_image.setImageDrawable(getDrawable(R.drawable.ic_led_off));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show());
//        queue.add(request);
//    }
//
//    private void getLastValue_Pump(){
//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-pump/data?limit=1";
//        @SuppressLint("UseCompatLoadingForDrawables")
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
//            try {
//                JSONObject info = response.getJSONObject(0);
//                if(info.getString("value").equals("1")){
//                    switch_pump.setChecked(true);
//                    pump_image.setImageDrawable(getDrawable(R.drawable.ic_motor_on));
//                } else{
//                    switch_pump.setChecked(false);
//                    pump_image.setImageDrawable(getDrawable(R.drawable.ic_motor_off));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show());
//        queue.add(request);
//    }
//}
