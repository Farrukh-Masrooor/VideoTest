package app.hcl.com.videotest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.util.Log;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    ArrayList<Videos> videosArrayList;
    int count=0;
    private LinearLayoutManager layoutManager;
    private RecyclerAdpater mAdapter;
    private RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar=findViewById(R.id.progress_bar);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Write a message to the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("videos");
        Log.d("My_log","adapter2=="+myRef.toString());
        videosArrayList=new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                count++;
                //Log.d("My_log","adapter3=="+dataSnapshot.getValue().toString());
                String url=dataSnapshot.child("url").getValue().toString();
                String title=dataSnapshot.child("title").getValue().toString();
                videosArrayList.add(new Videos(url,title));

                if (count>dataSnapshot.getChildrenCount())
                    Log.d("My_log","title="+title);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setAdapter(new RecyclerAdpater(getApplication(),videosArrayList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
    }
}
