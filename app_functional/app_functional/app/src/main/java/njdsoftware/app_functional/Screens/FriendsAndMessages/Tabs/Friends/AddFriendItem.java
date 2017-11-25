package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Friends;

import android.graphics.Bitmap;

/**
 * Class containing data relating to friend_item xml layout.
 */

public class AddFriendItem {
    public String instructionText;
    public Boolean expanded, errorShown, messageShown;
    public String username;

    public AddFriendItem(){
        expanded = false;
        errorShown = false;
        messageShown = false;
        username = "";
    }
}
