package org.myplugin.cordova.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.tencent.smtt.sdk.QbSdk;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

import java.io.File;
import java.util.HashMap;

/**
 * Created by zhang on 2017/6/28.
 */

public class OpenFileLocal extends CordovaPlugin {
    private static final String TAG = "ChooseFile";
    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
        if(action.equals("openFile")){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            //启动activity
            try {
                this.cordova.startActivityForResult(this, intent, 0);
            }catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(this.cordova.getActivity(), "请安装文件管理器.", Toast.LENGTH_SHORT).show();
                }
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // 根据resultCode判断处理结果
        if(resultCode == Activity.RESULT_OK){
            Uri uri = intent.getData();//得到uri，后面就是将uri转化成file的过程。
            Log.d(TAG, "File Uri: " + uri.toString());
            // Get the path
            String path = getPath(this.cordova.getActivity(), uri);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("style", "1");
            params.put("local", "true");
            QbSdk.openFileReader(this.cordova.getActivity(), path, params,null);
        }
    }

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it  Or Log it.
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
    * 打开文件
    * @param file
    */
    private void openFile(File file){

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
//获取文件file的MIME类型
        String type = getMIMEType(file);
//设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
//跳转
        this.cordova.getActivity().startActivity(intent); //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。

    }

    private final String[][] MIME_MapTable= {
//{后缀名，MIME类型}
        {".3gp", "video/3gpp"},
        {".apk", "application/vnd.android.package-archive"},
        {".asf", "video/x-ms-asf"},
        {".avi", "video/x-msvideo"},
        {".bin", "application/octet-stream"},
        {".bmp", "image/bmp"},
        {".c", "text/plain"},
        {".class", "application/octet-stream"},
        {".conf", "text/plain"},
        {".cpp", "text/plain"},
        {".doc", "application/msword"},
        {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
        {".xls", "application/vnd.ms-excel"},
        {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
        {".exe", "application/octet-stream"},
        {".gif", "image/gif"},
        {".gtar", "application/x-gtar"},
        {".gz", "application/x-gzip"},
        {".h", "text/plain"},
        {".htm", "text/html"},
        {".html", "text/html"},
        {".jar", "application/java-archive"},
        {".java", "text/plain"},
        {".jpeg", "image/jpeg"},
        {".jpg", "image/jpeg"},
        {".js", "application/x-JavaScript"},
        {".log", "text/plain"},
        {".m3u", "audio/x-mpegurl"},
        {".m4a", "audio/mp4a-latm"},
        {".m4b", "audio/mp4a-latm"},
        {".m4p", "audio/mp4a-latm"},
        {".m4u", "video/vnd.mpegurl"},
        {".m4v", "video/x-m4v"},
        {".mov", "video/quicktime"},
        {".mp2", "audio/x-mpeg"},
        {".mp3", "audio/x-mpeg"},
        {".mp4", "video/mp4"},
        {".mpc", "application/vnd.mpohun.certificate"},
        {".mpe", "video/mpeg"},
        {".mpeg", "video/mpeg"},
        {".mpg", "video/mpeg"},
        {".mpg4", "video/mp4"},
        {".mpga", "audio/mpeg"},
        {".msg", "application/vnd.ms-outlook"},
        {".ogg", "audio/ogg"},
        {".pdf", "application/pdf"},
        {".png", "image/png"},
        {".pps", "application/vnd.ms-powerpoint"},
        {".ppt", "application/vnd.ms-powerpoint"},
        {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
        {".prop", "text/plain"},
        {".rc", "text/plain"},
        {".rmvb", "audio/x-pn-realaudio"},
        {".rtf", "application/rtf"},
        {".sh", "text/plain"},
        {".tar", "application/x-tar"},
        {".tgz", "application/x-compressed"},
        {".txt", "text/plain"},
        {".wav", "audio/x-wav"},
        {".wma", "audio/x-ms-wma"},
        {".wmv", "audio/x-ms-wmv"},
        {".wps", "application/vnd.ms-works"},
        {".xml", "text/plain"},
        {".z", "application/x-compress"},
        {".zip", "application/x-zip-compressed"},
        {"", "*/*"}
    };

    /**
     * 根据文件后缀名获得对应的MIME类型。
     * @param file
     */
    private String getMIMEType(File file) {

        String type="*/*";
        String fName = file.getName();
//获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
/* 获取文件的后缀名*/
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
//在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }
}
