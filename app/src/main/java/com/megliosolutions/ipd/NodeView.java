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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

        //Initialize Data
        InitializeData();

        //Map Fragment
        ShowMapFragment();

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

    private void ShowMapFragment() {
        FragmentManager fmanager = getSupportFragmentManager();
        Fragment fragment = fmanager.findFragmentById(R.id.node_maps);
        SupportMapFragment supportmapfragment = (SupportMapFragment)fragment;
        GoogleMap supportMap = supportmapfragment.getMap();
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
    public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(41.889, -87.622), 16));

        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_wifi_black_48dp))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(41.889, -87.622)));

        //Change
    }
}

