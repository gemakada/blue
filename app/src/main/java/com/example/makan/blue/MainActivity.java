package com.example.makan.blue;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by makan on 16/1/2018.
 */

public class MainActivity extends AppCompatActivity {
    String[] SPINNERLIST = {"Mycotoxins detection", "Food spoilage"};
    String[] SPINNERLIST2 = {"Maize flour","Skimmed milk powder","Paprika powder","Almond","Peanuts"};
    String[] SPINNERLIST3 = {"Low","Medium","High"};
    String[] Mycotoxins = {"AF B1","Total AFs","DON"};
    String[] Granularity= {"Low","Medium","High"};
    String[] Foodlist={"Maize flour","Skimmed milk powder","Paprika powder","Almond","Peanuts"};
    String[] Foodlist2={"Rocket","Fish"};
    String[] Templist={"8","4","12"};
    String[] Explist={"0","14","24","38","48","86","110","134","158"};
    private ArrayList<Data> Datalist;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static int REQUEST_ENABLE_BT = 1;
    private ArrayAdapter<String> discoveredDevicesAdapter;
    private Button btnConnect;
    private Button btnDisConnect;
    private Button Send;
    private Button Chart;
    private Button Cancel;
    private Button NIR;
    private Button WhiteRef;
    private Button WhiteRefUV;
    private Button OpenConfiguration;
    private Button Parse;
    private Button Image;
    private Dialog dialog;
    private TextView status;
    private EditText tvisedit;
    private EditText tfluoedit;
    private BluetoothAdapter adapter;
    private BluetoothDevice connectingDevice;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "device_name";
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private ChatController chatController;
    private String Use_Case;
    private String Granularitystr;
    private String Food_Type;
    private String Food_Type2;
    private String Temperature;
    private String Exposure;
    private String JsonReceive=" ";
    private String tvis;
    private String tfluo;
    private String V_UV;
    private String Vw_vis;
    private String V_nir;
    private int ack=0;
    private ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream( );
    private boolean calibrationflag=false;
    private ByteArrayOutputStream outputStream;
    final Context c = this;
    private AlertDialog dia;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
      outputStream = new ByteArrayOutputStream( );
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }
        if (android.os.Build.VERSION.SDK_INT>=23) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        setContentView(R.layout.activity_main);

        dia=dialogue();
       final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
       final MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner);
        materialDesignSpinner.setAdapter(arrayAdapter2);

        final ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Foodlist);
      final  MaterialBetterSpinner materialDesignSpinner2 = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner3);
        materialDesignSpinner2.setAdapter(arrayAdapter3);

       final ArrayAdapter<String> arrayAdapter4 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Granularity);
        final MaterialBetterSpinner materialDesignSpinner4 = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner2);
        materialDesignSpinner4.setAdapter(arrayAdapter4);

        final ArrayAdapter<String> AdapterFood = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Foodlist2);
        final MaterialBetterSpinner FoodSpinner = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner_food2);
        FoodSpinner.setAdapter(AdapterFood);

        final ArrayAdapter<String> TemperatureAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Templist);
        final  MaterialBetterSpinner TemperatureSpinner = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner_Temperature);
        TemperatureSpinner.setAdapter(TemperatureAdapter);

        final ArrayAdapter<String> ExposureAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Explist);
        final MaterialBetterSpinner ExposureSpinner = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner_exposure);
        ExposureSpinner.setAdapter(ExposureAdapter);
////////////////////////////////listenesrs
        materialDesignSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
             //   quantity=materialDesignSpinner.getText().toString();
                Log.e(LOG_TAG,materialDesignSpinner.getText().toString());
                if (materialDesignSpinner.getText().toString().equals(SPINNERLIST[0])) {
                 //   SPINNERLIST2=Granularity;
                   // SPINNERLIST=Granularity;
                    Log.e(LOG_TAG,"mpike");
                    Use_Case=materialDesignSpinner.getText().toString();
                    FoodSpinner.setVisibility(View.INVISIBLE);
                    TemperatureSpinner.setVisibility(View.INVISIBLE);
                    ExposureSpinner.setVisibility(View.INVISIBLE);
                    materialDesignSpinner2.setVisibility(View.VISIBLE);
                    materialDesignSpinner4.setVisibility(View.VISIBLE);
                   // materialDesignSpinner.getAdapter();

                  //  arrayAdapter2.notifyDataSetChanged();

                   // ArrayAdapter<String> adapter= (ArrayAdapter<String>)materialDesignSpinner.getAdapter();
                //    adapter.
                }
                else {
                    Use_Case=materialDesignSpinner.getText().toString();
                    FoodSpinner.setVisibility(View.VISIBLE);
                    TemperatureSpinner.setVisibility(View.VISIBLE);
                    ExposureSpinner.setVisibility(View.VISIBLE);
                    materialDesignSpinner2.setVisibility(View.INVISIBLE);
                    materialDesignSpinner4.setVisibility(View.INVISIBLE);
                }

            }
        });


        materialDesignSpinner2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //   quantity=materialDesignSpinner.getText().toString();
                Log.e(LOG_TAG,materialDesignSpinner.getText().toString());

                    //   SPINNERLIST2=Granularity;
                    // SPINNERLIST=Granularity;
                    Log.e(LOG_TAG,"mpike");
                    Food_Type=materialDesignSpinner2.getText().toString();
                    // materialDesignSpinner.getAdapter();

                    //  arrayAdapter2.notifyDataSetChanged();

                    // ArrayAdapter<String> adapter= (ArrayAdapter<String>)materialDesignSpinner.getAdapter();
                    //    adapter.


            }
        });
        materialDesignSpinner4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //   quantity=materialDesignSpinner.getText().toString();
                Log.e(LOG_TAG,materialDesignSpinner.getText().toString());

                //   SPINNERLIST2=Granularity;
                // SPINNERLIST=Granularity;
                Log.e(LOG_TAG,"mpike");
                Granularitystr=materialDesignSpinner4.getText().toString();
                // materialDesignSpinner.getAdapter();

                //  arrayAdapter2.notifyDataSetChanged();

                // ArrayAdapter<String> adapter= (ArrayAdapter<String>)materialDesignSpinner.getAdapter();
                //    adapter.


            }
        });

        FoodSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //   quantity=materialDesignSpinner.getText().toString();
                Log.e(LOG_TAG,materialDesignSpinner.getText().toString());

                //   SPINNERLIST2=Granularity;
                // SPINNERLIST=Granularity;
                Log.e(LOG_TAG,"mpike");
                Food_Type2=FoodSpinner.getText().toString();
                // materialDesignSpinner.getAdapter();

                //  arrayAdapter2.notifyDataSetChanged();

                // ArrayAdapter<String> adapter= (ArrayAdapter<String>)materialDesignSpinner.getAdapter();
                //    adapter.


            }
        });
        TemperatureSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //   quantity=materialDesignSpinner.getText().toString();
                Log.e(LOG_TAG,materialDesignSpinner.getText().toString());

                //   SPINNERLIST2=Granularity;
                // SPINNERLIST=Granularity;
                Log.e(LOG_TAG,"mpike");
                Temperature=TemperatureSpinner.getText().toString();
                // materialDesignSpinner.getAdapter();

                //  arrayAdapter2.notifyDataSetChanged();

                // ArrayAdapter<String> adapter= (ArrayAdapter<String>)materialDesignSpinner.getAdapter();
                //    adapter.


            }
        });
        ExposureSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //   quantity=materialDesignSpinner.getText().toString();
                Log.e(LOG_TAG,materialDesignSpinner.getText().toString());

                //   SPINNERLIST2=Granularity;
                // SPINNERLIST=Granularity;
                Log.e(LOG_TAG,"mpike");
                Exposure=ExposureSpinner.getText().toString();
                // materialDesignSpinner.getAdapter();

                //  arrayAdapter2.notifyDataSetChanged();

                // ArrayAdapter<String> adapter= (ArrayAdapter<String>)materialDesignSpinner.getAdapter();
                //    adapter.


            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);                     //Mpainei toolbar
        setSupportActionBar(toolbar);






        status = (TextView) findViewById(R.id.status);
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Log.e(LOG_TAG, "Bluetooth not available");
        } else {
            Log.e(LOG_TAG, adapter.getAddress());
        }

        if (!adapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {

            Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
            btnConnect = (Button) findViewById(R.id.btn_connect);
            btnDisConnect=(Button) findViewById(R.id.btn_disconnect);
            Chart=(Button) findViewById(R.id.Chart);
            Send= (Button) findViewById(R.id.SendMsg);
            NIR=(Button)findViewById(R.id.NIR);
            WhiteRef=(Button)findViewById(R.id.WhiteReferenceVIS);
            WhiteRefUV=(Button)findViewById(R.id.WhiteReferenceUV);
            OpenConfiguration=(Button)findViewById(R.id.Configuration);
            Parse=(Button)findViewById(R.id.Parse);
            Image=(Button)findViewById(R.id.Image);

            Parse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String json = null;
                    try {
                        InputStream is = getBaseContext().getAssets().open("output.json");
                        int size = is.available();
                        byte[] buffer = new byte[size];
                        is.read(buffer);
                        is.close();
                        json = new String(buffer, "UTF-8");
                        Intent myIntent = new Intent(MainActivity.this, ChartActivity.class);
                        myIntent.putExtra("Json",json);
                        Log.e(LOG_TAG,json);
                        chatController.stop();
                        startActivity(myIntent);

                    } catch (IOException ex) {
                        ex.printStackTrace();

                    }
            }});

            NIR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ack=0;
                    JsonReceive=" ";
                    JSONObject request=null;
                    request = new JSONObject();
                  //  JSONObject RequestBody = new JSONObject();
                    try {
                        JSONObject visLeds= new JSONObject();
                        JSONObject RequestBody = new JSONObject();
                        JSONObject conf= new JSONObject();
                        JSONObject uvspectr= new JSONObject();
                        JSONObject nirspectr= new JSONObject();
                        JSONObject nirLeds= new JSONObject();


                        uvspectr.put("t_vis",Integer.valueOf(tvis));
                        uvspectr.put("t_fluo",Integer.valueOf(tfluo));
                        nirLeds.put("V_nir",Integer.valueOf(V_nir));
                        nirspectr.put("NirMicrolamps",nirLeds);
                        visLeds.put("V_UV",Integer.valueOf(V_UV));
                        visLeds.put("Vw_vis",Integer.valueOf(Vw_vis));
                        uvspectr.put("visLeds",visLeds);
                        conf.put("VisSpectrometer",uvspectr);
                        conf.put("NirSpectrometer",nirspectr);
                        RequestBody.put("Use cases", "sensorCalibrationNIR");
                        RequestBody.put("configuration",conf);
                        request.put("Request", RequestBody);

                        request.put("Request",RequestBody);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (request.toString()!=null) {
                        Intent myIntent = new Intent(MainActivity.this, NirSpecs.class);
                        //startActivity(myIntent);
                        sendMessage(request.toString());
                        calibrationflag=true;
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Message Set",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });



            Chart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent myIntent = new Intent(MainActivity.this, ChartActivity.class);
                    myIntent.putExtra("Json",JsonReceive);
                    Log.e(LOG_TAG,JsonReceive);
                    chatController.stop();
                    startActivity(myIntent);

                }
            });
            Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(MainActivity.this, ImageActivity.class);
                    myIntent.putExtra("Json",JsonReceive);
                   // myIntent.putExtra("bytes",outputStream.toByteArray());
                   // outputStream.reset();
                    Log.e(LOG_TAG,JsonReceive);
                    chatController.stop();
                    startActivity(myIntent);
                }
            });
            btnDisConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Log.e(LOG_TAG, "disconnect");
                    chatController.stop();
                    btnDisConnect.setEnabled(false);
                }
            });
            btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Log.e(LOG_TAG, "lalalal");
                    if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        showPrinterPickDialog();
                    }
                }
            });
            Send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONObject request=null;
                    ack=0;
                    JsonReceive=" ";
                    try {
                        if (Use_Case.equals("Mycotoxins detection")) {
                            request = new JSONObject();
                            JSONObject visLeds= new JSONObject();
                            JSONObject RequestBody = new JSONObject();
                            JSONObject conf= new JSONObject();
                            JSONObject uvspectr= new JSONObject();
                            JSONObject nirspectr= new JSONObject();
                            JSONObject nirLeds= new JSONObject();
                           RequestBody.put("Use cases", Use_Case);
                            RequestBody.put("Food type", Food_Type);
                            RequestBody.put("Granularity", Granularitystr);
                            uvspectr.put("t_vis",Integer.valueOf(tvis));
                            uvspectr.put("t_fluo",Integer.valueOf(tfluo));
                            nirLeds.put("V_nir",Integer.valueOf(V_nir));
                            nirspectr.put("NirMicrolamps",nirLeds);
                            visLeds.put("V_UV",Integer.valueOf(V_UV));
                            visLeds.put("Vw_vis",Integer.valueOf(Vw_vis));
                            uvspectr.put("visLeds",visLeds);
                            conf.put("VisSpectrometer",uvspectr);
                            conf.put("NirSpectrometer",nirspectr);
                            RequestBody.put("configuration",conf);
                            request.put("Request", RequestBody);
                        }
                        else {
                            request = new JSONObject();
                            JSONObject nirLeds= new JSONObject();
                            JSONObject nirspectr= new JSONObject();
                            JSONObject conf= new JSONObject();
                            JSONObject uvspectr= new JSONObject();
                            JSONObject visLeds= new JSONObject();
                            JSONObject RequestBody = new JSONObject();
                            RequestBody.put("Use cases", Use_Case);
                            RequestBody.put("Food type", Food_Type2);
                            RequestBody.put("Sample temperature", Temperature);
                            RequestBody.put("Exposure time", Exposure);
                            uvspectr.put("t_vis",Integer.valueOf(tvis));
                            uvspectr.put("t_fluo",Integer.valueOf(tfluo));
                            nirLeds.put("V_nir",Integer.valueOf(V_nir));
                            nirspectr.put("NirMicrolamps",nirLeds);
                            visLeds.put("V_UV",Integer.valueOf(V_UV));
                            visLeds.put("Vw_vis",Integer.valueOf(Vw_vis));
                            conf.put("VisSpectrometer",uvspectr);
                            RequestBody.put("configuration",conf);
                            request.put("Request", RequestBody);
                        }

                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                        // Do something to recover ... or kill the app.
                    }
                    if (request.toString()!=null) {
                        sendMessage(request.toString());
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Message Set",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            WhiteRef.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ack=0;
                    JsonReceive=" ";
                    JSONObject request=null;
                    request = new JSONObject();
                    JSONObject RequestBody = new JSONObject();
                    JSONObject uvspectr = new JSONObject();
                    JSONObject nirspectr= new JSONObject();
                    JSONObject nirLeds= new JSONObject();
                    JSONObject visLeds = new JSONObject();
                    JSONObject conf = new JSONObject();
                    try {
                        RequestBody.put("Use cases", "WhiteReferenceVIS");
                        uvspectr.put("t_vis",Integer.valueOf(tvis));
                        uvspectr.put("t_fluo",Integer.valueOf(tfluo));
                        nirLeds.put("V_nir",Integer.valueOf(V_nir));
                        nirspectr.put("NirMicrolamps",nirLeds);
                        visLeds.put("V_UV",Integer.valueOf(V_UV));
                        visLeds.put("Vw_vis",Integer.valueOf(Vw_vis));
                        uvspectr.put("visLeds",visLeds);
                        conf.put("VisSpectrometer",uvspectr);
                        conf.put("NirSpectrometer",nirspectr);
                        RequestBody.put("configuration",conf);
                        request.put("Request", RequestBody);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (request.toString()!=null) {
                        Intent myIntent = new Intent(MainActivity.this, NirSpecs.class);
                        //startActivity(myIntent);
                        sendMessage(request.toString());

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Message Set",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            });
            WhiteRefUV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ack=0;
                    JsonReceive=" ";
                    JSONObject request=null;
                    request = new JSONObject();
                    JSONObject RequestBody = new JSONObject();
                    JSONObject uvspectr = new JSONObject();
                    JSONObject nirspectr= new JSONObject();
                    JSONObject nirLeds= new JSONObject();
                    JSONObject visLeds = new JSONObject();
                    JSONObject conf = new JSONObject();
                    try {
                        RequestBody.put("Use cases", "WhiteReferenceUV");
                        uvspectr.put("t_vis",Integer.valueOf(tvis));
                        uvspectr.put("t_fluo",Integer.valueOf(tfluo));
                        nirLeds.put("V_nir",Integer.valueOf(V_nir));
                        nirspectr.put("NirMicrolamps",nirLeds);
                        visLeds.put("V_UV",Integer.valueOf(V_UV));
                        visLeds.put("Vw_vis",Integer.valueOf(Vw_vis));
                        uvspectr.put("visLeds",visLeds);
                        conf.put("VisSpectrometer",uvspectr);
                        conf.put("NirSpectrometer",nirspectr);
                        RequestBody.put("configuration",conf);
                        request.put("Request", RequestBody);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (request.toString()!=null) {
                        Intent myIntent = new Intent(MainActivity.this, NirSpecs.class);
                        //startActivity(myIntent);
                        sendMessage(request.toString());

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Message Set",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            });
            OpenConfiguration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
                    View mView = layoutInflaterAndroid.inflate(R.layout.dialogue, null);
                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                    alertDialogBuilderUserInput.setView(mView);

                    final EditText tvisedit = (EditText) mView.findViewById(R.id.tvis);
                    final EditText tfluosedit = (EditText) mView.findViewById(R.id.tfluo);
                    final EditText EditUV_V=(EditText) mView.findViewById(R.id.V_UV);
                    final EditText EditUw_V=(EditText) mView.findViewById(R.id.Vw_vis);
                    final EditText EditU_nir=(EditText) mView.findViewById((R.id.v_NIR));
                    alertDialogBuilderUserInput
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {

                                        tvis=tvisedit.getText().toString();
                                        tfluo=tfluosedit.getText().toString();
                                        V_UV=EditUV_V.getText().toString();
                                        Vw_vis=EditUw_V.getText().toString();
                                        V_nir=EditU_nir.getText().toString();
                                         Log.e(LOG_TAG,tvis);

                                    // ToDo get user input here
                                }
                            })

                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            dialogBox.cancel();
                                        }
                                    });

                    AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                    alertDialogAndroid.show();
                }

            });

        }
    }

    public void onStart() {
        super.onStart();
        if (!adapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            chatController = new ChatController(this, handler);
        }
    }

    @Override
    protected void onRestart() {
        Log.e("MYAPP","Restart");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MYAPP","Resume");
        if (chatController != null) {
            Log.e("MYAPP","Not Null");
            if (chatController.getState() == ChatController.STATE_NONE) {
                Log.e("MYAPP","Sgtate None");
                chatController.start();
                if (connectingDevice!=null)

                chatController.connect(connectingDevice);
            }
            if (chatController.getState() == ChatController.STATE_CONNECTED) {
                Log.e("MYAPP","Connected");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatController != null)
            chatController.stop();
    }

    private void setStatus(String s) {
        status.setText(s);
    }


    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ChatController.STATE_CONNECTED:
                            setStatus("Connected to: " + connectingDevice.getName());
                            btnConnect.setEnabled(false);
                            btnDisConnect.setEnabled(true);
                            break;
                        case ChatController.STATE_CONNECTING:
                            setStatus("Connecting...");
                            btnConnect.setEnabled(false);
                            break;
                        case ChatController.STATE_LISTEN:
                        case ChatController.STATE_NONE:
                            setStatus("Not connected");
                            btnConnect.setEnabled(true);
                            btnDisConnect.setEnabled(false);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;

                    String writeMessage = new String(writeBuf);
                   // chatMessages.add("Me: " + writeMessage);
                   // chatAdapter.notifyDataSetChanged();
                    Log.e(LOG_TAG,"Me: " + writeMessage);
                    break;
                case MESSAGE_READ:
                    int arg=(int)msg.arg2;


                    if (arg==0 || arg==1) {
                        double stage=(double)msg.obj;
                        Log.e(LOG_TAG,connectingDevice.getName()+" Percetage is  " + ":  " + String.valueOf(stage));
                        if(!dia.isShowing()) {
                            dia.show();
                        }
                        if (arg==0) {
                            ProgressBar progressBarData = (ProgressBar) dia.findViewById(R.id.progressBarData);
                            progressBarData.setProgress((int)stage);
                        }
                        else {
                            ProgressBar progressBarData = (ProgressBar) dia.findViewById(R.id.progressBarImage);
                            progressBarData.setProgress((int)stage);
                        }


                    }
                    else if (arg==3) {
                        if(!dia.isShowing()) {
                            dia.show();
                        }
                        ProgressBar progressBarData;
                        TextView measurementstatus;
                        switch ((String)msg.obj) {
                            case "00":
                                measurementstatus=(TextView) dia.findViewById(R.id.MeasurementStatusText);
                                measurementstatus.setText("(1/3) Getting VIS Data");
                                break;
                            case "01":
                                measurementstatus=(TextView) dia.findViewById(R.id.MeasurementStatusText);
                                measurementstatus.setText("(2/3) Getting NIR Data");
                                progressBarData= (ProgressBar) dia.findViewById(R.id.progressBarVIS);
                                progressBarData.setProgress(100);
                                break;
                            case "02":
                                measurementstatus=(TextView) dia.findViewById(R.id.MeasurementStatusText);
                                measurementstatus.setText("(3/3) Getting FLUO Data");
                                progressBarData = (ProgressBar) dia.findViewById(R.id.progressBarNIR);
                                progressBarData.setProgress(100);
                                break;
                            case "03":
                                measurementstatus=(TextView) dia.findViewById(R.id.MeasurementStatusText);
                                measurementstatus.setText("Measurement Finished");
                                progressBarData = (ProgressBar) dia.findViewById(R.id.progressBarFLUO);
                                progressBarData.setProgress(100);
                                break;
                        }


                      //  dia.dismiss();
                    }
                    else {
                        if (arg==5) {
                            byte[] readBuf = (byte[]) msg.obj;
                            try {
                                FileOutputStream fOut = openFileOutput("imagereceived.tif", getApplication().getBaseContext().MODE_PRIVATE);
                                fOut.write(readBuf);
                                fOut.flush();
                                fOut.close();
                                Log.e(LOG_TAG, connectingDevice.getName() + " Megethos  " + ":  " + String.valueOf(readBuf.length));


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            byte[] readBuf = (byte[]) msg.obj;
                            try {
                                FileOutputStream fOut = openFileOutput("output.txt", getApplication().getBaseContext().MODE_PRIVATE);
                                fOut.write(readBuf);
                                fOut.flush();
                                fOut.close();
                                Log.e(LOG_TAG, connectingDevice.getName() + " Megethos  " + ":  " + String.valueOf(readBuf.length));


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                   //
                    //String json= (String) msg.obj;

                   // String readMessage = new String(readBuf, 0, msg.arg1);


                   // chatMessages.add(connectingDevice.getName() + ":  " + readMessage);
                  //  chatAdapter.notifyDataSetChanged();
                 //   Toast.makeText(getApplicationContext(), readMessage,
                  //          Toast.LENGTH_SHORT).show();
                   // Log.e(LOG_TAG,connectingDevice.getName()+" Edw " + ":  " + readMessage);
                  /*  if (readMessage.equals("End of Response")) {
                        byte c[] = imagebuffer.toByteArray( );

                        imagebuffer= new ByteArrayOutputStream();
                        Toast.makeText(getApplicationContext(), readMessage,
                                          Toast.LENGTH_SHORT).show();
                        try {
                            FileOutputStream fOut = openFileOutput("Ximea.tif",getApplication().getBaseContext().MODE_PRIVATE);
                            fOut.write(c);
                            fOut.flush();
                            fOut.close();
                            //Log.e(LOG_TAG,connectingDevice.getName()+" Megethos  " + ":  " + c.length);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (calibrationflag) {
                            Intent myIntent = new Intent(MainActivity.this, NirSpecs.class);
                            if (!checkfail(JsonReceive)) {
                                myIntent.putExtra("Json", JsonReceive);

                               // outputStream.reset();
                               // Log.e(LOG_TAG, String.valueOf(JsonReceive.length()));
                                chatController.stop();
                                calibrationflag = false;
                                startActivity(myIntent);
                            }
                            else {
                                calibrationflag = false;
                                Toast.makeText(getApplicationContext(), "Devise Not Found",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else {
                        try {
                            imagebuffer.write(readBuf);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        JsonReceive+=readMessage;
                        ack++;
                      //  Log.e(LOG_TAG, "len is "+String.valueOf(ack) + " " + String.valueOf(readBuf.length));
                        //SendAck(ack);
                    }

                   // Log.e(LOG_TAG,connectingDevice.getName()+" Edw " + ":  " + readMessage);


                   // Decode(readMessage);
                   */
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(), "Connected to " + connectingDevice.getName(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void showPrinterPickDialog () {
        dialog= new Dialog(this);
        dialog.setContentView(R.layout.device_list);
        dialog.setTitle("Bluetooth Devices");
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        adapter.startDiscovery();

        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) dialog.findViewById(R.id.pairedDeviceList);
        ListView listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList);
        listView.setAdapter(pairedDevicesAdapter);
        listView2.setAdapter(discoveredDevicesAdapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDevicesAdapter.add("None Paired");
        }


        dialog.show();
        Button dialog_btn = (Button) dialog.findViewById(R.id.cancelButton);
        dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }

        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }
        });




    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    chatController = new ChatController(this, handler);
                } else {
                    Toast.makeText(this, "Bluetooth still disabled, turn off application!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
    private void connectToDevice(String deviceAddress) {
        adapter.cancelDiscovery();
        BluetoothDevice device = adapter.getRemoteDevice(deviceAddress);
        chatController.connect(device);
    }
    private void sendMessage(String message) {
        if (chatController.getState() != ChatController.STATE_CONNECTED) {
            Toast.makeText(this, "Connection was lost!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatController.write(send);
        }
    }


    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.e(LOG_TAG,"vrike");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.e(LOG_TAG,"den....");
                if (discoveredDevicesAdapter.getCount() == 0) {
                    discoveredDevicesAdapter.add("Nothing Found");
                }
            }
        }
    };

    private void SendAck(int ack) {
       // JSONObject=new JSONObject()
        JSONObject request=null;
        try {


                request = new JSONObject();
                JSONObject RequestBody = new JSONObject();
                RequestBody.put("Use cases", "ACK");
                RequestBody.put("ACK_NUM", String.valueOf(ack));

                request.put("Request", RequestBody);


        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
            // Do something to recover ... or kill the app.
        }
        if (request.toString()!=null) {
            sendMessage(request.toString());
        }
    }


    private boolean checkfail(String json) {
        try {
            JSONObject obj=(JSONObject) new JSONTokener(json).nextValue();
            JSONObject response=obj.getJSONObject("Response");
            String res=response.getString("Status");
            if (res.equals("FAIL")) {
                return true;
            }
        }
        catch (JSONException e) {

        }


        return false;
    }

    private AlertDialog dialogue() {

        AlertDialog b=null;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.downloading, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final ProgressBar progressBarDataVis= dialogView.findViewById(R.id.progressBarVIS);
        final ProgressBar progressBarDataNir= dialogView.findViewById(R.id.progressBarNIR);
        final ProgressBar progressBarDataFluo= dialogView.findViewById(R.id.progressBarFLUO);
        final ProgressBar progressBarData= dialogView.findViewById(R.id.progressBarData);
        final ProgressBar progressBarDataImage= dialogView.findViewById(R.id.progressBarImage);
        dialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBarDataVis.setProgress(0);
                progressBarDataNir.setProgress(0);
                progressBarDataFluo.setProgress(0);
                progressBarData.setProgress(0);
                progressBarDataImage.setProgress(0);
            }
        });

        b=dialogBuilder.create();

        //b.show();
        return b;
    }



}
