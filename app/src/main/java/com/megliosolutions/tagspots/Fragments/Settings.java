package com.megliosolutions.tagspots.Fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.megliosolutions.tagspots.R;
import com.megliosolutions.tagspots.Utils.Login;

/**
 * Created by Meglio on 7/11/16.
 */
public class Settings extends Fragment {

    public FirebaseUser mUser;
    public FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setInstances();

        UpdateTitle();

        Logout(view);

        return view;
    }

    private void setInstances() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    private void Logout(View view) {
        Button mLogout = (Button)view.findViewById(R.id.settings_logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Toast.makeText(getActivity(), "Logging Out.", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }

    private void UpdateTitle() {
        //Set Title to Description
        getActivity().setTitle("Settings");

    }
}
