package com.cs.cardwhere;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class CardFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private Context context;

    public CardFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList,  Context context) {
        super(fm);
        this.fragmentList = fragmentList;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        String Cards = context.getString(R.string.title_card);
        String Map = context.getString(R.string.title_activity_maps);

        switch (position) {
            case 0:
                return Cards;
            case 1:
                return Map;
            default:
                return null;
        }
    }

}
