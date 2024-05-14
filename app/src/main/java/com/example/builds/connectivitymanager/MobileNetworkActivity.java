package com.example.builds.connectivitymanager;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.*;
import android.telephony.SignalStrength;
import android.view.View;
import android.widget.*;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MobileNetworkActivity extends AppCompatActivity{
    private TelephonyManager telephonyManager;
    private CustomPhoneStateListener customPhoneStateListener;
    private Button btnGetSignalStrength;
    private TextView gsmStrengthTextView;
    private ImageView gsmStrengthImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_signal);

        customPhoneStateListener = new CustomPhoneStateListener();
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        gsmStrengthTextView = (TextView)findViewById(R.id.textViewMN);
        gsmStrengthImageView = (ImageView)findViewById(R.id.imageViewMN);
        btnGetSignalStrength = (Button)findViewById(R.id.btnGetSignalStrength);
        gsmStrengthTextView.setText("Signal Strength : " );
        telephonyManager.listen(customPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS); //initial check

        btnGetSignalStrength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telephonyManager.listen(customPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);//check when clicked
                gsmStrengthImageView.setImageLevel(customPhoneStateListener.signalStrengthValue);
                gsmStrengthTextView.setText("Signal Strength : " + customPhoneStateListener.signalStrengthValue);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class CustomPhoneStateListener extends PhoneStateListener {
        public int signalStrengthValue;

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            signalStrengthValue = signalStrength.getGsmSignalStrength();

            if (signalStrength.isGsm()) {
                if (signalStrength.getGsmSignalStrength() != 99)
                    signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
                else
                    signalStrengthValue = signalStrength.getGsmSignalStrength();
            } else {
                signalStrengthValue = signalStrength.getCdmaDbm();
            }
        }
    }
}
