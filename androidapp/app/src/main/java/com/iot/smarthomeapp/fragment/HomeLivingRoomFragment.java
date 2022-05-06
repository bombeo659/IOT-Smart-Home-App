package com.iot.smarthomeapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.iot.smarthomeapp.MQTTHelper;
import com.iot.smarthomeapp.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeLivingRoomFragment extends Fragment {

    MQTTHelper mqttHelper;

    private ImageView back;
    private SwitchCompat switch_led, switch_pump;
    private ImageView led_image, pump_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_living_room, container, false);

        back = view.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new HomeFragment());
            }
        });

        switch_led = view.findViewById(R.id.switch_led);
        switch_pump = view.findViewById(R.id.switch_pump);
        led_image = view.findViewById(R.id.led_image);
        pump_image = view.findViewById(R.id.pump_image);

        startMQTT();
        getLastValue_Led();
        getLastValue_Pump();


        switch_led.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-led", "1");
                    led_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_led_on));
                }else {
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-led", "0");
                    led_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_led_off));
                }
            }
        });
        switch_pump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-pump", "3");
                    pump_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_motor_on));
                }else {
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-pump", "2");
                    pump_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_motor_off));
                }
            }
        });

        return view;
    }

    private void startMQTT(){
        mqttHelper = new MQTTHelper(getContext(), "group1");
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }
    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_layout, fragment);
        transaction.commit();
    }

        private void getLastValue_Led(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-led/data?limit=1";
        @SuppressLint("UseCompatLoadingForDrawables")
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject info = response.getJSONObject(0);
                if(info.getString("value").equals("1")){
                    switch_led.setChecked(true);
                    led_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_led_on));
                } else{
                    switch_led.setChecked(false);
                    led_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_led_off));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void getLastValue_Pump(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-pump/data?limit=1";
        @SuppressLint("UseCompatLoadingForDrawables")
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject info = response.getJSONObject(0);
                if(info.getString("value").equals("3")){
                    switch_pump.setChecked(true);
                    pump_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_motor_on));
                } else{
                    switch_pump.setChecked(false);
                    pump_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_motor_off));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }
}