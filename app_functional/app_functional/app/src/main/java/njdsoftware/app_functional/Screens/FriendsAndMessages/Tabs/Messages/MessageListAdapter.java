package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Messages;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import njdsoftware.app_functional.MainActivity;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.UniversalUtilities.AppAnimation;
import njdsoftware.app_functional.UniversalUtilities.AppMemory;
import njdsoftware.app_functional.UniversalUtilities.ServerInterface;

/**
 * Created by Nathan on 15/08/2016.
 * Multi-viewholder-types code copied from stack overflow.
 *
 * My description:
 * The adapter holds an internal list of the details of every item in the list. This
 * here is called feedItemList. A recyclerview contains many viewholders, containers, with each
 * one able to hold 1 item in the list. The recyclerview has a limit to the number of viewholders it creates
 * though (to limit performance reductions), and once this maximum is reached it just shuffles the items within
 * the viewholders (giving the appearance of a continuous list of viewholders).
 * When a new viewholder needs to be created i.e. adding items (before reuse limit), onCreateViewHolder
 * is called. This inflates the item layout xml file, and creates an object (a viewholder) which references
 * elements in this layout.
 * The content of each item is set in the onBindViewHolder event. Here you should take the content from your
 * adapter's item list, and manipulate the properties of the viewholder in question accordingly.
 */

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static List<MessageItem> messageItemList;
    private static final int TYPE_NEW_MESSAGE = 0;
    private static final int TYPE_MESSAGE = 1;

    public MessageListAdapter(List<MessageItem> messageItemList) {
        this.messageItemList = messageItemList;
    }

    @Override
    public int getItemCount(){ //first thing called by recyclerview to determine number of viewholder items to load.
        return (messageItemList.size() + 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_NEW_MESSAGE;
        }else {
            return TYPE_MESSAGE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NEW_MESSAGE) {
            //inflate your layout and pass it to view holder
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.screens_friendsandmessages_tabs_messages_new_message_item, parent, false);
            return new VHNewMessage(itemView);
        } else if (viewType == TYPE_MESSAGE) {
            //inflate your layout and pass it to view holder
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.screens_friendsandmessages_tabs_messages_message_item, parent, false);
            return new VHMessage(itemView);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHNewMessage) {
            //cast holder to VHAddMessage and set data.
            bindNewMessageViewHolder((VHNewMessage)holder, position);
        } else if (holder instanceof VHMessage) {
            //cast holder to VHMessage and set data.
            bindMessageViewHolder((VHMessage)holder, position);
        }
    }

    public void bindNewMessageViewHolder(final VHNewMessage holder, int position) {
        if (messageItemList.size() == 0){   //will be last element in list - show no end divider.
            holder.endDividerView.setVisibility(View.GONE);
        }else{
            holder.endDividerView.setVisibility(View.VISIBLE);
        }
        holder.permanentSectionView.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                //load new message screen.
            }
        });
    }

    public void bindMessageViewHolder(final VHMessage holder, int position) {
        final int listPosition = position - 1;
        final MessageItem thisItem = messageItemList.get(listPosition);
        if (thisItem.expanded == true){
            holder.loadExpandedView();
        }else{
            holder.loadCollapsedView();
        }
        if (thisItem.seen == true){
            holder.makeSeen();
        }else{
            holder.makeUnseen();
        }
        if (listPosition == (messageItemList.size() - 1)){   //so no divider for last item in list.
            holder.endDividerView.setVisibility(View.GONE);
        }else{
            holder.endDividerView.setVisibility(View.VISIBLE);
        }
        holder.userPicView.setImageBitmap(thisItem.userPic);
        holder.userNameView.setText(thisItem.userName);
        holder.timestampView.setText(thisItem.timestamp);
        holder.messageView.setText(thisItem.message);
        holder.permanentSectionView.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                if (thisItem.expanded == true){
                    holder.collapseItemView();
                    thisItem.expanded = false;
                }else{
                    holder.expandItemView();
                    MessagesTab.getRecyclerView().post(new Runnable() {
                        @Override
                        public void run() {
                            MessagesTab.getRecyclerView().setVerticalScrollBarEnabled(false);
                            MessagesTab.getRecyclerView().smoothScrollToPosition(listPosition+1);
                        }
                    });
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MessagesTab.getRecyclerView().setVerticalScrollBarEnabled(true);
                        }
                    }, 500);
                    AppAnimation.fadeIn(holder.expandableSectionView);
                    if (thisItem.seen == false){
                        makeMessageSeen(thisItem.messageId, holder);
                    }
                    thisItem.expanded=true;
                }
            }
        });
    }

    class VHNewMessage extends RecyclerView.ViewHolder {
        public RelativeLayout permanentSectionView;
        public View endDividerView;
        public VHNewMessage(View view) {
            super(view);
            //set up view handles.
            permanentSectionView = (RelativeLayout) view.findViewById(R.id.permanent_section_view);
            endDividerView = (View) view.findViewById(R.id.end_divider_view);
        }
    }

    class VHMessage extends RecyclerView.ViewHolder {
        public CircleImageView userPicView;
        public TextView userNameView, messageView, timestampView;
        public RelativeLayout permanentSectionView, expandableSectionView;
        public View endDividerView;
        public VHMessage(View view) {
            super(view);
            //set up view handles.
            userPicView = (CircleImageView) view.findViewById(R.id.user_pic_view);
            userNameView = (TextView) view.findViewById(R.id.user_name_view);
            timestampView = (TextView) view.findViewById(R.id.timestamp_view);
            messageView = (TextView) view.findViewById(R.id.message_view);
            expandableSectionView = (RelativeLayout) view.findViewById(R.id.expandable_section_view);
            permanentSectionView = (RelativeLayout) view.findViewById(R.id.permanent_section_view);
            endDividerView = (View) view.findViewById(R.id.end_divider_view);
        }
        public void makeSeen(){
            userNameView.setTypeface(null, Typeface.NORMAL);
            timestampView.setTypeface(null, Typeface.NORMAL);
            timestampView.setAlpha(0.7f);
            messageView.setTypeface(null, Typeface.NORMAL);
            messageView.setAlpha(0.7f);
        }
        public void makeUnseen(){
            userNameView.setTypeface(null, Typeface.BOLD);
            timestampView.setTypeface(null, Typeface.BOLD);
            timestampView.setAlpha(1f);
            messageView.setTypeface(null, Typeface.BOLD);
            messageView.setAlpha(1f);
        }
        public void loadExpandedView(){
            messageView.setMaxLines(Integer.MAX_VALUE);
            expandableSectionView.setVisibility(View.VISIBLE);
        }
        public void loadCollapsedView(){
            messageView.setMaxLines(2);
            expandableSectionView.setVisibility(View.GONE);
        }
        public void collapseItemView(){
            messageView.setMaxLines(2);
            expandableSectionView.setVisibility(View.GONE);
            showExpandIcon();
        }
        public void expandItemView(){
            messageView.setMaxLines(Integer.MAX_VALUE);
            expandableSectionView.setVisibility(View.INVISIBLE);
            showCollapseIcon();
        }
        public void showCollapseIcon(){

        }
        public void showExpandIcon(){

        }
    }

    private static void makeMessageSeen(long messageId, VHMessage vh) {
        makeMessageSeenOnScreen(vh);
        AppMemory.makeMessageSeenInMemory(messageId);
        ServerInterface.makeMessageSeenOnServer(messageId);
    }

    private static void makeMessageSeenOnScreen(VHMessage vh){
        final int listPosition = vh.getAdapterPosition() - 1;
        messageItemList.get(listPosition).seen = true;
        vh.makeSeen();
    }

    private void showRemoveDialog(final long userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getContext());
        builder.setMessage("Delete message?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //removeMessage(userId);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //cancel pressed. Do nothing.
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    public void removeMessage(long userId){
        AppMemory.removeMessageFromMemory(userId);
        ServerInterface.removeMessageFromServer(userId);
        removeMessageFromScreen(userId);
    }

    private void removeMessageFromScreen(long userId){
        try {
            long listPosition = findMessagePosition(userId);
            messageItemList.remove((int)listPosition);
            notifyItemRemoved((int) listPosition + 1);    //+1 due to existence of addmessage item.
            notifyItemRangeChanged((int) listPosition + 1, messageItemList.size());
        }catch(Exception e){
            //will go here if message attempting to remove not on messages list for whatever reason.
        }
    }

    private long findMessagePosition(long userId){
        for (int i=0; i<messageItemList.size(); i++){
            if (userId == messageItemList.get(i).userId){
                return i;
            }
        }
        //if reaches here, couldn't find. Shouldn't happen but return -1;
        throw new RuntimeException("Message attempting to remove not on messages list.");
    }

    public static void addMessageViewProfileCallback(VHAddMessage holder, boolean success, long userId){
        if (success == true){
            //clear add message data then load new profile page.
            holder.hideLoader();
            holder.collapseItemView();
            holder.hideInstruction();
            addMessageItem.expanded = false;
            addMessageItem.username = "";
            ScreenManager.loadScreen("Profile", userId);
        }else{
            //show error screen.
            holder.hideLoader();
            holder.showError();
        }
    }

    public static void addMessageAddCallback(VHAddMessage holder, boolean success, long userId){
        if (success == true){
            int messageStatus = getMessageStatus(userId);
            switch(messageStatus){
                case MessageStatus.FRIENDS:
                    holder.hideLoader();
                    holder.showMessage();
                    break;
                case MessageStatus.FRIEND_REQUEST_SENT:
                    holder.hideLoader();
                    holder.clearAndCollapse();
                    addMessageItem.username = "";
                    addMessageItem.expanded = false;
                    Toast.makeText(MainActivity.getContext(), "Message request sent.", Toast.LENGTH_SHORT).show();
                    //no more action since new request not being sent.
                    break;
                case MessageStatus.FRIEND_REQUEST_RECEIVED:
                    holder.hideLoader();
                    holder.clearAndCollapse();
                    addMessageItem.username = "";
                    addMessageItem.expanded = false;
                    Toast.makeText(MainActivity.getContext(), "Message request sent.", Toast.LENGTH_SHORT).show();
                    //accept message request.
                    ReceivedRequestsListAdapter.acceptMessageRequest(userId);
                    break;
                case MessageStatus.NOT_FRIENDS_NO_REQUESTS:
                    holder.hideLoader();
                    holder.clearAndCollapse();
                    addMessageItem.username = "";
                    addMessageItem.expanded = false;
                    Toast.makeText(MainActivity.getContext(), "Message request sent.", Toast.LENGTH_SHORT).show();
                    //send message request.
                    SentRequestsListAdapter.sendMessageRequest(userId);
                    break;
            }
        }else{
            //show error screen.
            holder.hideLoader();
            holder.showError();
        }
    }

    private static int getMessageStatus(long userId){
        if (AppMemory.isUserInMessagesList(userId) == true){
            return MessageStatus.FRIENDS;
        }else if (AppMemory.isUserInSentRequestsList(userId) == true) {
            return MessageStatus.FRIEND_REQUEST_SENT;
        }
        else if (AppMemory.isUserInReceivedRequestsList(userId) == true) {
            return MessageStatus.FRIEND_REQUEST_RECEIVED;
        }else{
            return MessageStatus.NOT_FRIENDS_NO_REQUESTS;
        }
    }

    static class MessageStatus{
        static final int FRIENDS = 1;
        static final int FRIEND_REQUEST_SENT = 2;
        static final int FRIEND_REQUEST_RECEIVED = 3;
        static final int NOT_FRIENDS_NO_REQUESTS = 4;
    }
    */
}
