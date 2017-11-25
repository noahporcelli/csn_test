package njdsoftware.app_functional.Screens.Feeds.Tabs.NewsFeed;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import njdsoftware.app_functional.Screens.Feeds.FeedItem;
import njdsoftware.app_functional.R;

/**
 * Created by Nathan on 21/07/2016.
 */
public class NewsFeedTab extends Fragment {

    View thisTabView;
    static RecyclerView recyclerView;
    static RecyclerView.LayoutManager layoutManager;
    //FeedListAdapter adapter;
    List<FeedItem> feedItemList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshElement;

    public NewsFeedTab() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisTabView = inflater.inflate(R.layout.screens_feeds_tabs_newsfeed_news_feed_tab, container, false);
        getCommonComponents();
        setupRecyclerView();
        populateFeedList();
        return thisTabView;
    }

    public void populateFeedList() {
        clearCurrentData();
        getNewData();
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        //adapter.notifyDataSetChanged();
    }

    private void getNewData() {
        /*List<FeedInfo> feedInfoList = AppMemory.getFeedInfoList();
        for (int i = 0; i < feedInfoList.size(); i++) {
            FeedItem feedItem = new FeedItem();
            //set properties.
            //feedItem.userId = feedInfoList.get(i).userInfo.userId;
            feedItemList.add(feedItem);
        }
        */
    }

    private void clearCurrentData() {
        feedItemList.clear();
    }

    private void setupRecyclerView(){
        //RecyclerView is used in conjuction with a LayoutManager to improve the performance of
        //long lists of content.
        //As with any list populated programmatically in Android, an Adapter must be used to handle
        //list content.
        recyclerView.setHasFixedSize(true); //improves recyclerview performance according to android developers.
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        //adapter = new FeedListAdapter(feedItemList);
        //recyclerView.setAdapter(adapter);
        addRefreshListener();
    }

    private void addRefreshListener(){
        swipeRefreshElement.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //do refresh actions.
            }
        });
        swipeRefreshElement.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void getCommonComponents() {
        recyclerView = (RecyclerView) thisTabView.findViewById(R.id.friends_recycler_view);
        swipeRefreshElement = (SwipeRefreshLayout) thisTabView.findViewById(R.id.swipe_refresh);
    }

    public static RecyclerView getRecyclerView(){
        return recyclerView;
    }
}


