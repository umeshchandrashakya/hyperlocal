package com.hyperlocal.app.ui.customview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * @Author ${Umesh} on 12-04-2018.
 */

@SuppressLint("AppCompatCustomView")
public class RobotoBoldEditText extends EditText {
    public RobotoBoldEditText(Context context) {
        super(context);
        init();
    }

    public RobotoBoldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotoBoldEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RobotoBoldEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private void init() {
        Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Bold.ttf");
        setTypeface(myTypeface);

    }
}
