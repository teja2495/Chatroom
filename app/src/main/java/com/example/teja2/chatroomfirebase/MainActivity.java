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
import com.google.firebase.auth.FirebaseUser;

/* InClass09 - Group 32
Created by
1. Bala Guna Teja Karlapudi
2. Mandar Phapale
*/

public class MainActivity extends AppCompatActivity {
    EditText email, password;
    Button login, signup;
    String username;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            Intent myIntent = new Intent(MainActivity.this,Main3Activity.class);
            startActivity(myIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email=findViewById(R.id.editTextEmailLogin);
        password=findViewById(R.id.editTextPasswordLogin);
        login=findViewById(R.id.buttonLogin);
        signup=findViewById(R.id.buttonSignupLogin);
        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected()){
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                else if(email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty())
                    Toast.makeText(MainActivity.this, "Invalid/Missing Values", Toast.LENGTH_SHORT).show();

                else{
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent myIntent = new Intent(MainActivity.this,Main3Activity.class);
                                        startActivity(myIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    Intent myIntent = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(myIntent);
                    finish();
                } else
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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
