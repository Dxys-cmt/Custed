package cn.custed.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringDef;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.custed.app.ViewInit.NavBarListener;
import cn.custed.app.utils.NetUtils;
import cn.edu.cust.m.custed.R;
import cn.custed.app.ViewInit.MyWebActivity;
import cn.custed.app.database.UsrIfoDatebase;
import cn.custed.app.utils.FileUtils;
import cn.custed.app.utils.ImageEditUtils;
import cn.custed.app.utils.MyCircleImageView;
import cn.custed.app.utils.MyPermissionCheck;
import cn.custed.app.webView.MyWebChromeClient;
import cn.custed.app.webView.WebMain;

import static cn.custed.app.MyConstant.APP_VISIONCODE;
import static cn.custed.app.MyConstant.FILECHOOSER_RESULTCODE;
import static cn.custed.app.MyConstant.FILECHOOSER_RESULTCODE_2;
import static cn.custed.app.MyConstant.FIRST_START_PAGE_NAME;
import static cn.custed.app.MyConstant.NAV_HEADER_BACKGROUND;
import static cn.custed.app.MyConstant.NAV_HEADER_BACKGROUND_VALUE;
import static cn.custed.app.MyConstant.NAV_USR_IFO;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE_ID;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE_VALUE;
import static cn.custed.app.MyConstant.NAV_USR_NAME;
import static cn.custed.app.MyConstant.PHOTO_RESOULT;
import static cn.custed.app.MyConstant.PHOTO_ZOOM;
import static cn.custed.app.MyConstant.RESET_NAV_IFO_KEY;
import static cn.custed.app.MyConstant.RESET_NAV_IFO_ON;
import static cn.custed.app.MyConstant.URL_GRADE;
import static cn.custed.app.MyConstant.URL_INDEX;
import static cn.custed.app.MyConstant.URL_SCHEDULE;
import static cn.custed.app.MyConstant.URL_UPDATE_API;
import static cn.custed.app.MyConstant.URL_UPDATE_APK;
import static cn.custed.app.MyConstant.URL_USER;

/**
 * Created by dxys on 17/3/29.
 * app主activity，
 */

public class WebActivity extends MyWebActivity {

    public String main_url;
    public static String url;
    public static WebView main_webview;
    private boolean finish_activity = false;
    private MyWebChromeClient myWebChromeClient;
    private UsrIfoDatebase usrIfoDatebase;
    private Uri imageUri;
    private LinearLayout nav_header;
    private DrawerLayout drawerLayout;
    private MyCircleImageView icon_image;
    private TextView usr;
    private TextView sign;
    public Handler handler;
    public ImageView error_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.e("-------","start");

        usrIfoDatebase = super.getUsrIfoDatebase();
        setContentView(R.layout.activity_web);

        /**
         * 加载webview
         */
        if (getIntent().getAction().equals("android.intent.action.MYACTIVITY"))
            main_url = URL_SCHEDULE;
        else
            main_url = "http://m.cust.edu.cn/" + usrIfoDatebase.get_query_ifovalue(FIRST_START_PAGE_NAME);
        main_webview = (WebView) findViewById(R.id.main_webView);
        myWebChromeClient = new MyWebChromeClient(this);
        WebMain webMain = new WebMain(main_webview, myWebChromeClient, main_url, this);
        webMain.initWebView();
        webMain.onLoad();

        Looper looper = Looper.getMainLooper();
        handler = new MyHandler(looper);


        NavBarListener navBarListener = new NavBarListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_web);
        SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.nav_switch);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View head_view = navigationView.getHeaderView(0);
        nav_header = (LinearLayout) head_view.findViewById(R.id.nav_header_main);
        icon_image = (MyCircleImageView) head_view.findViewById(R.id.nav_usr_image);
        usr = (TextView) head_view.findViewById(R.id.nav_usr_name);
        sign = (TextView) head_view.findViewById(R.id.nav_usr_sign);
        error_image = (ImageView)findViewById(R.id.error_page);
        navBarListener.init_all_view(drawerLayout, navigationView, nav_header, switchCompat, icon_image, usr, sign ,error_image);


        if (NetUtils.get_content_type(this) == NetUtils.WIFI)
        new FileUtils().load_from_url(this,URL_UPDATE_API,null,13);


    }


    public boolean getPermission(String permissionName)
    {
        boolean result = false;
        if (permissionName.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            if (!new MyPermissionCheck(this).isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                show_dialog("提示：", "软件需要读取储存的权限，请授权。", "确定", 3);
                result = false;
            }

            else result =true;
        }
        return result;
    }


    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NAV_USR_IMAGE_ID: {
                    set_usr_image();
                    break;
                }
                case 11: {

                    if(msg.obj != null)
                    {
                        String[] ifo = (String[]) msg.obj;
                        usr.setText(ifo[1]);
                        sign.setText(ifo[0]);
                        usrIfoDatebase.insert_data(NAV_USR_NAME,ifo[1]);
                        usrIfoDatebase.insert_data(NAV_USR_IFO,ifo[0]);
                    }

                    break;
                }
                case 13:
                {
                    if (msg.obj != null)
                    {
                        String html = msg.obj.toString();
                        Log.e("WebActivity","update_api is:"+html);
                        int check_vison = Integer.parseInt(html);
                        if (check_vison > APP_VISIONCODE)
                        {
                            show_dialog_2button("提示","检测到有新的版本是否更新？","好的","暂时不要",13);
                        }
                    }
                }
            }

        }
    }



    public void start_intent(Intent intent, int request_code) {
        startActivityForResult(Intent.createChooser(intent, "File Chooser"), request_code);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public int check_Self_Permission(String permissionName) {
        return checkSelfPermission(permissionName);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void request_Permissions(String[] permissionName, int questCode) {

        requestPermissions(permissionName, questCode);

    }


    public void set_usr_image() {

        if (new File(FileUtils.get_my_imagedir_path(this) + NAV_USR_IMAGE_VALUE).exists()) {
            usrIfoDatebase.insert_data(NAV_USR_IMAGE, NAV_USR_IMAGE_VALUE);
            icon_image.setImageDrawable(Drawable.createFromPath(FileUtils.get_my_imagedir_path(this) + NAV_USR_IMAGE_VALUE));
        }
    }


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
                if (getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                new ImageEditUtils(this).intent_MediaStore(PHOTO_ZOOM);
                break;
            }
            case 13 :
            {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(URL_UPDATE_APK));
                startActivity(intent);

            }
            default: {
                dialog.dismiss();
                break;
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (!new File(FileUtils.get_my_imagedir_path(this)).exists())
            new File(FileUtils.get_my_imagedir_path(this)).mkdirs();

        if (!new File(FileUtils.get_my_files_path(this)).exists())
            new File(FileUtils.get_my_files_path(this)).mkdirs();


        switch (requestCode) {
            case FILECHOOSER_RESULTCODE: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    onActivityResultAboveL(requestCode, resultCode, intent);
                } else {


                    Uri result = null;
                    if (intent != null && intent.getData() != null) {
                        imageUri = intent.getData();
                        FileUtils.copyFile(FileUtils.getRealOPathForUri(imageUri,this),FileUtils.get_my_sd_files_path(this)+"temp.png");
                        imageUri = FileUtils.getUriForPath(FileUtils.get_my_sd_files_path(this)+"temp.png",this);
                        new ImageEditUtils(this).startPhotoZoom(imageUri, 200, 200, FILECHOOSER_RESULTCODE_2);
                    }
                    myWebChromeClient.value_callback(result);
                    myWebChromeClient.set_callback_value(null);
                }
                break;
            }
            case FILECHOOSER_RESULTCODE_2: {
                usrIfoDatebase.update_data(RESET_NAV_IFO_KEY,RESET_NAV_IFO_ON);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    onActivityResultAboveL(requestCode, resultCode, intent);
                } else {

                    Uri result = null;
                    if (intent != null && intent.getData() != null) {
                        result = imageUri;
                        usrIfoDatebase.update_data(RESET_NAV_IFO_KEY,RESET_NAV_IFO_ON);
                    }
                    myWebChromeClient.value_callback(result);
                    myWebChromeClient.set_callback_value(null);
                }
                break;
            }
            case PHOTO_ZOOM: {
                if (intent != null && intent.getData() != null) {
                    imageUri = intent.getData();
                    FileUtils.copyFile(FileUtils.getRealOPathForUri(imageUri,this), FileUtils.get_my_sd_files_path(this) + NAV_HEADER_BACKGROUND_VALUE);
                    imageUri = FileUtils.getUriForPath(FileUtils.get_my_sd_files_path(this) + NAV_HEADER_BACKGROUND_VALUE,this);
                    new ImageEditUtils(this).startPhotoZoom(imageUri, 840, 600, PHOTO_RESOULT);
                }
                break;
            }
            case PHOTO_RESOULT: {
                if (intent != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    FileUtils.copyFile(FileUtils.getRealOPathForUri(imageUri,this),FileUtils.get_my_imagedir_path(this)+NAV_HEADER_BACKGROUND_VALUE);
                    usrIfoDatebase.insert_data(NAV_HEADER_BACKGROUND, NAV_HEADER_BACKGROUND_VALUE);
                    nav_header.setBackground(ImageEditUtils.get_my_dir_image(NAV_HEADER_BACKGROUND_VALUE,this));
                }
                break;
            }

        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {


        switch (requestCode) {
            case FILECHOOSER_RESULTCODE: {
                imageUri = null;
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
                        Log.e("-----", "AAAAAAA");
                    }
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};

                    }
                    if (results != null) {
                        imageUri = results[0];
                        FileUtils.copyFile(FileUtils.getRealOPathForUri(imageUri,this),FileUtils.get_my_sd_files_path(this)+"temp.png");
                        imageUri = FileUtils.getUriForPath(FileUtils.get_my_sd_files_path(this)+"temp.png",this);
                        new ImageEditUtils(this).startPhotoZoom(imageUri, 200, 200, FILECHOOSER_RESULTCODE_2);
                    } else {
                        myWebChromeClient.value_callback_aboveL(null);
                        myWebChromeClient.set_callback_value_aboveL(null);
                    }

                } else {
                    myWebChromeClient.value_callback_aboveL(null);
                    myWebChromeClient.set_callback_value_aboveL(null);
                }


                break;
            }
            case FILECHOOSER_RESULTCODE_2: {
                Uri[] results = null;
                if (intent != null && imageUri != null) {
                    results = new Uri[]{imageUri};
                    usrIfoDatebase.update_data(RESET_NAV_IFO_KEY,RESET_NAV_IFO_ON);
                    Log.e("hase","234567");
                }

                myWebChromeClient.value_callback_aboveL(results);
                myWebChromeClient.set_callback_value_aboveL(null);
                break;
            }
        }

        if (requestCode == FILECHOOSER_RESULTCODE) {

        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!new File(FileUtils.get_my_imagedir_path(this)).exists())
            new File(FileUtils.get_my_imagedir_path(this)).mkdirs();

        if (!new File(FileUtils.get_my_files_path(this)).exists())
            new File(FileUtils.get_my_files_path(this)).mkdirs();


        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case 2: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "已经授予权限！", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 3: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    show_dialog_2button("提示", "授权被拒绝，是否跳转到 应用信息—权限管理 打开权限", "是", "否", 4);
                } else {
                    Toast.makeText(this, "已经授予权限!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
                break;
        }
    }


    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else {
            if (main_webview.canGoBack() && !(main_webview.getUrl().equals(URL_GRADE) || main_webview.getUrl().equals(URL_INDEX) || main_webview.getUrl().equals(URL_SCHEDULE) || main_webview.getUrl().equals(URL_USER))) {
                main_webview.goBack();
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
            }
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(url != null)
        {
            main_webview.loadUrl(url);
            url = null;
        }

        Log.e("onRestart","action is: "+getIntent().getAction());
        if (getUsrIfoDatebase().get_query_ifovalue(RESET_NAV_IFO_KEY).equals(RESET_NAV_IFO_ON) && CookieManager.getInstance().getCookie(URL_USER) != null && CookieManager.getInstance().getCookie(URL_USER).contains("custedcid"))
        {
            new FileUtils().load_from_url(this,"http://m.cust.edu.cn/pic_uid_avatar.jpg",FileUtils.get_my_imagedir_path(this)+NAV_USR_IMAGE_VALUE,NAV_USR_IMAGE_ID);
        }

    }



    public void set_reload(String url) {
        main_webview.loadUrl(url);
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



