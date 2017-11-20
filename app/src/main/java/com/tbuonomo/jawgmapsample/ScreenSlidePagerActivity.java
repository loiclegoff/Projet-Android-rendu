package com.tbuonomo.jawgmapsample;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by leo on 26/10/17.
 */

public class ScreenSlidePagerActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;
    private static final int MAP_PAGE_NUM = 0;
    private static final int HOME_PAGE_NUM = 1;
    private static final int CAMERA_PAGE_NUM = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Loading fonts
        Typeface cabinSketch = Typeface.createFromAsset(getAssets(), "CabinSketch.ttf");

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(HOME_PAGE_NUM);

        //Bind the tab menu to the pager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mPager);

        //Add custom icons to the tab menu
        tabLayout.getTabAt(MAP_PAGE_NUM).setIcon(R.drawable.selector_map);
        tabLayout.getTabAt(CAMERA_PAGE_NUM).setIcon(R.drawable.selector_camera);

        //Change the Font for the tab menu
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(cabinSketch);
                    ((TextView) tabViewChild).setTextSize(1, 15);
                }
            }
        }

    }

    /**
     * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case MAP_PAGE_NUM:
                    Log.e("PagerActivity", "New Map");
                    return new MapFragment();
                case HOME_PAGE_NUM:
                    Log.e("PagerActivity", "New Default");
                    return new HomeFragment();
                case CAMERA_PAGE_NUM:
                    Log.e("PagerActivity", "New Camera");
                    return new CameraFragment();
                default:
                    return ScreenSlidePageFragment.create(position);
            }

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                //case MAP_PAGE_NUM:
                //    return "MAP";
                case HOME_PAGE_NUM:
                    return "HOME";
                //case CAMERA_PAGE_NUM:
                //    return "CAMERA";
                default:
                    return "";
            }
        }
    }
}

