package com.cmput301w20t10.uberapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.cmput301w20t10.uberapp.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * This activity is responsible for scanning the QR code. The result of the qr code
 * is sent back through an intent under JSON tag
 *
 * The format for this was found on
 * https://stackoverflow.com/questions/8831050/android-how-to-read-qr-code-in-my-application
 * answered by Amardeep and edited by Ballu
 */
public class QRScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
    }

    @Override
    public void onResume() {
        super.onResume();

        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent();
        intent.putExtra("JSON", result.getText());
        setResult(RESULT_OK, intent);
        finish();
        // Do the transition here passing the intent
        Intent passedIntent = new Intent(this, LoginActivity.class);
        passedIntent.putExtra("JSON", result.getText());

        startActivity(passedIntent);
    }
}
