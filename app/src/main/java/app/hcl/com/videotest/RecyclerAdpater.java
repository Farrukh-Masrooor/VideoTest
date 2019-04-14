package app.hcl.com.videotest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerAdpater extends RecyclerView.Adapter<RecyclerAdpater.ViewHolder> {

    Context context;
    int like=0;
    ArrayList<Videos> videosArrayList;
    SimpleExoPlayer player;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String likes="";
    RecyclerAdpater (Context context,ArrayList<Videos> videosArrayList)
    {
        this.context=context;
        this.videosArrayList=videosArrayList;
        //Log.d("My_log","inisd econtrut");
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("videos");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view= LayoutInflater.from(context)
                .inflate(R.layout.recycler_single_view, viewGroup,false);
       ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        Log.d("My_log","onbindview i===+"+i);

        viewHolder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(context).load(videosArrayList.get(i).url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.cardView.setVisibility(View.VISIBLE);
                viewHolder.textView. setVisibility(View.VISIBLE);
                viewHolder.button.setVisibility(View.VISIBLE);
                viewHolder.button2.setVisibility(View.VISIBLE);
                viewHolder.textView.setText(videosArrayList.get(i).title);
                viewHolder.imageView.setVisibility(View.VISIBLE);
                return false;
            }
        })
                //thumbnail(Glide.with(context).load(videosArrayList.get(i).url))
                .into(viewHolder.imageView);

        viewHolder.button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                viewHolder.button2.setBackgroundResource(R.drawable.ic_favorite_black_fill24dp);
                Query query=databaseReference.orderByChild("videos").equalTo(String.valueOf(i+1));
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.hasChild("likes"))
                        {//Log.d("My_log","has child");
                            likes=dataSnapshot.child("likes").getValue().toString();
                            Log.d("My_log","no== "+likes);
                            try {
                                 like=Integer.parseInt(likes);
                            }catch (NumberFormatException e){
                                Log.d("My_log"," "+e);
                            }
                        }


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
                like=like+1;
                likes= String.valueOf(like);
                like=0;
               if (databaseReference.child(String.valueOf(i+1)).child("likes")!=null)
                   databaseReference.child(String.valueOf(i+1)).child("likes").setValue(likes);
               else
                   Log.d("My_log","not exists");
            }
        });
        Log.d("My_log","adapter");
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,VideoActivity.class);
                intent.putExtra("url",videosArrayList.get(i).url);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,VideoActivity.class);
                intent.putExtra("url",videosArrayList.get(i).url);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return videosArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        ImageButton button;
        ImageButton button2;
        TextView textView;
        ProgressBar progressBar;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.image_view);
            textView=itemView.findViewById(R.id.text_view);
            button=itemView.findViewById(R.id.button);
            button2=itemView.findViewById(R.id.liked_button);
            cardView=itemView.findViewById(R.id.card_view);
            progressBar=itemView.findViewById(R.id.progress_bar);

        }
    }
}
