package njdsoftware.app_functional.Screens.Feeds;

import njdsoftware.app_functional.CommonClasses.UserInfo;

/**
 * Class containing data relating to a feeditem, which is stored in the app's memory.
 */

public class FeedInfo {
    public UserInfo userInfo;
    public int feedItemType;
    public long likes, comments;
    public String quote;
    public Object typeSpecificObject;


    public FeedInfo(){
        this.userInfo = new UserInfo();
    }
}

