package com.dongwonkim.permitter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by Naver on 2016. 9. 18..
 */
public final class Permitter {
    private static final String TAG = "Permitter";
    private static final String PERMITTER_TAG = "permitter_tag";

    private OnPermissionResult mOnGranted;
    private OnPermissionResult mOnDenied;

    public Permitter(OnPermissionResult granted, OnPermissionResult denied){
        mOnGranted = granted;
        mOnDenied = denied;
    }

    public void execute(String[] permissions, Activity activity){
        boolean passed = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String p : permissions) {
                if (activity.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    passed = false;
                    break;
                }
            }
        }

        if(passed){
            mOnGranted.onResult();
            return;
        }

        // fragment version
        final FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(PermitterFragment.create(permissions, mHandler), PERMITTER_TAG).commit();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "handleMssage, " + msg.what);
            switch(msg.what){
                case 0:
                    mOnGranted.onResult();
                    break;
                case 1:
                    mOnDenied.onResult();
                    break;
            }
        }
    };
}
