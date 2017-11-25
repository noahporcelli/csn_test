package njdsoftware.app_functional.Screens.TimeTrial;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Polyline;

import njdsoftware.app_functional.Screens.TimeTrial.RouteInfo;
import njdsoftware.app_functional.Screens.TimeTrial.TimeTrialScreenFragment;

/**
 * Class containing data relating to a route.
 */

public class RouteItem {
    public RouteInfo routeInfo;
    public TimeTrialScreenFragment.ClusterStartMarkerItem startMarker;
    public TimeTrialScreenFragment.EndMarkerItem endMarker;
    public TimeTrialScreenFragment.CheckpointMarkerItemList checkpointMarkerItemList;
    public Polyline suggestedRoutePolyline;
    public boolean visibleOnMap;

    public RouteItem(RouteInfo routeInfo){
        this.routeInfo = routeInfo;
    }
}
