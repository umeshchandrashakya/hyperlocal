package com.hyperlocal.app.ui.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @Author ${Umesh} on 11-04-2018.
 */


@SuppressLint("AppCompatCustomView")
public class ComfortRegularTextView extends TextView {

    public ComfortRegularTextView(Context context) {
        super(context);
        init();
    }

    public ComfortRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComfortRegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    public ComfortRegularTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Comfortaa-Regular.ttf");
        setTypeface(myTypeface);
    }
}