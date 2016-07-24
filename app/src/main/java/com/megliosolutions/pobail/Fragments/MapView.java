package com.megliosolutions.pobail.Fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.megliosolutions.pobail.Objects.TagObject;
import com.megliosolutions.pobail.Objects.TagProperty;
import com.megliosolutions.pobail.R;

import java.util.ArrayList;

public class MapView extends Fragment implements OnMapReadyCallback {

    public static final String TAG = MapView.class.getSimpleName();

    public String static_ip;
    public String desc;
    public double lat;
    public double mLong;
    public String mKey;
    public String selectedKey;
    public String childTag = "tags";
    public String childTagProperty = "tagproperty";
    public String tag_title;
    public String permissions;
    public String currentUser;
    public String keys;
    public String title;

    public DatabaseReference mDatabase;
    public DatabaseReference mTag;
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;

    public ArrayList<TagObject> tagObjectArrayList = new ArrayList<>();
    public ArrayList<TagObject> tagPropertyArray = new ArrayList<>();
    public TagObject tag;
    public TagProperty tagProperty;

    public MapFragment mapFragment;

    public GoogleMap googleMap;
    public MarkerOptions markerOptions;

    public View view;

    public String[] requestPermissionsStrings = {Manifest.permission_group.LOCATION};
    public int PERMISSION_GRANTED = 1;
    public int PERMISSION_DENIED = 0;
    public int[] requestGrantResults = {PERMISSION_DENIED, PERMISSION_GRANTED};
    public static final int requestPermissionCode = 101;

    public ProgressDialog myProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mapview, container, false);
        this.getChildFragmentManager();
        Log.d(TAG, "onCreateView: After View created TAGS = " + tagObjectArrayList.size());

        //ChangeTitle
        UpdateTitle();
        Log.d(TAG, "onCreateView: After Title Updated created TAGS = " + tagObjectArrayList.size());

        //Set Instances
        setInstances();
        Log.d(TAG, "onCreateView: After Views Initialized created TAGS = " + tagObjectArrayList.size());

        pullTags();
        Log.d(TAG, "onCreateView: After Tags pulled from Firebase = " + tagObjectArrayList.size());
        //SetMap
        try {
            if (mapFragment == null) {
                mapFragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.node_maps);
                mapFragment.getMapAsync(this);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onCreateView: CATCH EXCEPTION");
        }
        Log.d(TAG, "onCreateView: After MapFragment has been loaded = " + tagObjectArrayList.size());

        //Convert LatLong to MGRS
        //ConvertToMGRS();

        //Log Data
        logDataFromVariables();

        return view;
    }

    private void pullTags() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TagObject mTag = dataSnapshot.getValue(TagObject.class);

                tagObjectArrayList.add(mTag);
                Log.d(TAG, "onChildAdded: " + tagObjectArrayList.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                tag = dataSnapshot.getValue(TagObject.class);

                tagObjectArrayList.add(tag);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /*tag = dataSnapshot.getValue(TagObject.class);

                tagObjectArrayList.remove(tag);*/
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                tag = dataSnapshot.getValue(TagObject.class);

                tagObjectArrayList.add(tag);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        };
        mTag.child(mUser.getUid()).addChildEventListener(childEventListener);
    }


    private void logDataFromVariables() {
        //Log some stuff
        Log.i(TAG, "USER: " + currentUser);
        Log.d(TAG, "logDataFromVariables: tagArraySize = " + tagObjectArrayList.size());

    }

    private void setInstances() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mTag = mDatabase.child(childTag);
        //mTagProperty = mDatabase.child(childTag).child(selectedKey).child(childTagProperty);
        Log.i(TAG, "SetInstances-SelectedKey: " + selectedKey);
        currentUser = mUser.getUid();
    }

    private void GenerateKey() {
        mKey = mDatabase.push().getKey();
        if (mKey.equalsIgnoreCase(mKey)) {
            mKey = mDatabase.push().getKey();
            Log.i(TAG, "NEW TAG CHILD KEY: " + mKey);
        }

        Log.i(TAG, "TAGPROPERTY CHILD KEY: " + mKey);
    }

    private void ConvertToMGRS() {
        //Convert Coordinates

    }

    private void UpdateTitle() {
        //Set Title to Description
        getActivity().setTitle("Map");
    }


    public void permissionDeniedToast() {
        Toast.makeText(getActivity(), "Location permissions were denied. Accept, and then restart the app.", Toast.LENGTH_SHORT).show();
    }

    public void permissionRationaleToast() {
        Toast.makeText(getActivity(), "Location permissions are needed to use the features of this app.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        tagObjectArrayList.size();
        googleMap = map;
        setUpMap(googleMap);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        } else {
            // Show rationale and request permission.
            permissionDeniedToast();

        }

        // Add ten cluster items in close proximity, for purposes of this example.
        Log.d(TAG, "onMapReady: " + tagObjectArrayList.size());
        /*
        */
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                for (int i = 0; i < tagObjectArrayList.size(); i++) {
                    double lat = tagObjectArrayList.get(i).getLat();
                    double lng = tagObjectArrayList.get(i).getLng();
                    String title = tagObjectArrayList.get(i).getTag_title();

                    markerOptions = new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(title);

                    googleMap.addMarker(markerOptions);
                }
            }
        });
        Log.d(TAG, "populateMarkers: ADDED MARKERS");

    }

    public void setUpMap(GoogleMap map) {
        this.googleMap = map;
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.d(TAG, "onMapLoaded: CALLBACK LOADED");
                //progressBarStop();
            }
        });
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                GenerateKey();
                //set a marker
                //AlertDialog
                lat = latLng.latitude;
                mLong = latLng.longitude;
                final AlertDialog.Builder tagBuilder = new AlertDialog.Builder(getActivity());
                tagBuilder.setTitle("Tag");
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.tag_new_dialog, null);
                tagBuilder.setView(dialogView);
                final EditText title_et = (EditText)
                        dialogView.findViewById(R.id.tag_et_title);
                tagBuilder.setPositiveButton("Save Tag", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tag_title = title_et.getText().toString();
                        title = tag_title;
                        permissions = "";
                        TagObject mTag = new TagObject(title, lat, mLong, mKey,permissions);
                        mDatabase.child("tags").child(mUser.getUid()).child(mKey).setValue(mTag);
                        Toast.makeText(getActivity(), "Tag Added!"
                                , Toast.LENGTH_SHORT).show();
                        //now add the marker after it is saved.
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat,mLong))
                                .title(title));
                    }
                }).
                        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "Cool, maybe later."
                                        , Toast.LENGTH_SHORT).show();
                            }
                        });
                tagBuilder.create().show();
                // Once the dialog is dismissed add the marker with the info saved.


            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == requestPermissionCode) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            requestPermissionCode);
                    return;
                }
                googleMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
                permissionDeniedToast();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        googleMap = mapFragment.getMap();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}

