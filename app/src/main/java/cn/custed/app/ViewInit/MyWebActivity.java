package cn.custed.app.ViewInit;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.custed.app.MyConstant;
import cn.custed.app.database.UsrIfoDatebase;
import cn.custed.app.widget.ClassWidget;

import static cn.custed.app.MyConstant.CLASS_DATA_ISRESET;
import static cn.custed.app.MyConstant.CLASS_DATA_KEY;
import static cn.custed.app.MyConstant.FIRST_START_PAGE_INDEX;
import static cn.custed.app.MyConstant.FIRST_START_PAGE_NAME;
import static cn.custed.app.MyConstant.RESET_NAV_IFO_KEY;
import static cn.custed.app.MyConstant.RESET_NAV_IFO_ON;

/**
 *
 * Created by dxys on 17/4/4.
 */

public class MyWebActivity extends AppCompatActivity {

    private static UsrIfoDatebase usrIfoDatebase;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("first","start1");

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

        usrIfoDatebase = new UsrIfoDatebase(this, MyConstant.DATABASE_NAME);
        if (!usrIfoDatebase.is_datebase_tab_exist())
        {
            usrIfoDatebase.creat_tab();
            usrIfoDatebase.insert_data(FIRST_START_PAGE_NAME,FIRST_START_PAGE_INDEX);
            usrIfoDatebase.insert_data(RESET_NAV_IFO_KEY,RESET_NAV_IFO_ON);
            usrIfoDatebase.insert_data(CLASS_DATA_KEY,CLASS_DATA_ISRESET);
        }

    }
    public UsrIfoDatebase getUsrIfoDatebase()
    {
        return usrIfoDatebase;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
