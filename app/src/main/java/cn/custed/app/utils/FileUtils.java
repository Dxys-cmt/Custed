package cn.custed.app.utils;

import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import cn.custed.app.ViewInit.NavBarListener;
import cn.custed.app.WebActivity;

import static cn.custed.app.MyConstant.MY_DATA_FILE_DIR;
import static cn.custed.app.MyConstant.NAV_USR_IMAGE_ID;

/**
 * Created by dxys on 17/4/3.
 */

public class FileUtils {

    public static String get_my_imagedir_path()
    {
        return Environment.getExternalStorageDirectory()+MY_DATA_FILE_DIR+"image/";
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
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

   public static void load_from_url(final WebActivity webActivity, final String urlStr, final String filename, final int requestCode)
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
                           if(new File(filename).exists())
                               deleteSDFile(new File(filename));
                           byte[] bs = new byte[1024];
                           int len;
                           OutputStream os = new FileOutputStream(filename);
                           while ((len = is.read(bs)) != -1) {
                               os.write(bs, 0, len);
                           }
                           os.close();
                           is.close();
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
                           Message msg=new Message();//或者Message msg=handle.obtainMessage();
                           msg.what=11;
                           msg.obj = html;
                           webActivity.handler.sendMessage(msg);
                           break;
                       }
                   }

               } catch (Exception e) {
                   Log.e("-----","hvae");
                   e.printStackTrace();
               }
           }
       }).start();

   }

}
