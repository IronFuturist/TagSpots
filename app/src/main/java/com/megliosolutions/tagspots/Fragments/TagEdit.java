package com.megliosolutions.tagspots.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.megliosolutions.tagspots.Objects.TagObject;
import com.megliosolutions.tagspots.R;

/**
 * Created by Wesley on 7/28/2016.
 *
 */

public class TagEdit extends Fragment {

    public String created, title, category, mKey, permission;
    public double mLat,mLong;
    public String tagsRef = "tags";
    public TextView created_tv, title_tv, category_tv;
    public Spinner permission_Spinner;
    public Button saveTagButton;
    public DatabaseReference mDatabase;
    public DatabaseReference mTags;
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;

    public static final String TAG = TagEdit.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tagedit,container,false);
        UpdateTitle();
        initializeStuff(view);
        setArguments();
        saveTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton();
            }
        });


        return view;
    }

    private void initializeStuff(View view) {
        created_tv = (TextView) view.findViewById(R.id.tagedit_created);
        title_tv = (TextView) view.findViewById(R.id.tagedit_title);
        category_tv = (TextView) view.findViewById(R.id.tagedit_category);
        saveTagButton = (Button) view.findViewById(R.id.tagedit_button_save);
        permission_Spinner = (Spinner) view.findViewById(R.id.tagedit_permissions_spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (getActivity(),android.R.layout.simple_list_item_1,
                        getResources().getStringArray(R.array.tagEdit_Permissions));
        permission_Spinner.setAdapter(arrayAdapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mTags = mDatabase.child(tagsRef);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    private void saveButton() {
        TagObject tag = new TagObject();
        title = title_tv.getText().toString().trim();
        category = category_tv.getText().toString().trim();
        permission = permission_Spinner.getSelectedItem().toString();
        if(title.matches("") && category.matches("")){
            Log.d(TAG, "saveButton: TextViews Empty");
            Toast.makeText(getActivity(),"Enter All Info Needed",Toast.LENGTH_SHORT).show();
        }else{
            tag.setTag_title(title);
            tag.setPermission(permission);
            tag.setCategory(category);
            tag.setCreated(created);
            tag.setKey(mKey);
            tag.setLat(mLat);
            tag.setLng(mLong);
            mTags.child(mUser.getUid()).push().setValue(tag);
        }
        goToMap();
    }

    private void goToMap() {
        MapView mapView = new MapView();
        FragmentManager fragmentManager = getFragmentManager();
        //Replace intent with Bundle and put it in the transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_FrameLayout, mapView);
        fragmentTransaction.commit();
    }

    private void setArguments() {
        Bundle b = getArguments();
        created = b.getString("created","never");
        mKey = b.getString("key", "key");
        mLat = b.getDouble("lat",0);
        mLong = b.getDouble("long",0);
        created_tv.setText(created);
    }

    private void UpdateTitle() {

        //Set Title to Description
        getActivity().setTitle("Edit Tag");

    }
}
