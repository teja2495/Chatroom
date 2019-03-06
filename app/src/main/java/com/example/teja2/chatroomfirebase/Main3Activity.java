package com.example.teja2.chatroomfirebase;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import org.ocpsoft.prettytime.PrettyTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/* InClass09 - Group 32
Created by
1. Bala Guna Teja Karlapudi
2. Mandar Phapale
*/

public class Main3Activity extends AppCompatActivity {

    ImageView send, logout, pickImage;
    ProgressBar progressBar;
    List<messages> messageList = new ArrayList<>();
    customAdapter customAdapter;
    ListView lv;
    EditText sendingMsg;
    TextView usernameTv, progressText;
    private FirebaseAuth mAuth;
    DatabaseReference mRootRef;
    FirebaseUser user = null;
    Uri selectedImage = null;
    messages message;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    StorageReference imageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        send = findViewById(R.id.sendButton);
        logout = findViewById(R.id.logoutButton);
        pickImage = findViewById(R.id.attachPhoto);
        lv = findViewById(R.id.listViewMessages);
        sendingMsg = findViewById(R.id.message);
        usernameTv = findViewById(R.id.usernameChat);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.uploadText);
        mRootRef = FirebaseDatabase.getInstance().getReference().child("messages");
        progressBar.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);

        usernameTv.setText(user.getDisplayName());

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    messages message = new messages();
                    message.setMsg(postSnapshot.child("msg").getValue().toString());
                    message.setTime(postSnapshot.child("time").getValue().toString());
                    if (!postSnapshot.child("imgRef").getValue().toString().equals(""))
                        message.setImgRef(postSnapshot.child("imgRef").getValue().toString());
                    message.setFname(postSnapshot.child("fname").getValue().toString());
                    message.setMsgId(postSnapshot.child("msgId").getValue().toString());
                    message.setUserId(postSnapshot.child("userId").getValue().toString());
                    messageList.add(message);
                }
                customAdapter = new customAdapter(messageList);
                lv.setAdapter(customAdapter);
                lv.setSelection(customAdapter.getCount() - 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    Toast.makeText(Main3Activity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else if (sendingMsg.getText().toString() == (null) || sendingMsg.getText().toString().length() == 0) {
                    Toast.makeText(Main3Activity.this, "Please enter the message", Toast.LENGTH_SHORT).show();
                } else {
                    message = new messages();
                    message.setMsg(sendingMsg.getText().toString());
                    message.setUserId(user.getUid());
                    message.setFname((user.getDisplayName() + " ").split(" ")[0]);
                    message.setLname(user.getDisplayName().substring(user.getDisplayName().lastIndexOf(" ") + 1));
                    message.setTime(Calendar.getInstance().getTime().toString());
                    message.setMsgId(mRootRef.push().getKey());

                    if (selectedImage != null) {
                        imageRef = storageRef.child("images/" + selectedImage.getLastPathSegment());
                        lv.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        progressText.setVisibility(View.VISIBLE);
                        UploadTask uploadTask = imageRef.putFile(selectedImage);

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return imageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    progressText.setVisibility(View.INVISIBLE);
                                    lv.setVisibility(View.VISIBLE);
                                    message.setImgRef(downloadUri.toString());
                                    Toast.makeText(Main3Activity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                    mRootRef.child(message.getMsgId()).setValue(message);
                                    pickImage.setImageURI(null);
                                    pickImage.setBackgroundResource(R.drawable.addimage);
                                    selectedImage = null;
                                } else {
                                    Toast.makeText(Main3Activity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else
                        mRootRef.child(message.getMsgId()).setValue(message);

                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    sendingMsg.setText("");
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    FirebaseAuth.getInstance().signOut();
                    Intent myIntent = new Intent(Main3Activity.this, MainActivity.class);
                    startActivity(myIntent);
                    finish();
                } else
                    Toast.makeText(Main3Activity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class customAdapter extends BaseAdapter {
        List<messages> displayList;

        public customAdapter(List<messages> list) {
            this.displayList = list;
        }

        @Override
        public int getCount() {
            return displayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.custom_layout, null);
            ImageView deleteButton = view.findViewById(R.id.deleteMsgButton);
            ImageView image = view.findViewById(R.id.imageView);
            TextView messageTitle = view.findViewById(R.id.message);
            TextView senderName = view.findViewById(R.id.sender);
            TextView time = view.findViewById(R.id.time);
            deleteButton.setVisibility(View.INVISIBLE);

            if (!displayList.get(position).getImgRef().equals("")) {
                image.setVisibility(View.VISIBLE);
                Picasso.get().load(displayList.get(position).getImgRef()).into(image);
            }

            if (displayList.get(position).getUserId().equals(user.getUid())) {
                deleteButton.setVisibility(View.VISIBLE);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            Date date = new Date();
            try {
                date = sdf.parse(displayList.get(position).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String prettyTime = new PrettyTime().format(date);
            time.setText(prettyTime);
            messageTitle.setText(displayList.get(position).getMsg());
            senderName.setText(displayList.get(position).getFname());
            deleteButton.setTag(position);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    if (isConnected()) {
                        mRootRef.child(displayList.get(position).getMsgId()).removeValue();
                        if (!displayList.get(position).getImgRef().equals("")) {
                            Uri uri = Uri.parse(displayList.get(position).getImgRef());
                            StorageReference deleteRef = storageRef.child(uri.getLastPathSegment());
                            //String s = deleteRef.toString();

                            deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Main3Activity.this, "Delete Successful", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.d("demo", exception.toString());
                                }
                            });
                        }
                    } else
                        Toast.makeText(Main3Activity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    pickImage.setImageURI(selectedImage);
                }
        }
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
