package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.FriendRequests;

import android.graphics.Bitmap;

/**
 * Class containing data relating to friend_item xml layout.
 */

public class ReceivedRequestItem {
    public long requestId;
    public Long userId;
    public Bitmap userPic;
    public String userName;
    public Boolean expanded;
    public Boolean seen;

    public ReceivedRequestItem(){
        expanded = false;
    }
}
