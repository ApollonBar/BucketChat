package us.xingkong.bucketchat.others;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import us.xingkong.bucketchat.app.activity.view.TestActivity;

/**
 * Created by 饶翰新 on 2017/7/14.
 */

public class Util {

    public static void jump(Activity activity,Class<?> nextActivity) {
        Intent intent = new Intent(activity, nextActivity);
        activity.startActivity(intent);
    }

    public static void jumpAndClose(Activity activity,Class<?> nextActivity) {
        jump(activity,nextActivity);
        activity.finish();
    }

    public static void restart(Activity activity) {
        jumpAndClose(activity,TestActivity.class);
    }

    public static void showText(Activity activity,String str) {
        try
        {
            Toast.makeText(activity,str,Toast.LENGTH_SHORT).show();
        }catch (RuntimeException e)
        {

        }
    }

    public static void showText(Activity activity,int resource) {
        try
        {
            Toast.makeText(activity,resource,Toast.LENGTH_SHORT).show();
        }catch (RuntimeException e)
        {

        }

    }


    /**
     * 获取拍照相片存储文件
     *
     * @param context
     * @return
     */
    public static File createFile(Context context) {
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String timeStamp = String.valueOf(new Date().getTime());
            file = new File(Environment.getExternalStorageDirectory() +
                    File.separator + timeStamp + ".jpg");
        } else {
            File cacheDir = context.getCacheDir();
            String timeStamp = String.valueOf(new Date().getTime());
            file = new File(cacheDir, timeStamp + ".jpg");
        }
        return file;
    }
    public static Bitmap string2Bitmap(String string){
        if(string == null)
            return null;
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static byte[] base64toByte(String string){
        if(string == null)
            return null;
        try {
            return Base64.decode(string, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String bitmap2String(Bitmap bitmap){
        if(bitmap == null)
            return "";
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }

    public static Bitmap bytes2Bimap(byte[] b) {

        if (b != null && b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static byte[] readFile(String path) {
        if(path == null)
            return null;
        byte[] result = null;
        try{

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] tmp = new byte[255];

            int length;
            while((length = in.read(tmp)) != -1)
            {
                out.write(tmp,0,length);

            }
            out.flush();
            result = out.toByteArray();
            out.close();
            in.close();

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getRealPathFromUri(Context context, String UriPath) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            Uri contentUri = Uri.parse(UriPath);
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }
    public static void writeFile(String path,byte[] bytes)
    {
            try {
                File file = new File(path);
                if(!file.exists())
                    file.createNewFile();
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                out.write(bytes);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static String getImageBase64(String path)
    {

        byte[] b = Util.readFile(path);
        //System.out.println(path + "   byte.length:" + (b!=null?b.length:"NULL"));
        Bitmap bitmap = bytes2Bimap(b);
        return bitmap2String(bitmap);
    }

    public static String encode(String in)
    {
        String out = in;


        return out;

    }
}
