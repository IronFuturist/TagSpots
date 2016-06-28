package com.megliosolutions.ipd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.megliosolutions.ipd.Objects.NodeObject;

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

    public ArrayList<NodeObject> nodeObjectArrayList = new ArrayList<>();
    public ArrayList<MarkerOptions> nodeMarkersList = new ArrayList<>();

    public MapFragment mapFragment;

    public GoogleMap googleMap;
    public Marker marker;
    public MarkerOptions markerOptions;
    private HashMap<Marker, NodeObject> nodeMarkerHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);


        //SetToolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Bring over list
        bringNodes();

        //SetMap
        SetMap();

        //Initialize Data
        InitializeData();

        //Gather data from itemclick
        gatherData();

        //Convert String to Double
        //ConverStrToDble();

        //ChangeTitle
        UpdateTitle();

        //Populate Data
        populateData();

        maps_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

    }

    private void bringNodes() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mNodeRef = mDatabase.getReference().child("nodes");
        mNodeRef.addChildEventListener(new ChildEventListener() {
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
        });
    }

    private void populateMarkers() {
        //Hash map
        nodeMarkerHashMap = new HashMap<Marker, NodeObject>();

        Log.i(TAG, "POPULATE DATA NODES: ----> " + nodeObjectArrayList.size());

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < nodeObjectArrayList.size(); i++) {
            double lat = nodeObjectArrayList.get(i).getLatitude();
            Log.i(TAG, "POPULATE DATA NODES: LAT----> " + nodeObjectArrayList.get(i).getLatitude());
            double lng = nodeObjectArrayList.get(i).getLongitude();
            Log.i(TAG, "POPULATE DATA NODES: LNG----> " + nodeObjectArrayList.get(i).getLongitude());
            String ip = nodeObjectArrayList.get(i).getStaticAddress();
            Log.i(TAG, "POPULATE DATA NODES: TITLE----> " + nodeObjectArrayList.get(i).getStaticAddress());
            String desc = nodeObjectArrayList.get(i).getDescription();
            Log.i(TAG, "POPULATE DATA NODES: SNIPPIT----> " + nodeObjectArrayList.get(i).getDescription());
            int icon = R.drawable.ic_wifi_tethering_white_48dp;
            Log.i(TAG, "POPULATE DATA NODES: Node " + i);

            markerOptions = new MarkerOptions()
                    .position(new LatLng(lat,lng))
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
        }
        catch (Exception e) {
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
        position = intent.getIntExtra("position",0);
    }

    private void InitializeData() {
        staticAddress_TV = (TextView) findViewById(R.id.nodeView_static);
        description_TV = (TextView) findViewById(R.id.nodeView_descrip);
        latitude_TV = (TextView) findViewById(R.id.nodeView_lat_long);
        maps_Button = (Button) findViewById(R.id.node_navigate);
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
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                double lat = latitude;
                double lng = longitude;

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(lat,lng))
                        .zoom(16)
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
                marker.showInfoWindow();
                String id = marker.getId();
                Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        SetMap();
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

