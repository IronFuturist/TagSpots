package com.megliosolutions.tagspots;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.megliosolutions.tagspots.Fragments.HashTag;
import com.megliosolutions.tagspots.Fragments.MapView;
import com.megliosolutions.tagspots.Fragments.ProfileViewPager;
import com.megliosolutions.tagspots.Fragments.Settings;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnPermissionCallback{

    private static final String SELECTED_ITEM_ID = "selected_item_id";

    //TAG STRING
    public static final String TAG = MainActivity.class.getSimpleName();

    public TextView nav_Header;

    //FragmentManager & Transaction
    public FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;

    //Fragments
    public MapView mapView;

    //Bundle
    public Bundle bundle;

    public PermissionHelper permissionHelper;

    //Firebase
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;
    public DatabaseReference mDatabase;
    public DatabaseReference mNodeRef;

    //Strings
    public String currentUser;
    public static final String TAG_MAPVIEW_FRAGMENT = "TAG_MAPVIEW_FRAGMENT";
    public static final String TAG_HASHTAG_FRAGMENT = "TAG_HASHTAG_FRAGMENT";
    public static final String TAG_SETTINGS_FRAGMENT = "TAG_SETTINGS_FRAGMENT";
    public static final String TAG_FRIENDS_FRAGMENT = "TAG_FRIENDS_FRAGMENT";
    public static final String TAG_PROFILE_FRAGMENT = "TAG_PROFILE_FRAGMENT";

    public int mSelectedID;

    //booleans
    public boolean mUserSawDrawer = false;

    //Nav Drawer stuff
    public DrawerLayout mDrawerLayout;
    public NavigationView mNavView;
    public ActionBarDrawerToggle drawerToggle;

    //toolbar
    public Toolbar toolbar;

    final String DIALOG_TITLE = "Access Location";
    final String DIALOG_MESSAGE = "We need to access your Location to use this app.";
    public final String PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Request Permissions
        RequestPermisions();

        //Instances
        setInstances();

        //Initialize View From XML
        InitializeStuff();

        //Toolbar
        setToolbar();

        //Setup Nav Drawer stuff
        setViews();

        //Set FrameLayout
        SetInitialFrame();

        //Generate key for nodes
        //GenerateKey();

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

    private void RequestPermisions() {
        permissionHelper = PermissionHelper.getInstance(this);
        permissionHelper.setForceAccepting(false).request(PERMISSION);
    }

    public void openDrawer(){
        mDrawerLayout.openDrawer(GravityCompat.START);
        Log.d(TAG, "Drawer was OPENED ");
    }

    private void navToID(int mSelectedID) {
        if(mSelectedID == R.id.nav_id_map){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Log.i("MAPVIEW", " LOADED");
            MapView fragment = new MapView();
            fragmentManager = getSupportFragmentManager();
            //Replace intent with Bundle and put it in the transaction
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_FrameLayout, fragment);
            fragmentTransaction.commit();
            setTitle("Map");
        }
        if(mSelectedID == R.id.nav_id_tag){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Log.i("TAG", " LOADED");
            HashTag tag = new HashTag();
            fragmentManager = getSupportFragmentManager();
            //Replace intent with Bundle and put it in the transaction
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_FrameLayout, tag);
            fragmentTransaction.commit();
            setTitle("Tags");
        }
        if(mSelectedID == R.id.nav_id_profile){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Log.i("PROFILE", " LOADED");
            ProfileViewPager profile = new ProfileViewPager();
            fragmentManager = getSupportFragmentManager();
            //Replace intent with Bundle and put it in the transaction
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_FrameLayout, profile);
            fragmentTransaction.commit();
            setTitle("Pobail");
        }
        if(mSelectedID == R.id.nav_id_settings){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Log.i("SETTINGS", " LOADED");
            Settings settings = new Settings();
            fragmentManager = getSupportFragmentManager();
            //Replace intent with Bundle and put it in the transaction
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_FrameLayout, settings);
            fragmentTransaction.commit();
            setTitle("Settings");
        }
    }

    private void loadSavedState(Bundle savedInstanceState) {
        mSelectedID = savedInstanceState == null ?
                R.id.nav_id_map : savedInstanceState.getInt(SELECTED_ITEM_ID);
    }

    private void setViews() {
        nav_Header = (TextView) findViewById(R.id.nav_header_text);
    }

    private void SetInitialFrame() {
        mapView = new MapView();
        fragmentManager = getSupportFragmentManager();
        //Replace intent with Bundle and put it in the transaction
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_FrameLayout, mapView,TAG_MAPVIEW_FRAGMENT);
        fragmentTransaction.commit();
    }

    private void setInstances() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mNodeRef = mDatabase.child("nodes");
        currentUser = mUser.getUid();
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        permissionHelper.onActivityForResult(requestCode);
    }

    private void showAlertDialog(String title, String message, final String permission) {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        permissionHelper.requestAfterExplanation(permission);
                    }
                })
                .create();

        dialog.show();
    }


    @Override
    public void onPermissionGranted(String[] permissionName) {
        Log.d(TAG, "onPermissionGranted: LOCATION PERMISSION GRANTED");

    }

    @Override
    public void onPermissionDeclined(String[] permissionName) {
        Log.d(TAG, "onPermissionGranted: LOCATION PERMISSION DENIED");
    }

    @Override
    public void onPermissionPreGranted(String permissionsName) {
        Log.d(TAG, "onPermissionGranted: LOCATION PERMISSION GRANTED");
    }

    @Override
    public void onPermissionNeedExplanation(String permissionName) {
         /*
        Show dialog here and ask permission again. Say why
         */

        showAlertDialog(DIALOG_TITLE, DIALOG_MESSAGE, PERMISSION);
    }

    @Override
    public void onPermissionReallyDeclined(String permissionName) {
        Log.e(TAG, "onPermissionReallyDeclined: PERMISSIONS SEVERLY DECLINED " + permissionName);
    }

    @Override
    public void onNoPermissionNeeded() {
        Log.d(TAG, "onNoPermissionNeeded: NO PERMISSIONS NEEDED");
    }
}
