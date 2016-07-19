package com.megliosolutions.pobail.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.megliosolutions.pobail.MainActivity;
import com.megliosolutions.pobail.Objects.UserObject;
import com.megliosolutions.pobail.R;

import java.util.Arrays;

public class Login extends BaseActivity {

    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;

    // [START declare_auth]
    public FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    public FirebaseUser user;

    // [END declare_auth]

    //Buttons
    public Button signInButton;

    //Strings
    public String userUID;
    public String mUsername;
    public String mEmail;
    public String mPassword;
    public String mName;
    public String mMoto;

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // [END initialize_auth]

        // [START auth_state_listener]
        authStateListener();

        // Views
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);


        // Buttons
        signInButton = (Button)findViewById(R.id.email_sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sign In

                signIn(mEmailField.getText().toString(),mPasswordField.getText().toString());


            }
        });


    }

    private void authStateListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged: USER:" + user.getUid());
                    mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                Log.i(TAG, "AuthStateListener-DataExists-IF: " + dataSnapshot.exists());
                                mEmail = mEmailField.getText().toString();
                                mPassword = mPasswordField.getText().toString();
                                mUsername = "";
                                mName = "";
                                mMoto = "";

                                //Make User Object
                                UserObject uo = new UserObject(mEmail,mUsername,mPassword,mName,mMoto);
                                uo.setEmail(mEmail);
                                uo.setUsername(mUsername);
                                uo.setPassword(mPassword);
                                uo.setName(mName);
                                uo.setMoto(mMoto);
                                //Add user to database for authentication access
                                mDatabase.child("users").child(user.getUid()).setValue(uo);

                                Intent intent = new Intent(Login.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                // [END sign_in_with_email]
                                // [START_EXCLUDE]
                                hideProgressDialog();
                                // [END_EXCLUDE]
                            }else{
                                Log.i(TAG, "AuthStateListener-DataExists-ELSE: " + !dataSnapshot.exists());
                                Toast.makeText(Login.this, "Logging in...",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(Login.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                // [END sign_in_with_email]
                                // [START_EXCLUDE]
                                hideProgressDialog();
                                // [END_EXCLUDE]
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "onAuthStateChanged-DatabaseError: " + databaseError.getMessage());
                        }
                    });
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        // [END auth_state_listener]
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "ISSUE LOOK HERE!!!!!! ---> " + mAuth.getCurrentUser());
                            // [START_EXCLUDE]
                            hideProgressDialog();
                            // [END_EXCLUDE]


                        }


                    }
                });

    }

    private boolean validateForm() {
        mAuth = FirebaseAuth.getInstance();

        boolean valid = true;
        int characters = mPasswordField.getText().length();

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        /*if (characters>=6){
            mPasswordField.setError(("Password must be more than 6 characters."));
            valid = false;
        } else{
            mPasswordField.setError(null);
        }*/

        return valid;
    }

}
