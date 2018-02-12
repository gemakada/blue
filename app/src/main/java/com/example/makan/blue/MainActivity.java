package com.example.makan.blue;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * Created by makan on 16/1/2018.
 */

public class MainActivity extends AppCompatActivity {
    String[] SPINNERLIST = {"Use Case 1", "Use Case 2"};
    String[] SPINNERLIST2 = {"Maize flour","Skimmed milk powder","Paprika powder","Almond","Peanuts"};
    String[] SPINNERLIST3 = {"Low","Medium","High"};
    String[] Mycotoxins = {"AF B1","Total AFs","DON"};
    String[] Granularity= {"Low","Medium","High"};
    String[] Foodlist={"Maize flour","Skimmed milk powder","Paprika powder","Almond","Peanuts"};
    String[] Foodlist2={"Rocket"};
    String[] Templist={"8C"};
    String[] Explist={"0h","14h","206h"};

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static int REQUEST_ENABLE_BT = 1;
    private ArrayAdapter<String> discoveredDevicesAdapter;
    private Button btnConnect;
    private Button btnDisConnect;
    private Button Send;
    private Button Chart;
    private Button Cancel;
    private Dialog dialog;
    private TextView status;
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
            Log.e(LOG_TAG, "Adapter Available");
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

            Chart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent myIntent = new Intent(MainActivity.this, ChartActivity.class);
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
                    try {
                        if (Use_Case.equals("Use Case 1")) {
                            request = new JSONObject();
                            JSONObject RequestBody = new JSONObject();
                            RequestBody.put("USE_CASE", Use_Case);
                            RequestBody.put("Food_Type", Food_Type);
                            RequestBody.put("Granularity", Granularitystr);
                            request.put("Request", RequestBody);
                        }
                        else {
                            request = new JSONObject();
                            JSONObject RequestBody = new JSONObject();
                            RequestBody.put("USE_CASE", Use_Case);
                            RequestBody.put("Food_Type", Food_Type2);
                            RequestBody.put("Temperature", Temperature);
                            RequestBody.put("Exposure_Hours", Exposure);
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
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);
                   // chatMessages.add(connectingDevice.getName() + ":  " + readMessage);
                  //  chatAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), readMessage,
                            Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG,connectingDevice.getName() + ":  " + readMessage);
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
}
