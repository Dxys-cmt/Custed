package cn.custed.app.webView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import cn.custed.app.WebActivity;
import cn.custed.app.utils.ImageEditUtils;
import cn.custed.app.utils.MyPermissionCheck;

import static cn.custed.app.MyConstant.FILECHOOSER_RESULTCODE;
import static cn.custed.app.MyConstant.QUEST_CODE_READ_STORAGE;

/**
 * Created by dxys on 17/3/30.
 */

public class MyWebChromeClient extends WebChromeClient {


    private Intent i;
    private WebActivity webActivity;
    private ValueCallback my_upload_message;
    private ValueCallback<Uri[]> my_upload_message_aboveL;
    private int request_code;

    /**
     * 类重构
     *
     * @param webActivity
     */
    public MyWebChromeClient(WebActivity webActivity) {
        this.webActivity = webActivity;
        this.request_code = FILECHOOSER_RESULTCODE;
    }

    /**
     * 处理文件上传
     *
     * @param uploadMsg
     * @param acceptType
     */
    //4.0+
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
        my_upload_message = uploadMsg;
        new ImageEditUtils(webActivity).intent_MediaStore(FILECHOOSER_RESULTCODE);
    }

    //4.1+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        my_upload_message = uploadMsg;
        new ImageEditUtils(webActivity).intent_MediaStore(FILECHOOSER_RESULTCODE);
    }

    //Android 5.0+
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        my_upload_message_aboveL = filePathCallback;
        if (webActivity.getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            new ImageEditUtils(webActivity).intent_MediaStore(FILECHOOSER_RESULTCODE);
        }
       else
        {
            value_callback_aboveL(null);
            set_callback_value_aboveL(null);
        }
        return true;
    }

    public void value_callback(Uri result) {
        my_upload_message.onReceiveValue(result);
    }


    public void set_callback_value(ValueCallback<Uri> valueCallback) {
        this.my_upload_message = valueCallback;
    }

    public void value_callback_aboveL(Uri[] resule) {
        my_upload_message_aboveL.onReceiveValue(resule);
    }

    public void set_callback_value_aboveL(ValueCallback<Uri[]> valueCallback) {
        this.my_upload_message_aboveL = valueCallback;
    }


    /**
     * 重写AlertDialog
     *
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
