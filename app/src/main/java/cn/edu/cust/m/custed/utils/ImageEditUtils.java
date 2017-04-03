package cn.edu.cust.m.custed.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import cn.edu.cust.m.custed.WebActivity;

import static cn.edu.cust.m.custed.MyConstant.IMAGE_UNSPECIFIED;
import static cn.edu.cust.m.custed.MyConstant.PHOTO_RESOULT;
import static cn.edu.cust.m.custed.MyConstant.PHOTO_ZOOM;

/**
 * Created by dxys on 17/4/3.
 */

public class ImageEditUtils {
    private WebActivity webActivity;


    public ImageEditUtils(WebActivity webActivity)
    {
        this.webActivity = webActivity;
    }

    public void intent_MediaStore()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        webActivity.start_intent(intent,PHOTO_ZOOM);
    }
    public void startPhotoZoom(Uri uri,int outputX,int outputY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        webActivity.start_intent(intent, PHOTO_RESOULT);
    }
    public File bitmap_to_image(Bitmap bitmap, String name) throws IOException {

        File fImage = new File(Environment.getExternalStorageDirectory()+"/"+name);
        fImage.createNewFile();
        FileOutputStream iStream = new FileOutputStream(fImage);
        iStream.close();
        return fImage;
    }
    public Drawable loadImageFromUrl(String urladdr) {
        Drawable drawable = null;
        try{
            drawable = Drawable.createFromStream(new URL(urladdr).openStream(), "image.jpg");
        }catch(IOException e){
            Log.d("test",e.getMessage());
        }
        return drawable;
    }
}
