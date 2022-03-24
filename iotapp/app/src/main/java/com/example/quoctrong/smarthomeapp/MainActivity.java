package com.example.quoctrong.smarthomeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    ConstraintLayout Login, Home;
    //    Login
    EditText user, password, phone;
    Button signIn, signUp;
    SwitchCompat switchCompat;
    TextView textForgot, textPhone;
    ImageView imgPhone;
    CheckBox cbRemember;
    SharedPreferences sharedpreferences;
    String userLogin = "admin";
    String passLogin = "admin";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String USERNAME = "userNameKey";
    public static final String PASS = "passKey";
    public static final String REMEMBER = "remember";
    //    Login

    // Home
    Button logout;
    TextView data_temp, data_temp_check, data_humi, data_humi_check;
    SwitchCompat switch_led, switch_pump;
    ImageView led_image, pump_image;
    // Home

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitMapping();
        StartMQTT();
        //Start Login
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable username = user.getText();
                Editable pass = password.getText();
                if(username.length() == 0){
                    Toast.makeText(MainActivity.this, "Please enter your Username", Toast.LENGTH_SHORT).show();
                }
                else if(pass.length() == 0){
                    Toast.makeText(MainActivity.this, "Please enter your Password", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(cbRemember.isChecked()){
                        saveLoginData(username.toString(), pass.toString());
                    }
                    else{
                        clearLoginData();
                    }
                    if(username.toString().equals(userLogin) && pass.toString().equals(passLogin)){
                        Home.setVisibility(View.VISIBLE);
                        Login.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Username or password is incorrect", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable username = user.getText();
                Editable pass = password.getText();
                Editable phoneNumber = phone.getText();
                if(username.length() <= 3){
                    Toast.makeText(MainActivity.this, "Please enter your Username above 3 character", Toast.LENGTH_SHORT).show();
                }
                else if(pass.length() <= 3){
                    Toast.makeText(MainActivity.this, "Please enter your Password above 3 character", Toast.LENGTH_SHORT).show();
                }
                else if(phoneNumber.length() != 10){
                    Toast.makeText(MainActivity.this, "Phone is not correct", Toast.LENGTH_SHORT).show();
                }
                else{
                    userLogin = username.toString();
                    passLogin = pass.toString();
                    Home.setVisibility(View.VISIBLE);
                    Login.setVisibility(View.GONE);
                }
            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                signIn.setVisibility(buttonView.INVISIBLE);
                cbRemember.setVisibility(buttonView.INVISIBLE);
                textForgot.setVisibility(buttonView.INVISIBLE);
                imgPhone.setVisibility(buttonView.VISIBLE);
                textPhone.setVisibility(buttonView.VISIBLE);
                phone.setVisibility(buttonView.VISIBLE);
                signUp.setVisibility(buttonView.VISIBLE);
            } else{
                signIn.setVisibility(buttonView.VISIBLE);
                cbRemember.setVisibility(buttonView.VISIBLE);
                textForgot.setVisibility(buttonView.VISIBLE);
                imgPhone.setVisibility(buttonView.INVISIBLE);
                textPhone.setVisibility(buttonView.INVISIBLE);
                phone.setVisibility(buttonView.INVISIBLE);
                signUp.setVisibility(buttonView.INVISIBLE);
            }
            }
        });
        //create shared preferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        // load username and password were saved
        loadLoginData();
        //End Login

        // Start Home
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home.setVisibility(View.GONE);
                Login.setVisibility(View.VISIBLE);
            }
        });

        switch_led.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-led", "1");
                    led_image.setImageDrawable(getDrawable(R.drawable.ic_led_on));
                }
                else{
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-led", "0");
                    led_image.setImageDrawable(getDrawable(R.drawable.ic_led_off));
                }
            }
        });
        switch_pump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-pump", "1");
                    pump_image.setImageDrawable(getDrawable(R.drawable.ic_motor_on));
                }
                else{
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-pump", "0");
                    pump_image.setImageDrawable(getDrawable(R.drawable.ic_motor_off));
                }
            }
        });
        // End Home
    }


    void InitMapping() {
        Login = findViewById(R.id.Login);
        Home = findViewById(R.id.Home);
        //Start Login
        user = findViewById(R.id.userName);
        password = findViewById(R.id.passWord);
        phone = findViewById(R.id.login_phoneNum);
        cbRemember = findViewById(R.id.cbRemember);
        textForgot = findViewById(R.id.text_forgot);
        signIn = findViewById(R.id.btnSubmit);
        switchCompat = findViewById(R.id.btnSign);
        signUp = findViewById(R.id.btnSub);
        textPhone = findViewById(R.id.text_phone);
        imgPhone = findViewById(R.id.image_phone);
        //End Login

        // Start Home
        logout = findViewById(R.id.logout_btn);
        data_temp = findViewById(R.id.data_temp);
        data_temp_check = findViewById(R.id.data_temp_check);
        data_humi = findViewById(R.id.data_humidity);
        data_humi_check = findViewById(R.id.data_humidity_check);
        switch_led = findViewById(R.id.switch_led);
        switch_pump = findViewById(R.id.switch_pump);
        led_image = findViewById(R.id.led_image);
        pump_image = findViewById(R.id.pump_image);
        // End Home
    }

    private void clearLoginData() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }
    private void saveLoginData(String username, String Pass) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USERNAME, username);
        editor.putString(PASS, Pass);
        editor.putBoolean(REMEMBER,cbRemember.isChecked());
        editor.apply();
    }
    private void loadLoginData() {
        if(sharedpreferences.getBoolean(REMEMBER,false)) {
            user.setText(sharedpreferences.getString(USERNAME, ""));
            password.setText(sharedpreferences.getString(PASS, ""));
            cbRemember.setChecked(true);
        } else
            cbRemember.setChecked(false);
    }

    private void StartMQTT(){
        mqttHelper = new MQTTHelper(getApplicationContext(), "nqt");
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.contains("bk-iot-temp")){
                    data_temp.setText(message.toString());
                    int temp = Integer.parseInt(message.toString());
                    if(temp > 28){
                        data_temp_check.setText("Hot");
                        data_temp_check.setTextColor(Color.RED);
                    }
                    else if(temp < 24){
                        data_temp_check.setText("Cold");
                        data_temp_check.setTextColor(Color.RED);
                    }else{
                        data_temp_check.setText("Normal");
                        data_temp_check.setTextColor(Color.BLACK);
                    }
                }

                if(topic.contains("bk-iot-humi")){
                    data_humi.setText(message.toString());
                    int humidity = Integer.parseInt(message.toString());
                    if(humidity > 70){
                        data_humi_check.setText("Humid air");
                        data_humi_check.setTextColor(Color.RED);
                    }
                    else if(humidity < 40){
                        data_humi_check.setText("Dry air");
                        data_humi_check.setTextColor(Color.RED);
                    }else{
                        data_humi_check.setText("Normal");
                        data_humi_check.setTextColor(Color.BLACK);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}
