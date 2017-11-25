package njdsoftware.app_functional.UniversalUtilities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import njdsoftware.app_functional.Screens.FriendsAndMessages.FriendsMessagesFragment;
import njdsoftware.app_functional.MainActivity;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.Screens.TimeTrial.TimeTrialScreenFragment;
import njdsoftware.app_functional.Test.DesignFragment;

/**
 * Created by Nathan on 14/08/2016.
 * Static class for using methods to access data in app memory.
 * (Static class == functions only class - no objects).
 */
public class ScreenManager {
    static MainActivity mainActivity = (MainActivity) MainActivity.getContext();
    static final int FULL_SCREEN = 1;
    static final int BELOW_APP_BAR = 2;

    public static void loadScreen(String screenName) {
        //Load one of the screens at the base of the programs hierarchy. (Ones in sidenav).
        Fragment fragmentToLoad;
        String appBarTitle;
        int fragmentHeight;

        if (screenName.equals("Home")){
            if (mainActivity.homeFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.homeFragment;
            appBarTitle = "Home";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("My profile")){
            if (mainActivity.profileFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.profileFragment;
            appBarTitle = "Profile";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("Time trial")){
            if (mainActivity.timeTrialFragment == null){
                mainActivity.timeTrialFragment = new TimeTrialScreenFragment();
            }
            fragmentToLoad = mainActivity.timeTrialFragment;
            appBarTitle = "TT";
            fragmentHeight = FULL_SCREEN;
            ToolbarController.collapseToolbar();
        }else if (screenName.equals("Achievements")){
            if (mainActivity.achievementsFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.achievementsFragment;
            appBarTitle = "Achievements";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("Friends + messages")){
            if (mainActivity.friendsMessagesFragment == null){
                mainActivity.friendsMessagesFragment = new FriendsMessagesFragment();
            }
            fragmentToLoad = mainActivity.friendsMessagesFragment;
            appBarTitle = "F / M";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("Challenges")){
            if (mainActivity.challengesFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.challengesFragment;
            appBarTitle = "Challenges";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("Profile")){
            if (mainActivity.profileFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.profileFragment;
            appBarTitle = "Profile";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("H2h")){
            if (mainActivity.h2hFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.h2hFragment;
            appBarTitle = "Head-to-head";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("Message")){
            if (mainActivity.newMessageFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.newMessageFragment;
            appBarTitle = "New message";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("Challenge")){
            if (mainActivity.newChallengeFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.newChallengeFragment;
            appBarTitle = "New challenge";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("Design")){
            fragmentToLoad = new DesignFragment();
            appBarTitle = "Design";
            fragmentHeight = BELOW_APP_BAR;
        }else{
            Toast.makeText(mainActivity, "Couldn't identify screen: " + screenName + ".", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ToolbarController.setToolbarTitle(appBarTitle);
        if (fragmentToLoad != null) {
            setFragmentContainerHeight(fragmentHeight);
            loadFragment(fragmentToLoad);
        }else{
            Toast.makeText(mainActivity, "Couldn't find screen fragment.", Toast.LENGTH_SHORT).show();
        }
    }
    
    public static void loadScreen(String screenName, long idValue){

        Fragment fragmentToLoad;
        String appBarTitle;
        int fragmentHeight;

        if (screenName.equals("H2h")){
            if (mainActivity.h2hFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.h2hFragment;
            appBarTitle = "Head to head";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("Message")){
            if (mainActivity.newMessageFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.newMessageFragment;
            appBarTitle = "New message";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("Challenge")){
            if (mainActivity.newChallengeFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.newChallengeFragment;
            appBarTitle = "New challenge";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("Profile")){
            if (mainActivity.profileFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.profileFragment;
            appBarTitle = "Profile";
            fragmentHeight = BELOW_APP_BAR;
        }else if (screenName.equals("Route stats")){
            if (mainActivity.routeStatsFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.routeStatsFragment;
            appBarTitle = "Route stats";
            fragmentHeight = BELOW_APP_BAR;
        }else{
            Toast.makeText(mainActivity, "Couldn't identify screen: " + screenName + ".", Toast.LENGTH_SHORT).show();
            return;
        }
        ToolbarController.setToolbarTitle(appBarTitle);
        if (fragmentToLoad != null) {
            setFragmentContainerHeight(fragmentHeight);
            loadFragment(fragmentToLoad);
        }else{
            Toast.makeText(mainActivity, "Couldn't find screen fragment.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void loadScreen(String screenName, long idValue, boolean compareAchievements){

        Fragment fragmentToLoad;
        String appBarTitle;
        int fragmentHeight;

        if (screenName.equals("H2h")){
            if (mainActivity.h2hFragment == null){
                //homeFragment = new HomeFragment();
            }
            fragmentToLoad = mainActivity.h2hFragment;
            if (compareAchievements == true){
                //set h2h selected tab appropriately.
            }
            appBarTitle = "Head to head";
            fragmentHeight = BELOW_APP_BAR;
        }else{
            Toast.makeText(mainActivity, "Couldn't identify screen: " + screenName + ".", Toast.LENGTH_SHORT).show();
            return;
        }
        ToolbarController.setToolbarTitle(appBarTitle);
        if (fragmentToLoad != null) {
            setFragmentContainerHeight(fragmentHeight);
            loadFragment(fragmentToLoad);
        }else{
            Toast.makeText(mainActivity, "Couldn't find screen fragment.", Toast.LENGTH_SHORT).show();
        }
    }

    private static void setFragmentContainerHeight(int fragmentHeight){
        if (fragmentHeight == BELOW_APP_BAR){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainActivity.findViewById(R.id.fragmentContainer).getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.tool_bar);
        }else{  //= full screen.
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainActivity.findViewById(R.id.fragmentContainer).getLayoutParams();
            params.addRule(RelativeLayout.BELOW, 0);
        }
    }

    private static void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = (mainActivity.getFragmentManager());
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
