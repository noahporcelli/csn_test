package njdsoftware.app_functional.Screens.RouteStats.Tabs.WorldLeaderboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import njdsoftware.app_functional.R;

/**
 * Created by Nathan on 21/07/2016.
 */
public class RouteWorldLeaderboardTab extends Fragment {

    public View thisView;

    public RouteWorldLeaderboardTab() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.screens_routestats_route_world_leaderboards_tab, container, false);
        return thisView;
    }
}


