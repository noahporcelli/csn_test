package njdsoftware.app_functional.Screens.Feeds;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import njdsoftware.app_functional.Screens.Feeds.Tabs.NewsFeed.NewsFeedTab;
import njdsoftware.app_functional.R;

/**
 * Created by Nathan on 12/08/2016.
 */
public class FeedsFragment extends Fragment {
    View thisFragmentView;
    TabLayout tabLayout;
    ViewPager viewPager;

    public FeedsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisFragmentView = inflater.inflate(R.layout.screens_feeds_feeds_fragment, container, false);
        getCommonComponents();
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        return thisFragmentView;
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new NewsFeedTab(), "NEWS FEED");
        //adapter.addFragment(new MyFeedTab(), "MY FEED");
        viewPager.setAdapter(adapter);
    }

    private void getCommonComponents(){
        tabLayout = (TabLayout) thisFragmentView.findViewById(R.id.feed_tabs);
        viewPager = (ViewPager) thisFragmentView.findViewById(R.id.feeds_viewpager);
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
}
