package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Friends;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import njdsoftware.app_functional.CustomWidgets.CustomFontTextView;
import njdsoftware.app_functional.CustomWidgets.TextButton;
import njdsoftware.app_functional.MainActivity;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.FriendRequests.ReceivedRequestsListAdapter;
import njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.FriendRequests.SentRequestsListAdapter;
import njdsoftware.app_functional.UniversalUtilities.AppAnimation;
import njdsoftware.app_functional.UniversalUtilities.AppMemory;
import njdsoftware.app_functional.UniversalUtilities.ScreenManager;
import njdsoftware.app_functional.UniversalUtilities.ServerInterface;
import njdsoftware.app_functional.UniversalUtilities.UsefulFunctions;

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

public class FriendsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static List<FriendItem> friendItemList;
    private static AddFriendItem addFriendItem = new AddFriendItem();
    private static final int TYPE_ADD_FRIEND = 0;
    private static final int TYPE_FRIEND = 1;

    public FriendsListAdapter(List<FriendItem> friendItemList) {
        this.friendItemList = friendItemList;
    }

    @Override
    public int getItemCount(){ //first thing called by recyclerview to determine number of viewholder items to load.
        return (friendItemList.size() + 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ADD_FRIEND;
        }else {
            return TYPE_FRIEND;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ADD_FRIEND) {
            //inflate your layout and pass it to view holder
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.screens_friendsandmessages_tabs_friends_add_friend_item, parent, false);
            return new VHAddFriend(itemView);
        } else if (viewType == TYPE_FRIEND) {
            //inflate your layout and pass it to view holder
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.screens_friendsandmessages_tabs_friends_friend_item, parent, false);
            return new VHFriend(itemView);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHAddFriend) {
            //cast holder to VHAddFriend and set data.
            bindAddFriendViewHolder((VHAddFriend)holder, position);
        } else if (holder instanceof VHFriend) {
            //cast holder to VHFriend and set data.
            bindFriendViewHolder((VHFriend)holder, position);
        }
    }

    public void bindAddFriendViewHolder(final VHAddFriend holder, int position) {
        holder.usernameEditText.setText(addFriendItem.username);
        if (addFriendItem.expanded == true){
            holder.expandableSectionView.setVisibility(View.VISIBLE);
        }else{
            holder.expandableSectionView.setVisibility(View.GONE);
        }
        if (addFriendItem.errorShown == true){
            holder.errorContainer.setVisibility(View.VISIBLE);
        }else{
            holder.errorContainer.setVisibility(View.GONE);
        }
        if (addFriendItem.messageShown == true){
            holder.messageContainer.setVisibility(View.VISIBLE);
        }else{
            holder.messageContainer.setVisibility(View.GONE);
        }
        if (friendItemList.size() == 0){   //will be last element in list - show no end divider.
            holder.endDividerView.setVisibility(View.GONE);
        }else{
            holder.endDividerView.setVisibility(View.VISIBLE);
        }
        holder.permanentSectionView.setTag(holder);
        holder.permanentSectionView.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                VHAddFriend holder = (VHAddFriend) view.getTag();
                if (addFriendItem.expanded == true){
                    holder.collapseItemView();
                    holder.hideInstruction();
                    addFriendItem.expanded = false;
                }else{
                    holder.usernameEditText.setText(addFriendItem.username);
                    holder.expandItemView();
                    holder.showInstruction();
                    FriendsTab.getRecyclerView().post(new Runnable() {
                        @Override
                        public void run() {
                            FriendsTab.getRecyclerView().setVerticalScrollBarEnabled(false);
                            FriendsTab.getRecyclerView().smoothScrollToPosition(0);
                        }
                    });
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FriendsTab.getRecyclerView().setVerticalScrollBarEnabled(true);
                        }
                    }, 500);
                    holder.expandableSectionView.setVisibility(View.VISIBLE);
                    holder.focusEditText();
                    //AppAnimation.fadeIn(holder.expandableSectionView);
                    addFriendItem.expanded = true;
                }
            }
        });
        holder.btnViewProfile.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                holder.showLoader();
                ServerInterface.checkIfExistsThenViewProfile(holder, addFriendItem.username);
            }
        });
        holder.btnAddFriend.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                holder.showLoader();
                ServerInterface.checkIfExistsThenAddFriend(holder, addFriendItem.username);
            }
        });
        holder.btnCancel.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                holder.clearAndCollapse();
                addFriendItem.username = "";
                addFriendItem.expanded = false;
            }
        });
        holder.usernameEditText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                holder.focusEditText();
            }
        });
        holder.usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                addFriendItem.username = s.toString();
            }
        });
    }

    public void bindFriendViewHolder(final VHFriend holder, int position) {
        final int listPosition = position - 1;
        final FriendItem thisItem = friendItemList.get(listPosition);
        if (thisItem.expanded == true){
            holder.expandableSectionView.setVisibility(View.VISIBLE);
        }else{
            holder.expandableSectionView.setVisibility(View.GONE);
        }
        if (listPosition == (friendItemList.size() - 1)){   //so no divider for last item in list.
            holder.endDividerView.setVisibility(View.GONE);
        }else{
            holder.endDividerView.setVisibility(View.VISIBLE);
        }
        holder.userPicView.setImageBitmap(thisItem.userPic);
        holder.userNameView.setText(thisItem.userName);
        holder.userBioView.setText(thisItem.userBio);
        holder.h2hUserNameView.setText(thisItem.userName);
        holder.myChallengeWinsView.setText(String.valueOf(thisItem.myChallengeWins));
        holder.friendChallengeWinsView.setText(String.valueOf(thisItem.friendChallengeWins));
        holder.myMutualRouteWinsView.setText(String.valueOf(thisItem.myMutualRouteWins));
        holder.friendMutualRouteWinsView.setText(String.valueOf(thisItem.friendMutualRouteWins));
        holder.myAchievementsView.setText(String.valueOf(thisItem.myAchievements));
        holder.friendAchievementsView.setText(String.valueOf(thisItem.friendAchievements));
        holder.myChallengeELOView.setText(String.valueOf(thisItem.myChallengeELO));
        holder.friendChallengeELOView.setText(String.valueOf(thisItem.friendChallengeELO));
        holder.permanentSectionView.setTag(holder);
        holder.permanentSectionView.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                final VHFriend holder = (VHFriend) view.getTag();
                if (thisItem.expanded == true){
                    holder.collapseItemView();
                    thisItem.expanded = false;
                }else{
                    holder.expandItemView();
                    FriendsTab.getRecyclerView().post(new Runnable() {
                        @Override
                        public void run() {
                            FriendsTab.getRecyclerView().setVerticalScrollBarEnabled(false);
                            FriendsTab.getRecyclerView().smoothScrollToPosition(listPosition+1);
                        }
                    });
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FriendsTab.getRecyclerView().setVerticalScrollBarEnabled(true);
                        }
                    }, 500);
                    ServerInterface.loadH2hSummary(1927l, holder.h2hSummaryContainer);
                    AppAnimation.fadeIn(holder.expandableSectionView);
                    thisItem.expanded=true;
                }
            }
        });
        holder.h2hYouView.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                ScreenManager.loadScreen("Profile");
            }
        });
        holder.h2hUserNameView.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                ScreenManager.loadScreen("Profile", thisItem.userId);
            }
        });
        holder.btnMessage.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                ScreenManager.loadScreen("Message", thisItem.userId);
            }
        });
        holder.btnChallenge.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                ScreenManager.loadScreen("Challenge", thisItem.userId);
            }
        });
        holder.btnFriendOptions.setOnClickListener(new View.OnClickListener(){  //for expanding/collapsing item.
            @Override
            public void onClick(View view) {
                showFriendOptionsPopup(thisItem.userId, holder.btnFriendOptions);
            }
        });
    }

    public class VHAddFriend extends RecyclerView.ViewHolder {
        public TextView instructionsView;
        public LinearLayout expandableSectionView;
        public RelativeLayout permanentSectionView, loaderContainer, errorContainer, messageContainer, usernameContainer;
        public EditText usernameEditText;
        public TextButton btnViewProfile, btnAddFriend, btnCancel;
        public View endDividerView;
        public VHAddFriend(View view) {
            super(view);
            //set up view handles.
            instructionsView = (TextView) view.findViewById(R.id.user_bio_view);
            expandableSectionView = (LinearLayout) view.findViewById(R.id.expandable_section_view);
            permanentSectionView = (RelativeLayout) view.findViewById(R.id.permanent_section_view);
            loaderContainer = (RelativeLayout) view.findViewById(R.id.loader_container);
            errorContainer = (RelativeLayout) view.findViewById(R.id.error_container);
            messageContainer = (RelativeLayout) view.findViewById(R.id.message_container);
            usernameContainer = (RelativeLayout) view.findViewById(R.id.username_container);
            usernameEditText = (EditText) view.findViewById(R.id.username_edit_text);
            btnAddFriend = (TextButton) view.findViewById(R.id.btnAddFriend);
            btnViewProfile = (TextButton) view.findViewById(R.id.viewProfileButton);
            btnCancel = (TextButton) view.findViewById(R.id.cancelAddButton);
            endDividerView = (View) view.findViewById(R.id.end_divider_view);

            clearAndCollapse();
        }
        public void collapseItemView(){
            expandableSectionView.setVisibility(View.GONE);
            final InputMethodManager imm = (InputMethodManager) MainActivity.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(usernameEditText.getWindowToken(), 0);
        }
        public void expandItemView(){
            expandableSectionView.setVisibility(View.INVISIBLE);
        }
        public void showInstruction(){
            //AppAnimation.fadeIn(instructionsView);
            instructionsView.setVisibility(View.VISIBLE);
        }
        public void hideInstruction(){
            instructionsView.setVisibility(View.INVISIBLE);
        }
        public void showLoader(){
            usernameContainer.setVisibility(View.GONE);
            loaderContainer.setVisibility(View.VISIBLE);
        }
        public void hideLoader(){
            loaderContainer.setVisibility(View.GONE);
            usernameContainer.setVisibility(View.VISIBLE);
        }
        public void showError(){
            errorContainer.setVisibility(View.VISIBLE);
        }
        public void hideError(){
            errorContainer.setVisibility(View.GONE);
        }
        public void showMessage(){
            messageContainer.setVisibility(View.VISIBLE);
        }
        public void hideMessage(){
            messageContainer.setVisibility(View.GONE);
        }
        public void focusEditText(){
            usernameEditText.requestFocusFromTouch();
            usernameEditText.setSelection(usernameEditText.getText().length());
            usernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        final InputMethodManager imm = (InputMethodManager) MainActivity.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(usernameEditText.getWindowToken(), 0);
                    }
                    return false;
                }
            });
            InputMethodManager imm = (InputMethodManager) MainActivity.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);
        }
        public void clearAndCollapse(){
            collapseItemView();
            hideInstruction();
            hideLoader();
            hideError();
            hideMessage();
        }
    }

    public class VHFriend extends RecyclerView.ViewHolder {
        public CircleImageView userPicView;
        public TextView h2hUserNameView, h2hYouView;
        public TextView userNameView, userBioView, myChallengeWinsView, friendChallengeWinsView;
        public TextView myMutualRouteWinsView, friendMutualRouteWinsView, myAchievementsView;
        public TextView friendAchievementsView, myChallengeELOView, friendChallengeELOView;
        public LinearLayout expandableSectionView, h2hSummaryContainer;
        public RelativeLayout permanentSectionView;
        public TextButton btnChallenge, btnMessage, btnFriendOptions;
        public View endDividerView;
        public VHFriend(View view) {
            super(view);
            //set up view handles.
            userPicView = (CircleImageView) view.findViewById(R.id.user_pic_view);
            userNameView = (TextView) view.findViewById(R.id.user_name_view);
            userBioView = (TextView) view.findViewById(R.id.user_bio_view);
            h2hUserNameView = (TextView) view.findViewById(R.id.h2h_user_name_view);
            h2hYouView = (TextView) view.findViewById(R.id.h2h_you_view);
            myChallengeWinsView = (TextView) view.findViewById(R.id.my_challenge_wins_view);
            friendChallengeWinsView = (TextView) view.findViewById(R.id.friend_challenge_wins_view);
            myMutualRouteWinsView = (TextView) view.findViewById(R.id.my_mutual_route_wins_view);
            friendMutualRouteWinsView = (TextView) view.findViewById(R.id.friend_mutual_route_wins_view);
            myAchievementsView = (TextView) view.findViewById(R.id.my_achievements_view);
            friendAchievementsView = (TextView) view.findViewById(R.id.friend_achievements_view);
            myChallengeELOView = (TextView) view.findViewById(R.id.my_challenge_elo_view);
            friendChallengeELOView = (TextView) view.findViewById(R.id.friend_challenge_elo_view);
            expandableSectionView = (LinearLayout) view.findViewById(R.id.expandable_section_view);
            permanentSectionView = (RelativeLayout) view.findViewById(R.id.permanent_section_view);
            h2hSummaryContainer = (LinearLayout) view.findViewById(R.id.h2h_summary_container);
            btnChallenge = (TextButton) view.findViewById(R.id.btnChallengeFriend);
            btnMessage = (TextButton) view.findViewById(R.id.btnMessageFriend);
            btnFriendOptions = (TextButton) view.findViewById(R.id.btnFriendsMore);
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

    private void showFriendOptionsPopup(final long userId, final View clickedView){
        PopupMenu popup = new PopupMenu(MainActivity.getContext(), clickedView);
        popup.getMenu().add("View profile");
        popup.getMenu().add("Full H2H");
        popup.getMenu().add("Compare achievements");
        popup.getMenu().add("Remove friend");

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("View profile")){
                    ScreenManager.loadScreen("Profile", userId);
                }else if (item.getTitle().equals("Full H2H")){
                    ScreenManager.loadScreen("H2h", userId);
                }else if (item.getTitle().equals("Compare achievements")){
                    ScreenManager.loadScreen("H2h", userId, true);
                }else if (item.getTitle().equals("Remove friend")){
                    showRemoveDialog(userId);
                }
                return true;
            }
        });
        popup.show();
    }
    private void showRemoveDialog(final long userId) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getContext(), R.style.LightAlertDialog);
        builder.setMessage("Remove friend?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeFriend(userId);
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
        //changeDialogToCorrectWidth(dialog);
    }
    private void changeDialogToCorrectWidth(AlertDialog dialog){
        Display display = ((Activity)MainActivity.getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = screenWidth - UsefulFunctions.dpToPx(80); //not quite correct since already has approx 32dp each side.
        dialog.getWindow().setAttributes(lp);
    }

    public void removeFriend(long userId){
        AppMemory.removeFriendFromMemory(userId);
        ServerInterface.removeFriendFromServer(userId);
        removeFriendFromScreen(userId);
    }

    private void removeFriendFromScreen(long userId){
        try {
            long listPosition = findFriendPosition(userId);
            friendItemList.remove((int)listPosition);
            notifyItemRemoved((int) listPosition + 1);    //+1 due to existence of addfriend item.
            notifyItemRangeChanged((int) listPosition + 1, friendItemList.size());
        }catch(Exception e){
            //will go here if friend attempting to remove not on friends list for whatever reason.
        }
    }

    private long findFriendPosition(long userId){
        for (int i=0; i<friendItemList.size(); i++){
            if (userId == friendItemList.get(i).userId){
                return i;
            }
        }
        //if reaches here, couldn't find. Shouldn't happen but return -1;
        throw new RuntimeException("Friend attempting to remove not on friends list.");
    }

    public static void addFriendViewProfileCallback(VHAddFriend holder, boolean success, long userId){
        if (success == true){
            //clear add friend data then load new profile page.
            holder.hideLoader();
            holder.collapseItemView();
            holder.hideInstruction();
            addFriendItem.expanded = false;
            addFriendItem.username = "";
            ScreenManager.loadScreen("Profile", userId);
        }else{
            //show error screen.
            holder.hideLoader();
            holder.showError();
        }
    }

    public static void addFriendAddCallback(VHAddFriend holder, boolean success, long userId){
        if (success == true){
            int friendStatus = getFriendStatus(userId);
            switch(friendStatus){
                case FriendStatus.FRIENDS:
                    holder.hideLoader();
                    holder.showMessage();
                break;
                case FriendStatus.FRIEND_REQUEST_SENT:
                    holder.hideLoader();
                    holder.clearAndCollapse();
                    addFriendItem.username = "";
                    addFriendItem.expanded = false;
                    Toast.makeText(MainActivity.getContext(), "Friend request sent.", Toast.LENGTH_SHORT).show();
                    //no more action since new request not being sent.
                break;
                case FriendStatus.FRIEND_REQUEST_RECEIVED:
                    holder.hideLoader();
                    holder.clearAndCollapse();
                    addFriendItem.username = "";
                    addFriendItem.expanded = false;
                    Toast.makeText(MainActivity.getContext(), "Friend request sent.", Toast.LENGTH_SHORT).show();
                    //accept friend request.
                    ReceivedRequestsListAdapter.acceptFriendRequest(userId);
                break;
                case FriendStatus.NOT_FRIENDS_NO_REQUESTS:
                    holder.hideLoader();
                    holder.clearAndCollapse();
                    addFriendItem.username = "";
                    addFriendItem.expanded = false;
                    Toast.makeText(MainActivity.getContext(), "Friend request sent.", Toast.LENGTH_SHORT).show();
                    //send friend request.
                    SentRequestsListAdapter.sendFriendRequest(userId);
                break;
            }
        }else{
            //show error screen.
            holder.hideLoader();
            holder.showError();
        }
    }

    private static int getFriendStatus(long userId){
        if (AppMemory.isUserInFriendsList(userId) == true){
            return FriendStatus.FRIENDS;
        }else if (AppMemory.isUserInSentRequestsList(userId) == true) {
            return FriendStatus.FRIEND_REQUEST_SENT;
        }
        else if (AppMemory.isUserInReceivedRequestsList(userId) == true) {
            return FriendStatus.FRIEND_REQUEST_RECEIVED;
        }else{
            return FriendStatus.NOT_FRIENDS_NO_REQUESTS;
        }
    }

    static class FriendStatus{
        static final int FRIENDS = 1;
        static final int FRIEND_REQUEST_SENT = 2;
        static final int FRIEND_REQUEST_RECEIVED = 3;
        static final int NOT_FRIENDS_NO_REQUESTS = 4;
    }
}
