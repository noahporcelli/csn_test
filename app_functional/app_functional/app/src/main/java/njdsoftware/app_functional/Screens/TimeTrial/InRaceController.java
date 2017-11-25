package njdsoftware.app_functional.Screens.TimeTrial;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.media.Image;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import njdsoftware.app_functional.CustomWidgets.CustomFontTextView;
import njdsoftware.app_functional.MainActivity;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.UniversalUtilities.AppAnimation;
import njdsoftware.app_functional.UniversalUtilities.ToolbarController;
import njdsoftware.app_functional.UniversalUtilities.UsefulFunctions;
import njdsoftware.app_functional.UniversalUtilities.LocationManager;

/**
 * Created by Nathan on 10/09/2016.
 */
public class InRaceController {
    private static LatLng userCurrentLocation;
    public static RouteItem route;
    public static GoogleMap googleMap;
    private static int displayState, routeState;
    private static int mapDisplayType;
    private static int screenWidth, screenHeight;
    private static CameraPosition fullRouteBaseCameraPosition;
    private static ScheduledExecutorService raceUpdates, raceCountdown, raceTimer;
    private static int lastCheckpointRoutePos, nextCheckpointRoutePos;
    private static float distanceRemaining;
    private static RoutePoint nextSignificantPoint;
    private static int checkpointsRemaining;
    private static int countdownLength;
    private static long raceTime;

    private static boolean isStartButtonAnimating, isStopButtonAnimating;

    private static boolean isUserMovingMap;
    private static CameraPosition preUserMoveCp;

    private static View inRaceContainer, inRaceFooter;
    private static ImageButton cancelRaceBtn;
    private static View inRaceStartFooter, inRaceRunningFooter, inRaceStopFooter, inRaceCountdownFooter;
    private static TextView inRaceInstruction;
    private static CustomFontTextView distanceRemainingText, checkpointsRemainingText, raceTimeText;
    private static ImageButton startRouteBtn, stopRouteBtn;
    private static ProgressBar startProgressBar, stopProgressBar;

    private static CustomFontTextView countdownText, countdownLengthText;

    private static final int LOCATION_ACCURACY_RADIUS = 20;
    private static final int ROUTE_STATE_MOVE_TO_START = 1;
    private static final int ROUTE_STATE_RACE_START = 2;
    private static final int ROUTE_STATE_RACE_RUNNING = 3;
    private static final int ROUTE_STATE_RACE_COUNTDOWN = 4;
    private static final int DISPLAY_STATE_MOVE_TO_START = 1;
    private static final int DISPLAY_STATE_RACE_START = 2;
    private static final int DISPLAY_STATE_RACE_RUNNING = 3;
    private static final int DISPLAY_STATE_RACE_CANCEL = 4;
    private static final int DISPLAY_STATE_RACE_COUNTDOWN = 5;
    private static final int MAP_DISPLAY_TYPE_FULL_ROUTE = 1;
    private static final int MAP_DISPLAY_TYPE_TRACKING = 2;
    private static final int MAP_DISPLAY_TYPE_CUSTOM = 3;
    private static final int DESIRED_MAP_PADDING = UsefulFunctions.dpToPx(8);
    private static final int DESIRED_MAP_TOP_PADDING = UsefulFunctions.dpToPx(64);
    private static final int DESIRED_MAP_BOTTOM_PADDING = UsefulFunctions.dpToPx(172);
    private static final int UPDATE_PERIOD = 1000;
    public static final int ROUTE_POINT_TYPE_START = 1;
    public static final int ROUTE_POINT_TYPE_END = 2;
    public static final int ROUTE_POINT_TYPE_CHECKPOINT = 3;

    public static void startRaceMode(RouteItem route, GoogleMap googleMap){
        InRaceController.route = route;
        InRaceController.googleMap = googleMap;
        getHandles();
        setClickListeners();
        setMapListeners();
        loadInRaceDisplay();
        getCurrentPosition();
        setInitialRouteState();
        setInitialDisplayState();
        setInitialMapDisplay();
        startRaceModeUpdates();
    }
    private static void getHandles(){
        inRaceContainer = ((Activity)MainActivity.getContext()).findViewById(R.id.inRaceOverlay);
        inRaceFooter = inRaceContainer.findViewById(R.id.inRaceFooter);
        cancelRaceBtn = (ImageButton) inRaceContainer.findViewById(R.id.cancelRaceBtn);
        inRaceStartFooter = inRaceFooter.findViewById(R.id.inRaceStartFooter);
        inRaceRunningFooter = inRaceFooter.findViewById(R.id.inRaceRunningFooter);
        inRaceStopFooter = inRaceFooter.findViewById(R.id.inRaceStopFooter);
        inRaceCountdownFooter = inRaceFooter.findViewById(R.id.inRaceCountdownFooter);
        inRaceInstruction = (TextView) inRaceFooter.findViewById(R.id.inRaceInstruction);
        raceTimeText = (CustomFontTextView) inRaceRunningFooter.findViewById(R.id.raceTimeText);
        distanceRemainingText = (CustomFontTextView) inRaceRunningFooter.findViewById(R.id.distanceRemainingText);
        checkpointsRemainingText = (CustomFontTextView) inRaceRunningFooter.findViewById(R.id.checkpointsRemainingText);
        startRouteBtn = (ImageButton) inRaceStartFooter.findViewById(R.id.inRouteStartBtn);
        stopRouteBtn = (ImageButton) inRaceStopFooter.findViewById(R.id.inRouteStopBtn);
        startProgressBar = (ProgressBar) inRaceStartFooter.findViewById(R.id.inRaceStartProgress);
        stopProgressBar = (ProgressBar) inRaceStopFooter.findViewById(R.id.inRaceStopProgress);
        countdownText = (CustomFontTextView) inRaceCountdownFooter.findViewById(R.id.inRaceCountdownText);
        countdownLengthText = (CustomFontTextView) inRaceCountdownFooter.findViewById(R.id.countdownLengthText);
    }
    private static void setClickListeners(){
        startRouteBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        startStartButtonAnimation();
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        clearStartButtonAnimation();
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        v.setPressed(false);
                        clearStartButtonAnimation();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        v.setPressed(false);
                        clearStartButtonAnimation();
                        break;
                }
                return true;    //note true consumes event.
            }
        });
        stopRouteBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        startStopButtonAnimation();
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        clearStopButtonAnimation();
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        v.setPressed(false);
                        clearStopButtonAnimation();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        v.setPressed(false);
                        clearStopButtonAnimation();
                        break;
                }
                return true;    //note true consumes event.
            }
        });
        cancelRaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (routeState == ROUTE_STATE_RACE_COUNTDOWN){
                    stopCountdown();
                    setRouteState(ROUTE_STATE_RACE_START);
                    indicateCountdownCancelled();
                }
                setDisplayState(DISPLAY_STATE_RACE_CANCEL);
            }
        });
    }
    private static void setMapListeners(){
        isUserMovingMap = false;
        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    //The user gestured on the map.
                    isUserMovingMap = true;
                    preUserMoveCp = googleMap.getCameraPosition();
                }
            }
        });
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (isUserMovingMap == true){
                    if (!isWithinDistance(preUserMoveCp.target, googleMap.getCameraPosition().target, 5)){
                        mapPanEvent();
                    }
                    if (UsefulFunctions.areDecimalsEqual(preUserMoveCp.zoom, googleMap.getCameraPosition().zoom, 0.01f)){
                        mapZoomEvent();
                    }
                    if (UsefulFunctions.areDecimalsEqual(preUserMoveCp.tilt, googleMap.getCameraPosition().tilt, 0.01f)){
                        mapTiltEvent();
                    }
                    isUserMovingMap = false;
                }
            }
        });
    }
    private static void loadInRaceDisplay(){
        setBlankFooter();
        cancelRaceBtn.setVisibility(View.VISIBLE);
        inRaceContainer.setVisibility(View.VISIBLE);
    }
    private static void setBlankFooter(){
        inRaceStartFooter.setVisibility(View.INVISIBLE);
        inRaceInstruction.setVisibility(View.INVISIBLE);
        inRaceRunningFooter.setVisibility(View.GONE);
        inRaceStopFooter.setVisibility(View.GONE);
        inRaceCountdownFooter.setVisibility(View.GONE);
    }
    private static void getCurrentPosition(){
        userCurrentLocation = LocationManager.getCurrentLocation();
    }
    private static void setInitialRouteState(){
        if (isWithinDistance(userCurrentLocation, route.routeInfo.startPoint.point, LOCATION_ACCURACY_RADIUS)){
            setRouteState(ROUTE_STATE_RACE_START);
        }else{
            setRouteState(ROUTE_STATE_MOVE_TO_START);
        }
    }
    private static void setRouteState(int stateType){
        routeState = stateType;
    }
    private static void setInitialDisplayState(){
        if (routeState == ROUTE_STATE_RACE_START){
            setDisplayState(DISPLAY_STATE_RACE_START);
        }else{
            setDisplayState(DISPLAY_STATE_MOVE_TO_START);
        }
    }
    private static void setDisplayState(int stateType){
        removeCurrentDisplayState();
        displayState = stateType;
        if (stateType == DISPLAY_STATE_MOVE_TO_START){
            inRaceStartFooter.setVisibility(View.INVISIBLE);
            inRaceInstruction.setText("Move to start location.");
            inRaceInstruction.setVisibility(View.VISIBLE);
            cancelRaceBtn.setVisibility(View.VISIBLE);
        }
        if (stateType == DISPLAY_STATE_RACE_START){
            inRaceStartFooter.setVisibility(View.VISIBLE);
            inRaceInstruction.setText("Hold to start race countdown.");
            inRaceInstruction.setVisibility(View.VISIBLE);
            cancelRaceBtn.setVisibility(View.VISIBLE);
        }
        if (stateType == DISPLAY_STATE_RACE_RUNNING){
            inRaceRunningFooter.setVisibility(View.VISIBLE);
            if (route.routeInfo.checkpoints.size() > 0) {
                inRaceInstruction.setText("Move to next checkpoint.");
            }else{
                inRaceInstruction.setText("Move to finish location.");
            }
            inRaceInstruction.setVisibility(View.VISIBLE);
            cancelRaceBtn.setVisibility(View.VISIBLE);
        }
        if (stateType == DISPLAY_STATE_RACE_CANCEL){
            inRaceStopFooter.setVisibility(View.VISIBLE);
            inRaceInstruction.setText("Hold to abandon route.");
            inRaceInstruction.setVisibility(View.VISIBLE);
            cancelRaceBtn.setVisibility(View.INVISIBLE);
        }
        if (stateType == DISPLAY_STATE_RACE_COUNTDOWN){
            inRaceCountdownFooter.setVisibility(View.VISIBLE);
            inRaceInstruction.setText("Time trial about to begin.");
            inRaceInstruction.setVisibility(View.VISIBLE);
            countdownText.setVisibility(View.INVISIBLE);
            cancelRaceBtn.setVisibility(View.VISIBLE);
        }
    }
    private static void removeCurrentDisplayState(){
        inRaceStartFooter.setVisibility(View.GONE);
        inRaceInstruction.setVisibility(View.INVISIBLE);
        inRaceRunningFooter.setVisibility(View.GONE);
        inRaceStopFooter.setVisibility(View.GONE);
        inRaceCountdownFooter.setVisibility(View.GONE);
        clearStartButtonAnimation();
        clearStopButtonAnimation();
    }
    private static boolean isWithinDistance(LatLng location1, LatLng location2, int accuracy){
        if (RouteDisplayAlgorithm.getDistance(location1, location2) <= (float)accuracy){
            return true;
        }else{
            return false;
        }
    }

    private static void startStartButtonAnimation(){
        ObjectAnimator startAnim = AppAnimation.startCircularProgressBarAnimation(startProgressBar, 2000, 500);
        startAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                startRaceCountdown();
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }
    private static void startStopButtonAnimation(){
        ObjectAnimator stopAnim = AppAnimation.startCircularProgressBarAnimation(stopProgressBar, 2000, 500);
        stopAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                stopRace();
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }
    private static void clearStartButtonAnimation(){
        isStartButtonAnimating = false;
        startProgressBar.clearAnimation();
        startProgressBar.setProgress(0);
    }
    private static void clearStopButtonAnimation(){
        isStopButtonAnimating = false;
        stopProgressBar.clearAnimation();
        stopProgressBar.setProgress(0);
    }

    private static void startRaceCountdown(){
        setRouteState(ROUTE_STATE_RACE_COUNTDOWN);
        setDisplayState(DISPLAY_STATE_RACE_COUNTDOWN);
        countdownLength = getCountdownLength();
        startCountdownTimer();
    }
    private static int getCountdownLength(){
        String valueWithS = String.valueOf(countdownLengthText.getText());
        String value = valueWithS.substring(0, valueWithS.length()-1);
        return Integer.parseInt(value);
    }
    private static void startCountdownTimer(){
        Runnable runnable = new Runnable() {
            public void run() {
                decrementRaceCountdown();
            }
        };
        raceCountdown = Executors.newScheduledThreadPool(1);
        raceCountdown.scheduleAtFixedRate(runnable, 925, 1000, TimeUnit.MILLISECONDS);
        initialCountdownFadeIn();
    }
    private static void initialCountdownFadeIn(){
        countdownText.setText(String.valueOf(countdownLength));
        AppAnimation.fadeIn(countdownText, 0, 75);
    }
    private static void decrementRaceCountdown(){
        AppAnimation.fadeOut(countdownText, 0, 75);
        //then after 75ms start fading new decremented values in again.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countdownLength = countdownLength - 1;
                if (countdownLength == 0){
                    stopCountdown();
                    startRace();
                }else {
                    countdownText.setText(String.valueOf(countdownLength));
                    AppAnimation.fadeIn(countdownText, 0, 75);
                }
            }
        }, 75);
    }
    private static void stopCountdown(){
        raceCountdown.shutdown();
    }
    private static void indicateCountdownCancelled(){

    }
    private static void indicateFalseStart(){

    }

    private static void stopRace(){
        raceTimer.shutdown();
        raceUpdates.shutdown();
        exitRaceMode();
    }
    private static void exitRaceMode(){
        TimeTrialScreenFragment.hideSelectedRouteFromMap();
        TimeTrialScreenFragment.unsetSelectedRoute();
        TimeTrialScreenFragment.setupMapIdleListener();
        hideRaceModeDisplay();
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                TimeTrialScreenFragment.restoreMapStartPoints();
                ToolbarController.showCreateRouteBtn();
            }
        }, 200);
    }
    private static void hideRaceModeDisplay(){
        setBlankFooter();
        cancelRaceBtn.setVisibility(View.GONE);
        inRaceContainer.setVisibility(View.GONE);
    }

    private static void startRaceModeUpdates(){
        Runnable runnable = new Runnable() {
            public void run() {
                updateLocationRouteMap();
            }
        };
        raceUpdates = Executors.newScheduledThreadPool(1);
        raceUpdates.scheduleAtFixedRate(runnable, UPDATE_PERIOD, UPDATE_PERIOD, TimeUnit.MILLISECONDS);
    }
    private static void updateLocationRouteMap(){
        getCurrentPosition();
        checkForRouteStateUpdates();
        checkForMapUpdates();
    }
    private static void checkForRouteStateUpdates(){
        if (routeState == ROUTE_STATE_MOVE_TO_START){
            if (atStartPoint()){
                setRouteState(ROUTE_STATE_RACE_START);
                if (displayState != DISPLAY_STATE_RACE_CANCEL){
                    setDisplayState(DISPLAY_STATE_RACE_START);
                }
            }
        }
        if (routeState == ROUTE_STATE_RACE_START){
            if (!(atStartPoint())){
                setRouteState(ROUTE_STATE_MOVE_TO_START);
                if (displayState != DISPLAY_STATE_RACE_CANCEL){
                    setDisplayState(DISPLAY_STATE_MOVE_TO_START);
                }
            }
        }
        if (routeState == ROUTE_STATE_RACE_COUNTDOWN){
            if (!(atStartPoint())){
                setRouteState(ROUTE_STATE_MOVE_TO_START);
                if (displayState != DISPLAY_STATE_RACE_CANCEL){
                    setDisplayState(DISPLAY_STATE_MOVE_TO_START);
                }
                indicateFalseStart();
            }
        }
        if (routeState == ROUTE_STATE_RACE_RUNNING){
            routeInProgressUpdate();
        }
    }
    private static boolean atStartPoint(){
        if (isWithinDistance(userCurrentLocation, route.routeInfo.startPoint.point, LOCATION_ACCURACY_RADIUS)){
            return true;
        }else{
            return false;
        }
    }
    private static void routeInProgressUpdate(){
        updateRemainingDistance();
        if (atNextSignificantPoint()){
            if (nextSignificantPoint == route.routeInfo.endPoint){
                raceComplete();
            }else{
                checkpointReached();
            }
        }
    }
    private static void updateRemainingDistance(){
        float newDistance = UsefulFunctions.round(getDistanceRemaining(),2);
        if (newDistance != UsefulFunctions.round(distanceRemaining, 2)){
            distanceRemaining = newDistance;
            distanceRemainingText.setText(String.valueOf(newDistance));
        }
    }
    private static float getDistanceRemaining(){
        float distance;
        RoutePoint nearestPoint = findNearestRoutePoint();
        distance = RouteDisplayAlgorithm.getDistance(userCurrentLocation, nearestPoint.point) + nearestPoint.distanceRem;
        return distance;
    }
    private static RoutePoint findNearestRoutePoint(){
        int nearestDistRoutePos = lastCheckpointRoutePos;
        float nearestDistance = RouteDisplayAlgorithm.getDistance(userCurrentLocation ,route.routeInfo.allPoints.get(nearestDistRoutePos).point);
        for (int i=lastCheckpointRoutePos+1; i<nextCheckpointRoutePos; i++){
            float distance = RouteDisplayAlgorithm.getDistance(userCurrentLocation ,route.routeInfo.allPoints.get(i).point);
            if (distance <= nearestDistance){
                nearestDistance = distance;
                nearestDistRoutePos = i;
            }
        }
        return route.routeInfo.allPoints.get(nearestDistRoutePos);
    }
    private static void checkpointReached(){
        nextCheckpointRoutePos = findNextCheckpointRoutePos();
        nextSignificantPoint = route.routeInfo.allPoints.get(nextCheckpointRoutePos);
        if (nextSignificantPoint == route.routeInfo.endPoint){
            setRouteInProgressInstruction("Move to finish point.");
        }
        checkpointsRemaining = checkpointsRemaining - 1;
        checkpointsRemainingText.setText(String.valueOf(checkpointsRemaining));
    }
    private static void setRouteInProgressInstruction(String instruction){
        if (displayState != DISPLAY_STATE_RACE_CANCEL){
            inRaceInstruction.setText(instruction);
        }
    }
    private static void raceComplete(){

    }
    private static boolean atNextSignificantPoint(){
        if (isWithinDistance(userCurrentLocation, nextSignificantPoint.point, LOCATION_ACCURACY_RADIUS)){
            return true;
        }else{
            return false;
        }
    }
    private static void checkForMapUpdates(){
        if (mapDisplayType == MAP_DISPLAY_TYPE_FULL_ROUTE){
            updateFullRouteMap();
        }
        if (mapDisplayType == MAP_DISPLAY_TYPE_TRACKING){
            updateTrackingMap();
        }
    }
    private static void updateFullRouteMap(){
        if (isMapTooSmallForUsersLocation()){
            zoomOutToFitUsersLocation();
        }else{
            if (!(isZoomOptimal())){
                zoomInOnRouteWithLocation();
            }
        }
    }
    private static void updateTrackingMap(){
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(userCurrentLocation));
    }

    private static void startRace(){
        initInRouteParameters();
        setRouteState(ROUTE_STATE_RACE_RUNNING);
        setDisplayState(DISPLAY_STATE_RACE_RUNNING);
        startRaceTimer();
        routeInProgressUpdate();
    }
    private static void initInRouteParameters(){
        lastCheckpointRoutePos = 0;
        nextCheckpointRoutePos = findNextCheckpointRoutePos();
        distanceRemaining = route.routeInfo.startPoint.distanceRem;
        nextSignificantPoint = route.routeInfo.allPoints.get(nextCheckpointRoutePos);
        checkpointsRemaining = route.routeInfo.checkpoints.size();
    }
    private static void startRaceTimer(){
        setInitialRaceTime();
        Runnable runnable = new Runnable() {
            public void run() {
                incrementRaceTimer();
            }
        };
        raceTimer = Executors.newScheduledThreadPool(1);
        raceTimer.scheduleAtFixedRate(runnable, 0, 1000, TimeUnit.MILLISECONDS);
    }
    private static void setInitialRaceTime(){
        raceTime = 0;
        raceTimeText.setText(UsefulFunctions.secondsToTime(raceTime));
    }
    private static void incrementRaceTimer(){
        raceTime = raceTime + 1;
        raceTimeText.setText(UsefulFunctions.secondsToTime(raceTime));
    }

    private static void mapPanEvent(){
        if (mapDisplayType == MAP_DISPLAY_TYPE_FULL_ROUTE){
            setMapDisplayType(MAP_DISPLAY_TYPE_CUSTOM);
        }
        if (mapDisplayType == MAP_DISPLAY_TYPE_TRACKING){
            setMapDisplayType(MAP_DISPLAY_TYPE_CUSTOM);
        }
    }
    private static void mapZoomEvent(){
        if (mapDisplayType == MAP_DISPLAY_TYPE_FULL_ROUTE){
            setMapDisplayType(MAP_DISPLAY_TYPE_CUSTOM);
        }
    }
    private static void mapTiltEvent(){
        if (mapDisplayType == MAP_DISPLAY_TYPE_FULL_ROUTE){
            setMapDisplayType(MAP_DISPLAY_TYPE_CUSTOM);
        }
    }

    private static void setMapDisplayType(int displayType){
        removeCurrentMapDisplayType();
        if (displayType == MAP_DISPLAY_TYPE_FULL_ROUTE){
            setMapDisplayFullRoute();
        }
        if (displayType == MAP_DISPLAY_TYPE_TRACKING){
            setMapDisplayTracking();
        }
        if (displayType == MAP_DISPLAY_TYPE_CUSTOM){
            mapDisplayType = MAP_DISPLAY_TYPE_CUSTOM;
        }
    }
    private static void removeCurrentMapDisplayType(){
        if (mapDisplayType == MAP_DISPLAY_TYPE_FULL_ROUTE){
            disactivateFullRouteButton();
        }
        if (mapDisplayType == MAP_DISPLAY_TYPE_TRACKING){
            disactivateTrackingButton();
        }
    }
    private static void setMapDisplayFullRoute(){
        activateFullRouteButton();
        mapDisplayType = MAP_DISPLAY_TYPE_FULL_ROUTE;
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(fullRouteBaseCameraPosition));
        if (isMapTooSmallForUsersLocation() == true) {
            zoomOutToFitUsersLocation();
        }
    }
    private static void setMapDisplayTracking(){
        activateTrackingButton();
        mapDisplayType = MAP_DISPLAY_TYPE_TRACKING;
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(userCurrentLocation));
    }
    private static void activateFullRouteButton(){

    }
    private static void disactivateFullRouteButton(){

    }
    private static void activateTrackingButton(){

    }
    private static void disactivateTrackingButton(){

    }

    private static int findNextCheckpointRoutePos(){
        for (int i=lastCheckpointRoutePos; i<route.routeInfo.allPoints.size(); i++){
            if ((route.routeInfo.allPoints.get(i).type == ROUTE_POINT_TYPE_CHECKPOINT) || (route.routeInfo.allPoints.get(i).type == ROUTE_POINT_TYPE_END)){
                return i;
            }
        }
        //shouldn't reach here, but if does:
        return lastCheckpointRoutePos;
    }

    private static void setInitialMapDisplay(){
        mapDisplayType = MAP_DISPLAY_TYPE_FULL_ROUTE;
        getMapVariables();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(route.routeInfo.optimumCameraPosition));
        if (isMapTooSmallForBaseCameraPosition() == true){
            zoomOutToFitRoute();
        }
        fullRouteBaseCameraPosition = googleMap.getCameraPosition();
        if (isMapTooSmallForUsersLocation() == true) {
            zoomOutToFitUsersLocation();
        }
    }
    private static void getMapVariables(){
        getDisplayParameters();
    }
    private static void getDisplayParameters(){
        View container = (View)((Activity)MainActivity.getContext()).findViewById(R.id.time_trial_fragment);
        screenWidth = container.getWidth();
        screenHeight = container.getHeight();
    }

    private static boolean isMapTooSmallForBaseCameraPosition(){
        //Base camera position: the camera position when the full route is shown on screen with maximum magnification.
        //In full-route mode, this camera position is only altered by zooming out if needs to in order to show user's
        //position.
        for (int i=0; i<route.routeInfo.allPoints.size(); i++){
            LatLng currentPoint = route.routeInfo.allPoints.get(i).point;
            if (notInVisibleArea(currentPoint)){
                return true;
            }
        }
        return false;
    }
    private static boolean isMapTooSmallForUsersLocation(){
        if (notInVisibleArea(userCurrentLocation)){
            return true;
        }else{
            return false;
        }
    }
    private static boolean notInVisibleArea(LatLng point){
        Point screenPoint = googleMap.getProjection().toScreenLocation(point);
        //first test if on whole screen.
        if (screenPoint.x < DESIRED_MAP_PADDING){
            return true;
        }else if (screenPoint.x > screenWidth - DESIRED_MAP_PADDING){
            return true;
        }else if (screenPoint.y < DESIRED_MAP_TOP_PADDING){
            return true;
        }else if (screenPoint.y > screenHeight - DESIRED_MAP_BOTTOM_PADDING){
            return true;
        }
        //if reaches here must be visible on screen.
        return false;
    }

    private static void zoomOutToFitRoute(){
        for (int i=0; i<1000; i++){ //basically a do-loop thats not going to crash my computer if it goes wrong.
            incrementalZoomOut();
            if (isMapTooSmallForBaseCameraPosition() == false){
                //all markers are now displayed on screen as desired.
                return;
            }
        }
    }
    private static void zoomOutToFitUsersLocation(){
        for (int i=0; i<1000; i++){ //basically a do-loop thats not going to crash my computer if it goes wrong.
            incrementalZoomOut();
            if (isMapTooSmallForUsersLocation() == false){
                //Then map zoomed out enough.
                return;
            }
        }
    }
    private static void incrementalZoomOut(){
        //zoom in by 0.1 on the google map zoom scale.
        CameraPosition cp = googleMap.getCameraPosition();
        LatLng currentCenter = cp.target;
        float currentZoom = cp.zoom;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentCenter, currentZoom - 0.1f));
    }

    private static void zoomInOnRouteWithLocation(){
        for (int i=0; i<1000; i++){ //basically a do-loop thats not going to crash my computer if it goes wrong.
            incrementalZoomIn();
            if (isMapTooSmallForUsersLocation()){
                //zoom back out to the previously acceptable level then finished.
                incrementalZoomOut();
                return;
            }
            if (isZoomOptimal()){
                return;
            }
        }
    }
    private static void incrementalZoomIn(){
        //zoom in by 0.1 on the google map zoom scale.
        CameraPosition cp = googleMap.getCameraPosition();
        float currentZoom = cp.zoom;
        CameraPosition pos = CameraPosition.builder(cp).tilt(RouteDisplayAlgorithm.DESIRED_TILT).zoom(currentZoom + 0.1f).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
    }
    private static boolean isZoomOptimal(){
        if (googleMap.getCameraPosition().zoom >= fullRouteBaseCameraPosition.zoom - 0.01){ //0.01 for float value error purposes.
            return true;
        }else{
            return false;
        }
    }

}
