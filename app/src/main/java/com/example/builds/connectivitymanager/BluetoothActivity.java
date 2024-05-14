package com.example.builds.connectivitymanager;

import android.app.Activity;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ToggleButton;
import android.os.Bundle;

import android.content.BroadcastReceiver;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Context;

import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.AdapterView;
import android.bluetooth.BluetoothManager;
import java.lang.reflect.Method;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ToggleButton toggleButton;
    private ListView listview;
    private ArrayAdapter adapter;
    private ArrayList list;

    private static final int ENABLE_BT_REQUEST_CODE = 1;
    private static final int DISCOVERABLE_BT_REQUEST_CODE = 2;
    private static final int DISCOVERABLE_DURATION = 300;
    private Set<BluetoothDevice>pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        listview = (ListView) findViewById(R.id.listView);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        list = new ArrayList();
        adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(broadcastReceiver, filter);
        adapter.clear();

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(), "This device does not support Bluetooth", Toast.LENGTH_SHORT).show();
                    toggleButton.setChecked(false);
                } else {

                    if (toggleButton.isChecked()) {
                        if (!bluetoothAdapter.isEnabled()) {
                            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBluetoothIntent, ENABLE_BT_REQUEST_CODE);
                        } else {
                            discoverDevices();
                            makeDiscoverable();
                        }
                    } else {
                        bluetoothAdapter.disable();
                        adapter.clear();
                    }
                }
            }
        });

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l ) {

                String selectedItem = listview.getItemAtPosition(position).toString();
                String MAC = selectedItem.substring(selectedItem.length() - 17);
                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(MAC);
                Toast.makeText(getApplicationContext(), MAC, Toast.LENGTH_LONG).show();

                pairedDevices = bluetoothAdapter.getBondedDevices();

                if (pairedDevices != null && pairedDevices.size() > 0)
                {
                    for(BluetoothDevice bt : pairedDevices)
                        if (bt.getAddress().equals(bluetoothDevice.getAddress())) // is this particular device already bonded?
                        {
                            Toast.makeText(getApplicationContext(), "These devices are already bonded", Toast.LENGTH_SHORT).show();
                            BluetoothManager blue = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
                            Intent blueIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                            startActivityForResult(blueIntent, 0); // Displays the built-in Bluetooth menu
                        }
                }

                ConnectingThread t = new ConnectingThread(bluetoothDevice); // Initiate a connection request in a separate thread
                t.start();
            }
        });
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (!list.contains(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress())){ // update for Galaxy Note9
                    list.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                }

                listview.setAdapter(adapter);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) && bluetoothAdapter.isEnabled())
            {
                Toast.makeText(context, "Discovery has finished!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENABLE_BT_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {

                makeDiscoverable();
                discoverDevices();

            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
                toggleButton.setChecked(false);
            }
        }
    }

    protected void discoverDevices(){

        if (bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    protected void makeDiscoverable(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
        startActivityForResult(discoverableIntent, DISCOVERABLE_BT_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.disable();
    }

    private class ConnectingThread extends Thread {
        private BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConnectingThread(BluetoothDevice device) {

            bluetoothDevice = device;

            try
            {
                Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
                bluetoothSocket = (BluetoothSocket) m.invoke(bluetoothDevice, 1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void run() {

            bluetoothAdapter.cancelDiscovery();

            try {
                bluetoothSocket.connect();
            } catch (IOException connectException) {
                connectException.printStackTrace();
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
