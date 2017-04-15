package cn.custed.app.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import cn.custed.app.WebActivity;

import static cn.custed.app.MyConstant.IMAGE_UNSPECIFIED;

/**
 * Created by dxys on 17/4/3.
 */

public class ImageEditUtils {
    private WebActivity webActivity;


    public ImageEditUtils(WebActivity webActivity)
    {
        this.webActivity = webActivity;
    }

    public void intent_MediaStore(int request)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        webActivity.start_intent(intent,request);
    }

    public void startPhotoZoom(Uri uri,int outputX,int outputY,int request) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", outputX);
        intent.putExtra("aspectY", outputY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        webActivity.start_intent(intent, request);
    }

    public static int calculateInSampleSize(String filePath,
                                            int reqWidth, int reqHeight,String newfilePath) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.e("-----","uiui"+String.valueOf(height));
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(newfilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        options.inSampleSize = inSampleSize;
        bitmap = BitmapFactory.decodeFile(filePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        bitmap.compress(Bitmap.CompressFormat.PNG, 60, out);
        return inSampleSize;
    }

    public static Drawable get_my_dir_image(String iamge_name, Context context)
    {
        Log.e("5678","56789"+FileUtils.get_my_imagedir_path(context));
        return Drawable.createFromPath(FileUtils.get_my_imagedir_path(context)+iamge_name);
    }


}
