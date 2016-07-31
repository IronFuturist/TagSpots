package com.megliosolutions.pobail.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.megliosolutions.pobail.R;

/**
 * Created by Wesley on 7/28/2016.
 *
 */

public class TagEdit extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tagedit,container,false);
        UpdateTitle();
        setViews();

        return view;
    }

    private void setViews() {

    }

    private void UpdateTitle() {

        //Set Title to Description
        getActivity().setTitle("Edit Tag");

    }
}
