package com.megliosolutions.ipd;

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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.megliosolutions.ipd.Adapters.StaticListAdapter;
import com.megliosolutions.ipd.Objects.NodeObject;
import com.megliosolutions.ipd.Objects.UserObject;
import com.megliosolutions.ipd.Utils.Login;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //TAG STRING
    public static String TAG = MainActivity.class.getSimpleName();

    public ListView main_ListView;
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;
    public DatabaseReference mDatabase;

    //Strings
    public String static_ip;
    public String lat = "5.0";
    public String mLong = "4.0";
    public String currentUser;
    public String setUsername;

    //Adapters
    public StaticListAdapter listAdapter;

    //UserObject
    public UserObject dude;

    //Node Object
    NodeObject node;
    //List of NodeObjects
    //Give default value
    public List<NodeObject> nodesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instances
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = mUser.getUid();

        checkUsername();

        //Initialize View From XML
        main_ListView = (ListView)findViewById(R.id.Main_listview);

        //Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //[End of Toolbar]


        //ListAdapter stuff
        listAdapter = new StaticListAdapter(getApplicationContext(),nodesList);

        //ListView stuff
        if(nodesList==null){
            Log.e(TAG, "NODESLIST IS NULL");
        }else{
            main_ListView.setAdapter(listAdapter);
        }

        //SetTitle to Username


        //Set OnItemClick Listener
        //When clicked, it will go to a single view of the node
        //populating it's data
        main_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Go to NodeView

                //Bring Data with
            }
        });

        //Set OnItemLong Click Listener
        //Delete Node when long pressed
        //Show dialog to give user a choice
        main_ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //Delete node
                listAdapter.mNodes.remove(position);
                mDatabase.child("nodes").push().getRef().removeValue();
                listAdapter.setNotifyOnChange(true);
                Toast.makeText(getApplicationContext(),"Removed Item at: " + position,Toast.LENGTH_SHORT).show();
                main_ListView.setAdapter(listAdapter);
                return true;
            }
        });

        //Log some stuff
        Log.i(TAG, "USER: " + currentUser);

    }

    private void checkUsername() {


        /*if(user.getUsername().equalsIgnoreCase("")) {
            //AlertDialog
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            dialogBuilder.setTitle("Umm...Username?");
            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.main_add_username, null);
            dialogBuilder.setView(dialogView);
            final EditText editText = (EditText)
                    dialogView.findViewById(R.id.main_username_et);
            dialogBuilder.setPositiveButton("Set Username", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setUsername = editText.getText().toString();
                    String username = setUsername;
                    mDatabase.child("users").child(mUser.getUid()).child("username").setValue(username);
                    setTitle(username);
                    Toast.makeText(getApplicationContext(), "Username: " + username + " assigned!"
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
        }*/
    }

    private void retrieveMoreData() {

        mDatabase.child("nodes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());


                NodeObject nodeObject = dataSnapshot.getValue(NodeObject.class);

                listAdapter.add(nodeObject);
                listAdapter.setNotifyOnChange(true);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                listAdapter.setNotifyOnChange(true);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

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
    }

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
            case R.id.getGPS:
               //nothing
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
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(getApplicationContext(), "Logging Out.", Toast.LENGTH_SHORT).show();
        startActivity(intent);

    }

    private void addNode() {
        //AlertDialog
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle("Dude, assign something...");

        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.main_add_node_dialog, null);

        dialogBuilder.setView(dialogView);

        final EditText editText = (EditText)
                dialogView.findViewById(R.id.static_et);
        dialogBuilder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                static_ip = editText.getText().toString();
                String ip = static_ip;
                node = new NodeObject(ip, lat, mLong);
                mDatabase.child("nodes").push().setValue(node);
                Toast.makeText(getApplicationContext(), "Static IP: " + static_ip + " assigned!"
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
        listAdapter.clear();
        //retrieveData();
        retrieveMoreData();
        Log.i(TAG, listAdapter.mNodes.size() +"");
        listAdapter.notifyDataSetChanged();
        main_ListView.setAdapter(listAdapter);
    }
}
