package ecg.ecg_collect.Algorithm;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 92198 on 2018/1/13.
 */

public class CommonUtils {
    // 三种地址的获取
    public static String getHeartPath(){
        // 心电数据
        Date currentTime = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年/MM月/dd天/HH-mm-ss");
        String dateString = formatter.format(currentTime);
        return Environment.getExternalStorageDirectory()+"/zxd/heartDate/"+dateString+".txt";
    }

    public static String getImagePath(){
        // 头像数据
        Date currentTime = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd/HH-mm-ss");
        String dateString = formatter.format(currentTime);
        return Environment.getExternalStorageDirectory()+"/zxd/UserPic/"+dateString+".jpg";
    }
    public static String getTempHeartPath(){
        // 临时心电数据
        Date currentTime = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年/MM月/dd天/HH-mm-ss");
        String dateString = formatter.format(currentTime);
        return Environment.getExternalStorageDirectory()+"/zxd/heartDateTemp/"+dateString+".txt";
    }
}
