package com.example.builds.connectivitymanager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import android.app.Activity;
import android.widget.*;
import android.widget.Toast;
import android.widget.EditText;

import android.content.Intent;
import android.view.View;
import android.provider.Settings;
import java.nio.charset.Charset;

import android.content.SharedPreferences.Editor;

public class NFC_Activity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {

    private NfcAdapter nfcAdapter;
    EditText editText;
    TextView msgTextView;
    String beamText;
    String CURSOR_HERE;
    String SAVED_TEXT;

    private final Handler nfcHandler;
    private static final int MESSAGE_SENT = 1;

    class HandlerClass extends Handler {
        final /* synthetic */ NFC_Activity this$0;

        HandlerClass(NFC_Activity sendTextViaNFCActivity) {
            super();
            this$0 = sendTextViaNFCActivity;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(this$0.getApplicationContext(), "Message from the dark side!", Toast.LENGTH_SHORT).show();
                    msgTextView.setText("Message delivered: \n" + editText.getText());
                    break;
            }
        }
    }

    public NFC_Activity() {
        super();
        SAVED_TEXT = "SAVED_TEXT";
        CURSOR_HERE = "";
        beamText = "";
        nfcHandler = new HandlerClass(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nfc);

        editText = (EditText) findViewById(R.id.editText);
        msgTextView = (TextView) findViewById(R.id.msgTextView);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Button btnNFC_Menu = (Button) findViewById(R.id.btnNFC_Menu);
        msgTextView.setText("");
        beamText = "";

        nfcAdapter.setNdefPushMessageCallback(this, this); // Register callback to set NDEF message
        nfcAdapter.setOnNdefPushCompleteCallback(this, this); // Register callback to listen for message-sent success


        btnNFC_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentOpenNFCSettings = new Intent();
                intentOpenNFCSettings.setAction(Settings.ACTION_NFC_SETTINGS);
                startActivity(intentOpenNFCSettings);
            }
        });

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "NFC is disabled.", Toast.LENGTH_LONG).show();
            return;
        } else if (nfcAdapter == null) {
            Toast.makeText(getApplicationContext(), "NFC is not available.", Toast.LENGTH_LONG).show(); // Device does not support NFC
            finish();
        }
    }

    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = (editText.toString());
        NdefRecord textRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, editText.getText().toString().getBytes(Charset.forName("US-ASCII")), new byte[0], new byte[0]);
        NdefMessage msg = new NdefMessage(new NdefRecord[]{
                        textRecord
                });
        return msg;
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String type = intent.getType();
        if (!"android.intent.action.SEND".equals(intent.getAction()) || type == null) {
            if ("android.nfc.action.NDEF_DISCOVERED".equals(getIntent().getAction())) {
                processIntent(getIntent());
            }
            String restoredText = getPreferences(0).getString(SAVED_TEXT, null);
            if (restoredText != null) {
                editText.setText(restoredText.replaceAll(CURSOR_HERE, beamText));
            }
        } else if ("text/plain".equals(type)) {
            editText.setText(intent.getStringExtra("android.intent.extra.TEXT"));
        }
        editText.setSelection(editText.getText().length());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void processIntent(Intent intent)
    {
        beamText = new String(((NdefMessage) intent.getParcelableArrayExtra("text/plain")[0]).getRecords()[0].getPayload());
    }

    public void onNdefPushComplete(NfcEvent arg0) {
        nfcHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    @Override
    protected void onPause() {
        super.onPause();
        editText.getText().insert(editText.getSelectionStart(), CURSOR_HERE);
        Editor editor = getPreferences(0).edit();
        editor.putString(SAVED_TEXT, editText.getText().toString());
        editor.commit();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}

