package com.iot.smarthomeapp.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.iot.smarthomeapp.MQTTHelper;
import com.iot.smarthomeapp.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeFragment extends Fragment {

    MQTTHelper mqttHelper;
    private TextView data_temp, data_temp_check, data_humi, data_humi_check;
    private ArcProgress data_gas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        data_temp = view.findViewById(R.id.data_temp);
        data_temp_check = view.findViewById(R.id.data_temp_check);
        data_humi = view.findViewById(R.id.data_humidity);
        data_humi_check = view.findViewById(R.id.data_humidity_check);
        data_gas = view.findViewById(R.id.arc_progress);

        CardView temp = view.findViewById(R.id.temp);
        CardView humidity = view.findViewById(R.id.humidity);
        CardView gas = view.findViewById(R.id.gas);
        CardView livingRoom = view.findViewById(R.id.livingRoom);
        CardView diningRoom = view.findViewById(R.id.diningRoom);
        CardView bathRoom = view.findViewById(R.id.bathRoom);
        CardView bedRoom = view.findViewById(R.id.bedRoom);

        livingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new HomeLivingRoomFragment());
            }
        });
        diningRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new HomeDiningRoomFragment());
            }
        });
        bathRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new HomeBathroomFragment());
            }
        });
        bedRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new HomeBedroomFragment());
            }
        });

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new TempGraphFragment());
            }
        });

        humidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new HunidityGraphFragment());
            }
        });

        gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new GasGraphFragment());
            }
        });

        StartMQTT();
        getLastValue_Temp();
        getLastValue_Humi();
        getLastValue_Gas();

        return view;
    }

    private void getLastValue_Gas() {
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_layout, fragment);
        transaction.commit();
    }

    private void StartMQTT(){
        mqttHelper = new MQTTHelper(getContext(), "group10");
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

                if(topic.contains("bk-iot-gas")){
                    int gas = Integer.parseInt(message.toString());
                    data_gas.setProgress(gas);
                    //??????
//                    if(gas>40){
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            NotificationChannel channel = new NotificationChannel("myCh", "My Chanel", NotificationManager.IMPORTANCE_DEFAULT);
//
//                            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
//                            notificationManager.createNotificationChannel(channel);
//                        }
//                        Intent intent = new Intent(getContext(), HomeLivingRoomFragment.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        PendingIntent pendingIntent = null;
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                            pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
//                        }
//
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "myCh")
//                                .setSmallIcon(R.drawable.ic_notification)
//                                .setContentTitle("My notification")
//                                .setContentText("Hello World!")
//                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                                // Set the intent that will fire when the user taps the notification
//                                .setContentIntent(pendingIntent)
//                                .setAutoCancel(true);
//
//                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
//                        notificationManager.notify(0, builder.build());
//                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void getLastValue_Temp(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
        }, error -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void getLastValue_Humi(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
        }, error -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

}