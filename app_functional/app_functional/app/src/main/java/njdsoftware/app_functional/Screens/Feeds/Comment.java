package njdsoftware.app_functional.Screens.Feeds;

import java.util.ArrayList;
import java.util.List;

import njdsoftware.app_functional.CommonClasses.UserInfo;

/**
 * Created by user on 26/07/2016.
 */
public class Comment {
    public UserInfo user;
    public String comment;
    public int likeNum;
    public List<UserInfo> likers = new ArrayList<>();

    public Comment(UserInfo user, String comment, int likeNum, List<UserInfo> likers){
        this.user = user;
        this.comment = comment;
        this.likeNum = likeNum;
        this.likers = likers;
    }
}
