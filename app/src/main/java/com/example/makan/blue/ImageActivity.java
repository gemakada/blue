package com.example.makan.blue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by makan on 23/3/2018.
 */

public class ImageActivity extends AppCompatActivity {

    private static final String LOG_TAG = NirSpecs.class.getSimpleName();
    private String json;
    private byte[] b;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.second_view);

        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }
        setContentView(R.layout.image_act);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();



        if (extras == null) {
            json = null;
        } else {
            json = extras.getString("Json");
           // b=extras.getByteArray("bytes");
            if (json != null)
                Log.e(LOG_TAG,"Arrraaay"+ String.valueOf(json.length()));
            Decode(json);

        }

        }






    private void Decode(String jsonstr) {
        String x=jsonstr;
        byte[] data = Base64.decode(x, Base64.DEFAULT);

        Bitmap bm = BitmapFactory.decodeByteArray(data,0,data.length);
        ImageView im=(ImageView)findViewById(R.id.area);
        Bitmap map=BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        //im.setImageBitmap(map);
        im.setImageBitmap(bm);


        im.setVisibility(View.GONE);
        im.setVisibility(View.VISIBLE);

    }

    private Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = encodedString.getBytes();
            try {
                FileOutputStream fOut = openFileOutput("tom.jpg",this.getBaseContext().MODE_PRIVATE);
                fOut.write(encodeByte);
                fOut.flush();
                fOut.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            InputStream targetStream=new ByteArrayInputStream(encodeByte);
            Bitmap bitmap = BitmapFactory.decodeStream(targetStream);
            return bitmap;


        } catch (Exception e) {
            e.getMessage();
            return null;
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
