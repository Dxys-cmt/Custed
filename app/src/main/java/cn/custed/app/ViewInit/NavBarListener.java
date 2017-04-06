package cn.custed.app.ViewInit;

import android.graphics.drawable.Drawable;
import android.os.Build;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.custed.app.WebActivity;
import cn.custed.app.database.UsrIfoDatebase;
import cn.custed.app.MyConstant;
import cn.custed.app.utils.FileUtils;
import cn.custed.app.utils.ImageEditUtils;
import cn.custed.app.webView.WebMain;
import cn.edu.cust.m.custed.R;

import static cn.custed.app.MyConstant.DATABASE_NAME;
import static cn.custed.app.MyConstant.FIRST_START_PAGE_INDEX;
import static cn.custed.app.MyConstant.FIRST_START_PAGE_NAME;
import static cn.custed.app.MyConstant.FIRST_START_PAGE_SCHEDULE;
import static cn.custed.app.MyConstant.NAV_HEADER_BACKGROUND;
import static cn.custed.app.MyConstant.NAV_HEADER_BACKGROUND_VALUE;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE_ID;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE_VALUE;


/**
 * Created by dxys on 17/4/4.
 */

public class NavBarListener implements NavigationView.OnNavigationItemSelectedListener {
    private WebActivity webActivity;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout linearLayout;
    private SwitchCompat switchCompat;
    private ImageView imageView;
    private TextView usr;
    private TextView sign;
    private TextView web_test;
    UsrIfoDatebase usrIfoDatebase;

    public NavBarListener(WebActivity webActivity)
    {
        this.webActivity = webActivity;
    }

    public void init_all_view(DrawerLayout drawerLayout, NavigationView navigationView, LinearLayout linearLayout, SwitchCompat switchCompat, ImageView imageView, TextView usr, TextView sign, final TextView web_test)
    {

        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;
        this.linearLayout = linearLayout;
        this.switchCompat = switchCompat;
        this.imageView = imageView;
        this.usr = usr;
        this.sign = sign;
        this.web_test = web_test;
        usrIfoDatebase = new UsrIfoDatebase(webActivity,DATABASE_NAME);

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


        if(usrIfoDatebase.get_query_ifovalue(NAV_HEADER_BACKGROUND) != null && new File(FileUtils.get_my_imagedir_path()+NAV_HEADER_BACKGROUND_VALUE).exists())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                linearLayout.setBackground(ImageEditUtils.get_my_dir_image(NAV_HEADER_BACKGROUND_VALUE));
            }
        }


        if (usrIfoDatebase.get_query_ifovalue(NAV_USR_IMAGE) != null && new File(FileUtils.get_my_imagedir_path()+NAV_USR_IMAGE_VALUE).exists())
        {
            Log.i("------","rrrrrrr");
            imageView.setImageDrawable(ImageEditUtils.get_my_dir_image(NAV_USR_IMAGE_VALUE));
        }


        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                webActivity.show_dialog_2button("提示", "是否更换背景图片？", "是的", "不用了", 5);
                return true;
            }
        });

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerOpened(View drawerView) {

                if(CookieManager.getInstance().getCookie("http://m.cust.edu.cn/user.cc").length() > 30)
                {
                    FileUtils.load_from_url(webActivity,"http://m.cust.edu.cn/pic_uid_avatar.jpg",FileUtils.get_my_imagedir_path()+NAV_USR_IMAGE_VALUE,NAV_USR_IMAGE_ID);

                    FileUtils.load_from_url(webActivity,"http://m.cust.edu.cn/user.cc",null,11);
                }
                else
                    Log.e("-----","H:"+CookieManager.getInstance().getCookie("http://m.cust.edu.cn/user.cc"));

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {}
            @Override
            public void onDrawerClosed(View drawerView) {}
            @Override
            public void onDrawerStateChanged(int newState) {}
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int item_id = item.getItemId();
        switch (item_id) {
            case R.id.nav_home: {
                webActivity.set_reload("http://m.cust.edu.cn/index.cc");
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
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private String getHtml(String Url) throws Exception
    {
        URL url = new URL(Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("Cookie",CookieManager.getInstance().getCookie("http://m.cust.edu.cn/user.cc"));
        conn.setConnectTimeout(6 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            byte[] data = readStream(inputStream);
            String html = new String(data, "utf-8");
            return html;
        }
        return null;
    }

    private byte[] readStream(InputStream inputStream) throws Exception
    {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1)
        {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        inputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }


}
