package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Messages;

import android.graphics.Bitmap;

/**
 * Class containing data relating to a friend, which is stored in the app's memory.
 */

public class MessageInfo {
    public long messageId;
    public Long userId;
    public Bitmap userPic;
    public String userName;
    public String message;
    public Boolean seen;
    public Long sentDateUTC;

    public MessageInfo(){

    }
}


/*TO-DO:
Additional info part.





 */
