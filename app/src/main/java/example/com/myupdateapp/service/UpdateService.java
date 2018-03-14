package example.com.myupdateapp.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import example.com.myupdateapp.constant.Common;
import example.com.myupdateapp.util.DeviceUtil;


public class UpdateService extends IntentService {

    /*** 检测升级 获取升级文件大小成功*/
    public static final int DOWN_START = 1;
    /*** 获取升级文件下载进度 */
    public static final int DOWN_PROGRESS = 2;

    private LocalBroadcastManager mLocalBroadcastManager;

    private int mProgress;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            sendUpdateStatus(DOWN_PROGRESS, mProgress);
            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    public UpdateService(String name) {
        super(name);
    }

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        downloadAPK();
    }

    private void downloadAPK() {

        String url = "http://shouji.360tpcdn.com/151111/cb562c7ff14adea9db96c515d5d7a10d/com.beyou.activity_17.apk";
//		String url="http://shouji.360tpcdn.com/170815/3d0520d698950157a7071f9a409c8455/com.dch.dai_14.apk";
        Log.e("111", "apk地址------==" + url);

        if (TextUtils.isEmpty(url)) {
            return;
        }

        HttpURLConnection connection = null;
        try {

            URL requestUrl = new URL(url);

            if (requestUrl.getProtocol().toLowerCase().equals("https")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) requestUrl.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                connection = https;
            } else {
                connection = (HttpURLConnection) requestUrl.openConnection();
            }
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setDoInput(true);
            connection.connect();

            if (connection.getResponseCode() == 200) {
                // 文件总大小
                //方式一：Accept-Length
                String b = connection.getHeaderField("Accept-Length");
                int c = connection.getHeaderFieldInt("Accept-Length", 0);
                //方式二：Content-Length
                int maxProgress = connection.getContentLength();
                System.out.println("maxProgress====" + maxProgress / 1024.00 / 1024.00);
                System.out.println("b====" + b);
                System.out.println("c====" + c / 1024.00 / 1024.00);

                sendUpdateStatus(DOWN_START, maxProgress);

                InputStream is = connection.getInputStream();
                FileOutputStream fileOutputStream = null;
                if (is != null) {

                    File downloadFile = DeviceUtil.getDownloadFile();

                    fileOutputStream = new FileOutputStream(downloadFile);
                    // 这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                    // 看不出progressbar的效果。
                    byte[] buf = new byte[1024];
                    int ch = -1;
                    mProgress = 0;

                    mHandler.postDelayed(mRunnable, 1000);

                    while ((ch = is.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, ch);
                        mProgress += ch;
                    }

                    sendUpdateStatus(DOWN_PROGRESS, mProgress);
                    mHandler.removeCallbacks(mRunnable);

                }
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                if (is != null) {
                    is.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    private void sendUpdateStatus(int type, int progress) {
        Intent intent = new Intent(Common.DOWNLOAD_APK_ACTION);
        intent.putExtra("type", type);
        intent.putExtra("progress", progress);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @SuppressLint("BadHostnameVerifier")
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @SuppressLint("TrustAllX509TrustManager")
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @SuppressLint("TrustAllX509TrustManager")
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
