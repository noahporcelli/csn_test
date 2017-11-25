package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Messages;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import njdsoftware.app_functional.R;
import njdsoftware.app_functional.UniversalUtilities.AppMemory;

/**
 * Created by Nathan on 12/08/2016.
 */
public class MessagesTab extends Fragment {
    View thisTabView;
    static RecyclerView recyclerView;
    static RecyclerView.LayoutManager layoutManager;
    MessageListAdapter adapter;
    List<MessageItem> messageItemList = new ArrayList<>();

    public MessagesTab() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisTabView = inflater.inflate(R.layout.screens_friendsandmessages_tabs_messages_messages_tab, container, false);
        getCommonComponents();
        setupRecyclerView();
        populateMessageList();
        return thisTabView;
    }

    public void populateMessageList() {
        clearCurrentData();
        getNewData();
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();
    }

    private void getNewData() {
        List<MessageInfo> messageInfoList = AppMemory.getMessageInfoList();
        for (int i = 0; i < messageInfoList.size(); i++) {
            MessageItem messageItem = new MessageItem();
            messageItem.messageId = messageInfoList.get(i).messageId;
            messageItem.userId = messageInfoList.get(i).userId;
            messageItem.userName = messageInfoList.get(i).userName;
            messageItem.message = messageInfoList.get(i).message;
            messageItem.userPic = messageInfoList.get(i).userPic;
            messageItem.timestamp = MessageItem.UTCToString(messageInfoList.get(i).sentDateUTC);
            messageItem.seen = messageInfoList.get(i).seen;
            messageItemList.add(messageItem);
        }
    }

    private void clearCurrentData() {
        messageItemList.clear();
    }

    private void setupRecyclerView() {
        //RecyclerView is used in conjuction with a LayoutManager to improve the performance of
        //long lists of content.
        //As with any list populated programmatically in Android, an Adapter must be used to handle
        //list content.

        recyclerView.setHasFixedSize(true); //improves recyclerview performance according to android developers.
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageListAdapter(messageItemList);
        recyclerView.setAdapter(adapter);
    }

    private void getCommonComponents() {
        recyclerView = (RecyclerView) thisTabView.findViewById(R.id.messages_recycler_view);
    }

    public static RecyclerView getRecyclerView(){
        return recyclerView;
    }
}
