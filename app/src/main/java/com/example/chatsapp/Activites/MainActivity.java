package com.example.chatsapp.Activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatsapp.Adapters.TopStatusAdapter;
import com.example.chatsapp.Adapters.UsersAdapter;
import com.example.chatsapp.Models.Status;
import com.example.chatsapp.Models.User;
import com.example.chatsapp.Models.UserStatus;
import com.example.chatsapp.R;
import com.example.chatsapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ProgressDialog dialog;

    ArrayList<User> users;//Models
    UsersAdapter usersAdapter;//Adapter

    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;

    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image...");
        dialog.setCancelable(false);


        database=FirebaseDatabase.getInstance();
        //auth= FirebaseAuth.getInstance();

        users=new ArrayList<>();
        userStatuses=new ArrayList<>();

        usersAdapter=new UsersAdapter(this,users);
        statusAdapter=new TopStatusAdapter(this,userStatuses);

        //RelativeLayout Style
        //binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusRecyclerView.setLayoutManager(linearLayoutManager);


        binding.recyclerview.setAdapter(usersAdapter);
        binding.statusRecyclerView.setAdapter(statusAdapter);

        binding.recyclerview.showShimmerAdapter();
        binding.statusRecyclerView.showShimmerAdapter();


        //status
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //status=stories
        database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            userStatuses.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                UserStatus status = new UserStatus();
                                status.setName(dataSnapshot.child("name").getValue(String.class));
                                status.setProfileImage(dataSnapshot.child("profileImage").getValue(String.class));
                                try{
                                    status.setLastUpdate(dataSnapshot.child("lastUpdate").getValue(Long.class));
                                }catch (NullPointerException exception)
                                {
                                }




                                ArrayList<Status> statuses = new ArrayList<>();
                                for(DataSnapshot statusSnapshot : dataSnapshot.child("statuses").getChildren())
                                {
                                   Status sampleStatus = statusSnapshot.getValue(Status.class);
                                   statuses.add(sampleStatus);
                                }

                                status.setStatuses(statuses);
                                userStatuses.add(status);
                            }
                            binding.statusRecyclerView.hideShimmerAdapter();
                            statusAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




        //get on firebase// ArrayList<User> users;//get to setup profile
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);

                    if(!user.getUid().equals(FirebaseAuth.getInstance().getUid()))  //did not find my profile for this line
                    users.add(user);
                }
                binding.recyclerview.hideShimmerAdapter();
                usersAdapter.notifyDataSetChanged();//update data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.status://status
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,75);
                        break;

                }


                return false;
            }
        });

    }//first bracket


    //status
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null)
        {
            if (data.getData() != null)
            {
                dialog.show();
                FirebaseStorage storage= FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime() +"" );


                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UserStatus userStatus = new UserStatus();
                                userStatus.setName(user.getName());
                                userStatus.setProfileImage(user.getProfileImage());
                                userStatus.setLastUpdate(date.getTime());

                                HashMap<String, Object> obj = new HashMap<>();
                                obj.put("name",userStatus.getName());
                                obj.put("profileImage",userStatus.getProfileImage());
                                obj.put("lastUpdate",userStatus.getLastUpdate());

                                String imageUrl = uri.toString();
                                Status status = new Status(imageUrl,userStatus.getLastUpdate());

                                database.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .updateChildren(obj);

                                database.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("statuses")
                                        .push()
                                        .setValue(status);

                                dialog.dismiss();

                            }
                        });
                    }
                });

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.groups:
                startActivity(new Intent(MainActivity.this,GroupChatActivity.class));
                break;

            case R.id.search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                break;

            case R.id.settings:
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                auth= FirebaseAuth.getInstance();
                auth.signOut();
                startActivity(new Intent(MainActivity.this, PhoneNumber.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

/*
    //online offline create for firebase
    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }


    @Override
    protected void onPause() {
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
        super.onPause();
    }
*/

    
    //top menu add
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}