package com.hyperlocal.app.ui.customview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @Author ${Umesh} on 12-04-2018.
 */

@SuppressLint("AppCompatCustomView")
public class RobotoMediumTextView extends TextView {
    public RobotoMediumTextView(Context context) {
        super(context);
        init();
    }


    public RobotoMediumTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotoMediumTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RobotoMediumTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
        setTypeface(myTypeface);

    }

}
