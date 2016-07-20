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
import com.megliosolutions.pobail.Fragments.Settings;
import com.megliosolutions.pobail.Fragments.UserProfile;
import com.megliosolutions.pobail.Objects.NodeObject;
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
    public NodeObject node;
    public NodeObject getNodes;

    //Nav Drawer stuff
    public DrawerLayout mDrawerLayout;
    public NavigationView mNavView;
    public ActionBarDrawerToggle drawerToggle;

    //toolbar
    public Toolbar toolbar;

    //List of NodeObjects
    //Give default value
    public List<NodeObject> nodesList = new ArrayList<>();


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

    private boolean drawerPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.getBoolean(FIRST_TIME,true);
        return mUserSawDrawer;
    }
    private void markDrawerAsShown(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = true;
        sharedPreferences.edit().putBoolean(FIRST_TIME,false).apply();
        Log.d(TAG, "markDrawerAsShown: " + mUserSawDrawer);
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
            UserProfile profile = new UserProfile();
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

    private void GenerateKey() {
        mKey = mNodeRef.push().getKey();
        Log.i(TAG, "NODE CHILD KEY: " + mKey);
        if(mKey.equalsIgnoreCase(mKey)){
            mKey = mNodeRef.push().getKey();
            Log.i(TAG, "NEW NODE CHILD KEY: " + mKey);
        }
    }

    private void logDataFromVariables() {
        //Log some stuff
        Log.i(TAG, "USER: " + currentUser);
        //Log Keys
        Log.i(TAG, "WHERE ARE THE KEYS BITCHES: " + keys);

    }

    private void SetAdapter() {
        //ListAdapter stuff
        listAdapter = new StaticListAdapter(getApplicationContext(),nodesList);

        //ListView stuff
        if(nodesList==null){
            Log.e(TAG, "NODESLIST IS NULL");
        }else{
            //listAdapter.sort();
            main_ListView.setAdapter(listAdapter);
        }


    }

    private void setClickListeners() {

        //Set OnItemClick Listener
        //When clicked, it will go to a single view of the node
        //populating it's data
        main_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Go to MapView Fragment
                /*SavedMapView = (MapView) getSupportFragmentManager()
                        .findFragmentByTag(TAG_MAPVIEW_FRAGMENT);
                if(SavedMapView == null){
                    mapView = new MapView();
                   *//* NodeObject pos = listAdapter.mNodes.get(position);
                    bundle = new Bundle();
                    bundle.putString("staticIP",pos.getStaticAddress());
                    bundle.putString("description",pos.getDescription());
                    bundle.putDouble("lat",pos.getLatitude());
                    bundle.putDouble("long",pos.getLongitude());
                    bundle.putString("key", pos.getKey());
                    mapView.setArguments(bundle);*//*
                    fragmentManager = getSupportFragmentManager();
                    //Replace intent with Bundle and put it in the transaction
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.fragment_view_mapview,mapView);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();*/




                //Bring Data with
            }
        });

        //Set OnItemLong Click Listener
        //Delete Node when long pressed
        //Show dialog to give user a choice
        main_ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setTitle("Please Don't Kill Me!");
                dialogBuilder.setPositiveButton("Kill Me...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete node
                        Log.d(TAG, "NUM of NODES: " + listAdapter.mNodes.size());
                        NodeObject pos = listAdapter.mNodes.get(position);
                        Log.i(TAG, "Generated Key: " + mKey);
                        String selectedKey = listAdapter.mNodes.get(position).getKey();
                        Log.i(TAG, "Selected Key: " + selectedKey);
                        mNodeRef.child(selectedKey).removeValue();
                        listAdapter.mNodes.remove(pos);
                        listAdapter.notifyDataSetChanged();
                        main_ListView.setAdapter(listAdapter);
                        Toast.makeText(getApplicationContext(), "Node has been murdered terribly."
                                , Toast.LENGTH_SHORT).show();
                    }
                }).
                        setNegativeButton("Don't Kill Me!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Fine, ya coward."
                                        , Toast.LENGTH_SHORT).show();
                            }
                        });

                dialogBuilder.create().show();
                /*
                Toast.makeText(getApplicationContext(),"Item at: " + position,Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), ""  +listAdapter.mNodes.size(), Toast.LENGTH_SHORT).show();*/


                return true;
            }
        });

    }

    private void retrieveMoreData() {

       mDatabase.child("nodes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                getNodes = dataSnapshot.getValue(NodeObject.class);


                keys = dataSnapshot.getKey();

                Log.i(TAG, "HERE ARE THE KEYS BITCHES: " + keys);


                listAdapter.add(getNodes);
                listAdapter.notifyDataSetChanged();
                listAdapter.setNotifyOnChange(true);

                Log.i(TAG, "Num Of Nodes: " + listAdapter.mNodes.size());

                Log.i(TAG, "Node Strings: " + listAdapter.mNodes.get(0).getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                listAdapter.notifyDataSetChanged();
                listAdapter.setNotifyOnChange(true);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String removedKey = dataSnapshot.getKey();
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                //For Loop to cycle through listview adapter array to get the right key
                //Then get that position, and remove it from the list.
                String removeNode;
                String getKey;
                for(int i=0; i<listAdapter.getCount();i++){
                    NodeObject nodeItem = listAdapter.getItem(i);
                    removeNode = nodeItem.getKey();
                    Log.i(TAG, "NODE TO REMOVE: " + removeNode);
                }

                //listAdapter.mNodes.remove();
                listAdapter.notifyDataSetChanged();
                listAdapter.setNotifyOnChange(true);
                main_ListView.setAdapter(listAdapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                listAdapter.setNotifyOnChange(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }/*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
               //nothing
                listAdapter.mNodes.clear();
                listAdapter.notifyDataSetChanged();
                retrieveMoreData();
                Toast.makeText(getApplicationContext(), "Refreshed."
                        , Toast.LENGTH_SHORT).show();
                listAdapter.mNodes.size();
                Toast.makeText(getApplicationContext(), "Nodes: " + listAdapter.mNodes.size()
                        , Toast.LENGTH_SHORT).show();
                return true;
            case R.id.addNode:
                addNode();
                return true;
            case R.id.logout:
                signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(getApplicationContext(), "Logging Out.", Toast.LENGTH_SHORT).show();
        startActivity(intent);

    }

    private void addNode() {
        //Get generated key set it to the node
        GenerateKey();
        Log.i(TAG, "ADD NODE GENERATED KEY: " + mKey);

        //AlertDialog
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle("Dude, assign something...");

        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.main_add_node_dialog, null);

        dialogBuilder.setView(dialogView);

        final EditText editText = (EditText)
                dialogView.findViewById(R.id.static_et);
        final EditText editText1 = (EditText)
                dialogView.findViewById(R.id.lat_et);
        final EditText editText2 = (EditText)
                dialogView.findViewById(R.id.long_et);
        final EditText editText3 = (EditText)
                dialogView.findViewById(R.id.desc_et);
        dialogBuilder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                static_ip = editText.getText().toString();
                desc = editText3.getText().toString();
                String tempLat = editText1.getText().toString();
                String tempLong = editText2.getText().toString();
                lat = Double.parseDouble(tempLat);
                mLong = Double.parseDouble(tempLong);
                node = new NodeObject(static_ip,desc,lat,mLong,mKey);
                mDatabase.child("nodes").child(mKey).setValue(node);
                Toast.makeText(getApplicationContext(), "Node: \n" + "Child Key:   " + mKey + "\n" +
                                                                     "StaticIP:    " + static_ip + "\n" +
                                                                     "Description: " + desc + "\n" +
                                                                     "Latitude:    " + lat + "\n" +
                                                                     "Longitude:   " + mLong
                        , Toast.LENGTH_SHORT).show();
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
    }
    @Override
    protected void onResume() {
        super.onResume();
        //listAdapter.clear();
        //retrieveMoreData();
        //Log.i(TAG, "Node Size: " + listAdapter.mNodes.size() +"");
        //listAdapter.notifyDataSetChanged();
        //main_ListView.setAdapter(listAdapter);
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
