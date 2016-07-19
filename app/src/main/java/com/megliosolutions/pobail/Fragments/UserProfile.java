package com.megliosolutions.pobail.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.megliosolutions.pobail.R;

/**
 * Created by Meglio on 7/17/16.
 */
public class UserProfile extends Fragment {

    private static final String TAG = UserProfile.class.getSimpleName();
    public DatabaseReference mDatabase;
    public DatabaseReference mUserInfo;
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;
    public String currentUser;
    private String getUsername;
    private String getName;
    private String getMoto;
    private String setUsername;
    private String setName;
    private String setMoto;
    private TextView username_tv,name_tv,moto_tv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userprofile,container,false);
        setInstances(view);
        getUserInfo();
        UpdateTitle();
        updateUserInfo();

        return view;
    }

    private void getUserInfo() {

            mUserInfo.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "USER KEY : " + dataSnapshot.getKey());
                    Log.i(TAG, "USER INFO: " + dataSnapshot.child("username").getValue());
                    getUsername = (String) dataSnapshot.child("username").getValue();
                    getName = (String) dataSnapshot.child("name").getValue();
                    getMoto = (String) dataSnapshot.child("moto").getValue();
                    updateUserInfo();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i(TAG, "USER INFO-ERROR: " + databaseError.getMessage());
                }
            });

    }

    private void updateUserInfo() {
        //Set info from savedPreferences
        setName = getName;
        setUsername = getUsername;
        setMoto = getMoto;
        if(setName == null && setUsername == null && setMoto == null){
            name_tv.setText("Error Report this bug");
            username_tv.setText("Error Report this bug");
            moto_tv.setText("Error Report this bug");
        }else{
            name_tv.setText(setName);
            username_tv.setText(setUsername);
            moto_tv.setText(setMoto);
        }

    }

    private void UpdateTitle() {
        //Set Title to Description
        getActivity().setTitle("Profile");

    }

    private void setInstances(View view) {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserInfo = mDatabase.child("users");
        currentUser = mUser.getUid();
        username_tv = (TextView) view.findViewById(R.id.profile_user_username);
        name_tv = (TextView) view.findViewById(R.id.profile_user_name);
        moto_tv = (TextView) view.findViewById(R.id.profile_user_moto);
    }


}
