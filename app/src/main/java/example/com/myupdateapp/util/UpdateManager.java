package example.com.myupdateapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import net.lemonsoft.lemonhello.LemonHello;
import net.lemonsoft.lemonhello.LemonHelloAction;
import net.lemonsoft.lemonhello.LemonHelloInfo;
import net.lemonsoft.lemonhello.LemonHelloView;
import net.lemonsoft.lemonhello.interfaces.LemonHelloActionDelegate;

import java.io.File;

import example.com.myupdateapp.constant.Common;
import example.com.myupdateapp.service.UpdateService;

/**
 * 对话框GitHub地址：
 * https://github.com/1em0nsOft/LemonHello4Android
 */
public class UpdateManager {

    private Context mContext;
    private String updateVersion = "1.0";//下载的apk版本

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    /**
     * 判断是否升级
     */
    public void checkUpdateInfo() {
        if (!NetUtil.isNetworkAvailable(mContext)) {
            ToastUtil.showToast("没有网络，请先连接网络后重试");
            return;
        }

        String localVersionName = DeviceUtil.getSoftVersionName(mContext);
        updateVersion = "2.3.3";
        int compareVersion = VersionUtil.compareVersion(updateVersion, localVersionName);
        if (compareVersion > 0) {
            showNoticeDialog();
        }
    }

    /**
     * 升级提示的对话框
     */
    private void showNoticeDialog() {
        LemonHello.getInformationHello("请升级APP至版本 " + updateVersion, null)
                .addAction(new LemonHelloAction("确定升级", Color.RED, new LemonHelloActionDelegate() {
                    @Override
                    public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                        //这里判断内存卡如果没有就不下载apk
                        File file = Environment.getExternalStorageDirectory();
                        if (file != null) {
                            if (file.canWrite()) {
                                // 后台下载：
                                Intent intent = new Intent(mContext, UpdateService.class);
                                mContext.startService(intent);
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

    /**
     * 下载apk的对话框
     */
    public ProgressDialog showDownloadDialog(int maxProgerss) {
        ProgressDialog downloadDialog = new ProgressDialog(mContext);
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadDialog.setMessage("正在更新中...");
        downloadDialog.setProgress(0);
        downloadDialog.setMax(maxProgerss);
        downloadDialog.show();
        downloadDialog.setCanceledOnTouchOutside(false);
        return downloadDialog;
    }

    /**
     * 安装apk
     */
    public void installApk() {
        if (DeviceUtil.existSDCard()) {
            File apkfile = new File(Environment.getExternalStorageDirectory(), Common.APK_NAME);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("UpdateManager", "手机上apk位置===" + apkfile.toString());
            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            mContext.startActivity(intent);
        }
    }
}