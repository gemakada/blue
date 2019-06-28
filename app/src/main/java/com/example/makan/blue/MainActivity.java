package com.example.makan.blue;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.example.makan.blue.ViewHolders.Player;
import com.example.makan.blue.ViewHolders.PlayersDataAdapter;
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
import java.util.List;
import java.util.Set;

/**
 * Created by makan on 16/1/2018.
 */

public class MainActivity extends AppCompatActivity {
    private BleConnectionService mBluetoothLeService;
    private ArrayList<Data> Datalist;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static int REQUEST_ENABLE_BT = 1;
    private ArrayAdapter<String> discoveredDevicesAdapter;
    private ArrayAdapter<String> leDeviceListAdapter;
    private Button btnConnect;
    private Button btnDisConnect;
    private PlayersDataAdapter mAdapter;
    private Dialog dialog;
    private TextView status;

    private BluetoothAdapter adapter;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "device_name";
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private ChatController chatController;


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BleConnectionService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
               Log.e(LOG_TAG, "Unable to initialize Bluetooth");
//                finish();
//            }
                // Automatically connects to the device upon successful start-up initialization.
                //    mBluetoothLeService.connect(mDeviceAddress);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
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



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);                     //Mpainei toolbar
        setSupportActionBar(toolbar);






        //status = (TextView) findViewById(R.id.status);
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





        }
        Intent gattServiceIntent = new Intent(this, BleConnectionService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        setPlayersDataAdapter();
        setupRecyclerView();
    }

    public void onStart() {
        super.onStart();
        if (!adapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void setStatus(String s) {
        status.setText(s);
    }



    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
    }

    private void setPlayersDataAdapter() {
        List<Player> players = new ArrayList<>();
        try {
            InputStreamReader is = new InputStreamReader(getAssets().open("players.csv"));

            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;
            String[] st;
            while ((line = reader.readLine()) != null) {
                st = line.split(",");
                Player player = new Player();
                player.setName(st[0]);
                player.setNationality(st[1]);
                player.setClub(st[4]);
                player.setRating(Integer.parseInt(st[9]));
               player.setAge(Integer.parseInt(st[14]));
                players.add(player);
            }
        } catch (IOException e) {

        }

        mAdapter = new PlayersDataAdapter(players);
    }
    private void showPrinterPickDialog () {
        dialog= new Dialog(this);
        dialog.setContentView(R.layout.device_list);
        dialog.setTitle("Bluetooth Devices");
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        adapter.startDiscovery();
        adapter.startLeScan(leScanCallback);
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        leDeviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) dialog.findViewById(R.id.pairedDeviceList);
        ListView listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList);
        ListView listView3 = (ListView) dialog.findViewById(R.id.discoveredBLEDeviceList);
        listView.setAdapter(pairedDevicesAdapter);
        listView2.setAdapter(discoveredDevicesAdapter);
        listView3.setAdapter(leDeviceListAdapter);

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

                //connectToDevice(address);
                dialog.dismiss();
            }
        });

        listView3.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);
                Log.e(LOG_TAG,address);
                if (mBluetoothLeService!=null) {

                }
                else{
                    Log.e(LOG_TAG,"Null Reference");
                }
                mBluetoothLeService.connect(address);
//                String info = ((TextView) view).getText().toString();
//                String address = info.substring(info.length() - 17);
//
//                connectToDevice(address);
                dialog.dismiss();
            }
        });





    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                   // chatController = new ChatController(this, handler);
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

    //private LeDeviceListAdapter leDeviceListAdapter;
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            leDeviceListAdapter.add(device.getName() + "\n" + device.getAddress());
                            //leDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };
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
