package com.megliosolutions.pobail.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.megliosolutions.pobail.Objects.TagProperty;
import com.megliosolutions.pobail.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 7/24/2016.
 */

public class TagPropertyList extends Fragment {

    public ListView mTagList;
    public FloatingActionButton mActionButton;
    public List<TagProperty> propertyList = new ArrayList<>();
    public String key;
    public String value;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_property,container,false);
        initializeStuff(view);

        SetupFloatingButton(view);

        getActivity().onBackPressed();

        return view;
    }

    private void SetupFloatingButton(View view) {

        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key = "Key";
                value = "Value";
                propertyList.add(new TagProperty(key,value));
            }
        });
    }

    private void initializeStuff(View view) {
        mActionButton = (FloatingActionButton)view.findViewById(R.id.tag_property_floatingAction);
        mTagList = (ListView) view.findViewById(R.id.tag_list);

    }




}
