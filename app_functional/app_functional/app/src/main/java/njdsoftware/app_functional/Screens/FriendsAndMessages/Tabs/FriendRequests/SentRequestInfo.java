package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.FriendRequests;

import njdsoftware.app_functional.CommonClasses.UserInfo;

/**
 * Class containing data relating to a friend, which is stored in the app's memory.
 */

public class SentRequestInfo {
    public long requestId;
    public UserInfo userInfo;
    //anything else sent-request specific.
    public SentRequestInfo(){
        userInfo = new UserInfo();
    }
}


/*TO-DO:
Additional info part.





 */
