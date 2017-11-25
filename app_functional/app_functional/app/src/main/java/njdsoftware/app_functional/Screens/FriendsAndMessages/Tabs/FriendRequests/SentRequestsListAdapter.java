package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.FriendRequests;

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

public class SentRequestsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static List<SentRequestItem> sentRequestItemList;

    public SentRequestsListAdapter(List<SentRequestItem> sentRequestItemList) {
        this.sentRequestItemList = sentRequestItemList;
    }

    @Override
    public int getItemCount(){ //first thing called by recyclerview to determine number of viewholder items to load.
        return (sentRequestItemList.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate your layout and pass it to view holder
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.screens_friendsandmessages_tabs_friendrequests_sent_request_item, parent, false);
        return new VHSentRequest(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindSentRequestViewHolder((VHSentRequest)holder, position);
    }

    public void bindSentRequestViewHolder(final VHSentRequest holder, int position) {
        final int listPosition = position;
        final SentRequestItem thisItem = sentRequestItemList.get(listPosition);
        if (thisItem.expanded == true){
            holder.expandableSectionView.setVisibility(View.VISIBLE);
        }else{
            holder.expandableSectionView.setVisibility(View.GONE);
        }
        if (listPosition == (sentRequestItemList.size() - 1)){   //so no divider for last item in list.
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
                    thisItem.expanded=true;
                }
            }
        });
        holder.btnCancelRequest.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                SentRequestItem clickedSentRequestItem = sentRequestItemList.get(listPosition);
                cancelFriendRequest(clickedSentRequestItem.userId);
            }
        });
    }

    class VHSentRequest extends RecyclerView.ViewHolder {
        public CircleImageView userPicView;
        public TextView userNameView, userBioView;
        public TextButton btnCancelRequest;
        public RelativeLayout permanentSectionView, expandableSectionView;
        public View endDividerView;
        public VHSentRequest(View view) {
            super(view);
            //set up view handles.
            userPicView = (CircleImageView) view.findViewById(R.id.user_pic_view);
            userNameView = (TextView) view.findViewById(R.id.user_name_view);
            btnCancelRequest = (TextButton) view.findViewById(R.id.btnCancelRequest);
            expandableSectionView = (RelativeLayout) view.findViewById(R.id.expandable_section_view);
            permanentSectionView = (RelativeLayout) view.findViewById(R.id.permanent_section_view);
            endDividerView = (View) view.findViewById(R.id.end_divider_view);
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

    public static void sendFriendRequest(long userId){

    }

    public static void cancelFriendRequest(long userId){

    }
}
