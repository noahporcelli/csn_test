package njdsoftware.app_functional.UniversalUtilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Friends.FriendInfo;
import njdsoftware.app_functional.MainActivity;
import njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Messages.MessageInfo;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.FriendRequests.ReceivedRequestInfo;
import njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.FriendRequests.SentRequestInfo;

/**
 * Created by Nathan on 14/08/2016.
 * Static class for using methods to access data in app memory.
 * (Static class == functions only class - no objects).
 */
public class AppMemory {

    public static List<FriendInfo> getFriendInfoList(){
        //at the moment this is test code.
        List<FriendInfo> friendInfoList = new ArrayList<>();
        FriendInfo friendInfo1 = new FriendInfo();
        friendInfo1.userInfo.userBio = "Bio of world #1 cyclist.";
        friendInfo1.userInfo.userName = "njdzxx";
        friendInfo1.userInfo.userId = 92841l;
        Bitmap bm = BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.test_dp);
        friendInfo1.userInfo.userPic = bm;
        FriendInfo friendInfo2 = new FriendInfo();
        friendInfo2.userInfo.userBio = "I've got to be honest, I suck at cycling.";
        friendInfo2.userInfo.userName = "ash9888";
        friendInfo2.userInfo.userId = 92842l;
        FriendInfo friendInfo3 = new FriendInfo();
        friendInfo3.userInfo.userBio = "bio #3.";
        friendInfo3.userInfo.userName = "ash9888";
        friendInfo3.userInfo.userId = 92843l;
        friendInfoList.add(friendInfo1);
        friendInfoList.add(friendInfo2);
        friendInfoList.add(friendInfo3);
        return friendInfoList;
    }

    public static List<SentRequestInfo> getSentRequestInfoList(){
        //at the moment this is test code.
        List<SentRequestInfo> sentRequestInfoList = new ArrayList<>();
        SentRequestInfo sentRequestInfo1 = new SentRequestInfo();
        sentRequestInfo1.requestId = 921l;
        sentRequestInfo1.userInfo.userName = "njdzxx";
        sentRequestInfo1.userInfo.userId = 92841l;
        Bitmap bm = BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.test_dp);
        sentRequestInfo1.userInfo.userPic = bm;
        SentRequestInfo sentRequestInfo2 = new SentRequestInfo();
        sentRequestInfo2.requestId = 922l;
        sentRequestInfo2.userInfo.userName = "ash9888";
        sentRequestInfo2.userInfo.userId = 92842l;
        SentRequestInfo sentRequestInfo3 = new SentRequestInfo();
        sentRequestInfo3.requestId = 923l;
        sentRequestInfo3.userInfo.userName = "ash9888";
        sentRequestInfo3.userInfo.userId = 92843l;
        sentRequestInfoList.add(sentRequestInfo1);
        sentRequestInfoList.add(sentRequestInfo2);
        sentRequestInfoList.add(sentRequestInfo3);
        return sentRequestInfoList;
    }
    public static List<ReceivedRequestInfo> getReceivedRequestInfoList(){
        //at the moment this is test code.
        List<ReceivedRequestInfo> receivedRequestInfoList = new ArrayList<>();
        ReceivedRequestInfo receivedRequestInfo1 = new ReceivedRequestInfo();
        receivedRequestInfo1.requestId = 921l;
        receivedRequestInfo1.userInfo.userName = "njdzxx";
        receivedRequestInfo1.userInfo.userId = 92841l;
        receivedRequestInfo1.seen = false;
        Bitmap bm = BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.test_dp);
        receivedRequestInfo1.userInfo.userPic = bm;
        ReceivedRequestInfo receivedRequestInfo2 = new ReceivedRequestInfo();
        receivedRequestInfo2.requestId = 922l;
        receivedRequestInfo2.userInfo.userName = "ash9888";
        receivedRequestInfo2.userInfo.userId = 92842l;
        receivedRequestInfo2.seen = false;
        ReceivedRequestInfo receivedRequestInfo3 = new ReceivedRequestInfo();
        receivedRequestInfo3.requestId = 923l;
        receivedRequestInfo3.userInfo.userName = "ash9888";
        receivedRequestInfo3.userInfo.userId = 92843l;
        receivedRequestInfo3.seen = true;
        ReceivedRequestInfo receivedRequestInfo4 = new ReceivedRequestInfo();
        receivedRequestInfo4.requestId = 924l;
        receivedRequestInfo4.userInfo.userName = "henry_j_malc";
        receivedRequestInfo4.userInfo.userId = 1123l;
        receivedRequestInfo4.userInfo.userPic = bm;
        receivedRequestInfo4.seen = true;
        receivedRequestInfoList.add(receivedRequestInfo1);
        receivedRequestInfoList.add(receivedRequestInfo2);
        receivedRequestInfoList.add(receivedRequestInfo3);
        receivedRequestInfoList.add(receivedRequestInfo4);
        return receivedRequestInfoList;
    }

    public static boolean isUserInFriendsList(long userId){
        List<FriendInfo> friendInfoList = getFriendInfoList();
        for (int i=0; i<friendInfoList.size(); i++){
            if (userId == friendInfoList.get(i).userInfo.userId){
                return true;
            }
        }
        //if here, no matches.
        return false;
    }

    public static boolean isUserInSentRequestsList(long userId){
        List<SentRequestInfo> infoList = getSentRequestInfoList();
        for (int i=0; i<infoList.size(); i++){
            if (userId == infoList.get(i).userInfo.userId){
                return true;
            }
        }
        //if here, no matches.
        return false;
    }

    public static boolean isUserInReceivedRequestsList(long userId){
        List<ReceivedRequestInfo> infoList = getReceivedRequestInfoList();
        for (int i=0; i<infoList.size(); i++){
            if (userId == infoList.get(i).userInfo.userId){
                return true;
            }
        }
        //if here, no matches.
        return false;
    }

    public static void removeFriendFromMemory(long userId){
        //need to program this.
    }

    public static List<MessageInfo> getMessageInfoList(){
        //at the moment this is test code.
        List<MessageInfo> messageInfoList = new ArrayList<>();
        MessageInfo messageInfo1 = new MessageInfo();
        messageInfo1.messageId = 54l;
        messageInfo1.message = "\"Couldn't be happier. Not at all how i imagined this would turn out. This is a really long message so that it spans mutliple lines so i can see how a multi-line message will look with the button layouts im deciding between.\"";
        messageInfo1.userName = "njdzxx";
        try {
            messageInfo1.sentDateUTC = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse("2016-09-20T15:01:00").getTime();
        } catch (ParseException e) {}
        messageInfo1.seen = false;
        Bitmap bm = BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.test_dp);
        messageInfo1.userPic = bm;
        MessageInfo messageInfo2 = new MessageInfo();
        messageInfo2.messageId = 55l;
        messageInfo2.message = "\"Couldn't be happier. Not at all how i imagined this would turn out. This is a really long message so that it spans mutliple lines so i can see how a multi-line message will look with the button layouts im deciding between.\"";
        messageInfo2.userName = "ash9888";
        try {
            messageInfo2.sentDateUTC = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse("2016-09-20T10:53:00").getTime();
        } catch (ParseException e) {}
        messageInfo2.seen = false;
        MessageInfo messageInfo3 = new MessageInfo();
        messageInfo3.messageId = 57l;
        messageInfo3.message = "\"Couldn't be happier. Not at all how i imagined this would turn out. This is a really long message so that it spans mutliple lines so i can see how a multi-line message will look with the button layouts im deciding between.\"";
        messageInfo3.userName = "h_malc";
        try {
            messageInfo3.sentDateUTC = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse("2016-09-19T12:02:00").getTime();
        } catch (ParseException e) {}
        messageInfo3.seen = true;
        MessageInfo messageInfo4 = new MessageInfo();
        messageInfo4.messageId = 61l;
        messageInfo4.message = "\"Couldn't be happier. Not at all how i imagined this would turn out. This is a really long message so that it spans mutliple lines so i can see how a multi-line message will look with the button layouts im deciding between.\"";
        messageInfo4.userName = "the_real_njd";
        messageInfo4.userPic = bm;
        try {
            messageInfo4.sentDateUTC = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse("2016-09-18T12:03:00").getTime();
        } catch (ParseException e) {}
        messageInfo4.seen = true;
        messageInfoList.add(messageInfo1);
        messageInfoList.add(messageInfo2);
        messageInfoList.add(messageInfo3);
        messageInfoList.add(messageInfo4);
        return messageInfoList;
    }

    public static void makeMessageSeenInMemory(long messageId){
        //need to program this.
    }
    public static void makeReceivedRequestSeenInMemory(long requestId){
        //need to program this.
    }
}
