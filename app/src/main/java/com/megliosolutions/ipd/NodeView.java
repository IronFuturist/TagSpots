package com.megliosolutions.ipd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NodeView extends AppCompatActivity implements OnMapReadyCallback {

    public TextView staticAddress_TV;
    public TextView description_TV;
    public TextView latitude_TV;

    public Button maps_Button;

    public String staticAddress;
    public String description;
    public String latitude;
    public String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);

        //SetToolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //SetMap
        SetMap();

        //Initialize Data
        InitializeData();

        //Gather data from itemclick
        gatherData();

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

    private void SetMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.node_maps);
        mapFragment.getMapAsync(this);
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
        latitude = intent.getStringExtra("lat");
        longitude = intent.getStringExtra("long");
    }

    private void InitializeData() {
        staticAddress_TV = (TextView)findViewById(R.id.nodeView_static);
        description_TV = (TextView)findViewById(R.id.nodeView_descrip);
        latitude_TV = (TextView)findViewById(R.id.nodeView_lat_long);
        maps_Button = (Button)findViewById(R.id.node_navigate);
    }

    private void populateData() {
        staticAddress_TV.setText(staticAddress);
        description_TV.setText(description);
        String latlong = latitude + " / " + longitude;
        latitude_TV.setText(latlong);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(30.410798,-86.781495))
                .zoom(16)
                .bearing(0)
                .tilt(45)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

        map.addMarker(new MarkerOptions()
                .position(new LatLng(30.410798, -86.781495))
                .title("Cody's House"))
                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_wifi_black_48dp));

    }
}

