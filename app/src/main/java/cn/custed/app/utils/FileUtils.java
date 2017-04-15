package cn.custed.app.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.custed.app.WebActivity;

import static cn.custed.app.MyConstant.CLASS_DATA_ISDONE;
import static cn.custed.app.MyConstant.CLASS_DATA_KEY;
import static cn.custed.app.MyConstant.MY_DATA_FILE_DIR;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE_ID;

/**
 * Created by dxys on 17/4/3.
 */

public class FileUtils {

    public static String get_my_imagedir_path(Context context)
    {
        if (!new File(context.getFilesDir()+"/image/").exists())
            new File(context.getFilesDir()+"/image/").mkdirs();
        return context.getFilesDir()+"/image/";
    }

    public static String get_my_files_path(Context context)
    {
        if (!new File(context.getFilesDir()+"/files/").exists())
            new File(context.getFilesDir()+"/files/").mkdirs();
        return context.getFilesDir()+"/files/";
    }

    public static String get_my_sd_files_path(Context context)
    {
        if (!new File(Environment.getExternalStorageDirectory()+MY_DATA_FILE_DIR+"files/").exists())
            new File(Environment.getExternalStorageDirectory()+MY_DATA_FILE_DIR+"files/").mkdirs();
        return Environment.getExternalStorageDirectory()+MY_DATA_FILE_DIR+"files/";
    }


    public static boolean deleteSDFile(File file) {

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    deleteSDFile(f);
                }
            }
            file.delete();
            return true;
        }
        else
        {
            return false;
        }
    }

    public static Uri getUriForPath(String path, Context context)
    {
        Uri uri =null;
        uri = Uri.fromFile(new File(path));
        if (uri == null)
        {
            Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(mediaUri,null,MediaStore.Images.Media.DISPLAY_NAME+"=?",new String[]{path.substring(path.lastIndexOf("/")+1)},null);

            if (cursor.moveToFirst())
            {
                uri= ContentUris.withAppendedId(mediaUri,cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));

            }
            cursor.close();
        }
        return uri;
    }

    public static String getRealOPathForUri(Uri uri,Context context)
    {
        if(uri == null)
            return null;
        String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme))
        {
            data = uri.getPath();
        }
        else if (ContentResolver.SCHEME_CONTENT.equals(scheme))
        {
            Cursor cursor = context.getContentResolver().query(uri,new String[]{MediaStore.Images.ImageColumns.DATA},null,null,null);
            if (null != cursor)
            {
                if (cursor.moveToFirst())
                {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1)
                    {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            new File(new File(newPath).getParent()).mkdirs();
            if(new File(newPath).exists())
                deleteSDFile(new File(newPath));
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
            else
                Log.e("oldPath","is "+oldPath);
        }
        catch (Exception e) {
            e.printStackTrace();

        }

    }

   public void load_from_url(final WebActivity webActivity, final String urlStr, final String filename, final int requestCode)
   {
       new Thread(new Runnable() {

           @Override

           public void run() {

               try {

                   URL url = new URL(urlStr);
                   URLConnection con = url.openConnection();
                   con.addRequestProperty("Cookie", CookieManager.getInstance().getCookie("http://m.cust.edu.cn/user.cc"));
                   InputStream is = con.getInputStream();
                   switch (requestCode)
                   {
                       case NAV_USR_IMAGE_ID:
                       {
                           Boolean is_key = false;
                           byte[] bs = new byte[1024];
                           int len;
                               if(new File(filename).exists()) deleteSDFile(new File(filename));
                               OutputStream os = new FileOutputStream(filename);
                               while ((len = is.read(bs)) != -1) {
                                   if(!is_key && len < 200)
                                   {
                                       break;
                                   }
                                   is_key = true;
                                   os.write(bs, 0, len);
                               }
                               os.close();
                               is.close();

                           if(!is_key)
                           {
                               url = new URL("http://m.cust.edu.cn/pic_uid_real.jpg");
                               con = url.openConnection();
                               con.addRequestProperty("Cookie", CookieManager.getInstance().getCookie("http://m.cust.edu.cn/user.cc"));
                               is = con.getInputStream();
                               if(new File(filename).exists()) deleteSDFile(new File(filename));
                               os = new FileOutputStream(filename);
                               while ((len = is.read(bs)) != -1) {
                                   os.write(bs, 0, len);
                               }
                               os.close();
                               is.close();
                           }

                           Message msg=new Message();//或者Message msg=handle.obtainMessage();
                           msg.what=NAV_USR_IMAGE_ID;
                           webActivity.handler.sendMessage(msg);
                           break;
                       }
                       case 11:
                       {
                           byte[] buffer = new byte[1024];
                           int len;
                           ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                           while ((len = is.read(buffer)) != -1)
                           {
                               byteArrayOutputStream.write(buffer, 0, len);
                           }
                           is.close();
                           byteArrayOutputStream.close();
                           String html = new String(byteArrayOutputStream.toByteArray(), "utf-8");


                           String ifo1, ifo2,ifo[];
                           ifo = new String[2];
                           Pattern pattern = Pattern.compile(".*custid..", Pattern.DOTALL);
                           Matcher matcher = pattern.matcher(html);
                           ifo1 = matcher.replaceAll("");
                           pattern = Pattern.compile(".;.*", Pattern.DOTALL);
                           matcher = pattern.matcher(ifo1);
                           if (matcher.replaceAll("") != null) {
                               ifo[0] = "学号：" + matcher.replaceAll("");
                           }
                           pattern = Pattern.compile(".*realname..", Pattern.DOTALL);
                           matcher = pattern.matcher(html);
                           ifo2 = matcher.replaceAll("");
                           pattern = Pattern.compile(".;.*", Pattern.DOTALL);
                           matcher = pattern.matcher(ifo2);
                           if (matcher.replaceAll("") != null) {
                               try {
                                   ifo[1] = new String(Base64.decode(matcher.replaceAll(""), Base64.DEFAULT), "UTF-8");
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                           }


                           Message msg=new Message();//或者Message msg=handle.obtainMessage();
                           msg.what=11;
                           msg.obj = ifo;
                           webActivity.handler.sendMessage(msg);
                           break;
                       }
                       case 12:
                       {
                           byte[] bs = new byte[1024];
                           int len;
                           ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                           while ((len = is.read(bs)) != -1)
                           {
                               byteArrayOutputStream.write(bs,0,len);
                           }
                           is.close();
                           byteArrayOutputStream.close();
                           String class_html = new String(byteArrayOutputStream.toByteArray(),"utf-8");

                           String temp,class_data = null;
                           Pattern pattern = Pattern.compile(".*parseJSON..", Pattern.DOTALL);
                           Matcher matcher = pattern.matcher(class_html);
                           temp = matcher.replaceAll("");
                           pattern = Pattern.compile("...../*sDB.*", Pattern.DOTALL);
                           matcher = pattern.matcher(temp);
                           if (matcher.replaceAll("") != null) {
                               class_data = matcher.replaceAll("");
                           }


                           if(new File(filename).exists()) deleteSDFile(new File(filename));
                           OutputStream os = new FileOutputStream(filename);
                           assert class_data != null;
                           os.write(class_data.getBytes());
                           os.close();
                           is.close();
                           if (class_data.contains("weekji"))
                           {
                               webActivity.getUsrIfoDatebase().update_data(CLASS_DATA_KEY,CLASS_DATA_ISDONE);
                           }
                           break;
                       }
                       case 13:
                       {
                           byte[] buffer = new byte[1024];
                           int len;
                           ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                           while ((len = is.read(buffer)) != -1)
                           {
                               byteArrayOutputStream.write(buffer, 0, len);
                           }
                           is.close();
                           byteArrayOutputStream.close();
                           String html = new String(byteArrayOutputStream.toByteArray(), "utf-8");
                           Message msg=new Message();
                           msg.what=13;
                           msg.obj = html;
                           webActivity.handler.sendMessage(msg);

                       }
                   }

               } catch (Exception e) {
                   Log.e("-----","hvae");
                   e.printStackTrace();
               }
           }
       }).start();

   }



    public static String read(String fileName) {
        String content = null;
        try {
            FileInputStream inputStream = new FileInputStream(new File(fileName));
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            content = new String(arrayOutputStream.toByteArray(),"utf-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    }
