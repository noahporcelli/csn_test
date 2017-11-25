package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.FriendRequests;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import njdsoftware.app_functional.CustomWidgets.CustomFontTextView;
import njdsoftware.app_functional.MainActivity;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.UniversalUtilities.AppMemory;

/**
 * Created by Nathan on 12/08/2016.
 */
public class FriendRequestsTab extends Fragment {
    View thisTabView;
    static RecyclerView recyclerView;
    static RecyclerView.LayoutManager layoutManager;
    ReceivedRequestsListAdapter receivedRequestsAdapter;
    SentRequestsListAdapter sentRequestsAdapter;
    List<ReceivedRequestItem> receivedRequestItemList = new ArrayList<>();
    List<SentRequestItem> sentRequestItemList = new ArrayList<>();
    LinearLayout requestsToggleButton;

    public FriendRequestsTab() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisTabView = inflater.inflate(R.layout.screens_friendsandmessages_tabs_friendrequests_friend_requests_tab, container, false);
        getCommonComponents();
        setupButtonListeners();
        setupRecyclerView();
        initializeRequestsLists();
        return thisTabView;
    }

    public void initializeRequestsLists() {
        //currently initializes with received requests data.
        clearCurrentData();
        getNewReceivedRequestsData();
        getNewSentRequestsData();
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        receivedRequestsAdapter.notifyDataSetChanged();
    }

    private void getNewSentRequestsData() {
        List<SentRequestInfo> sentRequestInfoList = AppMemory.getSentRequestInfoList();
        for (int i = 0; i < sentRequestInfoList.size(); i++) {
            SentRequestItem sentRequestItem = new SentRequestItem();
            sentRequestItem.requestId = sentRequestInfoList.get(i).requestId;
            sentRequestItem.userId = sentRequestInfoList.get(i).userInfo.userId;
            sentRequestItem.userName = sentRequestInfoList.get(i).userInfo.userName;
            sentRequestItem.userPic = sentRequestInfoList.get(i).userInfo.userPic;
            sentRequestItemList.add(sentRequestItem);
        }
    }

    private void getNewReceivedRequestsData() {
        List<ReceivedRequestInfo> receivedRequestInfoList = AppMemory.getReceivedRequestInfoList();
        for (int i = 0; i < receivedRequestInfoList.size(); i++) {
            ReceivedRequestItem receivedRequestItem = new ReceivedRequestItem();
            receivedRequestItem.requestId = receivedRequestInfoList.get(i).requestId;
            receivedRequestItem.userId = receivedRequestInfoList.get(i).userInfo.userId;
            receivedRequestItem.userName = receivedRequestInfoList.get(i).userInfo.userName;
            receivedRequestItem.userPic = receivedRequestInfoList.get(i).userInfo.userPic;
            receivedRequestItem.seen = receivedRequestInfoList.get(i).seen;
            receivedRequestItemList.add(receivedRequestItem);
        }
    }

    private void clearCurrentData() {
        receivedRequestItemList.clear();
        sentRequestItemList.clear();
    }

    private void setupRecyclerView() {
        //RecyclerView is used in conjuction with a LayoutManager to improve the performance of
        //long lists of content.
        //As with any list populated programmatically in Android, an Adapter must be used to handle
        //list content.

        recyclerView.setHasFixedSize(true); //improves recyclerview performance according to android developers.
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        receivedRequestsAdapter = new ReceivedRequestsListAdapter(receivedRequestItemList);
        sentRequestsAdapter = new SentRequestsListAdapter(sentRequestItemList);
        recyclerView.setAdapter(receivedRequestsAdapter);   //always initialize with received requests as usually more important than sent requests.
    }

    private void setupButtonListeners(){
        requestsToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestsToggleMenu(v);
            }
        });
    }

    private void getCommonComponents(){
        recyclerView = (RecyclerView) thisTabView.findViewById(R.id.friend_requests_recycler_view);
        requestsToggleButton = (LinearLayout) thisTabView.findViewById(R.id.interaction_container);
    }

    private void showRequestsToggleMenu(View view){
        final String popupText;
        final CustomFontTextView toggleTextView = (CustomFontTextView)view.findViewById(R.id.requests_toggle_text);
        if (toggleTextView.getText().toString().equals("Received")){
            popupText = "Sent";
        }else{
            popupText = "Received";
        }
        PopupMenu popup = new PopupMenu(MainActivity.getContext(), view.findViewById(R.id.img_requests_dropdown));
        popup.getMenu().add(popupText);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (popupText.equals("Received")){
                    //set received adapter as recyclerview adapter.
                    //recyclerView.setVisibility(View.INVISIBLE);
                    recyclerView.setAdapter(receivedRequestsAdapter);
                    //AppAnimation.fadeIn(recyclerView);
                    toggleTextView.setText("Received");
                }else{
                    //set sent adapter as recyclerview adapter.
                    //recyclerView.setVisibility(View.INVISIBLE);
                    recyclerView.setAdapter(sentRequestsAdapter);
                    //AppAnimation.fadeIn(recyclerView);
                    toggleTextView.setText("Sent");
                }
                return true;
            }
        });
        popup.show();
    }

    public static RecyclerView getRecyclerView(){
        return recyclerView;
    }
}
