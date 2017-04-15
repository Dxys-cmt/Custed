package cn.custed.app.webView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.custed.app.WebActivity;
import cn.custed.app.database.UsrIfoDatebase;
import cn.custed.app.utils.FileUtils;
import cn.custed.app.utils.NetUtils;
import cn.edu.cust.m.custed.R;

import static cn.custed.app.MyConstant.CLASS_DATA_VALUE;
import static cn.custed.app.MyConstant.DATABASE_NAME;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE_ID;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE_VALUE;
import static cn.custed.app.MyConstant.RESET_NAV_IFO_KEY;
import static cn.custed.app.MyConstant.RESET_NAV_IFO_OFF;
import static cn.custed.app.MyConstant.RESET_NAV_IFO_ON;
import static cn.custed.app.MyConstant.URL_INDEX;
import static cn.custed.app.MyConstant.URL_SCHEDULE;
import static cn.custed.app.MyConstant.URL_USER;

/**
 * Created by dxys on 17/3/29.
 * webview 配置类
 */

public class WebMain {
    private WebView webView;
    private String url;
    private MyWebChromeClient myWebChromeClient;
    private WebActivity webactivity;
    private String cache_switch_key = "";
    private String TAG = "WebMain.java";
    private static boolean reset = true;


    public WebMain(WebView webView, MyWebChromeClient myWebChromeClient, String url, WebActivity webActivity) {
        this.webView = webView;
        this.url = url;
        this.myWebChromeClient = myWebChromeClient;
        this.webactivity = webActivity;
    }

    /**
     * webveiew设置方法
     */
    public void initWebView() {
        //接收来自js的信息（备用）

        //        main_webview.addJavascriptInterface(new Object()
//        {
//            public String TAG;
//
//            public void send(String message)
//            {
//                Log.i(TAG,"massage:" + message);
//            }


        // webView.getSettings().setBlockNetworkImage(true); //图片加载模式，待测试

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAppCachePath(webactivity.getCacheDir()+"mwebview_caceh");
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString() + "; CustedAPP");
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }


    /**
     * 缓存模式切换
     */
    public void cache_mode_switch() {

        if (NetUtils.isNetworkAvailable(webactivity.getBaseContext())) {
//            Toast.makeText(context, "net",Toast.LENGTH_SHORT).show();
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
//            Toast.makeText(context, "cache", Toast.LENGTH_SHORT).show();
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }


    /**
     * cookie同步配置
     * @param context
     * @param url
     */
    public static void synCookies(Context context, String url) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie(url);

        if (TextUtils.isEmpty(cookie)) {
        } else {
            cookieManager.setCookie(url, cookie);
        }

    }



    public void onLoad() {

        try {
            webView.setWebViewClient(new WebViewClient() {


                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    Log.e(TAG,"load;"+url);
                    return false;
                }


                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    Log.e(TAG,"PageStart"+webView.getUrl());
                }


                @Override
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                    synCookies(webactivity.getBaseContext(), url);
                    if(!view.getUrl().equals(cache_switch_key))
                    {
                        cache_mode_switch();
                        cache_switch_key = view.getUrl();
                        Log.e(TAG,"setcache:"+webView.getUrl());
                        if(NetUtils.isNetworkAvailable(webactivity))
                        {
                            if (reset && webactivity.getUsrIfoDatebase().get_query_ifovalue(RESET_NAV_IFO_KEY).equals(RESET_NAV_IFO_ON) && CookieManager.getInstance().getCookie(URL_USER) != null && CookieManager.getInstance().getCookie(URL_USER).contains("custedcid"))
                            {
                                reset = false;
                                new FileUtils().load_from_url(webactivity,"http://m.cust.edu.cn/schedule.html",FileUtils.get_my_files_path(webactivity)+CLASS_DATA_VALUE,12);
                                new FileUtils().load_from_url(webactivity,"http://m.cust.edu.cn/pic_uid_avatar.jpg",FileUtils.get_my_imagedir_path(webactivity)+NAV_USR_IMAGE_VALUE,NAV_USR_IMAGE_ID);
                                new FileUtils().load_from_url(webactivity,URL_USER,null,11);
                                webactivity.getUsrIfoDatebase().update_data(RESET_NAV_IFO_KEY,RESET_NAV_IFO_OFF);
                            }
                            if(webView.getUrl().equals("http://m.cust.edu.cn/login.html"))
                            {
                                new UsrIfoDatebase(webactivity,DATABASE_NAME).update_data(RESET_NAV_IFO_KEY,RESET_NAV_IFO_ON);
                                reset = true;
                            }
                        }
                    }

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }



                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    webactivity.error_image.setVisibility(View.VISIBLE);
                }

            });

            webView.setWebChromeClient(myWebChromeClient);

            webView.loadUrl(url);
        } catch (Exception e) {
            Log.e("onLoad", "loaderror!");
        }
    }

}
