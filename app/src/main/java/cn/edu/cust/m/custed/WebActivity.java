package cn.edu.cust.m.custed;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.edu.cust.m.custed.database.DataBaseHelper;
import cn.edu.cust.m.custed.database.UsrIfoDatebase;
import cn.edu.cust.m.custed.utils.FileUtils;
import cn.edu.cust.m.custed.utils.ImageEditUtils;
import cn.edu.cust.m.custed.utils.MyCircleImageView;
import cn.edu.cust.m.custed.utils.MyPermissionCheck;
import cn.edu.cust.m.custed.webView.MyWebChromeClient;
import cn.edu.cust.m.custed.webView.WebMain;

import static android.R.attr.data;
import static android.R.attr.inflatedId;
import static cn.edu.cust.m.custed.MyConstant.DATABASE_NAME;
import static cn.edu.cust.m.custed.MyConstant.FILECHOOSER_RESULTCODE;
import static cn.edu.cust.m.custed.MyConstant.MY_DATA_FILE_DIR;
import static cn.edu.cust.m.custed.MyConstant.PHOTO_RESOULT;
import static cn.edu.cust.m.custed.MyConstant.PHOTO_ZOOM;
import static cn.edu.cust.m.custed.MyConstant.QUEST_CODE_READ_STORAGE;

/**
 * Created by dxys on 17/3/29.
 * app主activity，
 */

public class WebActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String main_url = "http://m.cust.edu.cn";
    private WebView main_webview;
    private boolean finish_activity = false;
    private MyWebChromeClient myWebChromeClient;
    private WebMain webMain;
    private UsrIfoDatebase usrIfoDatebase;
    private Uri imageUri;
    private LinearLayout nav_header;
    private MyCircleImageView icon_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_web);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_web);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        usrIfoDatebase = new UsrIfoDatebase(this, DATABASE_NAME);
        if (!usrIfoDatebase.is_datebase_tab_exist()) {
            Toast.makeText(this, "noTab", Toast.LENGTH_SHORT).show();
            usrIfoDatebase.creat_tab();
        }
        View head_view = navigationView.getHeaderView(0);

        nav_header = (LinearLayout) head_view.findViewById(R.id.nav_header_main);
        icon_image = (MyCircleImageView) head_view.findViewById(R.id.head_image_View);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (new File(Environment.getExternalStorageDirectory()+MY_DATA_FILE_DIR+"image/"+usrIfoDatebase.query_data("navBg").getAsString("ifovalue")).exists())
                head_view.setBackground(Drawable.createFromPath(Environment.getExternalStorageDirectory()+MY_DATA_FILE_DIR+"image/"+usrIfoDatebase.query_data("navBg").getAsString("ifovalue")));
            else
                Log.e("-----","M:"+Environment.getExternalStorageDirectory()+MY_DATA_FILE_DIR+"image/"+usrIfoDatebase.query_data("navBg").getAsString("ifovalue"));
        }
        nav_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_dialog_2button("提示", "是否更换背景图片？", "是的", "不用了", 5);
            }
        });
        /**
         * 加载webview
         */
        main_webview = (WebView) findViewById(R.id.main_webView);
        myWebChromeClient = new MyWebChromeClient(this);
        webMain = new WebMain(main_webview, myWebChromeClient, main_url, this);
        webMain.initWebView();
        webMain.onLoad();
        usrIfoDatebase = new UsrIfoDatebase(WebActivity.this, "usr");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int item_id = item.getItemId();
        switch (item_id) {
            case R.id.nav_home: {
                main_webview.loadUrl("http://m.cust.edu.cn/index.cc");
                break;
            }
            case R.id.nav_qc: {

                break;
            }
            case R.id.nav_settings: {
                String test  = CookieManager.getInstance().getCookie("http://m.cust.edu.cn/user.cc");
                Log.e("-----","H:"+test);
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_web);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * 文件上传intent启动回调方法
     *
     * @param intent
     * @param request_code
     */
    public void start_intent(Intent intent, int request_code) {
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
                    Uri packUri = Uri.parse("package:" + getPackageName());
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packUri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "跳转失败！", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                break;
            }
            case 5: {

                if(new MyPermissionCheck(this).isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE, QUEST_CODE_READ_STORAGE))
                new ImageEditUtils(this).intent_MediaStore();
                else
                show_dialog("提示：","该操作需要软件读取储存的权限，请授权。","确定",3);

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

        switch (requestCode) {
            case FILECHOOSER_RESULTCODE: {
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
                break;
            }
            case PHOTO_ZOOM: {
                if (intent != null && intent.getData() != null) {
                    new ImageEditUtils(this).startPhotoZoom(intent.getData(), 840, 600);
                    imageUri = intent.getData();
                }
                break;
            }
            case PHOTO_RESOULT: {
               if(intent != null) {
                   Bitmap bitmap;
                   try {
                       bitmap = BitmapFactory.decodeStream(getContentResolver()
                               .openInputStream(imageUri));
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                           Toast.makeText(this, "there!" + imageUri.getPath(), Toast.LENGTH_SHORT).show();
                           new FileUtils().copyFile(imageUri.getPath(), Environment.getExternalStorageDirectory() + MY_DATA_FILE_DIR + "image/navBG.png");
                           usrIfoDatebase.insert_data("navBg", "navBG.png");
                           nav_header.setBackground(Drawable.createFromPath(Environment.getExternalStorageDirectory() + MY_DATA_FILE_DIR + "image/" + usrIfoDatebase.query_data("navBg").getAsString("ifovalue")));
                       }
                   } catch (FileNotFoundException e) {
                       e.printStackTrace();
                   }
               }else
               {
                   break;
               }

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
                    Toast.makeText(this, "已经授予权限,请重新执行该操作！", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 3: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    show_dialog_2button("提示", "授权被拒绝，是否跳转到 应用信息—权限管理 打开权限", "是", "否", 4);
                } else {
                    Toast.makeText(this, "已经授予权限,请重新执行该操作！", Toast.LENGTH_SHORT).show();
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



