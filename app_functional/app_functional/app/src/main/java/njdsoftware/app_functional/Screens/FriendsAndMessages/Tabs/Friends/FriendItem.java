package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Friends;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Class containing data relating to friend_item xml layout.
 */

public class FriendItem {
    public Long userId;
    public Bitmap userPic;
    public String userName;
    public String userBio;
    public Integer myChallengeWins;
    public Integer friendChallengeWins;
    public Integer myMutualRouteWins;
    public Integer friendMutualRouteWins;
    public Integer myAchievements;
    public Integer friendAchievements;
    public Long myChallengeELO;
    public Long friendChallengeELO;
    public Boolean expanded;

    public FriendItem(){
        expanded = false;
    }
}
