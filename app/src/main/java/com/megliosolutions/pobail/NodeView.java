package com.megliosolutions.pobail;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
import com.google.firebase.database.ValueEventListener;
import com.megliosolutions.pobail.Objects.NodeObject;
import com.megliosolutions.pobail.Utils.Login;

import java.util.ArrayList;
import java.util.HashMap;

public class NodeView extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = NodeView.class.getSimpleName();

    public TextView staticAddress_TV;
    public TextView description_TV;
    public TextView latitude_TV;

    public Button maps_Button;

    public String staticAddress;
    public String description;
    public double latitude;
    public double longitude;
    public int position;

    public String static_ip;
    public String desc;
    public double lat;
    public double mLong;
    public String mKey;
    public String selectedKey;
    public String childNode = "nodes";
    public String childSubNode = "subnodes";

    public String currentUser;
    public String keys;

    public DatabaseReference mDatabase;
    public DatabaseReference mNodeRef;
    public DatabaseReference mSubNodeRef;
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;

    public ArrayList<NodeObject> nodeObjectArrayList = new ArrayList<>();
    public ArrayList<MarkerOptions> nodeMarkersList = new ArrayList<>();
    public ArrayList<NodeObject> subNodeArrayList = new ArrayList<>();
    public NodeObject node;
    public NodeObject subNode;

    public MapFragment mapFragment;

    public GoogleMap googleMap;
    public MarkerOptions markerOptions;
    public HashMap<Marker, NodeObject> nodeMarkerHashMap;

    //Get position of node array item in map list
    public int getPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);


        //SetToolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Gather data from itemclick
        gatherData();

        //Set Instances
        setInstances();

        //Bring over list
        bringNodes();
        bringSubNodes();

        //SetMap
        SetMap();

        //Initialize Data
        InitializeData();

        //Convert LatLong to MGRS
        //ConvertToMGRS();

        //ChangeTitle
        UpdateTitle();

        //Populate Data
        populateData();

        //Log Data
        logDataFromVariables();
    }

    private void logDataFromVariables() {
        //Log some stuff
        Log.i(TAG, "USER: " + currentUser);

    }

    private void setInstances() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mNodeRef = mDatabase.child(childNode);
        mSubNodeRef = mDatabase.child(childNode).child(selectedKey).child(childSubNode);
        Log.i(TAG, "SetInstances-SelectedKey: " + selectedKey);
        currentUser = mUser.getUid();
    }

    private void GenerateKey() {
        mKey = mSubNodeRef.push().getKey();
        if(mKey.equalsIgnoreCase(mKey)){
            mKey = mSubNodeRef.push().getKey();
            Log.i(TAG, "NEW SUBNODE CHILD KEY: " + mKey);
        }

        Log.i(TAG, "SUBNODE CHILD KEY: " + mKey);
    }

    private void ConvertToMGRS() {
        //Convert Coordinates

    }

    private void bringNodes() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                NodeObject nodeMarkers = dataSnapshot.getValue(NodeObject.class);
                nodeObjectArrayList.add(nodeMarkers);

                Log.i(TAG, "Nodes: ----> " + nodeObjectArrayList.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        mNodeRef.addChildEventListener(childEventListener);
    }

    private void bringSubNodes(){
        ChildEventListener subNodesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                subNode = dataSnapshot.getValue(NodeObject.class);
                Log.i(TAG, "SELECTED KEY: " + selectedKey);
                subNodeArrayList.add(subNode);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        Log.i(TAG, "bringSubNodes-SelectedKey: " + mDatabase.child(childNode).child(selectedKey).getKey());
        mSubNodeRef.addChildEventListener(subNodesListener);
    }

    private void populateMarkers() {
        //Hash map
        nodeMarkerHashMap = new HashMap<Marker, NodeObject>();

        Log.i(TAG, "POPULATE DATA NODES: ----> " + nodeObjectArrayList.size());
        Log.i(TAG, "POPULATE SUB NODES: -----> " + subNodeArrayList.size());

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < nodeObjectArrayList.size(); i++) {
            double lat = nodeObjectArrayList.get(i).getLatitude();
            //Log.i(TAG, "POPULATE DATA NODES: LAT----> " + nodeObjectArrayList.get(i).getLatitude());
            double lng = nodeObjectArrayList.get(i).getLongitude();
            //Log.i(TAG, "POPULATE DATA NODES: LNG----> " + nodeObjectArrayList.get(i).getLongitude());
            String ip = nodeObjectArrayList.get(i).getStaticAddress();
            //Log.i(TAG, "POPULATE DATA NODES: TITLE----> " + nodeObjectArrayList.get(i).getStaticAddress());
            String desc = nodeObjectArrayList.get(i).getDescription();
            //Log.i(TAG, "POPULATE DATA NODES: SNIPPIT----> " + nodeObjectArrayList.get(i).getDescription());
            int icon = R.drawable.ic_wifi_tethering_white_48dp;
            //Log.i(TAG, "POPULATE DATA NODES: Node " + i);

            markerOptions = new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(ip)
                    .snippet(desc)
                    .icon(BitmapDescriptorFactory.fromResource(icon));

            googleMap.addMarker(markerOptions);
        }

    }

    private void SetMap() {
        try {
            if (mapFragment == null) {
                mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.node_maps);
                mapFragment.getMapAsync(this);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UpdateTitle() {

        //Set Title to Description
        setTitle(description);

    }

    private void gatherData() {
        //Populate node
        Intent intent = getIntent();
        staticAddress = intent.getStringExtra("staticIP");
        description = intent.getStringExtra("description");
        latitude = intent.getDoubleExtra("lat", 0.0);
        longitude = intent.getDoubleExtra("long", 0.0);
        selectedKey = intent.getStringExtra("key");
        Log.i(TAG, "gatherData-SelectedKey: " + selectedKey);
    }

    private void InitializeData() {
        staticAddress_TV = (TextView) findViewById(R.id.nodeView_static);
        description_TV = (TextView) findViewById(R.id.nodeView_descrip);
        latitude_TV = (TextView) findViewById(R.id.nodeView_lat_long);
        //maps_Button = (Button) findViewById(R.id.node_navigate);
    }

    private void populateData() {
        staticAddress_TV.setText(staticAddress);
        description_TV.setText(description);
        String latlong = latitude + ", " + longitude;
        latitude_TV.setText(latlong);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().isCompassEnabled();
        googleMap.getUiSettings().isMapToolbarEnabled();
        googleMap.getUiSettings().isMyLocationButtonEnabled();
        googleMap.getUiSettings().isRotateGesturesEnabled();
        googleMap.getUiSettings().isTiltGesturesEnabled();
        googleMap.getMaxZoomLevel();
        googleMap.getMinZoomLevel();
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                double lat = latitude;
                double lng = longitude;

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(lat,lng))
                        .zoom(10)
                        .bearing(5)
                        .tilt(85)
                        .build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

                populateMarkers();
            }
        });



        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Node Object for Map Marker Click
                NodeObject nodeMarkerClickItem = new NodeObject();
                String markerNodeKey = nodeMarkerClickItem.getKey();
                Log.i(TAG, "MAP NODE CLICKED KEY: " + markerNodeKey);

                //Show Marker Info Window
                marker.showInfoWindow();

                String id = marker.getId();

                Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();
                return true;
            }
        });


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
                mSubNodeRef.push().setValue(node);
                Toast.makeText(getApplicationContext(),
                        "Sub-Node: \n" +
                                "Child Key:   " + mKey + "\n" +
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_node, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addNode:
                addNode();
                return true;
            case R.id.logout:
                signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(NodeView.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(getApplicationContext(), "Logging Out.", Toast.LENGTH_SHORT).show();
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        SetMap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SetMap();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}

