package com.megliosolutions.ipd;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ArrayAdapter;
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
import com.megliosolutions.ipd.Utils.Arrays;
import com.megliosolutions.ipd.Utils.Login;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //TAG STRING
    public static String TAG = MainActivity.class.getSimpleName();


    //Views
    public ListView main_ListView;
    public SwipeRefreshLayout mSwipeRefreshLayout;

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
    public String keys;

    //doubles
    public double lat;
    public double mLong;

    //Adapters
    public StaticListAdapter listAdapter;

    //UserObject
    public UserObject dude;


    //Node Object
    NodeObject node;
    NodeObject getNodes;

    //List of NodeObjects
    //Give default value
    public List<NodeObject> nodesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instances
        setInstances();

        //Check username & set it
        checkUsername();

        //Initialize View From XML
        InitializeStuff();

        //Toolbar
        setToolbar();

        //Generate key for nodes
        GenerateKey();

        //Set Adapter
        SetAdapter();

        //Set ClickListeners
        setClickListeners();

        //Logstuff
        logDataFromVariables();

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
                setUserTitle();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "USER INFO-ERROR: " + databaseError.getMessage());
            }
        });
    }


    private void setUserTitle() {
        if(getUsername.equalsIgnoreCase("")) {
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
        }else{
            setUsername = getUsername;
            setTitle(setUsername);
        }
    }

    private void InitializeStuff() {
        main_ListView = (ListView)findViewById(R.id.Main_listview);
    }

    private void setToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                //Go to NodeView
                NodeObject pos = listAdapter.mNodes.get(position);
                Intent intent = new Intent(MainActivity.this, NodeView.class);
                intent.putExtra("staticIP",pos.getStaticAddress());
                intent.putExtra("description",pos.getDescription());
                intent.putExtra("lat",pos.getLatitude());
                intent.putExtra("long",pos.getLongitude());
                intent.putExtra("key", pos.getKey());
                startActivity(intent);
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
        listAdapter.clear();
        retrieveMoreData();
        Log.i(TAG, "Node Size: " + listAdapter.mNodes.size() +"");
        listAdapter.notifyDataSetChanged();
        main_ListView.setAdapter(listAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
