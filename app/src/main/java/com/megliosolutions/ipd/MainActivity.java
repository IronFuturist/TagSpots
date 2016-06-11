package com.megliosolutions.ipd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // This method initializes stuff needed for the views
        InitializeStuff();

        // This method runs the loading pop up alert dialog
        LoadProgress();

        PopulateList();
    }

    private void InitializeStuff() {

    }

    private void LoadProgress() {

    }

    private void PopulateList() {

    }
}
