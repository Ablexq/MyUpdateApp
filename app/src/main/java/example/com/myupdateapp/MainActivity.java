package example.com.myupdateapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import example.com.myupdateapp.constant.Common;
import example.com.myupdateapp.service.UpdateService;
import example.com.myupdateapp.util.UpdateManager;


public class MainActivity extends AppCompatActivity {

    private LocalBroadcastManager mLocalBroadcastManager;
    private MyBroadcastReceiver mBroadcastReceiver;
    private UpdateManager updateManager;
    private ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Common.DOWNLOAD_APK_ACTION);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLocalBroadcastManager != null && mBroadcastReceiver != null) {
            mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
        }
    }

    public void onclick(View view) {
        updateManager = new UpdateManager(this);
        updateManager.checkUpdateInfo();
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Common.DOWNLOAD_APK_ACTION:
                    int type = intent.getIntExtra("type", 0);
                    int progress = intent.getIntExtra("progress", 0);

                    switch (type) {
                        case UpdateService.DOWN_START://获取maxProgress
                            mProgressBar = updateManager.showDownloadDialog(progress);
                            Log.e("MainActivity", "main progress==" + progress);
                            break;

                        case UpdateService.DOWN_PROGRESS://获取curProgress
                            if (mProgressBar != null && mProgressBar.isShowing()) {
                                mProgressBar.setProgress(progress);
                                Log.e("MainActivity", "main mProgressBar.getProgress()==" + mProgressBar.getProgress());
                                Log.e("MainActivity", "main mProgressBar.getMax()==" + mProgressBar.getMax());
                                if (mProgressBar.getProgress() >= mProgressBar.getMax()) {
                                    try {
                                        mProgressBar.cancel();
                                        updateManager.installApk();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            break;
                    }

                    break;
            }
        }
    }
}
