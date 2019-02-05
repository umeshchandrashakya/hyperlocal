package com.hyperlocal.app.utlity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.hyperlocal.app.R;

/**
 * @Author ${Umesh} on 06-04-2018.
 */

public class ScreenUtil {


     private ScreenUtil(){

     }
    public static void changeStatusBarColor(AppCompatActivity activity){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.bg_red_alpha));
        }
    }

}
