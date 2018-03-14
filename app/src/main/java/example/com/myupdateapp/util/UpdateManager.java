package example.com.myupdateapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import net.lemonsoft.lemonhello.LemonHello;
import net.lemonsoft.lemonhello.LemonHelloAction;
import net.lemonsoft.lemonhello.LemonHelloInfo;
import net.lemonsoft.lemonhello.LemonHelloView;
import net.lemonsoft.lemonhello.interfaces.LemonHelloActionDelegate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import example.com.myupdateapp.util.DeviceUtil;
import example.com.myupdateapp.util.NetUtil;
import example.com.myupdateapp.util.VersionUtil;

/**
 * 对话框GitHub地址：
 * https://github.com/1em0nsOft/LemonHello4Android
 */
public class UpdateManager {

    private static final String TAG = "UpdateManager";

    private Context mContext;

    private static final int DOWN_START = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;

    private ProgressDialog downloadDialog;
    private int progress = 0;
    private int maxProgress = 0;

    private String saveFileName = "";//下载的apk位置（包含名字）
    private String updateVersion = "1.0";//下载的apk版本

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_START:
                    downloadDialog.setMax(maxProgress);
                    break;

                case DOWN_UPDATE:
                    downloadDialog.setProgress(progress);
                    break;

                case DOWN_OVER:
                    downloadDialog.cancel();
                    installApk();
                    break;
            }
        }
    };
    private String localVersionName;

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    public void checkUpdateInfo() {
        if (!NetUtil.isNetworkAvailable(mContext)) {
            ToastUtil.showToast("没有网络，请先连接网络后重试");
            return;
        }

        localVersionName = DeviceUtil.getSoftVersionName(mContext);
        updateVersion = "2.3.3";
        int compareVersion = VersionUtil.compareVersion(updateVersion, localVersionName);
        if (compareVersion > 0) {
            showNoticeDialog();
        }
    }

    private void showNoticeDialog() {
        LemonHello.getInformationHello("请升级APP至版本 " + updateVersion, null)
                .addAction(new LemonHelloAction("确定升级", Color.RED, new LemonHelloActionDelegate() {
                    @Override
                    public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                        //这里判断内存卡如果没有就不下载apk
                        File file = Environment.getExternalStorageDirectory();
                        if (file != null) {
                            if (file.canWrite()) {

                                showDownloadDialog();
                            } else {
                                Toast.makeText(mContext, "请在设置-应用权限管理中打开贷财行的读写手机权限", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
                        }
                        helloView.hide();
                    }

                })).show(mContext);
    }

    private void showDownloadDialog() {
        downloadDialog = new ProgressDialog(mContext);
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadDialog.setMessage("正在更新中...");
        downloadDialog.setProgress(0);
        downloadDialog.show();
        downloadDialog.setCanceledOnTouchOutside(false);

        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
//                String apkUrl = "http://wap.apk.anzhi.com/data3/apk/201711/15/896b4e4c271d5e40fefc85855af2052e_85982100.apk";
                String apkUrl = "http://shouji.360tpcdn.com/151111/cb562c7ff14adea9db96c515d5d7a10d/com.beyou.activity_17.apk";

                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                maxProgress = conn.getContentLength();
                Log.e(TAG, "==apk大小== " + (float) maxProgress / 1024 / 1024 + "MB============");
                mHandler.sendEmptyMessage(DOWN_START);

                InputStream is = conn.getInputStream();

                String savePath;
                if (existSDCard()) {
                    savePath = Environment.getExternalStorageDirectory().getPath();
                    Log.e(TAG, "有内存卡==savePath============" + savePath);// /storage/emulated/0
                } else {
                    savePath = "/sdcard/update";
                    Log.e(TAG, "无内存卡==savePath============" + savePath);
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                }
                saveFileName = savePath + "/UpdateRelease.apk";
                Log.e(TAG, "==saveFileName============" + saveFileName);// /storage/emulated/0/UpdateRelease.apk
                File ApkFile = new File(saveFileName);//注意这里的文件/storage/emulated/0/UpdateRelease.apk在最外的内部存储

                FileOutputStream fos = new FileOutputStream(ApkFile);

                byte[] buf = new byte[1024];
                int ch = -1;
                while ((ch = is.read(buf)) != -1) {
                    fos.write(buf, 0, ch);
                    progress += ch;

                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (maxProgress <= progress) {
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                }

                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * 下载apk
     */

    private void downloadApk() {
        Thread downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    /**
     * 是否有内存卡
     */
    private boolean existSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}