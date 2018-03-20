package example.com.maps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    FirebaseUser user;
    String latitude,longitude,describe,city,key;
    FirebaseAuth firebaseAuth;
    Map<String, String> map;
    File file;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    RecyclerView recyclerView;
    Bitmap img;
    List<LocationDetails> list=new ArrayList<>();
    UserRecycleAdapter adapter;
    File f1;
    CardView textView;
    List<Bitmap> bitmapList = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        assert user != null;
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        textView=findViewById(R.id.main_cardView);
        recyclerView=findViewById(R.id.main_recycleView);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        adapter = new UserRecycleAdapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        databaseReference.child("Users").child(user.getUid()).child("Queries").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
             int a = (int) dataSnapshot.getChildrenCount();
            if (a==0){
                textView.setVisibility(View.VISIBLE);
            }
            else{
                textView.setVisibility(View.INVISIBLE);
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    key=dataSnapshot1.getKey();
                    latitude= String.valueOf(dataSnapshot1.child("lati").getValue());
                    longitude= String.valueOf(dataSnapshot1.child("longi").getValue());
                    describe= String.valueOf(dataSnapshot1.child("describe").getValue());
                    city= String.valueOf(dataSnapshot1.child("city").getValue());
                        f1=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"Maps",key);
                        if(!f1.exists()) f1.mkdirs();
                        storageReference = FirebaseStorage.getInstance().getReference("Location").child(city).child(dataSnapshot1.getKey());
                        storageReference.getFile(f1).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                img = BitmapFactory.decodeFile(file.getAbsolutePath());
                                LocationDetails  locationDetails = new LocationDetails(latitude,longitude,describe,city,img);
                                list.add(locationDetails);
                                adapter.notifyDataSetChanged();
                            }
                        });
                }
            }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.sign_out){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:
                animateFAB();
                break;
            case R.id.fab1:
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.fab2:
                Intent intent1 = new Intent(MainActivity.this,CameraActivity.class);
                startActivity(intent1);
                break;
        }
    }
    public void animateFAB(){
        if(isFabOpen){
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}

