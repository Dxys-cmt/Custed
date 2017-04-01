package cn.edu.cust.m.custed.webView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import cn.edu.cust.m.custed.WebActivity;
import cn.edu.cust.m.custed.utils.MyPermissionCheck;

/**
 * Created by dxys on 17/3/30.
 */

public class MyWebChromeClient extends WebChromeClient {


    private Intent i;
    private WebActivity webActivity;
    private ValueCallback<Uri> my_upload_message;
    private ValueCallback<Uri[]> my_upload_message_aboveL;
    private int request_code;

    /**
     * 类重构
     * @param webActivity
     */
    public MyWebChromeClient(WebActivity webActivity)
    {
        this.webActivity = webActivity;
        this.request_code = webActivity.FILECHOOSER_RESULTCODE;
    }

    /**
     * 处理文件上传
     * @param uploadMsg
     * @param acceptType
     */
    //4.0+
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
//        if (my_upload_message != null)
//        {
//            my_upload_message.onReceiveValue(null);
//        }
        Toast.makeText(webActivity, "this0", Toast.LENGTH_SHORT).show();
        my_upload_message = uploadMsg;
        open_file_choose_intent(acceptType);
    }

    //4.1+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        my_upload_message = uploadMsg;
        Toast.makeText(webActivity, "this3", Toast.LENGTH_SHORT).show();
        open_file_choose_intent(acceptType);
    }

    //Android 5.0+
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

            my_upload_message_aboveL = filePathCallback;
            open_file_choose_intent_aboveL(fileChooserParams.getAcceptTypes());

        return true;
    }

    /**
     * 创建文件选择intent
     * 下一个适配安卓L+
     * @param type
     */
    public void open_file_choose_intent(String type)
    {
        i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType(type);
        webActivity.choose_file_intent(i,request_code);
    }

    public void open_file_choose_intent_aboveL(String[] type)
    {
        if(new MyPermissionCheck(webActivity).isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE, MyPermissionCheck.QUEST_CODE_READ_STORAGE))
        {
            i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType(type[0]);
            webActivity.choose_file_intent(i,request_code);
        }
        else
        {
            webActivity.show_dialog("提示：","软件需要读取储存的权限，请授权。","确定",3);
            my_upload_message_aboveL.onReceiveValue(null);
        }
    }

    /**
     * web返回值回调处理
     * @param result
     */
    public void value_callback(Uri result)
    {
        my_upload_message.onReceiveValue(result);
    }


    /**
     * web返回值设置
     * @param valueCallback
     */
    public void set_callback_value(ValueCallback<Uri> valueCallback)
    {
        this.my_upload_message = valueCallback;
    }

    public void value_callback_aboveL(Uri[] resule)
    {
        my_upload_message_aboveL.onReceiveValue(resule);
    }

    public void set_callback_value_aboveL(ValueCallback<Uri[]> valueCallback)
    {
        this.my_upload_message_aboveL = valueCallback;
    }


    /**
     * 重写AlertDialog
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        AlertDialog.Builder b = new AlertDialog.Builder(view.getContext());
        b.setTitle("Confirm");
        b.setMessage(message);
        b.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        b.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.cancel();
            }
        });
        b.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                result.cancel();
            }
        });
        b.create().show();
        return true;
    }
}
