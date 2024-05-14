package com.example.builds.connectivitymanager;

import android.os.Bundle;
import android.Manifest;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

public class LocationActivity extends PermissionsManager {

    Button btnShowLocation;
    Button btnMaps;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    
    GPSTracker gps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        btnShowLocation = (Button) findViewById(R.id.btnLocation);
        btnMaps =(Button)findViewById(R.id.btnMaps);
        
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                
                gps = new GPSTracker(LocationActivity.this);
                
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                            + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    
                    gps.showSettingsAlert();
                }

            }
        });

        btnMaps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }
}
