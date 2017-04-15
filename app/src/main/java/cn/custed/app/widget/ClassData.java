package cn.custed.app.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.custed.app.utils.FileUtils;

import static android.R.attr.format;
import static cn.custed.app.MyConstant.CLASS_DATA_VALUE;
import static cn.custed.app.MyConstant.FIRST_DAY;

/**
 * Created by dxys on 17/4/9.
 */

public class ClassData {
    public static int differentDays(String date)
    {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(FIRST_DAY);
            date2 = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)
                {
                    timeDistance += 366;
                }
                else
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else
        {
            System.out.println("判断day2 - day1 : " + (day2-day1));
            return day2-day1;
        }
    }

    public static String get_data()
    {
        SimpleDateFormat   formatter   =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static int get_week()
    {
        return differentDays(get_data())/7+1;
    }

    public static int get_week_day()
    {
        return differentDays(get_data())%7+1;
    }

    public static String[][] get_class_name(int week_day, int weeki, Context context)
    {
        String week = String.valueOf(weeki);
        String[][] classIfo = new String[6][3];
        if(new File(FileUtils.get_my_files_path(context)+CLASS_DATA_VALUE).exists())
        {
            try {

            JSONObject jsonObject = new JSONObject(FileUtils.read(FileUtils.get_my_files_path(context)+CLASS_DATA_VALUE));

                int l = 0;


                for (int i =0;i<6;i++)
                {
                    classIfo[i] = new String[]{"","",""};
                    int class_id = (week_day-1)+i*7;
                    try {
                        if(jsonObject.getJSONArray(String.valueOf(class_id)) != null)
                        {
                            JSONArray jsonArray = jsonObject.getJSONArray(String.valueOf(class_id));
                            for (int j=0;j<jsonArray.length();j++)
                            {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                                String[] class_weeks = jsonObject1.getString("weeks").split(",");
                                for (int k = 0;k<class_weeks.length;k++)
                                {

                                    if(class_weeks[k].equals(week))
                                    {
                                        String[] classIfo1 = new String[3];

                                        classIfo1[0] = String.valueOf(i);
                                        classIfo1[1] = jsonObject1.getString("name");
                                        classIfo1[2] = jsonObject1.getString("room");

                                        classIfo[l] = classIfo1;

                                        Log.e("classIfo","is"+classIfo[l][0]+classIfo[l][1]+classIfo[l][2]);
                                        l++;
                                    }
                                }
                            }
                        }
                    } catch (JSONException e)
                    {
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else Log.e("dfghjkl;","fghjk");

return classIfo;
    }



    public static String get_week_day_cn(int week_day)
    {
        String[] week_day_cn = new String[] {"星期一","星期二","星期三","星期四","星期五","星期六","星期天"};
        return week_day_cn[week_day];
    }

    public static String get_jie_ke(String jies)
    {
     int jie = Integer.parseInt(jies);
        String[] jieke = new String[] {"1-2","3-4","5-6","7-8","9-10","11-12"};
        return jieke[jie];
    }

    public static boolean class_is_finish(int week,int week_day,int jie_ke)
    {
        boolean class_is_finish = false;
        int[] time_jie = {800,1005,1330,1525,1800,2000};
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmm");
        int time_currten = Integer.parseInt(simpleDateFormat.format(new Date()));

        if(week >= get_week())
        {
            if (week_day >= get_week_day())
            {
                if (week_day == get_week_day() && time_jie[jie_ke] < time_currten)
                {
                    class_is_finish = true;
                }
            }
            else class_is_finish =true;
        }
        else class_is_finish = true;

        return class_is_finish;
        }


}
