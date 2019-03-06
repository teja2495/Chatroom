package com.example.teja2.chatroomfirebase;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/* InClass09 - Group 32
Created by
1. Bala Guna Teja Karlapudi
2. Mandar Phapale
*/

public class Main2Activity extends AppCompatActivity {

    EditText fname, lname, email, password, cPassword;
    Button cancel, signup;
    private FirebaseAuth mAuth;
    DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mRootRef = FirebaseDatabase.getInstance().getReference().child("users");

        mAuth = FirebaseAuth.getInstance();
        fname=findViewById(R.id.editTextFname);
        lname=findViewById(R.id.editTextLname);
        email=findViewById(R.id.editTextEmail);
        password=findViewById(R.id.editTextPassword1);
        cPassword=findViewById(R.id.editTextPassword2);
        cancel=findViewById(R.id.buttonCancel);
        signup=findViewById(R.id.buttonSignup2);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Main2Activity.this,MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected()){
                    Toast.makeText(Main2Activity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                else if(fname.getText().toString() == (null) || fname.getText().toString().length() == 0 ||
                        lname.getText().toString() == (null) || lname.getText().toString().length() == 0 ||
                        email.getText().toString() == (null) || email.getText().toString().length() == 0 ||
                        password.getText().toString() == (null) || password.getText().toString().length() == 0 ||
                        cPassword.getText().toString() == (null) || cPassword.getText().toString().length() == 0){

                    Toast.makeText(Main2Activity.this, "Missing Values", Toast.LENGTH_SHORT).show();

                }
                else if(!password.getText().toString().equals(cPassword.getText().toString())){
                    Toast.makeText(Main2Activity.this, "Passwords didn't match", Toast.LENGTH_SHORT).show();
                }
                else if (password.getText().toString().length()<6){
                    Toast.makeText(Main2Activity.this, "Password length is less than 6 characters", Toast.LENGTH_SHORT).show();
                }
                else{
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(Main2Activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fname.getText().toString()+" "+lname.getText().toString())
                                            .build();
                                    user.updateProfile(profileUpdates);
                                    Toast.makeText(Main2Activity.this, "User has been created", Toast.LENGTH_SHORT).show();
                                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                            .addOnCompleteListener(Main2Activity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent myIntent = new Intent(Main2Activity.this,Main3Activity.class);
                                                        startActivity(myIntent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(Main2Activity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Log.w("demo", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(Main2Activity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI &&
                        networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }
}