package com.example.makan.blue;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by makan on 23/3/2018.
 */

public class NirSpecs extends AppCompatActivity {
    private String[] Data=new String[] {"SensorID","Status","Connectivity","wavelength","Voltage","MemFrequency"};

    private String[] Data2= new String[]{"140","150"};
    private static final String LOG_TAG = NirSpecs.class.getSimpleName();
    private String[] stats= new String[6];
    private List<Data>Datalist;
    private LineData lineData;
    private LineDataSet dataSet;
    private String json=null;
    private LineChart chart;
    private List<Entry> entries;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.second_view);

        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        setContentView(R.layout.calibration);
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
        Map<String, String> datum;

        for (int i=0; i<6; i++) {
            datum=new HashMap<String, String>(2);
            datum.put("title",Data[i]);
            datum.put("value",stats[i]);
            data.add(datum);
        }
        ListView listview=(ListView)findViewById(R.id.list);
        SimpleAdapter adapter= new SimpleAdapter(this,data,android.R.layout.simple_expandable_list_item_2,new String[]{"title","value"},new int[]{android.R.id.text1,android.R.id.text2});
        listview.setAdapter(adapter);

        chart = (LineChart) findViewById(R.id.chart);
        if ((Datalist != null) ) {
            Log.e(LOG_TAG, "Not Null edw");
            entries = new ArrayList<Entry>();

            for (int i = 0; i < Datalist.size(); i++) {
                entries.add(new Entry((float) Datalist.get(i).getx(), (float) Datalist.get(i).gety()));
            }


            dataSet = new LineDataSet(entries, "NIR Spectrum");

           lineData = new LineData(dataSet);
            chart.setData(lineData);

           chart.invalidate(); // refresh


        }





    }
    private void Decode(String jsonstr) {
        try {
            JSONObject json= (JSONObject) new JSONTokener(jsonstr).nextValue();
            JSONObject jsonresponse=json.getJSONObject("Response");
            JSONObject jsonSample=jsonresponse.getJSONObject("sensorData");
            stats[0]=jsonSample.getString("sensorID");
            stats[1]=jsonSample.getString("Status");
            stats[2]=jsonSample.getString("Connectivity");
            stats[3]=jsonSample.getString("wavelength");
            stats[4]=jsonSample.getString("Voltage");
            stats[5]=jsonSample.getString("MemFrequency");
            JSONArray UVArray=jsonSample.getJSONArray("generatedSpectrum");
           // JSONObject json3=jsonSample.getJSONObject("NIR");
           // JSONArray NIRArray=json3.getJSONArray("averageAbsorbance");
            Datalist= new ArrayList<Data>();
           // DatalistNIR= new ArrayList<Data>();
            for (int i=0; i<UVArray.length(); i++) {
                Datalist.add(new Data(UVArray.getJSONObject(i).getDouble("wave"),UVArray.getJSONObject(i).getDouble("value")));
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
