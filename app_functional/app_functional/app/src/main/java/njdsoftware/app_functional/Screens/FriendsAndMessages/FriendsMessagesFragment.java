package njdsoftware.app_functional.Screens.FriendsAndMessages;

import android.app.FragmentManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import njdsoftware.app_functional.MainActivity;
import njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.FriendRequests.FriendRequestsTab;
import njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Friends.FriendsTab;
import njdsoftware.app_functional.Screens.FriendsAndMessages.Tabs.Messages.MessagesTab;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.UniversalUtilities.ToolbarController;

/**
 * Created by Nathan on 12/08/2016.
 */
public class FriendsMessagesFragment extends Fragment {
    View thisFragmentView;
    TabLayout tabLayout;
    ViewPager viewPager;

    public FriendsMessagesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisFragmentView = inflater.inflate(R.layout.screens_friendsandmessages_friends_messages_fragment, container, false);
        getCommonComponents();
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        //changeTabsFont();
        return thisFragmentView;
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new FriendsTab(), "FRIENDS");
        adapter.addFragment(new FriendRequestsTab(), "REQUESTS");
        adapter.addFragment(new MessagesTab(), "MESSAGES");
        viewPager.setAdapter(adapter);
    }

    private void getCommonComponents(){
        tabLayout = (TabLayout) ToolbarController.toolbar.findViewById(R.id.friends_messages_tabs);
        viewPager = (ViewPager) thisFragmentView.findViewById(R.id.friends_messages_viewpager);
    }

    // Class necessary to make the tabs work.
    // Populates them + enables tab-related actions to occur.
    // Should use in each fragment that contains tabs.
    // Note that gradle dependency compile 'com.android.support:support-v13:+' is needed for FragmentPagerAdapter.
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void changeTabsFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(null, Typeface.BOLD); //can set to any font type here.
                }
            }
        }
    }
}
