package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.FriendRequests;

import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import njdsoftware.app_functional.CustomWidgets.TextButton;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.UniversalUtilities.AppAnimation;
import njdsoftware.app_functional.UniversalUtilities.AppMemory;
import njdsoftware.app_functional.UniversalUtilities.ServerInterface;

public class ReceivedRequestsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static List<ReceivedRequestItem> receivedRequestItemList;

    public ReceivedRequestsListAdapter(List<ReceivedRequestItem> receivedRequestItemList) {
        this.receivedRequestItemList = receivedRequestItemList;
    }

    @Override
    public int getItemCount(){ //first thing called by recyclerview to determine number of viewholder items to load.
        return (receivedRequestItemList.size());
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate your layout and pass it to view holder
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.screens_friendsandmessages_tabs_friendrequests_received_request_item, parent, false);
        return new VHReceivedRequest(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindReceivedRequestViewHolder((VHReceivedRequest)holder, position);
    }

    public void bindReceivedRequestViewHolder(final VHReceivedRequest holder, int position) {
        final int listPosition = position;
        final ReceivedRequestItem thisItem = receivedRequestItemList.get(listPosition);
        if (thisItem.expanded == true){
            holder.expandableSectionView.setVisibility(View.VISIBLE);
        }else{
            holder.expandableSectionView.setVisibility(View.GONE);
        }
        if (thisItem.seen == true){
            holder.makeSeen();
        }else{
            holder.makeUnseen();
        }
        if (listPosition == (receivedRequestItemList.size() - 1)){   //so no divider for last item in list.
            holder.endDividerView.setVisibility(View.GONE);
        }else{
            holder.endDividerView.setVisibility(View.VISIBLE);
        }
        holder.userPicView.setImageBitmap(thisItem.userPic);
        holder.userNameView.setText(thisItem.userName);

        holder.permanentSectionView.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                if (thisItem.expanded == true){
                    holder.collapseItemView();
                    thisItem.expanded = false;
                }else{
                    holder.expandItemView();
                    FriendRequestsTab.getRecyclerView().post(new Runnable() {
                        @Override
                        public void run() {
                            FriendRequestsTab.getRecyclerView().setVerticalScrollBarEnabled(false);
                            FriendRequestsTab.getRecyclerView().smoothScrollToPosition(listPosition+1);
                        }
                    });
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FriendRequestsTab.getRecyclerView().setVerticalScrollBarEnabled(true);
                        }
                    }, 500);
                    AppAnimation.fadeIn(holder.expandableSectionView);
                    if (thisItem.seen == false){
                        makeReceivedRequestSeen(thisItem.requestId, holder);
                    }
                    thisItem.expanded=true;
                }
            }
        });
        holder.btnAccept.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                ReceivedRequestItem clickedReceivedRequestItem = receivedRequestItemList.get(listPosition);
                acceptFriendRequest(clickedReceivedRequestItem.userId);
            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                ReceivedRequestItem clickedReceivedRequestItem = receivedRequestItemList.get(listPosition);
                rejectFriendRequest(clickedReceivedRequestItem.userId);
            }
        });
    }

    class VHReceivedRequest extends RecyclerView.ViewHolder {
        public CircleImageView userPicView;
        public TextView userNameView, userBioView;
        public TextButton btnAccept, btnReject;
        public RelativeLayout permanentSectionView, expandableSectionView;
        public View endDividerView;
        public VHReceivedRequest(View view) {
            super(view);
            //set up view handles.
            userPicView = (CircleImageView) view.findViewById(R.id.user_pic_view);
            userNameView = (TextView) view.findViewById(R.id.user_name_view);
            btnAccept = (TextButton) view.findViewById(R.id.btnAccept);
            btnReject = (TextButton) view.findViewById(R.id.btnReject);
            expandableSectionView = (RelativeLayout) view.findViewById(R.id.expandable_section_view);
            permanentSectionView = (RelativeLayout) view.findViewById(R.id.permanent_section_view);
            endDividerView = (View) view.findViewById(R.id.end_divider_view);
        }
        public void makeSeen(){
            userNameView.setTypeface(null, Typeface.NORMAL);
        }
        public void makeUnseen(){
            userNameView.setTypeface(null, Typeface.BOLD);
        }
        public void collapseItemView(){
            expandableSectionView.setVisibility(View.GONE);
            showExpandIcon();
        }
        public void expandItemView(){
            expandableSectionView.setVisibility(View.INVISIBLE);
            showCollapseIcon();
        }
        public void showCollapseIcon(){

        }
        public void showExpandIcon(){

        }
    }

    private static void makeReceivedRequestSeen(long requestId, VHReceivedRequest vh) {
        makeReceivedRequestSeenOnScreen(vh);
        AppMemory.makeReceivedRequestSeenInMemory(requestId);
        ServerInterface.makeReceivedRequestSeenOnServer(requestId);
    }

    private static void makeReceivedRequestSeenOnScreen(VHReceivedRequest vh){
        final int listPosition = vh.getAdapterPosition();
        receivedRequestItemList.get(listPosition).seen = true;
        vh.makeSeen();
    }

    public static void acceptFriendRequest(long userId){

    }

    public static void rejectFriendRequest(long userId){

    }
}
