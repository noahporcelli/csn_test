package njdsoftware.app_functional.UniversalUtilities;

import android.app.Fragment;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import njdsoftware.app_functional.CustomWidgets.CustomFontTextView;
import njdsoftware.app_functional.CustomWidgets.TextButton;
import njdsoftware.app_functional.MainActivity;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Friends.FriendsTab;
import njdsoftware.app_functional.Screens.TimeTrial.TimeTrialScreenFragment;

/**
 * Created by Nathan on 07/09/2016.
 */
public class ToolbarController {
    public static RelativeLayout toolbar;
    public static CustomFontTextView appBarText, expandIcon, collapseIcon;
    public static TextButton createRouteBtn, previewBackBtn, cancelCreateBtn;
    public static ImageButton menuButton, moreIcon;
    static MainActivity mainActivity = (MainActivity) MainActivity.getContext();

    public static void setToolbarTitle(String title){
        appBarText.setText(title);
    }
    public static void setupToolbar(){
        getCommonComponents();
        //menuButton.setColorFilter(0xff00ff00);
        menuButton.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                menuButtonClick();
            }
        });
        expandIcon.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                expandToolbar();
            }
        });
        collapseIcon.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                collapseToolbar();
            }
        });
        createRouteBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                TimeTrialScreenFragment.createRouteBtnClick();
            }
        });
        cancelCreateBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                TimeTrialScreenFragment.cancelCreateBtnClick();
            }
        });
        previewBackBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                TimeTrialScreenFragment.previewBackBtnClick();
            }
        });
    }
    private static void getCommonComponents(){
        toolbar = (RelativeLayout) mainActivity.findViewById(R.id.tool_bar);
        menuButton = (ImageButton) toolbar.findViewById(R.id.app_bar_menu_icon);
        appBarText = (CustomFontTextView) toolbar.findViewById(R.id.app_bar_text);
        moreIcon = (ImageButton) toolbar.findViewById(R.id.app_bar_more_icon);
        expandIcon = (CustomFontTextView) toolbar.findViewById(R.id.app_bar_expand_icon);
        collapseIcon = (CustomFontTextView) toolbar.findViewById(R.id.app_bar_collapse_icon);
        createRouteBtn = (TextButton) toolbar.findViewById(R.id.createRouteBtn);
        cancelCreateBtn = (TextButton) toolbar.findViewById(R.id.cancelCreateBtn);
        previewBackBtn = (TextButton) toolbar.findViewById(R.id.previewBackBtn);
    }
    public static void menuButtonClick(){
        PopupMenu popup = new PopupMenu(mainActivity, menuButton);
        popup.getMenuInflater().inflate(R.menu.menu_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String itemText = (String)item.getTitle();
                ScreenManager.loadScreen(itemText);
                return true;
            }
        });
        popup.show();
    }
    public static void expandToolbar(){
        expandLayout();
        //fadeInToolbarContents();
    }
    private static void fadeInToolbarContents(){
        AppAnimation.fadeIn(menuButton);
        AppAnimation.fadeIn(appBarText);
        AppAnimation.fadeIn(collapseIcon);
        AppAnimation.fadeIn(moreIcon);
    }
    private static void expandLayout(){
        menuButton.setVisibility(View.VISIBLE);
        appBarText.setVisibility(View.VISIBLE);
        collapseIcon.setVisibility(View.VISIBLE);
        moreIcon.setVisibility(View.VISIBLE);
        expandIcon.setVisibility(View.GONE);
    }
    public static void collapseToolbar(){
        fadeOutToolbarContents();
        collapseLayout();
        //fadeInExpandButton();
    }
    private static void fadeInExpandButton(){
        AppAnimation.fadeIn(expandIcon);
    }
    private static void collapseLayout(){
        moreIcon.setVisibility(View.GONE);
        collapseIcon.setVisibility(View.GONE);
        appBarText.setVisibility(View.GONE);
        menuButton.setVisibility(View.GONE);
        expandIcon.setVisibility(View.VISIBLE);
    }
    private static void fadeOutToolbarContents(){
        //not fading out for the moment since deemed unnecessary.
    }
    public static void showCreateRouteBtn(){
        createRouteBtn.setVisibility(View.VISIBLE);
    }
    public static void hideCreateRouteBtn(){
        createRouteBtn.setVisibility(View.GONE);
    }
    public static void showCancelCreateBtn(){
        cancelCreateBtn.setVisibility(View.VISIBLE);
    }
    public static void hideCancelCreateBtn(){
        cancelCreateBtn.setVisibility(View.GONE);
    }
    public static void showPreviewBackBtn(){
        previewBackBtn.setVisibility(View.VISIBLE);
    }
    public static void hidePreviewBackBtn(){
        previewBackBtn.setVisibility(View.GONE);
    }
    public static void showFriendsMessagesTabs(){
        toolbar.findViewById(R.id.friends_messages_tabs).setVisibility(View.VISIBLE);
    }
    public static void hideFriendsMessagesTabs(){
        toolbar.findViewById(R.id.friends_messages_tabs).setVisibility(View.GONE);
    }
}
