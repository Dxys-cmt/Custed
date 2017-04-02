package cn.edu.cust.m.custed;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import cn.edu.cust.m.custed.database.DataBaseHelper;
import cn.edu.cust.m.custed.database.UsrIfoDatebase;
import cn.edu.cust.m.custed.utils.MyPermissionCheck;
import cn.edu.cust.m.custed.webView.MyWebChromeClient;
import cn.edu.cust.m.custed.webView.WebMain;

/**
 * Created by dxys on 17/3/29.
 * app主activity，
 */

public class WebActivity extends AppCompatActivity {

    private String main_url = "http://m.cust.edu.cn";
    private WebView main_webview;
    private boolean finish_activity = false;
    private MyWebChromeClient myWebChromeClient;
    private WebMain webMain;
    public final static int FILECHOOSER_RESULTCODE = 10000;
    private UsrIfoDatebase usrIfoDatebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        /**
         * 加载webview
         */
        main_webview = (WebView) findViewById(R.id.main_webView);
        myWebChromeClient = new MyWebChromeClient(this);
        webMain = new WebMain(main_webview, myWebChromeClient, main_url, this);
        webMain.initWebView();
        webMain.onLoad();
        usrIfoDatebase = new UsrIfoDatebase(WebActivity.this,"usr");

    }

    /**
     * 文件上传intent启动回调方法
     *
     * @param intent
     * @param request_code
     */
    public void choose_file_intent(Intent intent, int request_code) {
        startActivityForResult(Intent.createChooser(intent, "File Chooser"), request_code);
    }


    /**
     * dialog启动回调方法
     *
     * @param title
     * @param message
     * @param s_button1
     */
    public void show_dialog(String title, String message, String s_button1, final int action_check) {

        AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(s_button1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog_action(dialog, action_check);
            }
        });
        builder.create().show();
    }


    public void show_dialog_2button(String title, String message, String s_button1, String s_button2, final int action_check) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(s_button1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog_action(dialog, action_check);
            }
        });
        builder.setNegativeButton(s_button2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    public void dialog_action(DialogInterface dialog, int action_check) {
        switch (action_check) {
            case 0: {
                dialog.dismiss();
                break;
            }
            case 1: {
                main_webview.reload();
                break;
            }
            case 2: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    request_Permissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, action_check);
                break;
            }
            case 3: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    request_Permissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, action_check);
                break;
            }
            case 4: {
                try {
                    Uri packUri = Uri.parse("package:"+getPackageName());
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packUri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "跳转失败！", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                break;
            }
            default: {
                dialog.dismiss();
                break;
            }
        }

    }

    /**
     * intent 返回结果处理
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                onActivityResultAboveL(requestCode, resultCode, intent);
            } else {

                Uri result = null;
                if (intent != null && intent.getData() != null) {
                    result = intent.getData();
                }
                myWebChromeClient.value_callback(result);
                myWebChromeClient.set_callback_value(null);
                webMain.clearWebViewCache();
            }

        }

    }

    /**
     * intent返回结果处理for安卓L+
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {


        if (requestCode == FILECHOOSER_RESULTCODE) {
            Uri[] results = null;
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();

                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
            myWebChromeClient.value_callback_aboveL(results);
            myWebChromeClient.set_callback_value_aboveL(null);
            webMain.clearWebViewCache();
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public int check_Self_Permission(String permissionName) {
        return checkSelfPermission(permissionName);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void request_Permissions(String[] permissionName, int questCode) {

        requestPermissions(permissionName, questCode);

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case 2: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "已经授予权限", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 3: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    show_dialog_2button("提示", "授权被拒绝，是否跳转到 应用信息—权限管理 打开权限", "是", "否", 4);
                } else {
                    Toast.makeText(this, "已经授予权限,请重新点击上传！", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
                break;
        }
    }


    /**
     * 返回键截获处理
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && main_webview.canGoBack() && main_webview.getUrl().indexOf("index.cc") == -1) {
            main_webview.goBack();
            return true;

        } else {
            if (finish_activity) {
                finish_activity = false;
                finish();
            } else {
                Toast.makeText(WebActivity.this, "再按一次返回键软件将退出！", Toast.LENGTH_SHORT).show();
                finish_activity = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish_activity = false;
                    }
                }, 1500);
            }
            return false;
        }
    }


    /**
     * 防止缓存溢出
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        main_webview.removeAllViews();
        main_webview.destroy();
    }


}



