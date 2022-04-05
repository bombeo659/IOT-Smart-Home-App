package com.iot.smarthomeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeScreen extends AppCompatActivity {

    MQTTHelper mqttHelper;
    private TextView data_temp, data_temp_check, data_humi, data_humi_check;
    private SwitchCompat switch_led, switch_pump;
    private ImageView led_image, pump_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Button logout = findViewById(R.id.logout_btn);

        data_temp = findViewById(R.id.data_temp);
        data_temp_check = findViewById(R.id.data_temp_check);
        data_humi = findViewById(R.id.data_humidity);
        data_humi_check = findViewById(R.id.data_humidity_check);
        switch_led = findViewById(R.id.switch_led);
        switch_pump = findViewById(R.id.switch_pump);
        led_image = findViewById(R.id.led_image);
        pump_image = findViewById(R.id.pump_image);

        StartMQTT();
        getLastValue_Temp();
        getLastValue_Humi();
        getLastValue_Led();
        getLastValue_Pump();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeScreen.this, MainActivity.class));
            }
        });
        switch_led.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-led", "1");
                    led_image.setImageDrawable(getDrawable(R.drawable.ic_led_on));
                }else {
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-led", "0");
                    led_image.setImageDrawable(getDrawable(R.drawable.ic_led_off));
                }
            }
        });
        switch_pump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-pump", "3");
                    pump_image.setImageDrawable(getDrawable(R.drawable.ic_motor_on));
                }else {
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-pump", "2");
                    pump_image.setImageDrawable(getDrawable(R.drawable.ic_motor_off));
                }
            }
        });
    }

    private void StartMQTT(){
        mqttHelper = new MQTTHelper(getApplicationContext(), "group10");
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Toast.makeText(HomeScreen.this, "Connect", Toast.LENGTH_SHORT).show();
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

    private void getLastValue_Temp(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(HomeScreen.this);
        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-temp/data?limit=1";

        @SuppressLint("SetTextI18n")
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject info = response.getJSONObject(0);
                data_temp.setText(info.getString("value"));
                int temp = Integer.parseInt(info.getString("value"));
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(HomeScreen.this, "Error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void getLastValue_Humi(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(HomeScreen.this);
        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-humi/data?limit=1";

        @SuppressLint("SetTextI18n")
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject info = response.getJSONObject(0);
                data_humi.setText(info.getString("value"));
                int humidity = Integer.parseInt(info.getString("value"));
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(HomeScreen.this, "Error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void getLastValue_Led(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(HomeScreen.this);
        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-led/data?limit=1";
        @SuppressLint("UseCompatLoadingForDrawables")
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject info = response.getJSONObject(0);
                if(info.getString("value").equals("1")){
                    switch_led.setChecked(true);
                    led_image.setImageDrawable(getDrawable(R.drawable.ic_led_on));
                } else{
                    switch_led.setChecked(false);
                    led_image.setImageDrawable(getDrawable(R.drawable.ic_led_off));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(HomeScreen.this, "Error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void getLastValue_Pump(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(HomeScreen.this);
        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-pump/data?limit=1";
        @SuppressLint("UseCompatLoadingForDrawables")
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject info = response.getJSONObject(0);
                if(info.getString("value").equals("3")){
                    switch_pump.setChecked(true);
                    pump_image.setImageDrawable(getDrawable(R.drawable.ic_motor_on));
                } else{
                    switch_pump.setChecked(false);
                    pump_image.setImageDrawable(getDrawable(R.drawable.ic_motor_off));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(HomeScreen.this, "Error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }
}