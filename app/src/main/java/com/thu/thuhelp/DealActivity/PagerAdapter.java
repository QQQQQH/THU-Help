package com.thu.thuhelp.DealActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;
    private int state;

    public PagerAdapter(FragmentManager fragmentManager, int numOfTabs, int state) {
        super(fragmentManager);
        this.numOfTabs = numOfTabs;
        this.state = state;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new DealListFragment(state, DealListActivity.INTI_NO);
        }
        return new DealListFragment(state, DealListActivity.INIT_YES);
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
