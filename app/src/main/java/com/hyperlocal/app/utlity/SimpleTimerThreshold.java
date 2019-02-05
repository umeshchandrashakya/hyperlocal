package com.hyperlocal.app.utlity;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Date;

/**
 * @author ${Umesh} on 07-05-2018.
 */

public class SimpleTimerThreshold {
    private int _timeoutThresholdSeconds;
    private Date _timeoutDate;
    private SharedPreferences _preferences;
    private String _referenceName;

    private SimpleTimerThreshold() {}

    /**
     * Creates a new timer,
     * call restartTimer()
     * then later check if the amout of time has elapsed
     * by calling hasTimerPassedThreshold()
     *
     * @param timeThresholdSeconds
     * 			How long the timer should be.
     */
    public SimpleTimerThreshold(int timeThresholdSeconds) {
        _timeoutThresholdSeconds = timeThresholdSeconds;
    }


    public SimpleTimerThreshold(int timeThresholdSeconds, Context context, String referenceName) {
        _timeoutThresholdSeconds = timeThresholdSeconds;
        _preferences = context.getSharedPreferences("SimpleTimerThreshold", 0);
        _referenceName = referenceName;
        Long dateLong = _preferences.getLong(_referenceName, 0);
        if (dateLong != 0)
        {
            _timeoutDate = new Date(dateLong);
        }
    }

    public void restartTimer() {

        // Get a refrence to the Calendar singleton
        Calendar calendar = Calendar.getInstance();

        // Record the current time
        calendar.setTime(new Date());

        // Add on X seconds
        calendar.add(Calendar.SECOND, _timeoutThresholdSeconds);

        // Save the expire time
        _timeoutDate = calendar.getTime();

        // If the cache is set up save the expire date to the cache
        if (_preferences != null && _referenceName != null)
        {
            _preferences.edit().putLong(_referenceName, _timeoutDate.getTime());
        }

        //Log.v(TAG, "TimeOutDate:" +_timeOutDate.toString());
    }

    public boolean hasTimerPassedThreshold() {
        if (_timeoutDate == null) return false;
        // Get the current time
        Date now = new Date();
        //Log.v(TAG, "Now:" + now.toString());
        //Log.v(TAG, "TimeOutDate:" + _timeOutDate.toString());
        // Compare it to the timeout time
        return (now.after(_timeoutDate));
    }

}
