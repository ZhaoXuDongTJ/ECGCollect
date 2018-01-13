package ecg.ecg_collect;

import android.app.Application;

import org.litepal.LitePalApplication;

import ecg.ecg_collect.blueTooth.BlueInfo;

/**
 * Created by 92198 on 2018/1/5.
 */

public class globalPool extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePalApplication.initialize(this);
    }

    public BlueInfo blueInfo=null;
}
