package com.example.makan.blue;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class BleConnectionService extends Service  {
    private final static String TAG = BleConnectionService.class.getSimpleName();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private String bluetoothDeviceAddress;
    private BluetoothGatt bluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private String mBluetoothDeviceAddress;
    private boolean flag =false;
    Rolling item;
    private  RssiReader reader = new RssiReader();
    //private int connectionState = STATE_DISCONNECTED;

    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

   // public final static UUID UUID_HEART_RATE_MEASUREMENT =
         //   UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }
    private final BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    String intentAction;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        intentAction = ACTION_GATT_CONNECTED;
                        mConnectionState = STATE_CONNECTED;
                      //  broadcastUpdate(intentAction);
                        Log.i(TAG, "Connected to GATT server.");
                        Log.i(TAG, "Attempting to start service discovery:" +
                                mBluetoothGatt.discoverServices());

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        reader.cancel(true);
                        intentAction = ACTION_GATT_DISCONNECTED;
                        mConnectionState = STATE_DISCONNECTED;
                        Log.i(TAG, "Disconnected from GATT server.");
                     //   broadcastUpdate(intentAction);
                    }
                }
                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status){
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        float distance;
                        float d1;
                        d1 = (float)(-59-rssi)/40;
                        distance = (float) Math.pow(10.0,d1);
                        item.add(distance);
                       // Log.w(TAG, String.format("BluetoothGatt ReadRssi[%d]", rssi));
                        Log.w(TAG, "Distance is "+String.valueOf(item.getAverage()));
                        if ((distance<2)&&(flag==false)) {
                            BluetoothGattCharacteristic characteristic = gatt.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")).getCharacteristic(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"));
                            byte [] array = {13,9,10,0,9,14,11,10,1,6,3,6,13,9,0,12};
                            characteristic.setValue(array);
                            gatt.writeCharacteristic(characteristic);
                            flag = true;
                        }

                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        for(int i=0; i<gatt.getServices().size(); i++) {
                            Log.w(TAG, "Available services are: " + gatt.getServices().get(i).getUuid().toString());
                        }
                        List<BluetoothGattCharacteristic> gattCharList;
                        gattCharList=gatt.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")).getCharacteristics();
                        for (int j=0; j<gattCharList.size(); j++) {
                            Log.w(TAG, "Available characteristics are: " + gattCharList.get(j).getUuid().toString());
                        }
                       BluetoothGattCharacteristic characteristic = gatt.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")).getCharacteristic(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"));
                        String text = "D9A09EBA1636D90C";
                        byte [] array = {13,9,10,0,9,14,11,10,1,6,3,6,13,9,0,12};
                        //characteristic.setValue(array);
                        for (int i=0; i<text.getBytes().length; i++) {
                            Log.w(TAG, "Byte is " + text.getBytes()[i]);
                           // Log.w(TAG, "Available characteristics are: " + gattCharList.get(j).getUuid().toString());
                        }
                       // gatt.writeCharacteristic(characteristic);
                       // gatt.readRemoteRssi();
                        //RssiReader reader = new RssiReader();
                       // Rolling item;
                        item = new Rolling(100);
                        reader.execute(gatt);

                       // broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                    //    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }
                }
       };

    public class LocalBinder extends Binder {
        BleConnectionService getService() {
            return BleConnectionService.this;
        }
    }
    public boolean connect(final String address) {
        Log.e(TAG, "lalalalallalalalalaSergice");
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, gattCallback);
        mBluetoothGatt.connect();
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }


    public class RssiReader extends AsyncTask<BluetoothGatt, Void, Void> {

        @Override
        protected Void doInBackground(BluetoothGatt... params) {
            while(!this.isCancelled()) {
               // Log.w(TAG,"Trying to read Rssi");
                params[0].readRemoteRssi();
            }
            // your load work
          //  return myString;
           // return "test";
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.w(TAG,"Terminated Thread");

        }

    }
    public class Rolling {

        private int size;
        private double total = 0d;
        private int index = 0;
        private double samples[];

        public Rolling(int size) {
            this.size = size;
            samples = new double[size];
            for (int i = 0; i < size; i++) samples[i] = 0d;
        }

        public void add(double x) {
            total -= samples[index];
            samples[index] = x;
            total += x;
            if (++index == size) index = 0; // cheaper than modulus
        }

        public double getAverage() {
            return total / size;
        }
    }

}

