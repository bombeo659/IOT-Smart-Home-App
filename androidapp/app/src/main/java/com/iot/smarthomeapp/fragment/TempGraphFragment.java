package com.iot.smarthomeapp.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.iot.smarthomeapp.MQTTHelper;
import com.iot.smarthomeapp.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;


public class TempGraphFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {

    private ImageView back;
    private LineChart graphTemp;
    private MQTTHelper mqttHelper;

    private boolean flagGraphTemp = false;
    private int tempAddToGraph;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_temp_graph, container, false);

        startMQTT();
        back = view.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new HomeFragment());
            }
        });


        graphTemp = view.findViewById(R.id.tempChart);

        graphTemp.getDescription().setEnabled(true);
        graphTemp.getDescription().setText("Temperature Log");
        graphTemp.setNoDataText("No data for the moment");

        graphTemp.setHighlightPerDragEnabled(true);
        graphTemp.setTouchEnabled(true);

        graphTemp.setDragEnabled(true);
        graphTemp.setScaleEnabled(true);
        graphTemp.setDrawGridBackground(false);

        graphTemp.setPinchZoom(true);
        graphTemp.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        graphTemp.setData(data);

        Legend l = graphTemp.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = graphTemp.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);


        YAxis leftAxis = graphTemp.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(30f);
        leftAxis.setAxisMinimum(-5f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.BLACK);

        YAxis rightAxis = graphTemp.getAxisRight();
        rightAxis.setEnabled(false);

//        lineChart.getDescription().setEnabled(true);
//        lineChart.getDescription().setText("Temperature Log");
//        lineChart.setOnChartGestureListener(this);
//        lineChart.setOnChartValueSelectedListener(this);
//        lineChart.setDragEnabled(true);
//        lineChart.setScaleEnabled(false);
//        lineChart.setBackgroundColor(Color.WHITE);
//
//        XAxis xAxis;
//        {   // // X-Axis Style // //
//            xAxis = lineChart.getXAxis();
//
//            // vertical grid lines
//            xAxis.enableGridDashedLine(10f, 10f, 0f);
//        }
//
//        YAxis yAxis;
//        {   // // Y-Axis Style // //
//            yAxis = lineChart.getAxisLeft();
//
//            // disable dual axis (only use LEFT axis)
//            lineChart.getAxisRight().setEnabled(false);
//
//            // horizontal grid lines
//            yAxis.enableGridDashedLine(10f, 10f, 0f);
//
//            // axis range
//            yAxis.setAxisMaximum(40f);
//            yAxis.setAxisMinimum(15f);
//        }
//
//        ArrayList<Entry> arrayList = new ArrayList<>();
//        arrayList.add(new Entry(0, 23f));
//        arrayList.add(new Entry(1, 24f));
//        arrayList.add(new Entry(2, 23f));
//        arrayList.add(new Entry(3, 22f));
//        arrayList.add(new Entry(4, 24f));
//        arrayList.add(new Entry(5, 25f));
//
//        LineDataSet lineDataSet = new LineDataSet(arrayList, "Dataset 1");
//        lineDataSet.setFillAlpha(110);
//        lineDataSet.setColor(Color.RED);
//        lineDataSet.setLineWidth(3f);
//        lineDataSet.setCircleRadius(6f);
//        lineDataSet.setValueTextSize(10f);
//
//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(lineDataSet);
//
//        LineData lineData = new LineData(dataSets);
//
//        lineChart.setData(lineData);

        return view;
    }

    private void startMQTT() {
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

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_layout, fragment);
        transaction.commit();
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    public final void runOnUiThread (Runnable action){

    }
    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!flagGraphTemp) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {

                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (flagGraphTemp) {
                                    addEntryToTempGraph();
                                    flagGraphTemp = false;
                                }
                            }
                        });
                    }
                }
            }
        }).start();
    }
    private LineDataSet createSetTemp() {
        LineDataSet set = new LineDataSet(null, "Temperature waves");
        set.setDrawCircles(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.GREEN);
        set.setCircleColor(Color.MAGENTA);
        set.setLineWidth(2.5f);
        set.setCircleRadius(3f);
        set.setFillAlpha(65);
        set.setFillColor(Color.GREEN);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.MAGENTA);
        set.setValueTextSize(10f);
        set.setDrawValues(true);
        return set;
    }
    private void addEntryToTempGraph() {

        LineData data = graphTemp.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSetTemp();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), tempAddToGraph), 0);
            data.notifyDataChanged();

            // let the graph know it's data has changed
            graphTemp.notifyDataSetChanged();

            // limit the number of visible entries
            graphTemp.setVisibleXRangeMaximum(5);

            // move to the latest entry
            graphTemp.moveViewToX(data.getEntryCount());

        }
    }
}