package com.example.makan.blue;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makan on 12/2/2018.
 */

public class ChartActivity extends AppCompatActivity {
    private List<Entry> entries;
    private LineChart chart;
    private Data[] dataObjects;
    private LineDataSet dataSet;
    private LineData lineData;
    private ArrayList<Data> Datalist;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            json= null;
        } else {
            json= extras.getString("Json");
            if (json!=null)
                Log.e(LOG_TAG,json);
            Decode(json);

        }
       chart = (LineChart) findViewById(R.id.chart);
        if (Datalist!=null) {
            Log.e(LOG_TAG,"Not Null edw");
            entries = new ArrayList<Entry>();
            for (int i = 0; i < Datalist.size(); i++) {
                entries.add(new Entry((float)Datalist.get(i).getx(),(float)Datalist.get(i).gety()));
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
                dataSet = new LineDataSet(entries, "Label");
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
            JSONObject json2 = json.getJSONObject("uV-VIS");
            JSONArray UVArray=json2.getJSONArray("Preprocessed");
            Datalist= new ArrayList<Data>();
            for (int i=0; i<UVArray.length(); i++) {
                Datalist.add(new Data(UVArray.getJSONObject(i).getDouble("wave"),UVArray.getJSONObject(i).getDouble("measurement")));
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
