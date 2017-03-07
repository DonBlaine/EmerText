package com.example.don.emertext;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

// External Library is Used for locking the application using password: https://github.com/mattsilber/applock
import com.guardanis.applock.CreateLockDialogBuilder;
import com.guardanis.applock.UnlockDialogBuilder;
import com.guardanis.applock.locking.ActionLockingHelper;
import com.guardanis.applock.locking.LockingHelper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class SecurityActivity extends AppCompatActivity {
    private KeyStore keyStore;
    private static final String KEY_NAME = "not_null";
    private Cipher cipher;

    public SecurityActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            fingerScannerMain(); // If android version greater than 23 call fingerscanning method
        } else {

            onResume(); // else go for the password lock
        }
    }

    // Password locking method: checking if there are saved password if not prompting to create one
    @Override
    public void onResume(){
        super.onResume();

        if(LockingHelper.hasSavedPIN(this))
            showUnlockDialog();
        else
            new CreateLockDialogBuilder(this, new CreateLockDialogBuilder.LockCreationListener(){
                public void onLockCanceled(){
                    finish(); // No PIN entered, don't let them through
                } // Dialog was closed without entry
                public void onLockSuccessful(){
                    showUnlockDialog();
                }})
                    .show();
    }

    // Shows the dialog box to unlock the next activity
    private void showUnlockDialog(){
        ActionLockingHelper.unlockIfRequired(this, new UnlockDialogBuilder.UnlockEventListener() {
            public void onCanceled() {
                finish(); // If no pin entered, it will not allow user to enter the app
            } // Dialog was closed without entry
            public void onUnlockFailed(String reason) {
                finish(); // If no pin entered, it will not allow user to enter the app
            } // Not called with default Dialog, instead is handled internally
            public void onUnlockSuccessful() {
                Intent intent = new Intent(SecurityActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Main finger scanning method which includes checks for hardware, permissions, has enrolled finger prints and cipher
    @TargetApi(23)
    public void fingerScannerMain(){
        KeyguardManager keyguardManager;
        FingerprintManager fingerprintManager;
        FingerprintManager.CryptoObject cryptoObject;

        fingerprintManager =
                (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (fingerprintManager.isHardwareDetected()){

            setContentView(R.layout.activity_security);
            keyguardManager =
                    (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (!keyguardManager.isKeyguardSecure()) {

                Toast.makeText(this,
                        "Lock screen security not enabled in Settings",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.USE_FINGERPRINT) !=
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "Fingerprint authentication permission not enabled",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // This happens when no fingerprints are registered.
                Toast.makeText(this,
                        "Register at least one fingerprint in settings to lock using fingerprint instead of pin",
                        Toast.LENGTH_LONG).show();
                return;
            }

            generateKey();

            if (cipherInit()) {
                cryptoObject = new FingerprintManager.CryptoObject(cipher);
                FingerprintHandler helper = new FingerprintHandler(this);
                helper.startAuth(fingerprintManager, cryptoObject);
            }
        }
        else{
            onResume();
        }
    }

    // Generates encrypted key which is then stored in the Android Keystore system securely
    @TargetApi(23)
    protected void generateKey() {
        KeyGenerator keyGenerator;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            throw new RuntimeException(
                    "Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // It creates the cipher instance and initialise it with the key stored in keystore.
    @TargetApi(23)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
}
