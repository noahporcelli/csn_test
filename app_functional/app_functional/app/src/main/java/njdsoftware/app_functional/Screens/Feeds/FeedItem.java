package njdsoftware.app_functional.Screens.Feeds;

import java.util.ArrayList;
import java.util.List;

import njdsoftware.app_functional.CommonClasses.UserInfo;

/**
 * Created by user on 26/07/2016.
 */
public class FeedItem {
    public UserInfo user;
    public String primaryText;
    public int notificationType;
    public String userQuote;
    public int likeNum;
    public int commentNum;
    public String additionalInfo;
    public List<UserInfo> likers = new ArrayList<>();
    public List<Comment> comments = new ArrayList<>();
    
}


/*TO-DO:
Additional info part.





 */
