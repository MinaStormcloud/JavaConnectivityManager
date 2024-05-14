package com.example.builds.connectivitymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends PermissionsManager {

    private Button btnBluetooth;
    private Button btnWiFi;
    private Button btnNFC;
    private Button btnMobileData;
    private Button btnGetSignalStrength;
    private Button btnPhoneCall;
    private Button btnSMS;
    private Button btnGPS;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (checkPermission()) {
            setContentView(R.layout.activity_main);
        }
        else{
            requestPermission();
        }

        btnBluetooth = (Button)findViewById(R.id.btnBluetooth);
        btnWiFi = (Button)findViewById(R.id.btnWiFi);
        btnNFC = (Button)findViewById(R.id.btnNFC);
        btnMobileData = (Button)findViewById(R.id.btnMobileData);
        btnGetSignalStrength = (Button)findViewById(R.id.btnGetSignalStrength);
        btnPhoneCall = (Button)findViewById(R.id.btnPhoneCall);
        btnGPS = (Button)findViewById(R.id.btnGPS);
        btnSMS = (Button)findViewById(R.id.btnSMS);

        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivity(intent);
            }
        });

        btnWiFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WiFiActivity.class);
                startActivity(intent);
            }
        });


        btnNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NFC_Activity.class);
                startActivity(intent);
            }
        });

        btnMobileData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity");
                startActivityForResult(intent, 1);
            }
        });

        btnGetSignalStrength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MobileNetworkActivity.class);
                startActivity(intent);
            }
        });

        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                startActivity(intent);
            }
        });

        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SMS_Activity.class);
                startActivity(intent);
            }
        });

        btnPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                startActivity(phoneIntent);
            }
        });
    }
}