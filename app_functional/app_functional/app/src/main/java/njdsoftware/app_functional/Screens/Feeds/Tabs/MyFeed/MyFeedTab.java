package njdsoftware.app_functional.Screens.Feeds.Tabs.MyFeed;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import njdsoftware.app_functional.R;

/**
 * Created by Nathan on 21/07/2016.
 */
public class MyFeedTab extends Fragment {

    public MyFeedTab() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.screens_feeds_tabs_myfeed_my_feed_tab, container, false);
    }

}