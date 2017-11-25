package njdsoftware.app_functional.Screens.TimeTrial;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Nathan on 12/09/2016.
 */
public class RoutePoint {
    LatLng point;
    float distanceRem;
    int type;

    public RoutePoint(LatLng latLng, float distanceRem, int type)
    {
        this.point = latLng;
        this.distanceRem = distanceRem;
        this.type = type;
    }
    public RoutePoint(){

    }
}
