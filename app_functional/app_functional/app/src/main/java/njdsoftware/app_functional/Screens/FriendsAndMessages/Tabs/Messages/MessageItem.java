package njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Messages;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import njdsoftware.app_functional.UniversalUtilities.UsefulFunctions;

/**
 * Class containing data relating to message_item xml layout.
 */

public class MessageItem {
    public long messageId;
    public Long userId;
    public Bitmap userPic;
    public String userName;
    public String message;
    public Boolean expanded;
    public Boolean seen;
    public String timestamp;

    public MessageItem(){
        expanded = false;
    }

    public static String UTCToString(Long UTCms){
        Date date = new Date(UTCms);
        Date now = new Date();
        long dateDifference = UsefulFunctions.daysBetween(date, now);
        if (dateDifference < 0){
            //datediff negative -> error - format same as same day incase 1 hour ahead or similar error.
            return (new SimpleDateFormat("HH:mm").format(date));
        }else if (dateDifference == 0){
            //same day
            return (new SimpleDateFormat("HH:mm").format(date));
        }else if(dateDifference < 7){
            //within last week. e.g. Mon
            return (new SimpleDateFormat("EEE").format(date));
        }else if (UsefulFunctions.isThisYear(date) == true){
            //this year
            return (new SimpleDateFormat("MMM dd").format(date));
        }else{
            //not this year
            return (new SimpleDateFormat("dd/MM/yyyy").format(date));
        }
    }
}
