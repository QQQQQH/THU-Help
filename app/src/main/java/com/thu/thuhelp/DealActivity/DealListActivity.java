package com.thu.thuhelp.DealActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;
import com.thu.thuhelp.MainActivity.MyFragment;
import com.thu.thuhelp.R;

public class DealListActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FrameLayout frameLayout;
    private PagerAdapter adapter;
    private int state;

    public static final int
            INIT_YES = 1,
            INTI_NO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_list);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);
        frameLayout = findViewById(R.id.fragment_layout);

        // set return actionBar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        state = intent.getIntExtra(MyFragment.EXTRA_DEAL_STATE, MyFragment.DEAL_PUBLISH);
        if (state == MyFragment.DEAL_PUBLISH) {
            // Publish -- 1 fragment
            actionBar.setTitle(R.string.my_publish);
            tabLayout.setVisibility(View.GONE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            DealListFragment dealListFragment = new DealListFragment(state, INIT_YES);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_layout, dealListFragment);
            fragmentTransaction.commit();
        } else {
            // Other -- 2 fragments
            switch (state) {
                case MyFragment.DEAL_ACCEPT:
                    actionBar.setTitle(R.string.my_accept);
                    break;
                case MyFragment.DEAL_CONFIRM:
                    actionBar.setTitle(R.string.my_confirm);
                    break;
                case MyFragment.DEAL_FINISH:
                    actionBar.setTitle(R.string.my_finish);
                    break;
            }
            frameLayout.setVisibility(View.GONE);
            tabLayout.addTab(tabLayout.newTab().setText(R.string.published_by_me));
            tabLayout.addTab(tabLayout.newTab().setText(R.string.published_by_others));
            adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), state);
            viewPager.setAdapter(adapter);

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }
    }

    // set return actionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
