package cn.custed.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import static cn.custed.app.MyConstant.DATABASE_NAME;
import static cn.custed.app.MyConstant.DATABASE_TABNAME;

/**
 *
 * Created by dxys on 17/4/1.
 */

public class UsrIfoDatebase {

    private Context context;
    static private SQLiteDatabase database;




    public UsrIfoDatebase(Context context,String database_name)
    {
        this.context = context;
    }


    private static SQLiteDatabase getDATABASE(Context context)
    {
        if(database == null)
        {
            DataBaseHelper helper = new DataBaseHelper(context, DATABASE_NAME);
            database = helper.getWritableDatabase();
        }
        return database;
    }
    public boolean is_datebase_tab_exist()
    {
        database = getDATABASE(context);
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
        try {
            cursor.close();
        }catch (NullPointerException e)
        {
            Log.e(" ","cursor.close");
        }
        return result;
    }

    public void close_datebase()
    {
        database.close();
    }

    public void creat_tab()
    {
        database = getDATABASE(context);
        database.execSQL("CREATE TABLE "+DATABASE_TABNAME+"(_id integer primary key autoincrement, ifoname TEXT, ifovalue TEXT);");
    }

    public void insert_data(String ifoname,String ifovalue)
    {
        database = getDATABASE(context);
        if(get_query_id(ifoname) == -1) {
            ContentValues contentvalues = new ContentValues();
            contentvalues.put("ifoname", ifoname);
            contentvalues.put("ifovalue", ifovalue);
            database.insert(DATABASE_TABNAME, null, contentvalues);
        }
        else
        {
            update_data(ifoname,ifovalue);
        }
    }

    public boolean update_data(String ifoname,String ifovalue)
    {
        database = getDATABASE(context);
        if(get_query_id(ifoname) != -1)
        {
            int id = get_query_id(ifoname);
            ContentValues rifovalue = new ContentValues();
            rifovalue.put("ifovalue",ifovalue);
            database.update(DATABASE_TABNAME,rifovalue,"_id="+String.valueOf(id),null);
            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean delete_data(String ifoname)
    {
        database = getDATABASE(context);
        if(get_query_id(ifoname) != -1)
        {
            int id = (int) query_data(ifoname).get("id");
            database.delete(DATABASE_TABNAME,"_id="+id,null);
            return true;
        }
        else
        {
            return false;
        }
    }
    private ContentValues query_data(String ifoname)
    {
        database = getDATABASE(context);
        Cursor all_cursor = database.rawQuery("select * from "+DATABASE_TABNAME,null);
        ContentValues contentValues = new ContentValues();
        while (all_cursor.moveToNext())
        {
            if(all_cursor.getCount() != 0 && all_cursor.getString(1).equals(ifoname))
            {
                int id = all_cursor.getInt(0);
                String rifoname = all_cursor.getString(1);
                String rifovalue = all_cursor.getString(2);
                contentValues.put("id",id);
                contentValues.put("ifoname",rifoname);
                contentValues.put("ifovalue",rifovalue);
            }

        }
        all_cursor.close();
        return contentValues;
    }
    public Integer get_query_id(String ifoname)
    {
        int result = -1;
        if(get_query_ifovalue(ifoname) != null)
        result = query_data(ifoname).getAsInteger("id");
        return result;
    }

    public String get_query_ifovalue(String ifoname)
    {
        String result;
        result = query_data(ifoname).getAsString("ifovalue");
        return result;
    }
}
