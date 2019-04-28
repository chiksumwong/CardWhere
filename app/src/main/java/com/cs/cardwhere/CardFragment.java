package com.cs.cardwhere;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CardFragment extends Fragment {

    View view;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_card, container, false);

        mViewPager = view.findViewById(R.id.pager);
        mTabLayout = view.findViewById(R.id.tabs);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setViewPager();
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setViewPager(){
        CardListFragment cardListFragment = new CardListFragment();
        MapsFragment mapsFragment = new MapsFragment();

        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(cardListFragment);
        fragmentList.add(mapsFragment);

        CardPagerFragmentAdapter myFragmentAdapter = new CardPagerFragmentAdapter(getActivity().getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(myFragmentAdapter);
    }




}
