package cn.custed.app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cn.custed.app.WebActivity;
import cn.custed.app.database.UsrIfoDatebase;
import cn.edu.cust.m.custed.R;

import static cn.custed.app.MyConstant.DATABASE_NAME;
import static cn.custed.app.MyConstant.WIDGET_ID;
import static cn.custed.app.MyConstant.WIDGET_WEEK;
import static cn.custed.app.MyConstant.WIDGET_WEEK_DAY;

/**
 * Implementation of App Widget functionality.
 */
public class ClassWidget extends AppWidgetProvider {

    private String TAG = "appwidgetProvider";

    private static final int BUTTON_PREVIEW = 0;
    private static final int BUTTON_NEXT = 10;
    private static int[] appWidgetId;

    private UsrIfoDatebase usrIfoDatebase;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        appWidgetId = appWidgetIds;
        updateAllAppWidgets(context, AppWidgetManager.getInstance(context), appWidgetIds, ClassData.get_week(), ClassData.get_week_day());

    }


    @Override
    public void onEnabled(Context context) {
        usrIfoDatebase = new UsrIfoDatebase(context, DATABASE_NAME);
        if (usrIfoDatebase.get_query_id(WIDGET_WEEK_DAY) == -1) {
            usrIfoDatebase.insert_data(WIDGET_WEEK, String.valueOf(ClassData.get_week()));
            usrIfoDatebase.insert_data(WIDGET_WEEK_DAY, String.valueOf(ClassData.get_week_day()));
            usrIfoDatebase.insert_data(WIDGET_ID,String.valueOf(0));
        }
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        String action = intent.getAction();
        usrIfoDatebase = new UsrIfoDatebase(context, DATABASE_NAME);
        Bundle extras = intent.getExtras();

        Log.e("onReceive","actionis:"+action);
        if("ITEM".equals(action))
        {
            Intent intent1 = new Intent();
            intent1.setAction("android.intent.action.MYACTIVITY");
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
        else
        if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE) && extras != null) {
            Uri data = intent.getData();
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            appWidgetId = new int[] {Integer.parseInt(usrIfoDatebase.get_query_ifovalue(WIDGET_ID))};
            int week_set = Integer.parseInt(usrIfoDatebase.get_query_ifovalue(WIDGET_WEEK));
            int week_day_set = Integer.parseInt(usrIfoDatebase.get_query_ifovalue(WIDGET_WEEK_DAY));
            switch (buttonId) {

                case BUTTON_PREVIEW: {
                    if (week_set > 1) {
                        if (week_day_set > 1) {
                            week_day_set--;
                        } else {
                            week_day_set = 7;
                            week_set--;
                        }

                    }
                    break;
                }
                case BUTTON_NEXT: {
                    if (week_set < 18) {
                        if (week_day_set < 7) {
                            week_day_set++;
                        } else {
                            week_day_set = 1;
                            week_set++;
                        }

                    }
                    break;
                }

                default: {
                }
            }
            updateAllAppWidgets(context, AppWidgetManager.getInstance(context), appWidgetId, week_set, week_day_set);
        }

        super.onReceive(context, intent);


    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }


    private void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIDs, int week, int week_day) {

        usrIfoDatebase = new UsrIfoDatebase(context, DATABASE_NAME);
        for (int appWidgetID : appWidgetIDs) {
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.class_widget);
            remoteView.setTextViewText(R.id.widget_title, "第" + String.valueOf(week) + "周");
            remoteView.setTextViewText(R.id.widget_week_day, ClassData.get_week_day_cn(week_day - 1));
            remoteView.setOnClickPendingIntent(R.id.widget_preview, getPendingIntent(context, BUTTON_PREVIEW, appWidgetID));
            remoteView.setOnClickPendingIntent(R.id.widget_next, getPendingIntent(context, BUTTON_NEXT, appWidgetID));
            Intent intent = new Intent(context, WidgetSevice.class);
            intent.putExtra("week", week);
            intent.putExtra("week_day", week_day);
            intent.putExtra("appWidgetID", appWidgetID);
            remoteView.setRemoteAdapter(R.id.widget_list, intent);
            Intent toastIntent = new Intent(context, ClassWidget.class);
            toastIntent.setAction("ITEM");
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteView.setPendingIntentTemplate(R.id.widget_list, toastPendingIntent);

            Log.e("appwidget","idis:"+String.valueOf(appWidgetID));
            usrIfoDatebase.update_data(WIDGET_WEEK, String.valueOf(week));
            usrIfoDatebase.update_data(WIDGET_WEEK_DAY, String.valueOf(week_day));
            usrIfoDatebase.update_data(WIDGET_ID,String.valueOf(appWidgetID));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetID, R.id.widget_list);
            appWidgetManager.updateAppWidget(appWidgetID, remoteView);
        }

    }


    private PendingIntent getPendingIntent(Context context, int buttonId, int appWidgetID) {
        Intent intent = new Intent();
        intent.setClass(context, ClassWidget.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("custom:" + buttonId));
        intent.putExtra("appWidgetID", appWidgetID);

        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

}

