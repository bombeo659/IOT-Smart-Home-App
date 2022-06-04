package com.iot.smarthomeapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
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

    private ImageView led_image, tv_image, ac_image;
    private SeekBar seekBar;
    private TextView tempData;

    private boolean ledDataCheck, tvDataCheck, acDataCheck;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_living_room, container, false);

        ImageView back = view.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new HomeFragment());
            }
        });

        CardView ledCard = (CardView) view.findViewById(R.id.ledCard);
        CardView tvCard = (CardView) view.findViewById(R.id.tvCard);
        CardView acCard = (CardView) view.findViewById(R.id.acCard);

        led_image = view.findViewById(R.id.led_image);
        tv_image = view.findViewById(R.id.tv_image);
        ac_image = view.findViewById(R.id.ac_image);

        tempData = view.findViewById(R.id.tempData);
        seekBar = view.findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tempData.setText(String.valueOf(progress)+"°C");
                mqttHelper.publishToTopic("iotg06/feeds/bk-iot-temp", String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        startMQTT();
        getLastValue_Led();
        getLastValue_Tv();
        getLastValue_Temp();

        ledCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ledDataCheck){
                    ledDataCheck = false;
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-led", "0");
                    led_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_led_off));
                } else {
                    ledDataCheck = true;
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-led", "1");
                    led_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_led_on));
                }
            }
        });

        tvCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvDataCheck){
                    tvDataCheck = false;
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-tv", "4");
                    tv_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_tv_off));
                } else {
                    tvDataCheck = true;
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-tv", "5");
                    tv_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_tv_on));
                }
            }
        });

        acCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(acDataCheck){
                    tvDataCheck = false;
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-ac", "6");
                    ac_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_air_conditioner_off));
                } else {
                    acDataCheck = true;
                    mqttHelper.publishToTopic("iotg06/feeds/bk-iot-ac", "7");
                    ac_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_air_conditioner_on));
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
                    ledDataCheck = true;
                    led_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_led_on));
                } else{
                    ledDataCheck = false;
                    led_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_led_off));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void getLastValue_Tv(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-tv/data?limit=1";
        @SuppressLint("UseCompatLoadingForDrawables")
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject info = response.getJSONObject(0);
                if(info.getString("value").equals("5")){
                    tvDataCheck = true;
                    tv_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_tv_on));
                } else{
                    tvDataCheck = false;
                    tv_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_tv_off));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }
    private void getLastValue_Temp(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-temp/data?limit=1";

        @SuppressLint("SetTextI18n")
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject info = response.getJSONObject(0);
                tempData.setText(info.getString("value"));
                int temp = Integer.parseInt(info.getString("value"));
                seekBar.setProgress(temp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void getLastValue_Ac(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://io.adafruit.com/api/v2/iotg06/feeds/bk-iot-ac/data?limit=1";
        @SuppressLint("UseCompatLoadingForDrawables")
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject info = response.getJSONObject(0);
                if(info.getString("value").equals("7")){
                    acDataCheck = true;
                    ac_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_air_conditioner_on));
                } else{
                    acDataCheck = false;
                    ac_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_air_conditioner_off));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }
}