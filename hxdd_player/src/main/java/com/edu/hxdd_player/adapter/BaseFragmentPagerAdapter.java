package com.edu.hxdd_player.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;


/**
 * Created by Admin on 2016/8/5.
 */
public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    protected String[] mTabs;
    protected List<Fragment> frags;

    public BaseFragmentPagerAdapter(FragmentManager fm, String[] mTabs, List<Fragment> frags) {
        super(fm);
        this.mTabs = mTabs;
        this.frags = frags;
        if(mTabs.length!=frags.size()){
            throw new RuntimeException("FragmentPagerAdapter初始化错误！");
        }
    }

    @Override
    public Fragment getItem(int position) {
        return frags.get(position);
    }

    @Override
    public int getCount() {
        return mTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs[position];
    }
}
