package com.megliosolutions.pobail;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.megliosolutions.pobail.Adapters.StaticListAdapter;
import com.megliosolutions.pobail.Fragments.HashTag;
import com.megliosolutions.pobail.Fragments.MapView;
import com.megliosolutions.pobail.Fragments.ProfileViewPager;
import com.megliosolutions.pobail.Fragments.Settings;
import com.megliosolutions.pobail.Fragments.UserProfile;
import com.megliosolutions.pobail.Objects.TagObject;
import com.megliosolutions.pobail.Objects.UserObject;
import com.megliosolutions.pobail.Utils.Login;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private static final String FIRST_TIME = "first_time";

    //TAG STRING
    public static final String TAG = MainActivity.class.getSimpleName();


    //Views
    public ListView main_ListView;
    public TextView nav_Header;

    //FragmentManager & Transaction
    public FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;

    //Fragments
    public MapView mapView;

    //Bundle
    public Bundle bundle;

    //Firebase
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;
    public DatabaseReference mDatabase;
    public DatabaseReference mNodeRef;

    //Strings
    public String static_ip;
    public String desc;
    public String mKey;
    public String currentUser;
    public String getUsername;
    public String setUsername;
    public String getName;
    public String setName;
    public String getMoto;
    public String setMoto;
    public String keys;
    public static final String TAG_MAPVIEW_FRAGMENT = "TAG_MAPVIEW_FRAGMENT";
    public static final String TAG_HASHTAG_FRAGMENT = "TAG_HASHTAG_FRAGMENT";
    public static final String TAG_SETTINGS_FRAGMENT = "TAG_SETTINGS_FRAGMENT";
    public static final String TAG_FRIENDS_FRAGMENT = "TAG_FRIENDS_FRAGMENT";
    public static final String TAG_PROFILE_FRAGMENT = "TAG_PROFILE_FRAGMENT";

    //doubles
    public double lat;
    public double mLong;

    //ints
    public int position = 0;
    public int mSelectedID;

    //Adapters
    public StaticListAdapter listAdapter;

    //booleans
    public boolean mUserSawDrawer = false;

    //Objects
    public UserObject userObject;
    public TagObject tag;

    //Nav Drawer stuff
    public DrawerLayout mDrawerLayout;
    public NavigationView mNavView;
    public ActionBarDrawerToggle drawerToggle;

    //toolbar
    public Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instances
        setInstances();

        //Initialize View From XML
        InitializeStuff();

        //Check username & set it
        checkUsername();

        //Toolbar
        setToolbar();

        //Setup Nav Drawer stuff
        setViews();

        //Set FrameLayout
        SetFrameLayout();

        //Generate key for nodes
        //GenerateKey();

        //Set Adapter
        //SetAdapter();

        //Set ClickListeners
        //setClickListeners();

        //Logstuff
        //logDataFromVariables();

        //NavDrawer Setup
        mNavView = (NavigationView) findViewById(R.id.navigationView);
        if(mNavView != null){
            mNavView.setNavigationItemSelectedListener(this);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //Load Saved State
        loadSavedState(savedInstanceState);

        //Navigate to ID
        navToID(mSelectedID);

        Log.d(TAG, "User Saw Drawer: " + mUserSawDrawer);
        //Did user see drawer?
        // use thread for performance
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                SharedPreferences sp = getSharedPreferences("yoursharedprefs", 0);
                mUserSawDrawer = sp.getBoolean("key", true);
                // we will not get a value  at first start, so true will be returned

                // if it was the first app start
                if(mUserSawDrawer) {
                    openDrawer();
                    SharedPreferences.Editor e = sp.edit();
                    // we save the value "false", indicating that it is no longer the first appstart
                    e.putBoolean("key", false);
                    e.apply();
                }
            }
        });

        t.start();
    }

    public void openDrawer(){
        mDrawerLayout.openDrawer(GravityCompat.START);
        Log.d(TAG, "Drawer was OPENED ");
    }

    public void closeDrawer(){
        mDrawerLayout.closeDrawer(GravityCompat.START);
        Log.d(TAG, "Drawer was CLOSED ");
    }

    private void navToID(int mSelectedID) {
        if(mSelectedID == R.id.nav_id_map){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Log.i("MAPVIEW", " LOADED");
            MapView fragment = new MapView();
            fragmentManager = getFragmentManager();
            //Replace intent with Bundle and put it in the transaction
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_FrameLayout, fragment);
            fragmentTransaction.commit();
        }
        if(mSelectedID == R.id.nav_id_tag){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Log.i("TAG", " LOADED");
            HashTag tag = new HashTag();
            fragmentManager = getFragmentManager();
            //Replace intent with Bundle and put it in the transaction
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_FrameLayout, tag);
            fragmentTransaction.commit();
        }
        if(mSelectedID == R.id.nav_id_profile){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Log.i("PROFILE", " LOADED");
            ProfileViewPager profile = new ProfileViewPager();
            fragmentManager = getFragmentManager();
            //Replace intent with Bundle and put it in the transaction
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_FrameLayout, profile);
            fragmentTransaction.commit();
        }
        if(mSelectedID == R.id.nav_id_settings){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Log.i("SETTINGS", " LOADED");
            Settings settings = new Settings();
            fragmentManager = getFragmentManager();
            //Replace intent with Bundle and put it in the transaction
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_FrameLayout, settings);
            fragmentTransaction.commit();
        }
    }

    private void loadSavedState(Bundle savedInstanceState) {
        mSelectedID = savedInstanceState == null ?
                R.id.nav_id_map : savedInstanceState.getInt(SELECTED_ITEM_ID);
    }

    private void setViews() {
        nav_Header = (TextView) findViewById(R.id.nav_header_text);
    }

    private void SetFrameLayout() {
        mapView = new MapView();
        fragmentManager = getFragmentManager();
        //Replace intent with Bundle and put it in the transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_FrameLayout, mapView);
        fragmentTransaction.commit();
    }

    private void setInstances() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mNodeRef = mDatabase.child("nodes");
        currentUser = mUser.getUid();
    }

    private void checkUsername() {

        mDatabase.child("users").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "USER KEY : " + dataSnapshot.getKey());
                Log.i(TAG, "USER INFO: " + dataSnapshot.child("username").getValue());
                getUsername = (String) dataSnapshot.child("username").getValue();
                getName = (String) dataSnapshot.child("name").getValue();
                getMoto = (String) dataSnapshot.child("moto").getValue();
                setProfileInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "USER INFO-ERROR: " + databaseError.getMessage());
            }
        });
    }

    private void setProfileInfo() {
        if(getUsername.equalsIgnoreCase("")
                && getName.equalsIgnoreCase("")
                && getMoto.equalsIgnoreCase("")) {
            //AlertDialog
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            dialogBuilder.setTitle("Profile Info");
            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.main_add_userinfo, null);
            dialogBuilder.setView(dialogView);
            final EditText username_et = (EditText)
                    dialogView.findViewById(R.id.main_username_et);
            final EditText name_et = (EditText)
                    dialogView.findViewById(R.id.main_name_et);
            final EditText moto_et = (EditText)
                    dialogView.findViewById(R.id.main_moto_et);
            dialogBuilder.setPositiveButton("Save Info", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setUsername = username_et.getText().toString();
                    setName = name_et.getText().toString();
                    setMoto = moto_et.getText().toString();
                    String username = setUsername;
                    String name = setName;
                    String moto = setMoto;
                    mDatabase.child("users").child(mUser.getUid()).child("username").setValue(username);
                    mDatabase.child("users").child(mUser.getUid()).child("name").setValue(name);
                    mDatabase.child("users").child(mUser.getUid()).child("moto").setValue(moto);
                    Toast.makeText(getApplicationContext(), "User info saved!", Toast.LENGTH_SHORT).show();
                }
            }).
                    setNegativeButton("Or Not...", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Fine, nvm then..."
                                    , Toast.LENGTH_SHORT).show();
                        }
                    });

            dialogBuilder.create().show();
        }else{
            //Log
            Log.d(TAG, "setProfileInfo: " + "Info was previously filled.");
        }
        Log.d(TAG, "setProfileInfo Username: " + getUsername);
        Log.d(TAG, "setProfileInfo Name:     " + getName);
        Log.d(TAG, "setProfileInfo Moto:     " + getMoto);

    }

    private void InitializeStuff() {
        //disregard for now
        //main_ListView = (ListView)findViewById(R.id.Main_listview);
    }

    private void setToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);

        mSelectedID = item.getItemId();

        navToID(mSelectedID);

        Log.d(TAG, "onNavigationItemSelected: " + mSelectedID);

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putInt(SELECTED_ITEM_ID, mSelectedID);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }
}
