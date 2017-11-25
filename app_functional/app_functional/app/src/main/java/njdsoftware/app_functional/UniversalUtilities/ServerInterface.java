package njdsoftware.app_functional.UniversalUtilities;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Friends.FriendsListAdapter;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.Screens.TimeTrial.InRaceController;
import njdsoftware.app_functional.Screens.TimeTrial.RouteInfo;
import njdsoftware.app_functional.Screens.TimeTrial.RoutePoint;
import njdsoftware.app_functional.Screens.TimeTrial.TimeTrialScreenFragment;

/**
 * Created by Nathan on 13/08/2016.
 */
public class ServerInterface {

    public static void sendNearbyRoutesRequest(LatLng currentLocation, LatLngBounds currentBounds){
        //post data to server, which then calls TimeTrialScreenFragment.putRoutesOnMap function.
        //for now, test.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<RouteInfo> routeInfoList = new ArrayList<RouteInfo>();
                //populate with test values.
                RouteInfo route1 = new RouteInfo();
                route1.routeId = 10l;
                route1.startPoint = new RoutePoint(new LatLng(51.4955,-2.61906), 0, InRaceController.ROUTE_POINT_TYPE_START);
                route1.checkpoints = new ArrayList<RoutePoint>();
                route1.checkpoints.add(new RoutePoint(new LatLng(51.7955,-2.31906), 0, InRaceController.ROUTE_POINT_TYPE_CHECKPOINT));
                route1.checkpoints.add(new RoutePoint(new LatLng(51.6955,-2.60906), 0, InRaceController.ROUTE_POINT_TYPE_CHECKPOINT));
                route1.endPoint = new RoutePoint(new LatLng(51.5955,-2.62906), 0, InRaceController.ROUTE_POINT_TYPE_END);
                route1.makeAllPointsArray();
                route1.routeDistanceMeters = 8888;
                route1.routeName = "RACE UNO V1";
                route1.routeRecordSeconds = 973l;
                route1.optimumCameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(51.634395d, -2.4725374d))
                        .bearing(1.7031f)
                        .tilt(31.875f)
                        .zoom(10.3f)
                        .build();
                RouteInfo route2 = new RouteInfo();
                route2.routeId = 11l;
                route2.startPoint = new RoutePoint(new LatLng(51.5955,-2.61906), 0, InRaceController.ROUTE_POINT_TYPE_START);
                route2.endPoint = new RoutePoint(new LatLng(51.6955,-2.62906), 0, InRaceController.ROUTE_POINT_TYPE_END);
                route2.routeDistanceMeters = 8888;
                route2.routeName = "RACE UNO V2";
                route2.routeRecordSeconds = 973l;
                RouteInfo route3 = new RouteInfo();
                route3.routeId = 12l;
                route3.startPoint = new RoutePoint(new LatLng(51.6955,-2.61906), 0, InRaceController.ROUTE_POINT_TYPE_START);
                route3.endPoint = new RoutePoint(new LatLng(51.7955,-2.62906), 0, InRaceController.ROUTE_POINT_TYPE_END);
                route3.routeDistanceMeters = 8888;
                route3.routeName = "RACE UNO V3";
                route3.routeRecordSeconds = 973l;
                RouteInfo route4 = new RouteInfo();
                route4.routeId = 13l;
                route4.startPoint = new RoutePoint(new LatLng(51.4955,-2.71906), 0, InRaceController.ROUTE_POINT_TYPE_START);
                route4.endPoint = new RoutePoint(new LatLng(51.5955,-2.62906), 0, InRaceController.ROUTE_POINT_TYPE_END);
                route4.routeDistanceMeters = 8888;
                route4.routeName = "RACE UNO V4";
                route4.routeRecordSeconds = 973l;
                RouteInfo route5 = new RouteInfo();
                route5.routeId = 14l;
                route5.startPoint = new RoutePoint(new LatLng(51.5955,-2.71906), 0, InRaceController.ROUTE_POINT_TYPE_START);
                route5.endPoint = new RoutePoint(new LatLng(51.5955,-2.62906), 0, InRaceController.ROUTE_POINT_TYPE_END);
                route5.routeDistanceMeters = 8888;
                route5.routeName = "RACE UNO V5";
                route5.routeRecordSeconds = 973l;
                RouteInfo route6 = new RouteInfo();
                route6.routeId = 10l;
                route6.startPoint = new RoutePoint(new LatLng(51.6955,-2.71906), 0, InRaceController.ROUTE_POINT_TYPE_START);
                route6.endPoint = new RoutePoint(new LatLng(51.5955,-2.62906), 0, InRaceController.ROUTE_POINT_TYPE_END);
                route6.routeDistanceMeters = 8888;
                route6.routeName = "RACE UNO V6";
                route6.routeRecordSeconds = 973l;
                routeInfoList.add(route1);
                routeInfoList.add(route2);
                routeInfoList.add(route3);
                routeInfoList.add(route4);
                routeInfoList.add(route5);
                routeInfoList.add(route6);
                TimeTrialScreenFragment.putRoutesOnMap(routeInfoList);
            }
        }, 500);
    }

    public static void makeMessageSeenOnServer(long messageId){

    }

    public static void makeReceivedRequestSeenOnServer(long requestId){

    }

    public static void removeFriendFromServer(long userId){
        //send notice of friend removal to server.
    }

    public static void loadH2hSummary(long userId, View h2hSummaryContainer){
        //retrieve stuff from server.

        //on response:
        String friendName;
        long myChallengeWins, myMutualRouteWins, myAchievements, myChallengeELO;
        long friendChallengeWins, friendMutualRouteWins, friendAchievements, friendChallengeELO;

        //test values.
        friendName = "jagex_mod";
        myChallengeWins = 5;
        myMutualRouteWins = 11;
        myAchievements = 32;
        myChallengeELO = 1071;
        friendChallengeWins = 3;
        friendMutualRouteWins = 7;
        friendAchievements = 18;
        friendChallengeELO = 1794;

        ((TextView)h2hSummaryContainer.findViewById(R.id.my_challenge_wins_view)).setText(String.valueOf(myChallengeWins));
        ((TextView)h2hSummaryContainer.findViewById(R.id.my_mutual_route_wins_view)).setText(String.valueOf(myMutualRouteWins));
        ((TextView)h2hSummaryContainer.findViewById(R.id.my_achievements_view)).setText(String.valueOf(myAchievements));
        ((TextView)h2hSummaryContainer.findViewById(R.id.my_challenge_elo_view)).setText(String.valueOf(myChallengeELO));

        ((TextView)h2hSummaryContainer.findViewById(R.id.friend_challenge_wins_view)).setText(String.valueOf(friendChallengeWins));
        ((TextView)h2hSummaryContainer.findViewById(R.id.friend_mutual_route_wins_view)).setText(String.valueOf(friendMutualRouteWins));
        ((TextView)h2hSummaryContainer.findViewById(R.id.friend_achievements_view)).setText(String.valueOf(friendAchievements));
        ((TextView)h2hSummaryContainer.findViewById(R.id.friend_challenge_elo_view)).setText(String.valueOf(friendChallengeELO));
    }

    public static void checkIfExistsThenViewProfile(final FriendsListAdapter.VHAddFriend holder, final String username){
        //looks up if a userid exists on the server, then call callback function FriendsListAdapter.addFriendViewProfileCallback.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FriendsListAdapter.addFriendViewProfileCallback(holder, true, 12010l);
            }
        }, 500);
    }

    public static void checkIfExistsThenAddFriend(final FriendsListAdapter.VHAddFriend holder, final String username){
        //looks up if a userid exists on the server, then call callback function FriendsListAdapter.addFriendAddCallback.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FriendsListAdapter.addFriendAddCallback(holder, true, 12010l);
            }
        }, 500);
    }
}
