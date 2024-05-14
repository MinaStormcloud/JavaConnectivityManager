package com.example.builds.connectivitymanager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.ScanResult;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import android.text.method.PasswordTransformationMethod;

public class WiFiActivity extends AppCompatActivity{
    private ToggleButton toggleButtonWiFi;
    private ListView listViewWiFi;
    private ArrayAdapter wifiAdapter;
    private ArrayList list;
    private WifiManager wifi;
    private WifiConfiguration config;
    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        toggleButtonWiFi = (ToggleButton) findViewById(R.id.toggleButtonWiFi);
        listViewWiFi = (ListView) findViewById(R.id.listViewWiFi);
        list = new ArrayList();
        wifiAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE); // Assign a value to the wifi manager
        wifi.setWifiEnabled(false);
        wifiAdapter.clear();
        config = new WifiConfiguration();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(wifiReceiver, filter);

        toggleButtonWiFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (wifi.isWifiEnabled() == true)
                {
                    wifi.setWifiEnabled(false);
                    wifiAdapter.clear();
                }
                else
                {
                    wifi.setWifiEnabled(true);
                    wifi.startScan();
                }
            }
        });

        listViewWiFi.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l ) {
                String selectedItem = listViewWiFi.getItemAtPosition(position).toString();
                String MAC = selectedItem.substring(selectedItem.length() - 17);
                String ssid = ((TextView) view).getText().toString();

                showPwdDialog(selectedItem);
            }
        });
    }

    private void showPwdDialog(final String wifiSSID) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connect);
        TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);

        Button dialogButton = (Button) dialog.findViewById(R.id.btnOK);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        pass = (EditText) dialog.findViewById(R.id.textPassword);
        pass.setTransformationMethod(new PasswordTransformationMethod());
        textSSID.setText(wifiSSID);

        dialogButton.setOnClickListener(new View.OnClickListener() { // if the button is clicked, connect to the network;
            @Override
            public void onClick(View v) {
                String checkPassword = pass.getText().toString();

                if (!isConnected()){
                    connectToNetworkWPA(wifiSSID, checkPassword);

                    if(!pass.equals(config.preSharedKey)){
                        Toast.makeText(getApplicationContext(), "The password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_SHORT).show();
                    }
                }

                else{
                    Toast.makeText(getApplicationContext(), "The device is already connected to a network", Toast.LENGTH_SHORT).show();}

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "The connection request was canceled", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public boolean connectToNetworkWPA(String networkSSID, String password ) //For WPA2
    {
        try {            
            config.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain SSID in quotes
            config.preSharedKey = "\"" + password + "\"";

            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            wifi.addNetwork(config); // Update works for Galaxy S6 Edge, Galaxy S7 Edge and GalaxyNote9. Galaxy Note8 shows no connection toast
            int networkId = wifi.addNetwork(config);
            wifi.disconnect();
            wifi.enableNetwork(networkId, true);
            wifi.reconnect();
            Toast.makeText(getApplicationContext(), "Reconnecting...", Toast.LENGTH_SHORT).show();

            //Wi-Fi Connection success, return true
            return true;
        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    private void connectDefault(){
        Intent discoverableIntent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK); // Displays the built-in menu
        startActivityForResult(discoverableIntent, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(wifiReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(wifiReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifi.setWifiEnabled(false);
    }

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            wifiAdapter.clear(); // Remove all previous search results

            String action = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action))
            {
                List<ScanResult> scanwifinetworks = wifi.getScanResults();
                int foundCount = scanwifinetworks.size();
                for (ScanResult wifinetwork : scanwifinetworks)
                {
                    list.add(wifinetwork.SSID + "\n" + wifinetwork.BSSID + "\n" + wifinetwork.level + " dB" + "\n" + wifinetwork.capabilities);
                    listViewWiFi.setAdapter(wifiAdapter); // Connect the results to the arrayAdapter
                }
                Toast.makeText(context, foundCount + " networks", Toast.LENGTH_SHORT).show();
            }

            if (isConnected()){
                Toast.makeText(getApplicationContext(), "Connected to the network", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
