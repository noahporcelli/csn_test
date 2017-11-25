package njdsoftware.app_functional;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import njdsoftware.app_functional.CustomWidgets.CustomFontTextView;
import njdsoftware.app_functional.UniversalUtilities.ScreenManager;
import njdsoftware.app_functional.UniversalUtilities.ToolbarController;

public class MainActivity extends AppCompatActivity {
    public FrameLayout fragmentContainer;
    public Fragment homeFragment;
    public Fragment profileFragment;
    public Fragment timeTrialFragment;
    public Fragment achievementsFragment;
    public Fragment friendsMessagesFragment;
    public Fragment challengesFragment;
    public Fragment h2hFragment;
    public Fragment newMessageFragment;
    public Fragment newChallengeFragment;
    public Fragment routeStatsFragment;
    private static Activity thisActivity;

    public static Context getContext(){ //got this from stack overflow.
        return thisActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupContextReference();
        getCommonComponents();
        ToolbarController.setupToolbar();
        ScreenManager.loadScreen("Friends + messages");
    }

    private void setupContextReference(){
        thisActivity = this;
    }

    private void getCommonComponents(){
        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
    }

}