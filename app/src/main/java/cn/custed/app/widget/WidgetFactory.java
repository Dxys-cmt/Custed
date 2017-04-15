package cn.custed.app.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import cn.custed.app.database.UsrIfoDatebase;
import cn.edu.cust.m.custed.R;

import static cn.custed.app.MyConstant.CLASS_DATA_ISDONE;
import static cn.custed.app.MyConstant.CLASS_DATA_KEY;
import static cn.custed.app.MyConstant.DATABASE_NAME;
import static cn.custed.app.MyConstant.WIDGET_WEEK;
import static cn.custed.app.MyConstant.WIDGET_WEEK_DAY;

/**
 * Created by dxys on 17/4/8.
 */

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private static String[][] class_ifo;
    private int week;
    private int week_day;
    private static UsrIfoDatebase usrIfoDatebase;

    public WidgetFactory(Context context,Intent intent)
    {
        this.context = context;
        week = intent.getIntExtra("week",ClassData.get_week());
        week_day = intent.getIntExtra("week_day",ClassData.get_week_day());

        usrIfoDatebase = new UsrIfoDatebase(context,DATABASE_NAME);
        if (usrIfoDatebase.get_query_ifovalue(CLASS_DATA_KEY).equals(CLASS_DATA_ISDONE))
        class_ifo = ClassData.get_class_name(week_day,week,context);
        Log.e("widgetFactory","this0");
    }

    @Override
    public void onCreate() {

    }



    @Override
    public void onDataSetChanged() {
        week = Integer.parseInt(usrIfoDatebase.get_query_ifovalue(WIDGET_WEEK));
        week_day = Integer.parseInt(usrIfoDatebase.get_query_ifovalue(WIDGET_WEEK_DAY));
        if (usrIfoDatebase.get_query_ifovalue(CLASS_DATA_KEY).equals(CLASS_DATA_ISDONE))
        class_ifo = ClassData.get_class_name(week_day,week,context);
        Log.e("datachanged","istrue");

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        int mCount = 0;
        if (class_ifo != null)
        {
            for (String[] aClass_ifo : class_ifo) {
                if (!aClass_ifo[0].equals("")) {
                    mCount++;
                    Log.e("classifo", "is" + aClass_ifo[0]);
                }
            }
        }
        Log.e("mcount","is:"+String.valueOf(mCount));

        return mCount;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        Log.e("getViewat","this2"+String.valueOf(position));
        remoteViews.setTextViewText(R.id.widget_item_title,class_ifo[position][1]);
        remoteViews.setTextViewText(R.id.widget_item_room,class_ifo[position][2]);
        remoteViews.setTextViewText(R.id.widget_item_jie,ClassData.get_jie_ke(class_ifo[position][0]));
        if (ClassData.class_is_finish(week,week_day,Integer.parseInt(class_ifo[position][0])))
        {
            remoteViews.setImageViewResource(R.id.widget_item_checkbox,R.drawable.widget_checked);
        }
        else
        {
            remoteViews.setImageViewResource(R.id.widget_item_checkbox,R.drawable.widget_uncheck);
        }
        Intent intent = new Intent();
        remoteViews.setOnClickFillInIntent(R.id.widget_item_layout,intent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
