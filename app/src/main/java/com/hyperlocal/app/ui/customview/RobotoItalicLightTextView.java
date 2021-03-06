package com.hyperlocal.app.ui.customview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @Author ${Umesh} on 11-04-2018.
 */

@SuppressLint("AppCompatCustomView")
public class RobotoItalicLightTextView extends TextView {

    public RobotoItalicLightTextView(Context context) {
        super(context);
        init();
    }

    public RobotoItalicLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotoItalicLightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RobotoItalicLightTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-ItalicLight.ttf");
        setTypeface(myTypeface);
    }
}