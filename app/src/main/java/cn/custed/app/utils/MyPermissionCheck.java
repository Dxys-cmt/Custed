package cn.custed.app.utils;

import android.content.pm.PackageManager;
import android.os.Build;

import cn.custed.app.WebActivity;

/**
 * Created by dxys on 17/3/31.
 * 权限检查
 */

public class MyPermissionCheck {


    private WebActivity webActivity;

    public MyPermissionCheck(WebActivity webActivity)
    {
        this.webActivity = webActivity;
    }

    public boolean isPermissionGranted(String permissionName){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        int hasPermision = webActivity.check_Self_Permission(permissionName);
        if (hasPermision != PackageManager.PERMISSION_GRANTED) {

            return false;
        }
        return true;
    }

}
