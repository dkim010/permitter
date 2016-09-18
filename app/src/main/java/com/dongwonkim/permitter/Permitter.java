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

    private static OnPermissionResult mOnGranted;
    private static OnPermissionResult mOnDenied;

    public static void execute(String[] permissions, OnPermissionResult granted, OnPermissionResult denied, Activity activity){
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
            granted.onResult();
            return;
        }

        mOnGranted = granted;
        mOnDenied = denied;

        // activity version
//        Intent i = new Intent(activity, PermitterActivity.class);
//        final Bundle bundle = new Bundle();
//        bundle.putStringArray(PermitterActivity.KEY_PERMISSIONS, permissions);
//        bundle.putParcelable(PermitterActivity.KEY_MESSENGER, new Messenger(mHandler));
//        i.putExtras(bundle);
//        activity.startActivity(i);

        // fragment version
        final FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(PermitterFragment.create(permissions, mHandler), PERMITTER_TAG).commit();
    }

    private static Handler mHandler = new Handler(){
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
