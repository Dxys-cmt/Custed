package cn.edu.cust.m.custed.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 *
 * Created by dxys on 17/4/1.
 */

public class UsrIfoDatebase {

    private Context context;
    private String database_name;
    private DataBaseHelper helper;
    private SQLiteDatabase database;

    private static final String DATABASE_TABNAME = "UsrIfo";


    public UsrIfoDatebase(Context context,String database_name)
    {
        this.context = context;
        this.database_name = database_name;
    }


    public boolean is_datebase_tab_exist()
    {
        helper = new DataBaseHelper(context,database_name);
        database = helper.getReadableDatabase();
        boolean result = false;
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from sqlite_master where type = 'table' and name = ?";
            cursor = database.rawQuery(sql, new String[] {DATABASE_TABNAME});
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }

        }
        catch (Exception e) {
            Log.e("UsrIfodatabase","isdatabaseexist");
        }
        database.close();
        try {
            cursor.close();
        }catch (Exception e)
        {
            Log.e(" ","cursor.close");
        }
        return result;
    }

    public void creat_tab()
    {
        helper = new DataBaseHelper(context,database_name);
        database = helper.getReadableDatabase();
        database.execSQL("CREATE TABLE "+DATABASE_TABNAME+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, ifoname TEXT, ifovalue TEXT);");
        database.close();
    }

}
