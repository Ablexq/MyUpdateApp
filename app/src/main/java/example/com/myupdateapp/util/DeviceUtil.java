package example.com.myupdateapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * 手机设备相关
 */

public class DeviceUtil {

    /**
     * 获取DisplayMetrics
     */
    private static DisplayMetrics obtain(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getDeviceWidth(Context context) {
        DisplayMetrics outMetrics = obtain(context);
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getDeviceHeight(Context context) {
        DisplayMetrics outMetrics = obtain(context);
        return outMetrics.heightPixels;
    }

    /**
     * 获取屏幕大小[0]宽，[1]高
     */
    public static int[] getDeviceResolution(Context context) {
        DisplayMetrics outMetrics = obtain(context);
        int[] sizes = new int[2];
        sizes[0] = outMetrics.widthPixels;
        sizes[1] = outMetrics.heightPixels;
        return sizes;
    }

    /**
     * 获取设备屏幕密度dpi，每寸所包含的像素点
     */
    public static float getDeviceDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    /**
     * 获取设备屏幕密度,像素的比例
     */
    public static float getDeviceDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取状态栏高度
     *
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 截取当前屏幕画面为bitmap图片
     *
     * @param activity
     * @param hasStatusBar 是否包含当前状态栏,true:包含
     * @return
     */
    public static Bitmap snapCurrentScreenShot(Activity activity, boolean hasStatusBar) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap bmp = decorView.getDrawingCache();
        int deviceSize[] = getDeviceResolution(activity);
        int coordinateY = 0;
        int cutHeight = deviceSize[1];
        if (!hasStatusBar) {
            Rect frame = new Rect();
            decorView.getWindowVisibleDisplayFrame(frame);
            coordinateY += frame.top;
            cutHeight -= frame.top;
        }
        Bitmap shot = Bitmap.createBitmap(bmp, 0, coordinateY, deviceSize[0], cutHeight);
        decorView.destroyDrawingCache();
        return shot;
    }

    /*---------------------------------------手机信息--------------------------------------------*/

    /**
     * 获取手机IMEI号
     * add <uses-permission android:name="android.permission.READ_PHONE_STATE" /> in AndroidManifest.xml
     *
     * @param context
     * @return getDeviceId
     */
    public static String getDeviceIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取手机imsi号
     * add <uses-permission android:name="android.permission.READ_PHONE_STATE" /> in AndroidManifest.xml
     *
     * @param context
     * @return getSubscriberId
     */
    public static String getDeviceIMSI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSubscriberId();
    }

    /**
     * 获取手机号
     * add <uses-permission android:name="android.permission.READ_PHONE_STATE" /> in AndroidManifest.xml
     *
     * @param context
     * @return
     */
    public static String getDeviceLine1Number(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1Number();
    }

    /**
     * 获取手机SimSerialNumber号
     * add <uses-permission android:name="android.permission.READ_PHONE_STATE" /> in AndroidManifest.xml
     *
     * @param context
     * @return
     */
    public static String getDeviceSimSerialNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }

    /**
     * 服务商名称：
     * 例如：中国移动、联通
     * SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
     *
     * @param context
     * @return getSimOperatorName
     */
    public static String getDeviceSimOperatorName(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimOperatorName();
    }

    /*
     * 手机类型：
     * 例如： PHONE_TYPE_NONE  无信号
       PHONE_TYPE_GSM   GSM信号
       PHONE_TYPE_CDMA  CDMA信号
     *
     * @param context
     * @return getPhoneType
     */
    public static int getDevicePhoneType(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getPhoneType();
    }

    /*
     * 设备的软件版本号：
     * 例如：the IMEI/SV(software version) for GSM phones.
     * Return null if the software version is not available.
     */
    public static String getDeviceDeviceSoftwareVersion(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceSoftwareVersion();
    }

    /*
     * 当前使用的网络类型：
     * 例如： NETWORK_TYPE_UNKNOWN  网络类型未知  0
       NETWORK_TYPE_GPRS     GPRS网络  1
       NETWORK_TYPE_EDGE     EDGE网络  2
       NETWORK_TYPE_UMTS     UMTS网络  3
       NETWORK_TYPE_HSDPA    HSDPA网络  8
       NETWORK_TYPE_HSUPA    HSUPA网络  9
       NETWORK_TYPE_HSPA     HSPA网络  10
       NETWORK_TYPE_CDMA     CDMA网络,IS95A 或 IS95B.  4
       NETWORK_TYPE_EVDO_0   EVDO网络, revision 0.  5
       NETWORK_TYPE_EVDO_A   EVDO网络, revision A.  6
       NETWORK_TYPE_1xRTT    1xRTT网络  7
     */
    public static int getDeviceNetworkType(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkType();
    }

    /*
     * Returns the ISO country code equivalent for the SIM provider's country code.
     * 获取ISO国家码，相当于提供SIM卡的国家码。
     *
     *
     * @param context
     * @return getSimCountryIso
     */
    public static String getDeviceSimCountryIso(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimCountryIso();
    }

    /*
       * SIM的状态信息：
       * SIM_STATE_UNKNOWN          未知状态 0
       * SIM_STATE_ABSENT           没插卡 1
       * SIM_STATE_PIN_REQUIRED     锁定状态，需要用户的PIN码解锁 2
       * SIM_STATE_PUK_REQUIRED     锁定状态，需要用户的PUK码解锁 3
       * SIM_STATE_NETWORK_LOCKED   锁定状态，需要网络的PIN码解锁 4
       * SIM_STATE_READY            就绪状态 5
       *
       * @param context
       * @return getSimState
       */
    public static int getDeviceSimState(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimState();
    }

    /**
     * 电话状态：
     * 1.tm.CALL_STATE_IDLE=0     无活动
     * 2.tm.CALL_STATE_RINGING=1  响铃
     * 3.tm.CALL_STATE_OFFHOOK=2  摘机
     *
     * @param context
     * @return getCallState
     */
    public static int getDeviceCallState(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getCallState();
    }

    /*
    * 是否漫游:
    * (在GSM用途下)
    */
    public static boolean getDeviceIsNetworkRoaming(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.isNetworkRoaming();
    }

    /*
      * ICC卡是否存在
      */
    public static boolean getDeviceHasIccCard(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.hasIccCard();
    }

    /**
     * 获取手机厂商
     *
     * @return
     */
    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机ROM
     *
     * @return
     */
    public static String getDeviceRom() {
        return Build.DISPLAY;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机系统版本号
     *
     * @return
     */
    public static String getDeviceSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /*--------------------------------------------单位转换---------------------------------------------*/

    /**
     * 将px值转变成dip
     *
     * @param context
     * @param px
     * @return
     */
    public static float pxToDip(Context context, float px) {
        return px / getDeviceDensity(context) + 0.5f;
    }

    /**
     * 将dip值转成px
     *
     * @param context
     * @param dip
     * @return
     */
    public static float dipToPx(Context context, float dip) {
        return dip * getDeviceDensity(context) + 0.5f;
    }

    /**
     * 将px值转成sp值
     *
     * @param context
     * @param px
     * @return
     */
    public static float pxToSp(Context context, float px) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return px / fontScale + 0.5f;
    }

    /**
     * 将sp值转成px值
     *
     * @param context
     * @param sp
     * @return
     */
    public static float spTpPx(Context context, float sp) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * fontScale + 0.5f;
    }

    /*--------------------------------------------APP版本相关---------------------------------------------*/

    /**
     * 获取软件versionName
     *
     * @param context
     * @return
     */
    public static String getSoftVersionName(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1.0";
    }

    /**
     * 获取软件VersionCode
     *
     * @param context
     * @return
     */
    public static int getSoftVersionCode(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }
    /*----------------------------------------------------------------------------------------*/

}