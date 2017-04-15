package cn.custed.app.ViewInit;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.custed.app.MipcaActivityCapture;
import cn.custed.app.SettingsActivity;
import cn.custed.app.WebActivity;
import cn.custed.app.database.UsrIfoDatebase;
import cn.custed.app.utils.FileUtils;
import cn.custed.app.utils.ImageEditUtils;
import cn.custed.app.utils.NetUtils;
import cn.edu.cust.m.custed.R;

import static cn.custed.app.MyConstant.FIRST_START_PAGE_INDEX;
import static cn.custed.app.MyConstant.FIRST_START_PAGE_NAME;
import static cn.custed.app.MyConstant.FIRST_START_PAGE_SCHEDULE;
import static cn.custed.app.MyConstant.NAV_HEADER_BACKGROUND;
import static cn.custed.app.MyConstant.NAV_HEADER_BACKGROUND_VALUE;
import static cn.custed.app.MyConstant.NAV_USR_IFO;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE_ID;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE_VALUE;
import static cn.custed.app.MyConstant.NAV_USR_NAME;
import static cn.custed.app.MyConstant.RESET_NAV_IFO_KEY;
import static cn.custed.app.MyConstant.RESET_NAV_IFO_OFF;
import static cn.custed.app.MyConstant.RESET_NAV_IFO_ON;
import static cn.custed.app.MyConstant.URL_USER;


/**
 * Created by dxys on 17/4/4.
 */

public class NavBarListener implements NavigationView.OnNavigationItemSelectedListener {
    private WebActivity webActivity;
    private DrawerLayout drawerLayout;
    private UsrIfoDatebase usrIfoDatebase;


    public NavBarListener(WebActivity webActivity)
    {
        this.webActivity = webActivity;
    }

    public void init_all_view(final DrawerLayout drawerLayout, NavigationView navigationView, LinearLayout linearLayout, SwitchCompat switchCompat, ImageView imageView, TextView usr, TextView sign, final ImageView error_image)
    {

        this.drawerLayout = drawerLayout;
        usrIfoDatebase = webActivity.getUsrIfoDatebase();

        navigationView.setNavigationItemSelectedListener(this);

        if(usrIfoDatebase.get_query_ifovalue(FIRST_START_PAGE_NAME).equals(FIRST_START_PAGE_INDEX))
            switchCompat.setChecked(false);
        else switchCompat.setChecked(true);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) usrIfoDatebase.update_data(FIRST_START_PAGE_NAME,FIRST_START_PAGE_SCHEDULE);
                else usrIfoDatebase.update_data(FIRST_START_PAGE_NAME,FIRST_START_PAGE_INDEX);
                webActivity.main_url = usrIfoDatebase.get_query_ifovalue(FIRST_START_PAGE_NAME);
            }
        });


        if(usrIfoDatebase.get_query_ifovalue(NAV_HEADER_BACKGROUND) != null && new File(FileUtils.get_my_imagedir_path(webActivity)+NAV_HEADER_BACKGROUND_VALUE).exists())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                linearLayout.setBackground(ImageEditUtils.get_my_dir_image(NAV_HEADER_BACKGROUND_VALUE,webActivity));
            }
        }
        else
        {
            linearLayout.setBackgroundResource(R.drawable.nav_head_background);
        }


        if (usrIfoDatebase.get_query_ifovalue(NAV_USR_IMAGE) != null && new File(FileUtils.get_my_imagedir_path(webActivity)+NAV_USR_IMAGE_VALUE).exists())
        {
            imageView.setImageDrawable(ImageEditUtils.get_my_dir_image(NAV_USR_IMAGE_VALUE,webActivity));
        }

        if (usrIfoDatebase.get_query_ifovalue(NAV_USR_NAME) != null)
        {
            usr.setText(usrIfoDatebase.get_query_ifovalue(NAV_USR_NAME));
        }

        if (usrIfoDatebase.get_query_ifovalue(NAV_USR_IFO) != null)
        {
            sign.setText(usrIfoDatebase.get_query_ifovalue(NAV_USR_IFO));
        }

        error_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webActivity.set_reload(webActivity.main_webview.getUrl());
                error_image.setVisibility(View.INVISIBLE);
            }
        });

        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                webActivity.show_dialog_2button("提示", "是否更换背景图片？", "是的", "不用了", 5);
                return true;
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webActivity.set_reload(URL_USER);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int item_id = item.getItemId();
        switch (item_id) {
            case R.id.nav_home: {
                webActivity.set_reload("http://m.cust.edu.cn/index.cc");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.nav_qc: {

                Intent intent = new Intent();
                intent.setClass(webActivity, MipcaActivityCapture.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    webActivity.startActivity(intent);
                break;
            }
            case R.id.nav_settings: {
                Intent intent = new Intent();
                intent.setClass(webActivity, SettingsActivity.class);
                webActivity.startActivity(intent);
                break;
            }
            case R.id.nav_share:
            {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
//                File(Environment.getExternalStorageDirectory()+"/name.png");

//                Uri uri = Uri.fromFile(f); intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                intent.putExtra(Intent.EXTRA_TEXT, "I have successfully share my message through my app");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                webActivity.startActivity(Intent.createChooser(intent, webActivity.getTitle()));
            }

        }

        return true;
    }


}
