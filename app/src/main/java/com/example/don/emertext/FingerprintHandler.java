package com.example.don.emertext;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;


//
// Created by Abhijeet on 11/11/2016.
//

@TargetApi(23)
class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private Context appContext;

    FingerprintHandler(Context context) {
        appContext = context;
    }

    // Method will start the authentication process: permission checks
    void startAuth(FingerprintManager manager,
                   FingerprintManager.CryptoObject cryptoObject) {

        CancellationSignal cancellationSignal = new CancellationSignal();

        if (ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    // Toasts the message on an error
    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
        Toast.makeText(appContext,
                "Authentication error\n" + errString,
                Toast.LENGTH_LONG).show();
    }

    //Toasts the message when recoverable error has been encountered during authentication
    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
        Toast.makeText(appContext,
                "Authentication help\n" + helpString,
                Toast.LENGTH_LONG).show();
    }

    // Toasts the message on authentication failed
    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(appContext,
                "Authentication failed.",
                Toast.LENGTH_LONG).show();
    }

    // Toasts the message on authntication successful and moves to the next activity
    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {

        Toast.makeText(appContext,
                "Authentication succeeded.",
                Toast.LENGTH_LONG).show();

        Intent i = new Intent(appContext, WelcomeActivity.class);
        appContext.startActivity(i);
        ((Activity) appContext).finish();
    }


}