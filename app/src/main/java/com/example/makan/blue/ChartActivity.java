package com.example.makan.blue;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by makan on 12/2/2018.
 */

public class ChartActivity extends AppCompatActivity {
    String[] SPINNERLIST2 = {"UV_VIS","NIR","FLUO"};
    private List<Entry> entries;
    private List<Entry> entries2;
    private List<Entry> entries3;
    private LineChart chart;
    private Data[] dataObjects;
    private LineDataSet dataSet;
    private LineDataSet dataSet2;
    private LineDataSet dataSet3;
    private LineData lineData;
    private ArrayList<Data> Datalist;
    private ArrayList<Data> DatalistNIR;
    private ArrayList<Data> DatalistFLUO;
    private String json=null;
    private static final String LOG_TAG = ChartActivity.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.second_view);

        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }
        setContentView(R.layout.chart);
        final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST2);
        final MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner);
        materialDesignSpinner.setAdapter(arrayAdapter2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            json = null;
        } else {
            json = extras.getString("Json");
            if (json != null)
                Log.e(LOG_TAG, json);
            Decode(json);

        }
        chart = (LineChart) findViewById(R.id.chart);
        if ((Datalist != null) && (DatalistNIR != null) && (DatalistFLUO!=null)) {
            Log.e(LOG_TAG, "Not Null edw");
            entries = new ArrayList<Entry>();
            entries2 = new ArrayList<Entry>();
            entries3= new ArrayList<Entry>();
            for (int i = 0; i < Datalist.size(); i++) {
                entries.add(new Entry((float) Datalist.get(i).getx(), (float) Datalist.get(i).gety()));
            }
            for (int i = 0; i < DatalistNIR.size(); i++) {
                entries2.add(new Entry((float) DatalistNIR.get(i).getx(), (float) DatalistNIR.get(i).gety()));
            }
            for (int i = 0; i < DatalistFLUO.size(); i++) {
                entries3.add(new Entry((float) DatalistFLUO.get(i).getx(), (float) DatalistFLUO.get(i).gety()));
            }
      /* dataObjects= new Data[3];

        dataObjects[0]= new Data(1.5,1.5);
        dataObjects[1]= new Data(2.5,2.5);
        dataObjects[2]= new Data(3.5,3.5);
        entries = new ArrayList<Entry>();
        for (Data data : dataObjects) {

            // turn your data into Entry objects
            entries.add(new Entry((float)data.getx(), (float)data.gety()));
        }*/
            // Decode();
            dataSet = new LineDataSet(entries, "VIS");
            dataSet2 = new LineDataSet(entries2, "NIR");
            dataSet3= new LineDataSet(entries3, "FLUO");
            lineData = new LineData(dataSet);
            chart.setData(lineData);

            chart.invalidate(); // refresh


            //   entries.add(new Entry(5, 5));
            //lineData.addEntry(new Entry(5, 5),3);
            //chart.setData(lineData);
            //  dataSet.notifyDataSetChanged();
            //  lineData.notifyDataChanged();

            //   chart.notifyDataSetChanged();
            //  chart.invalidate(); // refresh

        }

        materialDesignSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (materialDesignSpinner.getText().toString().equals(SPINNERLIST2[0])) {
                    lineData = new LineData(dataSet);
                    chart.setData(lineData);
                    chart.notifyDataSetChanged();
                    chart.invalidate(); // refresh
                }
                else if (materialDesignSpinner.getText().toString().equals(SPINNERLIST2[1])) {
                    lineData = new LineData(dataSet2);
                    chart.setData(lineData);
                    chart.notifyDataSetChanged();
                    chart.invalidate(); // refresh
                }
                else {
                    lineData = new LineData(dataSet3);
                    chart.setData(lineData);
                    chart.notifyDataSetChanged();
                    chart.invalidate(); // refresh
                }
            }
        });
    }

    @Override
    protected void onRestart() {

        super.onRestart();
        entries.add(new Entry(5, 5));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  entries.add(new Entry(5, 5));
      //  lineData.addEntry(new Entry(5, 5),3);
        //chart.setData(lineData);

      //  chart.invalidate(); // refresh



    }


    private void Decode(String jsonstr) {
        try {
            JSONObject json= (JSONObject) new JSONTokener(jsonstr).nextValue();
            JSONObject jsonresponse=json.getJSONObject("Response");
            JSONObject jsonSample=jsonresponse.getJSONObject("Sample");
            JSONObject json2 = jsonSample.getJSONObject("VIS");
            JSONArray UVArray=json2.getJSONArray("Preprocessed");
            JSONObject json3=jsonSample.getJSONObject("NIR");
            JSONArray NIRArray=json3.getJSONArray("averageAbsorbance");
            JSONObject json4=jsonSample.getJSONObject("FLUO");
            JSONArray FLUOArray=json4.getJSONArray("Preprocessed");
            Datalist= new ArrayList<Data>();
            DatalistNIR= new ArrayList<Data>();
            DatalistFLUO= new ArrayList<Data>();
            for (int i=0; i<UVArray.length(); i++) {
                Datalist.add(new Data(UVArray.getJSONObject(i).getDouble("wave"),UVArray.getJSONObject(i).getDouble("measurement")));
            }
            for (int i=0; i<NIRArray.length(); i++) {
                DatalistNIR.add(new Data(NIRArray.getJSONObject(i).getDouble("wave"),NIRArray.getJSONObject(i).getDouble("measurement")));
            }
            for (int i=0; i<FLUOArray.length(); i++) {
                DatalistFLUO.add(new Data(FLUOArray.getJSONObject(i).getDouble("wave"),FLUOArray.getJSONObject(i).getDouble("measurement")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.e(LOG_TAG, "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
