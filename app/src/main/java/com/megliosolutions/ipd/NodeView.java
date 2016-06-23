package com.megliosolutions.ipd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NodeView extends AppCompatActivity {

    public TextView staticAddress_TV;
    public TextView description_TV;
    public TextView latitude_TV;
    public TextView longitude_TV;

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
        latitude_TV = (TextView)findViewById(R.id.nodeView_lat);
        longitude_TV = (TextView)findViewById(R.id.nodeView_long);
        maps_Button = (Button)findViewById(R.id.node_maps);
    }

    private void populateData() {
        staticAddress_TV.setText(staticAddress);
        description_TV.setText(description);
        latitude_TV.setText(latitude);
        longitude_TV.setText(longitude);
    }

}
