package com.megliosolutions.pobail.Fragments;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import com.megliosolutions.pobail.Adapters.ViewPagerAdapter;
import com.megliosolutions.pobail.MainActivity;
import com.megliosolutions.pobail.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Meglio on 7/22/16.
 */
public class ProfileViewPager extends Fragment {

    public ViewPager viewPager;
    public TabLayout tabLayout;

    public ProfileViewPager() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_viewpager, container,false);

        //Switch all fragments to Support Library

        return view;
    }


}