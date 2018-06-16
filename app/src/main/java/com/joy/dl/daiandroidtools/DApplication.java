package com.joy.dl.daiandroidtools;

import android.app.Application;

/**
 * Created by dl on 2018/06/16 0016.
 */

public class DApplication extends Application {

    private static DApplication instance;

    public static DApplication getInstance(){
        if(instance==null){
            instance=new DApplication();
        }

        return instance;
    }
}
