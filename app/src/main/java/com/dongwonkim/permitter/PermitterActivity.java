package com.dongwonkim.permitter;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by Naver on 2016. 9. 18..
 */
public class PermitterActivity extends Activity {
    public static final String TAG = "Permitter";
    public static final String KEY_PERMISSIONS = "permissions";
    public static final String KEY_MESSENGER = "granted";

    private String[] mPermissions;
    private Messenger mMessenger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissions = getIntent().getExtras().getStringArray(KEY_PERMISSIONS);
        mMessenger = getIntent().getExtras().getParcelable(KEY_MESSENGER);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(mPermissions, 42);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult");
        boolean ret = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                Log.e(TAG, "permission denied: " + permissions[i]);
                ret = false;
            }
        }

        if (ret) {
            send(0);
        } else {
            send(1);
        }

        this.finish();
    }

    private void send(int what){
        Message msg = Message.obtain();
        msg.what = what;
        try {
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
