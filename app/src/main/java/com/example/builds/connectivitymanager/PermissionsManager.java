package com.example.builds.connectivitymanager;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.CHANGE_NETWORK_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.SEND_SMS;

public class PermissionsManager extends AppCompatActivity {
    final private int RequestPermissionCode = 1;

    public  void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE, BLUETOOTH,
                BLUETOOTH_ADMIN, CHANGE_NETWORK_STATE, CHANGE_WIFI_STATE, INTERNET, SEND_SMS}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean BluetoothPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean BluetoothAdminPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean AccessCoarseLocationPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean AccessFineLocationPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean AccessNetworkStatePermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean AccessWifiStatePermission = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean ChangeNetworkStatePermission = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean ChangeWifiStatePermission = grantResults[7] == PackageManager.PERMISSION_GRANTED;
                    boolean InternetPermission = grantResults[8] == PackageManager.PERMISSION_GRANTED;
                    boolean SMSPermission = grantResults[9] == PackageManager.PERMISSION_GRANTED;

                    if (BluetoothPermission && BluetoothAdminPermission && AccessCoarseLocationPermission &&
                            AccessFineLocationPermission && AccessNetworkStatePermission && AccessWifiStatePermission &&
                            ChangeNetworkStatePermission && ChangeWifiStatePermission && InternetPermission && SMSPermission) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), BLUETOOTH);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), BLUETOOTH_ADMIN);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_WIFI_STATE);
        int result6 = ContextCompat.checkSelfPermission(getApplicationContext(), CHANGE_NETWORK_STATE);
        int result7 = ContextCompat.checkSelfPermission(getApplicationContext(), CHANGE_WIFI_STATE);
        int result8 = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int result9 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED
                && result6 == PackageManager.PERMISSION_GRANTED && result7 == PackageManager.PERMISSION_GRANTED
                && result8 == PackageManager.PERMISSION_GRANTED && result9 == PackageManager.PERMISSION_GRANTED;
    }
}
