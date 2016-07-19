package com.megliosolutions.pobail.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.megliosolutions.pobail.R;

/**
 * Created by Meglio on 7/17/16.
 */
public class UserProfile extends Fragment {

    public DatabaseReference mDatabase;
    public DatabaseReference mUserInfo;
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;
    public String currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userprofile,container,false);
        setInstances();
        UpdateTitle();
        updateUserInfo();

        return view;
    }

    private void updateUserInfo() {
        //Set info from savedPreferences
    }

    private void UpdateTitle() {
        //Set Title to Description
        getActivity().setTitle("Profile");

    }

    private void setInstances() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserInfo = mDatabase.child("users");
        currentUser = mUser.getUid();
    }
}
