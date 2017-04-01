package cn.edu.cust.m.custed.webView;

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

import cn.edu.cust.m.custed.R;
import cn.edu.cust.m.custed.WebActivity;
import cn.edu.cust.m.custed.utils.NetUtils;

/**
 * Created by dxys on 17/3/29.
 * webview 配置类
 */

public class WebMain {
    private WebView webView;
    private String url;
    private MyWebChromeClient myWebChromeClient;
    private WebActivity webactivity;


    /**
     * 类重构
     * @param webView
     * @param myWebChromeClient
     * @param url
     * @param webActivity
     */
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
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
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


    /**
     * 清除缓存
     */
    public void clearWebViewCache() {
//        context.deleteDatabase("WebView.db");
        webactivity.deleteDatabase("WebViewCache.db");
//        String cacheDirPath = ().getAbsolutePath()+APP_CACAHE_DIRNAME;

    }


    /**
     * 加载url方法
     */
    public void onLoad() {

        try {
            webView.setWebViewClient(new WebViewClient() {

                /**
                 * 不要跳转到浏览器
                 * @param view
                 * @param request
                 * @return
                 */
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return true;
                }

                /**
                 * url加载时调用
                 * @param view
                 * @param url
                 * @param favicon
                 */
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }


                /**
                 * 加载web资源时调用
                 * @param view
                 * @param url
                 */
                @Override
                public void onLoadResource(WebView view, String url) {
                    synCookies(webactivity.getBaseContext(), url);
                    cache_mode_switch();
                    super.onLoadResource(view, url);
                }

                /**
                 * url加载错误时调用
                 * @param view
                 * @param errorCode
                 * @param description
                 * @param failingUrl
                 */
                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    webactivity.show_dialog(webactivity.getString(R.string.nonet_dialog_title), webactivity.getString(R.string.nonet_dialog_message), webactivity.getString(R.string.nonet_dialog_button1),1);
                }

            });

            webView.setWebChromeClient(myWebChromeClient);

            webView.loadUrl(url);
        } catch (Exception e) {
            Log.e("onLoad", "loaderror!");
            return;
        }
    }

}
