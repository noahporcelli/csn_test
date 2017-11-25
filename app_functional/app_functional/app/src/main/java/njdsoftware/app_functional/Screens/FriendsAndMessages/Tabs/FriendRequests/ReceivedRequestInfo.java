package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.FriendRequests;

import njdsoftware.app_functional.CommonClasses.UserInfo;

/**
 * Class containing data relating to a friend, which is stored in the app's memory.
 */

public class ReceivedRequestInfo {
    public long requestId;
    public UserInfo userInfo;
    public Boolean seen;
    //anything else received-request specific.
    public ReceivedRequestInfo(){
        userInfo = new UserInfo();
    }
}


/*TO-DO:
Additional info part.





 */
