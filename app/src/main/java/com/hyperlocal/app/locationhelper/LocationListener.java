package com.hyperlocal.app.locationhelper;

import com.google.android.gms.location.LocationResult;

/**
 * @author ${Umesh} on 12-07-2018.
 */

public interface LocationListener {
    void onLocationUpdated(LocationResult locationResult);
}
