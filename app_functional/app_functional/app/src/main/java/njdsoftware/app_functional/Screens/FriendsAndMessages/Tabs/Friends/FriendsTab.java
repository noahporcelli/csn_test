package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Friends;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import njdsoftware.app_functional.R;
import njdsoftware.app_functional.UniversalUtilities.AppMemory;

/**
 * Created by Nathan on 12/08/2016.
 */
public class FriendsTab extends Fragment {
    View thisTabView;
    static RecyclerView recyclerView;
    static RecyclerView.LayoutManager layoutManager;
    FriendsListAdapter adapter;
    List<FriendItem> friendItemList = new ArrayList<>();

    public FriendsTab() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisTabView = inflater.inflate(R.layout.screens_friendsandmessages_tabs_friends_friends_tab, container, false);
        getCommonComponents();
        setupRecyclerView();
        populateFriendsList();
        return thisTabView;
    }

    public void populateFriendsList() {
        clearCurrentData();
        getNewData();
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();
    }

    private void getNewData() {
        List<FriendInfo> friendInfoList = AppMemory.getFriendInfoList();
        for (int i = 0; i < friendInfoList.size(); i++) {
            FriendItem friendItem = new FriendItem();
            friendItem.userId = friendInfoList.get(i).userInfo.userId;
            friendItem.userName = friendInfoList.get(i).userInfo.userName;
            friendItem.userBio = friendInfoList.get(i).userInfo.userBio;
            friendItem.userPic = friendInfoList.get(i).userInfo.userPic;
            friendItemList.add(friendItem);
        }
    }

    private void clearCurrentData() {
        friendItemList.clear();
    }

    private void setupRecyclerView() {
        //RecyclerView is used in conjuction with a LayoutManager to improve the performance of
        //long lists of content.
        //As with any list populated programmatically in Android, an Adapter must be used to handle
        //list content.

        recyclerView.setHasFixedSize(true); //improves recyclerview performance according to android developers.
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FriendsListAdapter(friendItemList);
        recyclerView.setAdapter(adapter);
    }

    private void getCommonComponents() {
        recyclerView = (RecyclerView) thisTabView.findViewById(R.id.friends_recycler_view);
    }

    public static RecyclerView getRecyclerView(){
        return recyclerView;
    }

}
