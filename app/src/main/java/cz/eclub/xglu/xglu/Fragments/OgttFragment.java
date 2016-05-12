package cz.eclub.xglu.xglu.Fragments;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import cz.eclub.xglu.xglu.MeasuringActivity;
import cz.eclub.xglu.xglu.R;
import cz.eclub.xglu.xglu.Test;

/**
 * A placeholder fragment containing a simple view.
 */
public class OgttFragment extends Fragment {
    private Button btnStartOgtt,btnClearAll,btnOgttEvaluate;
    private TextView tvOgttActive,tvOgttResult;
    private ArrayList<Button> btnOgttMeasure = new ArrayList<>();
    private ArrayList<TextView> tvMeasure = new ArrayList<>();
    private ArrayList<Float> ogttValues = new ArrayList<>();
    private ArrayList<PendingIntent> pendingIntents = new ArrayList<>();
    private LineChart lineChart;

    private TextView tvOgttTime;
    private int btnId=-1;

    public OgttFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ogtt, container, false);

        btnStartOgtt = (Button) v.findViewById(R.id.btnStartOgtt);
        btnStartOgtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAlarms();
            }
        });

        btnClearAll = (Button) v.findViewById(R.id.btnClearAll);
        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });

        tvOgttActive = (TextView) v.findViewById(R.id.tvOgttActive);

        tvOgttResult = (TextView) v.findViewById(R.id.tvOgttResult);

        btnOgttEvaluate = (Button) v.findViewById(R.id.btnOgttEvaluate);
        btnOgttEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluate();
            }
        });

        lineChart = (LineChart) v.findViewById(R.id.chart1);
        lineChart.setDescription("");
        lineChart.setTouchEnabled(false);

        setButtons(v);



        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    updateGUI();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        tvOgttTime = (TextView) v.findViewById(R.id.tvOgttTime);



        ArrayList<ILineDataSet> sampleGraphs = sampleGraphs();

        LineData data = new LineData(getValuesX(), sampleGraphs);
        lineChart.setData(data);

        updateGUI();
        return v;
    }

    private void evaluate() {
        if(getContext()==null)return;
        if(getActivity()==null)return;

        SharedPreferences sharedPref = getContext().getSharedPreferences("OGTT", Context.MODE_PRIVATE);

        ogttValues.clear();
        ogttValues.add(sharedPref.getFloat("OGTTvalue0", -1));
        ogttValues.add(sharedPref.getFloat("OGTTvalue1", -1));
        ogttValues.add(sharedPref.getFloat("OGTTvalue2", -1));
        ogttValues.add(sharedPref.getFloat("OGTTvalue3", -1));
        ogttValues.add(sharedPref.getFloat("OGTTvalue4", -1));

        if(ogttValues.get(0)==-1){
            Toast.makeText(getContext(), "1st measurement missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ogttValues.get(4)==-1){
            Toast.makeText(getContext(), "5th measurement missing", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for(int i=0;i<5;i++){
            float value = ogttValues.get(i);
            if(value==-1)continue;
            yVals.add(new Entry((float) value, i));
        }

        LineDataSet set1 = new LineDataSet(yVals, "measured");
        set1.setDrawCubic(true);
        set1.setColor(Color.BLACK);
        set1.setLineWidth(2f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);



        ArrayList<ILineDataSet> sampleGraphs = sampleGraphs();
        sampleGraphs.add(set1);

        final LineData data = new LineData(getValuesX(), sampleGraphs);

        lineChart.setData(data);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();



        Log.d("OGTT", "graph generated");


        if(ogttValues.get(0)<=5.6 && ogttValues.get(4)<=7.8){
            tvOgttResult.setText("OK");
            return;
        }

        if(ogttValues.get(0)>=7 && ogttValues.get(4)>=11.1){
            tvOgttResult.setText("KO");
            return;
        }

        tvOgttResult.setText("??");


    }

    private ArrayList<String> getValuesX(){
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            xVals.add((i*30) + " min");
        }
        return xVals;
    }

    private ArrayList<ILineDataSet> sampleGraphs() {
        //normal
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry((float) 4.8, 0));
        yVals.add(new Entry((float) 8.5, 1));
        yVals.add(new Entry((float) 7, 2));
        yVals.add(new Entry((float) 6, 3));
        yVals.add(new Entry((float) 5.0, 4));

        LineDataSet set1 = new LineDataSet(yVals, "normal");
        set1.setDrawCubic(true);
        set1.setColor(Color.GREEN);
        set1.setLineWidth(2f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.enableDashedLine(10f,10f,0f);

        //diabetes mellitus
        ArrayList<Entry> yValsDiabetes = new ArrayList<Entry>();
        yValsDiabetes.add(new Entry((float) 8.5, 0));
        yValsDiabetes.add(new Entry((float) 12.5, 1));
        yValsDiabetes.add(new Entry((float) 15, 2));
        yValsDiabetes.add(new Entry((float) 15, 3));
        yValsDiabetes.add(new Entry((float) 12.5, 4));

        LineDataSet set2 = new LineDataSet(yValsDiabetes, "diabetes");
        set2.setDrawCubic(true);
        set2.setColor(Color.RED);
        set2.setLineWidth(2f);
        set2.setDrawCircles(false);
        set2.setDrawValues(false);
        set2.enableDashedLine(10f, 10f, 0f);

        //hypertyreosis
        ArrayList<Entry> yValsHypertyreosis= new ArrayList<Entry>();
        yValsHypertyreosis.add(new Entry((float) 4.8, 0));
        yValsHypertyreosis.add(new Entry((float) 9.5, 1));
        yValsHypertyreosis.add(new Entry((float) 11.5, 2));
        yValsHypertyreosis.add(new Entry((float) 9, 3));
        yValsHypertyreosis.add(new Entry((float) 5, 4));

        LineDataSet set3 = new LineDataSet(yValsHypertyreosis, "hypertyreosis");
        set3.setDrawCubic(true);
        set3.setColor(Color.YELLOW);
        set3.setLineWidth(2f);
        set3.setDrawValues(false);
        set3.setDrawCircles(false);
        set3.enableDashedLine(10f, 10f, 0f);

        //malabsorption
        ArrayList<Entry> yValsMalabsorption= new ArrayList<Entry>();
        yValsMalabsorption.add(new Entry((float) 4, 0));
        yValsMalabsorption.add(new Entry((float) 5, 1));
        yValsMalabsorption.add(new Entry((float) 6, 2));
        yValsMalabsorption.add(new Entry((float) 5, 3));
        yValsMalabsorption.add(new Entry((float) 4, 4));

        LineDataSet set4 = new LineDataSet(yValsMalabsorption, "malabsorption");
        set4.setDrawCubic(true);
        set4.setColor(Color.BLUE);
        set4.setLineWidth(2f);
        set4.setDrawValues(false);
        set4.setDrawCircles(false);
        set4.enableDashedLine(10f,10f,0f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);
        dataSets.add(set4);

        return dataSets;
    }

    private void clearAll() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("OGTT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("OGTTactive",0);
        editor.putFloat("OGTTvalue0", -1);
        editor.putFloat("OGTTvalue1", -1);
        editor.putFloat("OGTTvalue2", -1);
        editor.putFloat("OGTTvalue3", -1);
        editor.putFloat("OGTTvalue4", -1);
        editor.commit();

        NotificationManager notificationManager = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        for(PendingIntent pi: pendingIntents){
            alarmManager.cancel(pi);
        }

        tvOgttResult.setText("--");

        updateGUI();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateGUI();
    }

    private void setButtons(View v){
        SharedPreferences sharedPref = getContext().getSharedPreferences("OGTT",Context.MODE_PRIVATE);
        Log.d("OGTT sp", sharedPref.getInt("OGTTactive",0) + "");

        btnOgttMeasure.add((Button) v.findViewById(R.id.btnOgttMeasure0));
        btnOgttMeasure.add((Button) v.findViewById(R.id.btnOgttMeasure1));
        btnOgttMeasure.add((Button) v.findViewById(R.id.btnOgttMeasure2));
        btnOgttMeasure.add((Button) v.findViewById(R.id.btnOgttMeasure3));
        btnOgttMeasure.add((Button) v.findViewById(R.id.btnOgttMeasure4));

        tvMeasure.add((TextView) v.findViewById(R.id.tvMeasure0));
        tvMeasure.add((TextView) v.findViewById(R.id.tvMeasure1));
        tvMeasure.add((TextView) v.findViewById(R.id.tvMeasure2));
        tvMeasure.add((TextView) v.findViewById(R.id.tvMeasure3));
        tvMeasure.add((TextView) v.findViewById(R.id.tvMeasure4));

        for(int i=0;i<5;i++){
            final int j = i;
            btnOgttMeasure.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    measureGlucose(j);
                }
            });
        }

    }

    private void updateGUI(){
        if(getContext()==null)return;
        SharedPreferences sharedPref = getContext().getSharedPreferences("OGTT",Context.MODE_PRIVATE);
        final int id = sharedPref.getInt("OGTTactive",0);



        ogttValues.clear();
        ogttValues.add(sharedPref.getFloat("OGTTvalue0", -1));
        ogttValues.add(sharedPref.getFloat("OGTTvalue1", -1));
        ogttValues.add(sharedPref.getFloat("OGTTvalue2", -1));
        ogttValues.add(sharedPref.getFloat("OGTTvalue3", -1));
        ogttValues.add(sharedPref.getFloat("OGTTvalue4", -1));


        if(getActivity()==null)return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvOgttActive.setText("" + id);
                for(int i=0;i<5;i++){
                    //btnOgttMeasure.get(i).setEnabled(i==id);
                    tvMeasure.get(i).setText((ogttValues.get(i)==-1)?"--":(ogttValues.get(i)+""));
                }
            }
        });


    }


    public void startAlarms(){
/*
        if(ogttValues.get(0)==-1) {
            Toast.makeText(getContext(), "Take first measurement", Toast.LENGTH_SHORT).show();
            return;
        }*/

        SharedPreferences sharedPref = getContext().getSharedPreferences("OGTT", Context.MODE_PRIVATE);
        if(sharedPref.getFloat("OGTTvalue0",-1)<-0.5)return;

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("OGTTactive", -2);
        editor.putFloat("OGTTvalue1", -1);
        editor.putFloat("OGTTvalue2", -1);
        editor.putFloat("OGTTvalue3", -1);
        editor.putFloat("OGTTvalue4", -1);
        editor.commit();


        //int interval = 1800000;
        //int delay = 2*60*1000;
        int interval = 5000;
        int delay = 1000;

        long currentTime = System.currentTimeMillis();

        AlarmManager manager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent;
        PendingIntent pendingIntent;
        //for(int i=1;i<2;i++){
        for(int i=1;i<5;i++){
            Intent intent = new Intent(getContext(), Test.class).putExtra("OGTT", i);
            pendingIntent = PendingIntent.getBroadcast(getContext(),i, intent,PendingIntent.FLAG_ONE_SHOT);
            manager.setExact(AlarmManager.RTC_WAKEUP, currentTime + i*interval, pendingIntent);
            Log.d("OGTT", "set alarm:" + i + " " + i * interval);
            pendingIntents.add(pendingIntent);
        }


        Intent intent = new Intent(getContext(), Test.class).putExtra("OGTT", -4);
        pendingIntent = PendingIntent.getBroadcast(getContext(),-4, intent,PendingIntent.FLAG_ONE_SHOT);
        manager.setExact(AlarmManager.RTC_WAKEUP, currentTime + 4*interval+delay, pendingIntent);
        Log.d("OGTT", "set alarm stop:4 " + (4 * interval + delay));
        pendingIntents.add(pendingIntent);


        Toast.makeText(getContext(), "OGTT started", Toast.LENGTH_SHORT).show();
    }

    public void measureGlucose(int id) {
        Intent intent = new Intent(getContext(), MeasuringActivity.class);
        intent.putExtra("OGTTID",id);
        startActivityForResult(intent, 65);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==65&&resultCode==1){
            Log.d("OGTT", "save result ogtt " + requestCode + " " + resultCode);
            float glucose = (float)data.getDoubleExtra("glucose",-1);
            int ogttid = data.getIntExtra("OGTTID", -1);
            Log.d("OGTT","save result "+glucose+" "+ogttid);
            if(ogttid!=-1){
                SharedPreferences sharedPref = getContext().getSharedPreferences("OGTT", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putFloat("OGTTvalue" + ogttid, glucose);
                editor.commit();
            }


        }
    }

}
