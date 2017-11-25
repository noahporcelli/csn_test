package njdsoftware.app_functional.Screens.TimeTrial;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Class containing data relating to a route.
 */

public class RouteInfo {
    public long routeId;
    public String routeName;
    public RoutePoint startPoint = new RoutePoint();
    public RoutePoint endPoint = new RoutePoint();
    public List<RoutePoint> checkpoints = new ArrayList<>();
    public long routeRecordSeconds, routeDistanceMeters;
    public List<RoutePoint> allPoints = new ArrayList<>();
    public CameraPosition optimumCameraPosition;

    public RouteInfo(){
        startPoint.type = InRaceController.ROUTE_POINT_TYPE_START;
        endPoint.type = InRaceController.ROUTE_POINT_TYPE_END;
    }
    public void makeAllPointsArray(){
        allPoints.add(startPoint);
        for (int i=0; i<checkpoints.size(); i++){
            allPoints.add(checkpoints.get(i));
        }
        allPoints.add(endPoint);
    }
}
