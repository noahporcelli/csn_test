package njdsoftware.app_functional.Screens.TimeTrial;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import njdsoftware.app_functional.CustomWidgets.TextButton;
import njdsoftware.app_functional.MainActivity;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.UniversalUtilities.LocationManager;
import njdsoftware.app_functional.UniversalUtilities.ScreenManager;
import njdsoftware.app_functional.UniversalUtilities.ServerInterface;
import njdsoftware.app_functional.UniversalUtilities.ToolbarController;

/**
 * Created by Nathan on 21/07/2016.
 */
public class TimeTrialScreenFragment extends Fragment implements OnMapReadyCallback {
    static View thisFragmentView;
    MapFragment mapFragment;
    static GoogleMap googleMap;
    static ClusterManager<ClusterStartMarkerItem> clusterManager;
    static LatLng currentLocation;
    static LatLngBounds currentBounds;
    static boolean searchForNewRoutesOnMap;
    static List<RouteItem> routesOnMap = new ArrayList<>();
    static RouteItem selectedRoute;
    static CameraPosition prePreviewCp;
    static final long OVERLAY_COLOR = 0x80000000; //Black 30% overlay. Dark enough for contrast with map markers, but light enough so can still see roads when brightness down.
    static final int DEFAULT_ZOOM_LEVEL = 13;
    static final int DEFAULT_MARKER_CIRCLE_RADIUS = 20;
    static final int MAP_MODE_NORMAL = 1;
    static final int MAP_MODE_START_POINT = 2;
    static final int MAP_MODE_FINISH_POINT = 3;
    static final int MAP_MODE_CHECKPOINT = 4;
    static final int CHECKPOINT_NOT_SELECTED = 1;
    static final int CHECKPOINT_SELECTED = 2;
    static final int CHECKPOINT_NOT_VISIBLE = 3;
    static final int CHECKPOINT_ADD = 4;

    public TimeTrialScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisFragmentView = inflater.inflate(R.layout.screens_timetrial_time_trial_fragment, container, false);
        LocationManager.startLocationServices();
        startLoadingMap();
        return thisFragmentView;
    }

    private void startLoadingMap(){
        mapFragment = MapFragment.newInstance();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mapFragment, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setupMapConfig();
        updateCurrentLocation();
        moveCameraToCurrentLocation(DEFAULT_ZOOM_LEVEL);    //on move automatically gets nearby routes.
        ToolbarController.showCreateRouteBtn();
    }

    private static void getNearbyRoutes(){
        currentBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        ServerInterface.sendNearbyRoutesRequest(currentLocation, currentBounds);
    }

    private void moveCameraToCurrentLocation(int zoomLevel){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
    }

    private void updateCurrentLocation(){
        currentLocation = LocationManager.getCurrentLocation();
    }

    private static void setupMapConfig(){
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        applyOverlay(OVERLAY_COLOR);
        setUpClusterer();   //so can use marker clustering functionality.
        setupMapIdleListener();
        searchForNewRoutesOnMap = true;
    }
    public static void setupMapIdleListener(){
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //Map just changed - therefore search for nearby routes if desired.
                if (searchForNewRoutesOnMap == true){
                    getNearbyRoutes();
                }
                //following call necessary for clustering functionality to work.
                clusterManager.onCameraIdle();
            }
        });
    }

    private static void setUpClusterer() {
        clusterManager = new ClusterManager<ClusterStartMarkerItem>(MainActivity.getContext(), googleMap);
        clusterManager.setRenderer(new CustomMarkerRenderer(MainActivity.getContext(), googleMap, clusterManager));
        googleMap.setOnMarkerClickListener(clusterManager);
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterStartMarkerItem>() {
            @Override
            public boolean onClusterItemClick(ClusterStartMarkerItem clusterStartMarkerItem) {
                setSelectedRoute(clusterStartMarkerItem.mRoute);
                savePrePreviewCameraPosition();
                animateToPreview(clusterStartMarkerItem.mRoute);
                ToolbarController.hideCreateRouteBtn();
                showPreviewButtons();
                return true;
            }
        });
    }
    private static void setSelectedRoute(RouteItem route){
        selectedRoute = route;
    }
    public static void unsetSelectedRoute(){
        selectedRoute = null;
    }
    private static void savePrePreviewCameraPosition(){
        prePreviewCp = googleMap.getCameraPosition();
    }
    private static void animateToPreview(final RouteItem route){
        hideOtherStartPoints(route);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {    //small delay removes bugginess.
            @Override
            public void run() {
                displayEndPoint(route);
                displayCheckPoints(route);
                displaySuggestedRoute(route);
                route.routeInfo.optimumCameraPosition = RouteDisplayAlgorithm.findBestDisplaySettings(route, googleMap);
                //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(route.routeInfo.optimumCameraPosition));
            }
        }, 100);
    }
    private static void showPreviewButtons(){
        LinearLayout previewBottomBtnContainer =  (LinearLayout)thisFragmentView.findViewById(R.id.preview_bottom_btn_container);
        previewBottomBtnContainer.setVisibility(View.VISIBLE);
        ToolbarController.showPreviewBackBtn();
        //set click listeners.
        TextButton raceBtn = (TextButton) previewBottomBtnContainer.findViewById(R.id.btnRace);
        raceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRaceMode();
            }
        });
        TextButton routeStatsBtn = (TextButton) previewBottomBtnContainer.findViewById(R.id.btnRouteStats);
        routeStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenManager.loadScreen("Route stats", selectedRoute.routeInfo.routeId);
            }
        });
    }
    private static void hidePreviewButtons(){
        ToolbarController.hidePreviewBackBtn();
        LinearLayout previewBottomBtnContainer =  (LinearLayout)thisFragmentView.findViewById(R.id.preview_bottom_btn_container);
        previewBottomBtnContainer.setVisibility(View.GONE);
    }
    public static void createRouteBtnClick(){
        hideAllStartPoints();
        RouteCreationObject.initialize();
        RouteCreationObject.startRouteCreation();
    }
    public static void cancelCreateBtnClick(){
        RouteCreationObject.cancelRouteCreation();
        restoreMapStartPoints();
    }
    public static void previewBackBtnClick(){
        hideSelectedRouteFromMap();
        unsetSelectedRoute();
        restoreMapStartPoints();
        restorePrePreviewCameraPosition();
        hidePreviewButtons();
        ToolbarController.showCreateRouteBtn();
    }
    private static void restorePrePreviewCameraPosition(){
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(prePreviewCp));
    }
    public static void restoreMapStartPoints(){
        for (int i=0; i<routesOnMap.size(); i++){
            addToMapDisplay(routesOnMap.get(i));
        }
        invalidateMap();
    }
    public static void hideSelectedRouteFromMap(){
        selectedRoute.suggestedRoutePolyline.remove();
        for (int i=0; i<selectedRoute.routeInfo.checkpoints.size(); i++){
            selectedRoute.checkpointMarkerItemList.checkpoints.get(i).removeCircle();
            selectedRoute.checkpointMarkerItemList.checkpoints.get(i).marker.remove();
        }
        selectedRoute.endMarker.removeCircle();
        selectedRoute.endMarker.marker.remove();
        selectedRoute.startMarker.removeCircle();
        clusterManager.removeItem(selectedRoute.startMarker);
        selectedRoute.visibleOnMap = false;
    }
    private static void startRaceMode(){
        InRaceController.startRaceMode(selectedRoute, googleMap);
    }
    private static void displaySuggestedRoute(RouteItem route){
        PolylineOptions polylineOptions = new PolylineOptions();
        for (int i=0; i<route.routeInfo.allPoints.size(); i++){
            polylineOptions.add(route.routeInfo.allPoints.get(i).point);
        }
        polylineOptions.clickable(false).color(0xFFFFFF00).width(5).zIndex(3);
        route.suggestedRoutePolyline = googleMap.addPolyline(polylineOptions);
    }
    private static void hideOtherStartPoints(RouteItem route){
        for (int i=0; i<routesOnMap.size(); i++){
            RouteItem comparisonRoute = routesOnMap.get(i);
            if (comparisonRoute.routeInfo.routeId != route.routeInfo.routeId){
                //hide it from map.
                comparisonRoute.startMarker.removeCircle();
                clusterManager.removeItem(comparisonRoute.startMarker);
                comparisonRoute.visibleOnMap = false;
            }
        }
        invalidateMap();
    }
    private static void hideAllStartPoints(){
        for (int i=0; i<routesOnMap.size(); i++){
            RouteItem nroute = routesOnMap.get(i);
            //hide it from map.
            nroute.startMarker.removeCircle();
            clusterManager.removeItem(nroute.startMarker);
            nroute.visibleOnMap = false;
        }
        invalidateMap();
    }
    private static void displayEndPoint(RouteItem route){
        route.endMarker = new EndMarkerItem(route);
        route.endMarker.marker = googleMap.addMarker(route.endMarker.markerOptions);
        route.endMarker.addCircle();
    }

    private static void displayCheckPoints(RouteItem route){
        route.checkpointMarkerItemList = new CheckpointMarkerItemList(route);
        for (int i=0; i<route.checkpointMarkerItemList.checkpoints.size(); i++){
            route.checkpointMarkerItemList.checkpoints.get(i).marker = googleMap.addMarker(route.checkpointMarkerItemList.checkpoints.get(i).markerOptions);
            route.checkpointMarkerItemList.checkpoints.get(i).addCircle();
        }
    }

    private static void applyOverlay(long color){
        //Draws darkened rectangles on map, covering entire world map.
        List points1 = Arrays.asList(new LatLng(-89,-180),
                new LatLng(89,-180),
                new LatLng(89,-30),
                new LatLng(-89,-30));
        List points2 = Arrays.asList(new LatLng(-89,-30),
                new LatLng(89,-30),
                new LatLng(89,30),
                new LatLng(-89,30));
        List points3 = Arrays.asList(new LatLng(-89,30),
                new LatLng(89,30),
                new LatLng(89,180),
                new LatLng(-89,180));
        PolygonOptions options1 = new PolygonOptions();
        options1.addAll(points1);
        options1.strokeWidth(0);
        options1.zIndex(2);
        //int color1 = 0xB3000000;
        int color1 = (int)color;
        options1.fillColor(color1);
        options1.clickable(false);
        PolygonOptions options2 = new PolygonOptions();
        options2.addAll(points2);
        options2.strokeWidth(0);
        options2.zIndex(2);
        options2.fillColor(color1);
        options2.clickable(false);
        PolygonOptions options3 = new PolygonOptions();
        options3.addAll(points3);
        options3.strokeWidth(0);
        options3.zIndex(2);
        options3.fillColor(color1);
        options3.clickable(false);
        googleMap.addPolygon(options1);
        googleMap.addPolygon(options2);
        googleMap.addPolygon(options3);
    }

    public static void putRoutesOnMap(List<RouteInfo> routeInfoList){
        for (int i=0; i<routeInfoList.size(); i++) {
            if (isRouteAlreadyDisplayed(routeInfoList.get(i)) == false) {
                addRouteToMap(routeInfoList.get(i));
            }//else do nothing with this route since already added.
        }
        //refresh map to make sure all content added on it.
        invalidateMap();
    }

    private static void invalidateMap(){
        clusterManager.cluster();
    }

    private static void addRouteToMap(RouteInfo routeInfo){
        RouteItem routeItem = new RouteItem(routeInfo);
        addToRouteItemList(routeItem);
        addToMapDisplay(routeItem);
    }

    private static void addToMapDisplay(RouteItem route){
        //do the clusterfuck thing.
        addStartMarker(route);
    }

    private static void addStartMarker(RouteItem route) {
        ClusterStartMarkerItem markerItem = new ClusterStartMarkerItem(route);
        markerItem.addCircle();
        clusterManager.addItem(markerItem);
        route.startMarker = markerItem;
        route.visibleOnMap = true;
    }

    private static void addToRouteItemList(RouteItem routeItem){
        routesOnMap.add(routeItem);
    }

    private static boolean isRouteAlreadyDisplayed(RouteInfo routeInfo){
        for (int i=0; i<routesOnMap.size(); i++){
            if (routeInfo.routeId == routesOnMap.get(i).routeInfo.routeId){
                return true;
            }
        }
        //if reaches here, not on map.
        return false;
    }

    static class ClusterStartMarkerItem implements ClusterItem {
        //necessary for the clustermanager to work.
        private final LatLng mPosition;
        public final RouteItem mRoute;
        public Circle accompanyingCircle;

        public ClusterStartMarkerItem(RouteItem route) {
            mPosition = route.routeInfo.startPoint.point;
            mRoute = route;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        public void addCircle(){
            accompanyingCircle = googleMap.addCircle(new CircleOptions()
                    .center(mPosition)
                    .radius(DEFAULT_MARKER_CIRCLE_RADIUS)
                    .strokeWidth(1)
                    .strokeColor(Color.argb(0xFF, 0x00, 0xFF, 0x00))
                    .fillColor(Color.argb(0x4D, 0x00, 0xFF, 0x00))
                    .zIndex(4)
                    .clickable(false));
        }
        public void removeCircle(){
            accompanyingCircle.remove();
        }
    }

    static class CheckpointMarkerItemList{
        public final RouteItem mRoute;
        public List<CheckpointMarkerItem> checkpoints = new ArrayList<>();

        public CheckpointMarkerItemList(RouteItem route){
            mRoute = route;
            for (int i=0; i<route.routeInfo.checkpoints.size(); i++){
                checkpoints.add(new CheckpointMarkerItem(route.routeInfo.checkpoints.get(i).point));
            }
        }

        static class CheckpointMarkerItem{
            private final LatLng mPosition;
            public MarkerOptions markerOptions;
            public Circle accompanyingCircle;
            public Marker marker;

            public CheckpointMarkerItem(LatLng checkpointPosition){
                mPosition = checkpointPosition;
                markerOptions = new MarkerOptions().position(mPosition)
                        .icon(BitmapDescriptorFactory.defaultMarker(220));
            }
            public void addCircle(){
                accompanyingCircle = googleMap.addCircle(new CircleOptions()
                        .center(mPosition)
                        .radius(DEFAULT_MARKER_CIRCLE_RADIUS)
                        .strokeWidth(1)
                        .strokeColor(Color.argb(0xFF, 0x00, 0x55, 0xFF))
                        .fillColor(Color.argb(0X4D, 0x00, 0x55, 0xFF))
                        .zIndex(4)
                        .clickable(false));
            }
            public void removeCircle(){
                accompanyingCircle.remove();
            }
        }
    }

    static class EndMarkerItem{
        private final LatLng mPosition;
        public final RouteItem mRoute;
        public MarkerOptions markerOptions;
        public Circle accompanyingCircle;
        public Marker marker;

        public EndMarkerItem(RouteItem route){
            mPosition = route.routeInfo.endPoint.point;
            mRoute = route;
            markerOptions = new MarkerOptions().position(mPosition)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        public void addCircle(){
            accompanyingCircle = googleMap.addCircle(new CircleOptions()
                    .center(mPosition)
                    .radius(DEFAULT_MARKER_CIRCLE_RADIUS)
                    .strokeWidth(1)
                    .strokeColor(Color.argb(0xFF, 0xFF, 0x00, 0x00))
                    .fillColor(Color.argb(0x4D, 0xFF, 0x00, 0x00))
                    .zIndex(4)
                    .clickable(false));
        }
        public void removeCircle(){
            accompanyingCircle.remove();
        }
    }

    static class CustomMarkerRenderer extends DefaultClusterRenderer<ClusterStartMarkerItem> {
        public CustomMarkerRenderer(Context context, GoogleMap map,
                               ClusterManager<ClusterStartMarkerItem> clusterManager) {
            super(context, map, clusterManager);
        }
        @Override
        protected void onBeforeClusterItemRendered(ClusterStartMarkerItem item,
                                                   MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
    }

    static class RouteCreationObject{
        public static View stepsContainer;
        private static int expandedStep;
        public static boolean startPointSelected, endPointSelected, checkpointsSelected;
        public static boolean step1Confirmed, step2Confirmed, step3Confirmed, step4Confirmed;
        public static View step1Container, step2Container, step3Container, step4Container, step5Container;
        private static TextView step1StepTextVert, step2StepTextVert, step3StepTextVert, step4StepTextVert;
        private static TextView step1StepTextHoriz, step2StepTextHoriz, step3StepTextHoriz;
        private static TextView step1PrimaryTextVert, step2PrimaryTextVert, step3PrimaryTextVert, step4PrimaryTextVert;
        private static TextView step1Instruction, step2Instruction, step3Instruction1, step3Instruction2;
        public static View step1ConfirmBtn, step2ConfirmBtn, step4ConfirmBtn, step5PreviewRaceBtn;
        public static View step3YesBtn, step3NoBtn, step3CheckpointsRow1, step3CheckpointsRow2;
        public static View step3RemoveCheckpointBtn, step3ConfirmCheckpointBtn, step3ConfirmSelectionBtn;
        public static List<View> checkpointCircles;
        private static boolean hasInitialQuestionBeenAnswered, areCheckpointsWanted, isCheckpointSelected;
        private static int selectedCheckpointNumber;
        public static EditText step4EditText;
        public static View horizStepsLayout;
        private static View step1Horiz, step2Horiz, step3Horiz;
        private static boolean isHorizStepsLayout;

        public static RouteInfo createdRouteInfo;
        private static Marker startMarker, endMarker;
        private static List<Marker> checkpointMarkers = new ArrayList<>();

        public static void initialize(){
            createdRouteInfo = new RouteInfo();
            stepsContainer = thisFragmentView.findViewById(R.id.createRouteStepsContainer);
            setupStep1Stuff();
            setupStep2Stuff();
            setupStep3Stuff();
            setupStep4Stuff();
            setupStep5Stuff();
            setupHorizLayout();
        }
        public static void startRouteCreation(){
            showCreateRouteStepsContainer();
            expandStep(1);
            ToolbarController.hideCreateRouteBtn();
            ToolbarController.showCancelCreateBtn();
        }
        public static void cancelRouteCreation(){
            hideCreateRouteStepsContainer();
            clearCreateRouteDataFromMap();
            ToolbarController.hideCancelCreateBtn();
            ToolbarController.showCreateRouteBtn();
        }
        private static void setupStep1Stuff(){
            startPointSelected = false;
            step1Confirmed = false;
            //get handles.
            step1Container = thisFragmentView.findViewById(R.id.step_1_large_container);
            step1StepTextVert = (TextView)step1Container.findViewById(R.id.s1steptextVert);
            step1StepTextHoriz = (TextView)thisFragmentView.findViewById(R.id.s1steptextHoriz);
            step1PrimaryTextVert = (TextView)step1Container.findViewById(R.id.s1PrimaryText);
            step1Instruction = (TextView)step1Container.findViewById(R.id.s1Instruction);
            step1ConfirmBtn = step1Container.findViewById(R.id.btnConfirmStartPoint);

            //set button click events.
            step1Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedStep != 1) {
                        collapseStep(expandedStep);
                    }
                    expandStep(1);
                    fitRouteToNewMapSpace();
                }
            });
            step1ConfirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    step1Confirmed = true;
                    collapseStep(1);
                    if (isStepComplete(2) == false) {
                        expandStep(2);
                    }else{
                        if (areSteps1To4Complete() == true){
                            expandStep(5);
                        }
                    }
                    fitRouteToNewMapSpace();
                }
            });

            //set initial view.
            step1Container.setVisibility(View.GONE);
        }
        private static void setupStep2Stuff(){
            endPointSelected = false;
            step2Confirmed = false;
            //get handles.
            step2Container = thisFragmentView.findViewById(R.id.step_2_large_container);
            step2StepTextVert = (TextView)step2Container.findViewById(R.id.s2steptextVert);
            step2StepTextHoriz = (TextView)thisFragmentView.findViewById(R.id.s2steptextHoriz);
            step2PrimaryTextVert = (TextView)step2Container.findViewById(R.id.s2PrimaryText);
            step2Instruction = (TextView)step2Container.findViewById(R.id.s2Instruction);
            step2ConfirmBtn = step2Container.findViewById(R.id.btnConfirmFinishPoint);

            //set button click events.
            step2Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedStep != 2) {
                        collapseStep(expandedStep);
                    }
                    expandStep(2);
                    fitRouteToNewMapSpace();
                }
            });

            step2ConfirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    step2Confirmed = true;
                    collapseStep(2);
                    if (isStepComplete(3) == false) {
                        expandStep(3);
                    }else{
                        if (areSteps1To4Complete() == true){
                            expandStep(5);
                        }
                    }
                    fitRouteToNewMapSpace();
                }
            });

            //set initial view.
            step2Container.setVisibility(View.GONE);
        }
        private static void setupStep3Stuff(){
            checkpointsSelected = false;
            step3Confirmed = false;
            hasInitialQuestionBeenAnswered = false;
            isCheckpointSelected = false;
            //get handles.
            step3Container = thisFragmentView.findViewById(R.id.step_3_large_container);
            step3StepTextVert = (TextView)step3Container.findViewById(R.id.s3steptextVert);
            step3StepTextHoriz = (TextView)thisFragmentView.findViewById(R.id.s3steptextHoriz);
            step3PrimaryTextVert = (TextView)step3Container.findViewById(R.id.s3PrimaryText);
            step3Instruction1 = (TextView)step3Container.findViewById(R.id.s3Instruction1);
            step3Instruction2 = (TextView)step3Container.findViewById(R.id.s3Instruction2);
            step3YesBtn = step3Container.findViewById(R.id.btnYesAddCheckpoints);
            step3NoBtn = step3Container.findViewById(R.id.btnNoAddCheckpoints);
            step3CheckpointsRow1 = step3Container.findViewById(R.id.checkpointRow1);
            step3CheckpointsRow2 = step3Container.findViewById(R.id.checkpointRow2);
            step3RemoveCheckpointBtn = step3Container.findViewById(R.id.btnRemoveCheckpoint);
            step3ConfirmCheckpointBtn = step3Container.findViewById(R.id.btnConfirmCheckpoint);
            step3ConfirmSelectionBtn = step3Container.findViewById(R.id.btnConfirmCheckpoints);
            checkpointCircles = new ArrayList<>();
            for (int i=0; i<5; i++){
                checkpointCircles.add(((LinearLayout)step3CheckpointsRow1).getChildAt(i));
            }
            for (int i=5; i<10; i++){
                checkpointCircles.add(((LinearLayout)step3CheckpointsRow2).getChildAt(i-5));
            }

            //set button click events.
            step3Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedStep != 3) {
                        collapseStep(expandedStep);
                    }
                    expandStep(3);
                    fitRouteToNewMapSpace();
                }
            });
            step3YesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rearrangeStepsToHorizLayout();  //occurs when the checkpoint question answered.
                    hasInitialQuestionBeenAnswered = true;
                    areCheckpointsWanted = true;
                    showStep3CheckpointSelection();
                    fitRouteToNewMapSpace();
                }
            });

            step3NoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rearrangeStepsToHorizLayout();  //occurs when the checkpoint question answered.
                    hasInitialQuestionBeenAnswered = true;
                    areCheckpointsWanted = false;
                    step3Confirmed = true;
                    collapseStep(3);
                    if (isStepComplete(4) == false) {
                        expandStep(4);
                    }else{
                        if (areSteps1To4Complete() == true){
                            expandStep(5);
                        }
                    }
                    fitRouteToNewMapSpace();
                }
            });
            step3RemoveCheckpointBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeCurrentCheckpoint();
                }
            });
            step3ConfirmCheckpointBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deselectCurrentCheckpoint();
                }
            });
            step3ConfirmSelectionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    step3Confirmed = true;
                    collapseStep(3);
                    if (isStepComplete(4) == false) {
                        expandStep(4);
                    }else{
                        if (areSteps1To4Complete() == true){
                            expandStep(5);
                        }
                    }
                    fitRouteToNewMapSpace();
                }
            });

            //set initial view.
            step3Container.setVisibility(View.GONE);
        }
        private static void setupStep4Stuff(){
            step4Confirmed = false;
            //get handles.
            step4Container = thisFragmentView.findViewById(R.id.step_4_large_container);
            step4StepTextVert = (TextView)step4Container.findViewById(R.id.s4steptextVert);
            step4PrimaryTextVert = (TextView)step4Container.findViewById(R.id.s4PrimaryText);
            step4EditText = (EditText)step4Container.findViewById(R.id.s4EditText);
            step4ConfirmBtn = step4Container.findViewById(R.id.btnConfirmRouteName);

            //set button click events.
            step4Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedStep != 4) {
                        collapseStep(expandedStep);
                    }
                    expandStep(4);
                    fitRouteToNewMapSpace();
                }
            });
            step4EditText.setText("");
            step4EditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0){
                        step4ConfirmBtn.setVisibility(View.VISIBLE);
                    }else{
                        step4ConfirmBtn.setVisibility(View.GONE);
                    }
                }
            });
            step4ConfirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    step4Confirmed = true;
                    collapseStep(4);
                    if (areSteps1To4Complete() == true){
                        expandStep(5);
                    }
                    fitRouteToNewMapSpace();
                }
            });

            //set initial view.
            step4Container.setVisibility(View.GONE);
        }
        private static void setupStep5Stuff(){
            //get handles.
            step5Container = thisFragmentView.findViewById(R.id.step_5_large_container);
            step5PreviewRaceBtn = step5Container.findViewById(R.id.btnPreviewCreatedRoute);
            step5PreviewRaceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            //set initial view.
            step5Container.setVisibility(View.GONE);
        }
        private static void setupHorizLayout(){
            isHorizStepsLayout = false;
            horizStepsLayout = thisFragmentView.findViewById(R.id.horizStepsLayout);
            step1Horiz = horizStepsLayout.findViewById(R.id.step1Horiz);
            step2Horiz = horizStepsLayout.findViewById(R.id.step2Horiz);
            step3Horiz = horizStepsLayout.findViewById(R.id.step3Horiz);

            step1Horiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedStep != 1) {
                        collapseStep(expandedStep);
                    }
                    expandStep(1);
                    fitRouteToNewMapSpace();
                }
            });
            step2Horiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedStep != 2) {
                        collapseStep(expandedStep);
                    }
                    expandStep(2);
                    fitRouteToNewMapSpace();
                }
            });
            step3Horiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedStep != 3) {
                        collapseStep(expandedStep);
                    }
                    expandStep(3);
                    fitRouteToNewMapSpace();
                }
            });
            horizStepsLayout.setVisibility(View.GONE);
        }
        private static void showCreateRouteStepsContainer(){
            stepsContainer.setVisibility(View.VISIBLE);
        }
        private static void hideCreateRouteStepsContainer(){
            stepsContainer.setVisibility(View.GONE);
            step1Container.setVisibility(View.GONE);
            step2Container.setVisibility(View.GONE);
            step3Container.setVisibility(View.GONE);
            step4Container.setVisibility(View.GONE);
            step5Container.setVisibility(View.GONE);
            horizStepsLayout.setVisibility(View.GONE);
        }

        public static void expandStep(int stepNumber){
            if (stepNumber == 1){
                expandStep1();
            }
            if (stepNumber == 2){
                expandStep2();
            }
            if (stepNumber == 3){
                expandStep3();
            }
            if (stepNumber == 4){
                expandStep4();
            }
            if (stepNumber == 5){
                expandStep5();
            }
        }
        private static void expandStep1(){
            step1StepTextVert.setText("1");
            step1StepTextHoriz.setText("1");
            step1PrimaryTextVert.setText("Select start point");
            step1Instruction.setVisibility(View.VISIBLE);
            if (startPointSelected == true){
                step1ConfirmBtn.setVisibility(View.VISIBLE);
            }else{
                step1ConfirmBtn.setVisibility(View.GONE);
            }
            step1Container.setVisibility(View.VISIBLE);
            expandedStep = 1;
            setGoogleMapClickListener(1);
        }
        private static void expandStep2(){
            step2StepTextVert.setText("2");
            step2StepTextHoriz.setText("2");
            step2PrimaryTextVert.setText("Select finish point");
            step2Instruction.setVisibility(View.VISIBLE);
            if (endPointSelected == true){
                step2ConfirmBtn.setVisibility(View.VISIBLE);
            }else{
                step2ConfirmBtn.setVisibility(View.GONE);
            }
            step2Container.setVisibility(View.VISIBLE);
            expandedStep = 2;
            setGoogleMapClickListener(2);
        }
        private static void expandStep3(){
            step3StepTextVert.setText("3");
            step3StepTextHoriz.setText("3");
            if (hasInitialQuestionBeenAnswered == true){
                if (areCheckpointsWanted == true){
                    showStep3CheckpointSelection();
                }else{  //previously did not want checkpoints - showCheckpointQuestion again.
                    showCheckpointQuestion();
                }
            }else{
                showCheckpointQuestion();
            }
            step3Container.setVisibility(View.VISIBLE);
            expandedStep = 3;
        }
        private static void expandStep4(){
            step4StepTextVert.setText("4");
            step4PrimaryTextVert.setText("Name route");
            step4EditText.setVisibility(View.VISIBLE);
            if (step4EditText.getText().length() > 0){
                step4ConfirmBtn.setVisibility(View.VISIBLE);
            }else{
                step4ConfirmBtn.setVisibility(View.GONE);
            }
            step4Container.setVisibility(View.VISIBLE);
            expandedStep = 4;
            setGoogleMapClickListener(0);
        }
        private static void expandStep5(){
            step5Container.setVisibility(View.VISIBLE);
            expandedStep = 5;
            setGoogleMapClickListener(0);
        }

        private static void setGoogleMapClickListener(int stepNumber){
            if (stepNumber == 0){   //Parameter value for no map listener.
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        //do nothing - simply replaces previous click listener on map.
                    }
                });
            }
            if (stepNumber == 1){
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        startPointSelected = true;
                        createdRouteInfo.startPoint.point = latLng;
                        if (startMarker != null){
                            startMarker.remove();
                        }
                        startMarker = googleMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        step1ConfirmBtn.setVisibility(View.VISIBLE);
                    }
                });
            }
            if (stepNumber == 2){
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        endPointSelected = true;
                        createdRouteInfo.endPoint.point = latLng;
                        if (endMarker != null){
                            endMarker.remove();
                        }
                        endMarker = googleMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        step2ConfirmBtn.setVisibility(View.VISIBLE);
                    }
                });
            }
            if (stepNumber == 3){
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        RoutePoint newCheckpoint = new RoutePoint(latLng, 0, InRaceController.ROUTE_POINT_TYPE_CHECKPOINT);
                        createdRouteInfo.checkpoints.set(selectedCheckpointNumber, newCheckpoint);
                        Marker thisCheckpointMarker = checkpointMarkers.get(selectedCheckpointNumber);
                        if (thisCheckpointMarker != null){
                            thisCheckpointMarker.remove();
                        }
                        thisCheckpointMarker = googleMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(220)));
                        step3ConfirmCheckpointBtn.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
        private static void clearCreateRouteDataFromMap(){
            if (startMarker != null){
                startMarker.remove();
            }
            if (endMarker != null){
                endMarker.remove();
            }
            for (int i=0; i<checkpointMarkers.size(); i++){
                Marker thisCheckpointMarker = checkpointMarkers.get(i);
                if (thisCheckpointMarker != null){
                    thisCheckpointMarker.remove();
                }
            }
        }

        public static void collapseStep(int stepNumber){
            if (stepNumber == 1){
                collapseStep1();
            }
            if (stepNumber == 2){
                collapseStep2();
            }
            if (stepNumber == 3){
                collapseStep3();
            }
            if (stepNumber == 4){
                collapseStep4();
            }
            if (stepNumber == 5){
                collapseStep5();
            }
        }
        private static void collapseStep1(){
            if (step1Confirmed == true){
                step1StepTextVert.setText("✔");
                step1StepTextHoriz.setText("✔");
                step1PrimaryTextVert.setText("Start point");
            }else{
                step1StepTextVert.setText("1");
                step1StepTextHoriz.setText("1");
                step1PrimaryTextVert.setText("Select start point");
            }
            step1ConfirmBtn.setVisibility(View.GONE);
            step1Instruction.setVisibility(View.GONE);
            if (isHorizStepsLayout == true){
                step1Container.setVisibility(View.GONE);
            }
            setGoogleMapClickListener(0);   //i.e. no map click listener.
        }
        private static void collapseStep2(){
            if (step2Confirmed == true){
                step2StepTextVert.setText("✔");
                step2StepTextHoriz.setText("✔");
                step2PrimaryTextVert.setText("Finish point");
            }else{
                step2StepTextVert.setText("2");
                step2StepTextHoriz.setText("2");
                step2PrimaryTextVert.setText("Select finish point");
            }
            step2ConfirmBtn.setVisibility(View.GONE);
            step2Instruction.setVisibility(View.GONE);
            if (isHorizStepsLayout == true){
                step2Container.setVisibility(View.GONE);
            }
            setGoogleMapClickListener(0);   //i.e. no map click listener.
        }
        private static void collapseStep3(){
            if (step3Confirmed == true){
                step3StepTextVert.setText("✔");
                step3StepTextHoriz.setText("✔");
            }else{
                step3StepTextVert.setText("3");
                step3StepTextHoriz.setText("3");
            }
            step3ConfirmSelectionBtn.setVisibility(View.GONE);
            step3ConfirmCheckpointBtn.setVisibility(View.GONE);
            step3RemoveCheckpointBtn.setVisibility(View.GONE);
            step3Instruction2.setVisibility(View.GONE);
            step3CheckpointsRow2.setVisibility(View.GONE);
            step3CheckpointsRow1.setVisibility(View.GONE);
            step3YesBtn.setVisibility(View.GONE);
            step3NoBtn.setVisibility(View.GONE);
            step3Instruction1.setVisibility(View.GONE);
            if (isHorizStepsLayout == true){
                step3Container.setVisibility(View.GONE);
            }
            setGoogleMapClickListener(0);   //i.e. no map click listener.
        }
        private static void collapseStep4(){
            if (step4Confirmed == true){
                step4StepTextVert.setText("✔");
                step4PrimaryTextVert.setText("Route name");
            }else{
                step4StepTextVert.setText("4");
                step4PrimaryTextVert.setText("Name route");
            }
            step4ConfirmBtn.setVisibility(View.GONE);
            step4EditText.setVisibility(View.GONE);
        }
        private static void collapseStep5(){
            step5Container.setVisibility(View.GONE);
        }

        private static void rearrangeStepsToHorizLayout(){
            hideVertSteps1and2();
            showHorizSteps();
            isHorizStepsLayout = true;
        }
        private static void hideVertSteps1and2(){
            step1Container.setVisibility(View.GONE);
            step2Container.setVisibility(View.GONE);
        }
        private static void showHorizSteps(){
            horizStepsLayout.setVisibility(View.VISIBLE);
        }

        private static boolean isStepComplete(int stepNumber){
            if (stepNumber==1){
                return step1Confirmed;
            }else if (stepNumber == 2) {
                return step2Confirmed;
            }else if (stepNumber == 3){
                return step3Confirmed;
            }else{
                return false;
            }
        }
        private static boolean areSteps1To4Complete(){
            if ((step1Confirmed == true) && (step2Confirmed == true) && (step3Confirmed == true) && (step4Confirmed == true)){
                return true;
            }else{
                return false;
            }
        }

        private static void showCheckpointQuestion(){
            step3PrimaryTextVert.setText("Add checkpoints?");
            step3Instruction1.setVisibility(View.VISIBLE);
            step3YesBtn.setVisibility(View.VISIBLE);
            step3NoBtn.setVisibility(View.VISIBLE);
            step3CheckpointsRow1.setVisibility(View.GONE);
            step3CheckpointsRow2.setVisibility(View.GONE);
            step3Instruction2.setVisibility(View.GONE);
            step3RemoveCheckpointBtn.setVisibility(View.GONE);
            step3ConfirmCheckpointBtn.setVisibility(View.GONE);
            step3ConfirmSelectionBtn.setVisibility(View.GONE);
        }
        private static void showStep3CheckpointSelection(){
            for (int i=0; i<10; i++){
                int checkpointNumber = i;
                if (i<createdRouteInfo.checkpoints.size()){
                    setCheckpointAtPosition(checkpointNumber);
                }
                if (i==createdRouteInfo.checkpoints.size()){
                    setAddCheckpointAtPosition(checkpointNumber);
                }
                if (i>createdRouteInfo.checkpoints.size()){
                    hideCheckpointAtPosition(checkpointNumber);
                }
            }
            step3PrimaryTextVert.setText("Checkpoints");
            step3Instruction1.setVisibility(View.GONE);
            step3YesBtn.setVisibility(View.GONE);
            step3NoBtn.setVisibility(View.GONE);
            step3CheckpointsRow1.setVisibility(View.VISIBLE);
            if (createdRouteInfo.checkpoints.size() >= 5){
                step3CheckpointsRow2.setVisibility(View.VISIBLE);
            }else{
                step3CheckpointsRow2.setVisibility(View.GONE);
            }
            step3Instruction2.setText("Add checkpoints or confirm selection.");
            step3RemoveCheckpointBtn.setVisibility(View.GONE);
            step3ConfirmCheckpointBtn.setVisibility(View.GONE);
            step3ConfirmSelectionBtn.setVisibility(View.VISIBLE);
        }
        private static void setCheckpointAtPosition(final int checkpointNumber){
            setCheckpointCircleAppearance(checkpointNumber, CHECKPOINT_NOT_SELECTED);
            checkpointCircles.get(checkpointNumber).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deselectCurrentCheckpoint();
                    selectCheckpoint(checkpointNumber);
                }
            });
        }
        private static void setAddCheckpointAtPosition(final int checkpointNumber){
            setCheckpointCircleAppearance(checkpointNumber, CHECKPOINT_ADD);
            checkpointCircles.get(checkpointNumber).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deselectCurrentCheckpoint();
                    addCheckpointToCreatedRouteObject();
                    setCheckpointAtPosition(checkpointNumber);
                    selectCheckpoint(checkpointNumber);
                    if (checkpointNumber < 9) { //i.e. not last checkpoint.
                        setAddCheckpointAtPosition(checkpointNumber + 1);
                    }
                }
            });
        }
        private static void hideCheckpointAtPosition(final int checkpointNumber){
            setCheckpointCircleAppearance(checkpointNumber, CHECKPOINT_NOT_VISIBLE);
        }
        private static void selectCheckpoint(int checkpointNumber){
            isCheckpointSelected = true;
            selectedCheckpointNumber = checkpointNumber;
            setCheckpointCircleAppearance(checkpointNumber, CHECKPOINT_SELECTED);
            step3Instruction2.setText("Tap point on map.");
            step3RemoveCheckpointBtn.setVisibility(View.VISIBLE);
            if (checkpointMarkers.get(checkpointNumber) != null) {
                step3ConfirmCheckpointBtn.setVisibility(View.VISIBLE);
            }else{
                step3ConfirmCheckpointBtn.setVisibility(View.GONE);
            }
            step3ConfirmSelectionBtn.setVisibility(View.GONE);
            setGoogleMapClickListener(3);
        }
        private static void deselectCheckpoint(int checkpointNumber){
            setCheckpointCircleAppearance(checkpointNumber, CHECKPOINT_NOT_SELECTED);
            step3Instruction2.setText("Add checkpoints or confirm selection.");
            step3RemoveCheckpointBtn.setVisibility(View.GONE);
            step3ConfirmCheckpointBtn.setVisibility(View.GONE);
            step3ConfirmSelectionBtn.setVisibility(View.VISIBLE);
            setGoogleMapClickListener(0);
        }
        private static void deselectCurrentCheckpoint(){
            if (isCheckpointSelected == true){
                deselectCheckpoint(selectedCheckpointNumber);
            }
        }
        private static void setCheckpointCircleAppearance(int checkpointNumber, int appearanceType){
            RelativeLayout checkpointCircle = (RelativeLayout)checkpointCircles.get(checkpointNumber);
            ImageView stepImageView = (ImageView) ((RelativeLayout)checkpointCircle.getChildAt(0)).getChildAt(0);
            TextView stepTextView = (TextView) ((RelativeLayout)checkpointCircle.getChildAt(0)).getChildAt(1);
            if (appearanceType == CHECKPOINT_NOT_SELECTED){
                stepTextView.setText(String.valueOf(checkpointNumber+1));
                checkpointCircle.setVisibility(View.VISIBLE);
            }
            if (appearanceType == CHECKPOINT_SELECTED){
                stepTextView.setText(String.valueOf(checkpointNumber+1));
                checkpointCircle.setVisibility(View.VISIBLE);
            }
            if (appearanceType == CHECKPOINT_NOT_VISIBLE){
                checkpointCircle.setVisibility(View.GONE);
            }
            if (appearanceType == CHECKPOINT_ADD){
                stepTextView.setText("\u002B");
                checkpointCircle.setVisibility(View.VISIBLE);
            }
        }
        private static void removeCurrentCheckpoint(){
            removeCheckpoint(selectedCheckpointNumber);
        }
        private static void removeCheckpoint(int checkpointNumber){
            deselectCurrentCheckpoint();
            removeCheckpointFromMap(checkpointNumber);
            removeCheckpointFromCreatedRouteObject(checkpointNumber);
            updateCheckpointCircles();
        }
        private static void removeCheckpointFromMap(int checkpointNumber){
            if (checkpointMarkers.get(checkpointNumber) != null) {
                checkpointMarkers.get(checkpointNumber).remove();
            }
            //remove from marker list also.
            checkpointMarkers.remove(checkpointNumber);
        }
        private static void removeCheckpointFromCreatedRouteObject(int checkpointNumber){
            createdRouteInfo.checkpoints.remove(checkpointNumber);
        }
        private static void addCheckpointToCreatedRouteObject(){
            createdRouteInfo.checkpoints.add(new RoutePoint(null, 0, InRaceController.ROUTE_POINT_TYPE_CHECKPOINT));
        }
        private static void updateCheckpointCircles(){
            showStep3CheckpointSelection(); //recalling this method refreshes checkpoint circles.
        }

        private static void fitRouteToNewMapSpace(){
            if (endPointSelected == true) { //then at least 2 points on the map. Go ahead with method.
                LatLngBounds mapElementBounds = getBoundsOfMapElements();
                //then need to animate the camera to there. Consider cameraupdate animation with
                //latlngbounds and googlemap temporary padding.
            }
        }
        private static LatLngBounds getBoundsOfMapElements(){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(createdRouteInfo.startPoint.point);
            builder.include(createdRouteInfo.endPoint.point);
            for (int i=0; i<createdRouteInfo.checkpoints.size(); i++) {
                builder.include(createdRouteInfo.checkpoints.get(i).point);
            }
            return builder.build();
        }
    }
}


