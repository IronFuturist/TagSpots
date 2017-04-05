package com.megliosolutions.tagspots.Fragments;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.megliosolutions.tagspots.Objects.TagObject;
import com.megliosolutions.tagspots.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MapView extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MapView.class.getSimpleName();
    public static final int ACCESS_REQUEST = 123;

    public double user_lat;
    public double user_long;
    public double lat;
    public double mLong;
    public String mKey;
    public String selectedKey;
    public String childTag = "tags";
    public String currentUser;
    public String title;
    public String created;


    public DatabaseReference mDatabase;
    public DatabaseReference mTag;
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;

    public ArrayList<TagObject> userTagsArray = new ArrayList<>();
    public ArrayList<TagObject> publicTagArray = new ArrayList<>();
    public ArrayList<TagObject> friendTagsArray = new ArrayList<>();
    public TagObject tag;

    public SupportMapFragment mapFragment;

    public GoogleMap googleMap;
    public MarkerOptions markerOptions;

    public GoogleApiClient mGoogleApiClient;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int permission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission_group.LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to write to external storage is denied");
            MakePermissionsRequest();
        }
        else{
            Log.i(TAG,"Permission Granted!!!!");
        }
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapview, container, false);

        //ChangeTitle
        UpdateTitle();

        //Set Instances
        setInstances();

        pullCurrentUserTags();
        //SetMap
        setMap();
        return view;
    }

    protected void MakePermissionsRequest() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission_group.LOCATION},
                ACCESS_REQUEST);
    }

    private void setMap() {
        try {
            if (mapFragment == null) {
                mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.node_maps);
                mapFragment.getMapAsync(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onCreateView: CATCH EXCEPTION");
        }
    }

    private void pullPublicTags(){
       // mTag.
    }

    private void pullCurrentUserTags() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TagObject mTag = dataSnapshot.getValue(TagObject.class);

                userTagsArray.add(mTag);
                Log.d(TAG, "onChildAdded: " + userTagsArray.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                tag = dataSnapshot.getValue(TagObject.class);

                userTagsArray.add(tag);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /*tag = dataSnapshot.getValue(TagObject.class);

                userTagsArray.remove(tag);*/
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                tag = dataSnapshot.getValue(TagObject.class);

                userTagsArray.add(tag);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        };
        mTag.child(mUser.getUid()).addChildEventListener(childEventListener);
    }

    private void pullPrivateTags() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TagObject mTag = dataSnapshot.getValue(TagObject.class);

                userTagsArray.add(mTag);
                Log.d(TAG, "onChildAdded: " + userTagsArray.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                tag = dataSnapshot.getValue(TagObject.class);

                userTagsArray.add(tag);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /*tag = dataSnapshot.getValue(TagObject.class);

                userTagsArray.remove(tag);*/
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                tag = dataSnapshot.getValue(TagObject.class);

                userTagsArray.add(tag);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        };
        mTag.child(mUser.getUid()).addChildEventListener(childEventListener);
    }


    private void pullFriendTags() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TagObject mTag = dataSnapshot.getValue(TagObject.class);

                userTagsArray.add(mTag);
                Log.d(TAG, "onChildAdded: " + userTagsArray.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                tag = dataSnapshot.getValue(TagObject.class);

                userTagsArray.add(tag);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /*tag = dataSnapshot.getValue(TagObject.class);

                userTagsArray.remove(tag);*/
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                tag = dataSnapshot.getValue(TagObject.class);

                userTagsArray.add(tag);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        };
        mTag.child(mUser.getUid()).addChildEventListener(childEventListener);
    }


    private void setInstances() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mTag = mDatabase.child(childTag);
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

    private void UpdateTitle() {
        //Set Title to Description
        getActivity().setTitle("Map");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setUpMap(googleMap);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        }
        //ON map loaded load the tags on the map for the current user.
        //Then move the camera to the user's location
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                for (int i = 0; i < userTagsArray.size(); i++) {
                    String permission = userTagsArray.get(i).getPermission();
                    if(permission.equalsIgnoreCase("public")){

                    }
                    else if(permission.equalsIgnoreCase("private")){

                    }
                    else if(permission.equalsIgnoreCase("friends")){

                    }
                    double lat = userTagsArray.get(i).getLat();
                    double lng = userTagsArray.get(i).getLng();
                    String title = userTagsArray.get(i).getTag_title();

                    markerOptions = new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(title);

                    googleMap.addMarker(markerOptions);

                    LatLng me = new LatLng(user_lat,user_long);

                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(me)
                            .zoom(19)
                            .bearing(90)
                            .build();

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me,19));

                    /*googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                            1000,null);*/

                    mGoogleApiClient.disconnect();

                }
            }
        });
        Log.d(TAG, "populateMarkers: ADDED MARKERS");

    }

    public void tagCreated() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:s a z", Locale.ENGLISH);
        String date = dateFormat.format(new Date());
        String time = timeFormat.format(new Date());
        created = String.format("%s - %s", date, time);
        Log.d(TAG, "tagCreated: " + created);
    }


    public void setUpMap(GoogleMap map) {
        this.googleMap = map;

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                GenerateKey();
                tagCreated();
                //set a marker
                //AlertDialog
                lat = latLng.latitude;
                mLong = latLng.longitude;
                Log.i("TagEditScreen", " LOADED");
                TagEdit tagEdit = new TagEdit();
                FragmentManager fragmentManager = getFragmentManager();
                Bundle bundle = new Bundle();
                //Create title in TagEdit
                bundle.putString("created", created);
                bundle.putString("key", mKey);
                bundle.putDouble("lat", lat);
                bundle.putDouble("long", mLong);
                //Select permission in Tagedit
                tagEdit.setArguments(bundle);
                //Replace intent with Bundle and put it in the transaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_FrameLayout, tagEdit);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        //What to do onResume?!?!?
    }

    @Override
    public void onPause() {
        super.onPause();

        mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            user_lat = mLastLocation.getLatitude();
            user_long = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
/*
    @Override
    public void onMapLoaded() {
        //Animate camera to current location after map loads
        // Get the shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

// Get the zoom level (change the 13f to the default zoom level that you want)
        float zoom = sharedPreferences.getFloat("Zoom_value", 13f);

        LatLng me = new LatLng(user_lat,user_long);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(googleMap.getCameraPosition().target)
                .zoom(zoom)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 19));

        *//*googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        Log.d(TAG, "onMapLoaded: CALLBACK LOADED");*//*
        //progressBarStop();
    }*/
}

