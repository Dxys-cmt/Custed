package cn.edu.cust.m.custed.utils;

import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.cust.m.custed.WebActivity;

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

    public boolean isPermissionGranted(String permissionName, int questCo){
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
