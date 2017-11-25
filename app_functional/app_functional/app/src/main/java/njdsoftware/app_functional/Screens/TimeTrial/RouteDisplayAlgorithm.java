package njdsoftware.app_functional.Screens.TimeTrial;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import njdsoftware.app_functional.MainActivity;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.UniversalUtilities.UsefulFunctions;

/**
 * Created by Nathan on 24/08/2016.
 */
public class RouteDisplayAlgorithm {
    private static RouteItem route;
    private static GoogleMap googleMap;
    private static RoutePoint startPoint, endPoint;
    private static List<RoutePoint> pointArray;
    private static float bearingStartEnd;
    private static float distanceStartEnd;
    public static float DESIRED_TILT = 45f;
    private static final int DESIRED_MAP_PADDING = UsefulFunctions.dpToPx(8);
    private static final int DESIRED_MAP_TOP_PADDING = UsefulFunctions.dpToPx(32);
    private static final int DESIRED_MAP_BOTTOM_PADDING = UsefulFunctions.dpToPx(98);
    private static int screenWidth, screenHeight;

    public static CameraPosition findBestDisplaySettings(RouteItem route, final GoogleMap googleMap){
        setupInternalVars(route, googleMap);
        LatLng furthestPoint = determineFurthestPointAlongStartFinishLine();
        aimMapAtFurthestPoint(furthestPoint);
        zoomTilScreenFilledWithRoute(googleMap.getCameraPosition().target);
        /*googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                incrementalZoomIn(googleMap.getCameraPosition().target);
                boolean b = isMapTooSmall();
                int i = 1 + 1;
            }
        });*/
        LatLng criticalPoint = findCriticalPoint(null);
        rotateToOptimalRotation(criticalPoint);
        zoomTilScreenFilledWithRoute(criticalPoint);
        rotateToOptimalRotation(criticalPoint); //iteration provides additional convergence. Necessary since map tilt skews ratio of how fast a point moves to each side of map.
        zoomTilScreenFilledWithRoute(criticalPoint);
        //2 further optimisation iterations - should converge on an optimum display.
        for (int i=0; i<2; i++) {
            criticalPoint = findCriticalPoint(criticalPoint);
            rotateToOptimalRotation(criticalPoint);
            zoomTilScreenFilledWithRoute(criticalPoint);
            rotateToOptimalRotation(criticalPoint);
            zoomTilScreenFilledWithRoute(criticalPoint);
        }
        return googleMap.getCameraPosition();
    }

    private static void setupInternalVars(RouteItem route, GoogleMap googleMap){
        RouteDisplayAlgorithm.route = route;
        RouteDisplayAlgorithm.googleMap = googleMap;
        RouteDisplayAlgorithm.pointArray = route.routeInfo.allPoints;
        RouteDisplayAlgorithm.startPoint = route.routeInfo.startPoint;
        RouteDisplayAlgorithm.endPoint = route.routeInfo.endPoint;

        //display parameters.
        View container = (View)((Activity)MainActivity.getContext()).findViewById(R.id.time_trial_fragment);
        screenWidth = container.getWidth();
        screenHeight = container.getHeight();
    }

    private static LatLng determineFurthestPointAlongStartFinishLine(){
        LatLng furthestPoint = route.routeInfo.endPoint.point;
        getStartFinishStats();
        float furthestDistance = getDistanceAlongStartFinishLine(furthestPoint);
        float thisPointDistance;
        for(int i=0; i<pointArray.size(); i++){
            thisPointDistance = getDistanceAlongStartFinishLine(pointArray.get(i).point);
            if (thisPointDistance > furthestDistance){
                furthestPoint = pointArray.get(i).point;
                furthestDistance = thisPointDistance;
            }
        }
        return furthestPoint;
    }
    private static void getStartFinishStats(){
        bearingStartEnd = getBearing(startPoint.point,endPoint.point);
        distanceStartEnd = getDistance(startPoint.point, endPoint.point);
    }
    private static float getDistanceAlongStartFinishLine(LatLng point){
        float bearingStartPoint = getBearing(startPoint.point, point);
        float bearingDifference;
        if (bearingStartEnd >= bearingStartPoint){
            bearingDifference = bearingStartEnd - bearingStartPoint;
        }else{
            bearingDifference = bearingStartPoint - bearingStartEnd;
        }
        float distanceStartPoint = getDistance(startPoint.point,point);
        float distanceAlongStartFinishLine = distanceStartPoint * (float)Math.cos(Math.toRadians(bearingDifference));
        return distanceAlongStartFinishLine;
    }

    private static void aimMapAtFurthestPoint(LatLng furthestPoint){
        float startToFurthestPointBearing = getBearing(startPoint.point, furthestPoint);
        LatLng startFurthestMidPoint = getMidPoint(startPoint.point, furthestPoint);
        CameraPosition cp = new CameraPosition.Builder()
                .bearing(-startToFurthestPointBearing)
                .zoom(RouteDisplayAlgorithm.googleMap.getCameraPosition().zoom)
                .tilt(DESIRED_TILT)
                .target(startFurthestMidPoint)
                .build();
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cp);
        RouteDisplayAlgorithm.googleMap.moveCamera(cu);
    }

    private static void zoomTilScreenFilledWithRoute(LatLng zoomPoint){
        bugFixMakeSureZoomPointOnMap(zoomPoint);
        Point initScreenPos = googleMap.getProjection().toScreenLocation(zoomPoint);
        if (isMapTooSmall() == true){
            zoomOutToFit(zoomPoint, initScreenPos);
        }else{
            zoomInToFit(zoomPoint, initScreenPos);
        }
    }
    private static boolean isMapTooSmall(){
        for (int i=0; i<pointArray.size(); i++){
            LatLng currentPoint = pointArray.get(i).point;
            if (notInVisibleArea(currentPoint, 0)){
                return true;
            }
        }
        return false;
    }
    private static boolean notInVisibleArea(LatLng point, int safetyMargin){
        Point screenPoint = googleMap.getProjection().toScreenLocation(point);
        //first test if on whole screen.
        if (screenPoint.x < DESIRED_MAP_PADDING + safetyMargin){
            return true;
        }else if (screenPoint.x > screenWidth - DESIRED_MAP_PADDING - safetyMargin){
            return true;
        }else if (screenPoint.y < DESIRED_MAP_TOP_PADDING + safetyMargin){
            return true;
        }else if (screenPoint.y > screenHeight - DESIRED_MAP_BOTTOM_PADDING - safetyMargin){
            return true;
        }
        //then test if behind text.
        if (screenPoint.y < UsefulFunctions.dpToPx(150) + safetyMargin){
            if (screenPoint.x < screenPoint.y + safetyMargin){
                //within text triangle - 'not visible'.
                return true;
            }
        }
        //if reaches here must be visible on screen.
        return false;
    }
    private static void zoomOutToFit(LatLng zoomPoint, Point initScreenPos){
        for (int i=0; i<1000; i++){ //basically a do-loop thats not going to crash my computer if it goes wrong.
            incrementalZoomOut(zoomPoint, initScreenPos);
            if (isMapTooSmall() == false){
                //all markers are now displayed on screen as desired.
                return;
            }
        }
    }
    private static void incrementalZoomOut(LatLng zoomPoint, Point initScreenPos){
        //zoom in by 0.1 on the google map zoom scale.
        CameraPosition cp = googleMap.getCameraPosition();
        float currentZoom = cp.zoom;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomPoint, currentZoom - 0.05f));
        movePointBackToItsScreenPosition(zoomPoint, initScreenPos);
    }
    private static void zoomInToFit(LatLng zoomPoint, Point initScreenPos){
        for (int i=0; i<1000; i++){ //basically a do-loop thats not going to crash my computer if it goes wrong.
            incrementalZoomIn(zoomPoint, initScreenPos);
            if (isMapTooSmall()){
                //zoom back out to the previously acceptable level then finished.
                incrementalZoomOut(zoomPoint, initScreenPos);
                return;
            }
        }
    }
    private static void incrementalZoomIn(LatLng zoomPoint, Point initScreenPos){
        //zoom in by 0.1 on the google map zoom scale.
        CameraPosition cp = googleMap.getCameraPosition();
        float currentZoom = cp.zoom;
        CameraPosition pos = CameraPosition.builder(cp).target(zoomPoint).tilt(DESIRED_TILT).zoom(currentZoom + 0.05f).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        movePointBackToItsScreenPosition(zoomPoint, initScreenPos);
    }
    private static void bugFixMakeSureZoomPointOnMap(LatLng zoomPoint){
        if (notInVisibleArea(zoomPoint, 4)){
            for (int i=0;i<1000; i++) { //essentially a 'do-loop'.
                googleMap.moveCamera(CameraUpdateFactory.zoomBy(-0.02f));
                if (notInVisibleArea(zoomPoint, 4) == false){  //i.e. now point is on visible area of map.
                    return;
                }
            }
        }
    }

    private static LatLng findCriticalPoint(LatLng lastCriticalPoint){
        //returns point closest to screen edge.
        LatLng criticalPoint = lastCriticalPoint; //immediately overwritten but set just to satisfy android studio that a value will always be returned.
        int criticalPointDistanceFromViewBounds = 10000;    //arbitrarily high so gets overwritten in 1st loop.
        for (int i=0; i<pointArray.size(); i++){
            LatLng currentPoint = pointArray.get(i).point;
            if (currentPoint != lastCriticalPoint) {    //want a different critical point to the lastcriticalpoint.
                int distanceFromMapViewBounds = getDistanceFromMapViewBounds(currentPoint);
                if (distanceFromMapViewBounds < criticalPointDistanceFromViewBounds) {
                    criticalPoint = currentPoint;
                    criticalPointDistanceFromViewBounds = distanceFromMapViewBounds;
                }
            }
        }
        return criticalPoint;
    }
    private static int getDistanceFromMapViewBounds(LatLng point){
        Point screenPoint = googleMap.getProjection().toScreenLocation(point);
        //5 boundaries - top,left,right,bottom, and upper left corner text triangle (100pixels isosceles).
        int distToTop = screenPoint.y - DESIRED_MAP_TOP_PADDING;
        int distToBottom = screenHeight - screenPoint.y - DESIRED_MAP_BOTTOM_PADDING;
        int distToLeft = screenPoint.x - DESIRED_MAP_PADDING;
        int distToRight = screenWidth - screenPoint.x - DESIRED_MAP_PADDING;
        int distToTriangle;
        //perpendicular distance to diagonal line of upper left hand triangle.
        //=vertical height to line * sin(45) = y - yline at that x value.
        //yline at that x value = 100 - x.
        //therefore dist to line = (y - (100-x))*sin(45).
        //then include padding.
        final double sin45 = 1/Math.sqrt(2);
        distToTriangle = (int)(((double)(screenPoint.y - (UsefulFunctions.dpToPx(150)-screenPoint.x))) * sin45);
        distToTriangle = distToTriangle - DESIRED_MAP_PADDING;

        //Now determine minimum distance and return it.
        int minDistance = distToTop;
        if (distToBottom < minDistance){
            minDistance = distToBottom;
        }
        if (distToLeft < minDistance){
            minDistance = distToLeft;
        }
        if (distToRight < minDistance){
            minDistance = distToRight;
        }
        if (distToTriangle < minDistance){
            minDistance = distToTriangle;
        }
        return minDistance;
    }

    private static void rotateToOptimalRotation(LatLng rotationPoint){
        Point initScreenPos = googleMap.getProjection().toScreenLocation(rotationPoint);
        rotateCamera(rotationPoint, -90, initScreenPos);
        //sweep through 180 degrees rotation, looking for maximum min distance to visible bounds.
        int maximumMinDistance = 0;
        int rotationAtMaximumMinDistance = 90;
        for (int i=0; i<=180;){
            int secondaryPointMinDistance = findSecondaryCritPointMinDistance(rotationPoint);
            if (secondaryPointMinDistance > maximumMinDistance){
                rotationAtMaximumMinDistance = i;
                maximumMinDistance = secondaryPointMinDistance;
            }
            /*int rotAngle = 5 + findAngleToSkip(rotationPoint, secondaryPointMinDistance);
            rotateCamera(rotationPoint, rotAngle);
            i = i + rotAngle;*/
            rotateCamera(rotationPoint, 5, initScreenPos);
            i = i + 5;
        }
        //Sweep completed. Now rotate to the optimal rotation angle just found.
        rotateCamera(rotationPoint, rotationAtMaximumMinDistance - 185, initScreenPos);
    }
    private static void rotateCamera(LatLng rotationPoint, int clockwiseDegrees, Point initScreenPoint){
        //First of all rotate the map, about the rotation point - note that this will center the rotation point.
        //Then, move the rotation point back to its initial screen location.
        CameraPosition oldPos = googleMap.getCameraPosition();
        CameraPosition pos = CameraPosition.builder(oldPos).target(rotationPoint).bearing(oldPos.bearing - clockwiseDegrees).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        movePointBackToItsScreenPosition(rotationPoint, initScreenPoint);
    }
    private static void movePointBackToItsScreenPosition(LatLng rotPoint, Point initScreenPoint){
        //Rot point to latlng at init screen point as rot point is to point x.
        //Find point x and center it, then rot point will be back in its init screen position.
        LatLng pointAtScreenPos = googleMap.getProjection().fromScreenLocation(initScreenPoint);
        double ptXLat = rotPoint.latitude + (rotPoint.latitude - pointAtScreenPos.latitude);
        double ptXLong = rotPoint.longitude + (rotPoint.longitude - pointAtScreenPos.longitude);
        LatLng pointX = new LatLng(ptXLat, ptXLong);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pointX));
        //pointSlightlyOutBugFix(rotPoint, initScreenPoint);
    }
    private static void pointSlightlyOutBugFix(LatLng rotPoint, Point initScreenPoint){
        //run function again. Will deal with majority of cases, if not then isn't major issue anyway.
        Point rotScreenPos = googleMap.getProjection().toScreenLocation(rotPoint);
        int dx = initScreenPoint.x - rotScreenPos.x;
        int dy = initScreenPoint.y - rotScreenPos.y;
        if ((dx != 0) || (dy != 0)){
            googleMap.moveCamera(CameraUpdateFactory.scrollBy((float)-dx,(float)-dy));
        }
    }
    private static int findSecondaryCritPointMinDistance(LatLng lastCriticalPoint){
        //returns distance from edge of point closest to screen edge.
        int criticalPointDistanceFromViewBounds = 10000;    //arbitrarily high so gets overwritten in 1st loop.
        for (int i=0; i<pointArray.size(); i++){
            LatLng currentPoint = pointArray.get(i).point;
            if (currentPoint != lastCriticalPoint) {    //want a different critical point to the lastcriticalpoint.
                int distanceFromMapViewBounds = getDistanceFromMapViewBounds(currentPoint);
                if (distanceFromMapViewBounds < criticalPointDistanceFromViewBounds) {
                    criticalPointDistanceFromViewBounds = distanceFromMapViewBounds;
                }
            }
        }
        return criticalPointDistanceFromViewBounds;
    }

    private static int findAngleToSkip(LatLng rotPoint, int secondaryPointMinDistance){
        if (secondaryPointMinDistance >= 0){
            //no point is off the screen - no angle to skip.
            return 0;
        }
        Projection pj = googleMap.getProjection();
        Point rotScreenPoint = pj.toScreenLocation(rotPoint);
        Point pointOffScreen = findPointOffScreen(pj);
        if (pointOffScreen == null){
            return 0;
        }
        Point reentryPoint = findReentryPoint(pointOffScreen, rotScreenPoint);
        double angleToSkip = bearingToScreenPoint(rotScreenPoint, reentryPoint) - bearingToScreenPoint(rotScreenPoint, pointOffScreen);
        if (angleToSkip < 5){
            return 0;
        }else {
            return (int) Math.ceil(angleToSkip) - 5;
        }
    }
    private static Point findPointOffScreen(Projection pj){
        for (int i=0; i<pointArray.size(); i++){
            LatLng currentPoint = pointArray.get(i).point;
            if (notInVisibleArea(currentPoint, 0)){
                return pj.toScreenLocation(currentPoint);
            }
        }
        //if reaches here, all points on screen.
        return null;
    }
    private static Point findReentryPoint(Point pointOffScreen, Point rotPoint){
        int reentryX, reentryY;
        int distBetweenPoints = distanceToPoint(rotPoint, pointOffScreen);
        if (pointOffScreenToLeft(pointOffScreen)){
            reentryX = 0;
            int edgeDistance = rotPoint.x;
            reentryY = rotPoint.y - (int)(Math.sqrt(distBetweenPoints * distBetweenPoints - edgeDistance * edgeDistance));
        }else if (pointOffScreenToTop(pointOffScreen)){
            reentryY = 0;
            int edgeDistance = rotPoint.y;
            reentryX = rotPoint.x + (int)(Math.sqrt(distBetweenPoints * distBetweenPoints - edgeDistance * edgeDistance));
        }
        else if (pointOffScreenToRight(pointOffScreen)){
            reentryX = screenWidth;
            int edgeDistance = screenWidth - rotPoint.x;
            reentryY = rotPoint.y + (int)(Math.sqrt(distBetweenPoints * distBetweenPoints - edgeDistance * edgeDistance));
        }
        else if (pointOffScreenToBottom(pointOffScreen)){
            reentryY = screenHeight;
            int edgeDistance = screenHeight - rotPoint.y;
            reentryX = rotPoint.x - (int)(Math.sqrt(distBetweenPoints * distBetweenPoints - edgeDistance * edgeDistance));
        }else{
            //Will be error - to satisfy android studio just set reentry point = current point so doesn't mess up anything.
            reentryX = pointOffScreen.x;
            reentryY = pointOffScreen.y;
        }
        return new Point(reentryX, reentryY);
    }
    private static boolean pointOffScreenToLeft(Point p){
        if (p.x < 0){
            return true;
        }else{
            return false;
        }
    }
    private static boolean pointOffScreenToTop(Point p){
        if (p.y < 0){
            return true;
        }else{
            return false;
        }
    }
    private static boolean pointOffScreenToRight(Point p){
        if (p.x > screenWidth){
            return true;
        }else{
            return false;
        }
    }
    private static boolean pointOffScreenToBottom(Point p){
        if (p.y > screenHeight){
            return true;
        }else{
            return false;
        }
    }
    private static int distanceToPoint(Point p1, Point p2){
        return (int)Math.sqrt(((p1.x - p2.x) * (p1.x - p2.x)) + ((p1.y - p2.y) * (p1.y - p2.y)));
    }
    private static double bearingToScreenPoint(Point p1, Point p2){
        double angle = (double) Math.toDegrees(Math.atan2(p2.y - p1.y, p2.x - p1.x));
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

    private static LatLng getMidPoint(LatLng point1, LatLng point2){
        double midlat = (point1.latitude + point2.latitude)/2;
        double midlng = (point1.longitude + point2.longitude)/2;
        LatLng midPoint = new LatLng(midlat, midlng);
        return midPoint;
    }
    private static float getBearing(LatLng point1, LatLng point2){
        double dLon = (point2.longitude - point1.longitude);
        double y = Math.sin(Math.toRadians(dLon)) * Math.cos(Math.toRadians(point2.latitude));
        double x = Math.cos(Math.toRadians(point1.latitude))*Math.sin(Math.toRadians(point2.latitude)) - Math.sin(Math.toRadians(point1.latitude))*Math.cos(Math.toRadians(point2.latitude))*Math.cos(Math.toRadians(dLon));
        float bearing = (float)Math.toDegrees((Math.atan2(y, x)));
        bearing = (360 - ((bearing + 360) % 360));
        return bearing;
    }
    public static float getDistance(LatLng point1, LatLng point2){
        double lat1 = point1.latitude;
        double lat2 = point2.latitude;
        double lon1 = point1.longitude;
        double lon2 = point2.longitude;
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.609344 * 1000; //in meters.
        return (float)dist;
    }

    /*private static void drawPaddingPlz(){
        Projection pj = googleMap.getProjection();
        LatLng botleft = pj.fromScreenLocation(new Point(DESIRED_MAP_PADDING, screenHeight - DESIRED_MAP_BOTTOM_PADDING));
        LatLng midleft = pj.fromScreenLocation(new Point(DESIRED_MAP_PADDING, UsefulFunctions.dpToPx(150)));
        LatLng midtop = pj.fromScreenLocation(new Point(UsefulFunctions.dpToPx(150), DESIRED_MAP_TOP_PADDING));
        LatLng topRight = pj.fromScreenLocation(new Point(screenWidth - DESIRED_MAP_PADDING, DESIRED_MAP_TOP_PADDING));
        LatLng botRight = pj.fromScreenLocation(new Point(screenWidth - DESIRED_MAP_PADDING, screenHeight - DESIRED_MAP_BOTTOM_PADDING));
        googleMap.addPolygon(new PolygonOptions().fillColor(140).add(botleft, midleft, midtop, topRight, botRight));
    }*/
}
