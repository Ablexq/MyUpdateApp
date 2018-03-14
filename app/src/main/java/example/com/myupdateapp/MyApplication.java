package example.com.myupdateapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by lenovo on 2018/3/14.
 */

public class MyApplication extends Application {


    private static MyApplication mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
