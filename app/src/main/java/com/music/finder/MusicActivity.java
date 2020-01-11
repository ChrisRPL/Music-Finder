package com.music.finder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MusicActivity extends AppCompatActivity {








    ImageView imageView;
    Boolean ifNotAdded;
    String tytul;
    String wykonawca;
    String tekst;
    String YTid;
    Boolean lyricsClicked = false;

    Bundle extras;
    boolean connected = false;





    YouTubePlayerSupportFragment youTubePlayerView;


    YouTubePlayer.OnInitializedListener onInitializedListener;


    ImageView play;
    Button showLyrics;
    TextView artist;
    TextView title;
    TextView lyrics;
    SharedPreferences sharedPreferences;
    String toIto;
    TextView first;
    TextView second;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        sharedPreferences = this.getSharedPreferences("com.music.finder", Context.MODE_PRIVATE);
        first = findViewById(R.id.textView9);
        second = findViewById(R.id.textView10);
        extras = getIntent().getExtras();
        scrollView = findViewById(R.id.scrollView);


        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else{
            connected = false;

            new AlertDialog.Builder(this)
                    .setTitle("NO INTERNET CONNECTION")
                    .setMessage("Please check your internet connection")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }





        if (extras != null) {
            String value = extras.getString("key");
            //The key argument here must match that used in the other activity

            tytul = extras.getString("Tytul");
            wykonawca = extras.getString("Wykonawca");
            tekst = extras.getString("Tekst");
            YTid = extras.getString("YTid");


        }







        play = findViewById(R.id.imageView4);
        showLyrics = findViewById(R.id.showLyrics);
        imageView = findViewById(R.id.imageView6);
        youTubePlayerView = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.view2);
        artist = findViewById(R.id.textView6);
        title = findViewById(R.id.textView7);
        lyrics = findViewById(R.id.lyricsView);
        lyrics.setText(tekst);
        artist.setText(wykonawca);
        title.setText(tytul);

        artist.setSelected(true);
        title.setSelected(true);

        final TextView main1 = findViewById(R.id.title);
        final TextView main2 = findViewById(R.id.subtitle);

        if(sharedPreferences.getString("friends", "").contains(wykonawca + " - " + tytul)||sharedPreferences.getString("friends", "").contains(tytul + " - " + wykonawca)||sharedPreferences.getString("friends", "").contains(wykonawca + "-" + tytul)||sharedPreferences.getString("friends", "").contains(tytul + "-" + wykonawca)){
            imageView.setImageResource(R.drawable.dodane);
            first.setText("ADDED TO");
            first.setTextColor(getResources().getColor(R.color.twitter));
            }
        else {
            first.setText("ADD TO");
            first.setTextColor(getResources().getColor(R.color.light_grey));
            imageView.setImageResource(R.drawable.dodaj);
        }



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(sharedPreferences.getString("friends", "").contains(wykonawca + " - " + tytul)||sharedPreferences.getString("friends", "").contains(tytul + " - " + wykonawca)||sharedPreferences.getString("friends", "").contains(wykonawca + "-" + tytul)||sharedPreferences.getString("friends", "").contains(tytul + "-" + wykonawca))) {
                    imageView.setImageResource(R.drawable.dodane);
                    ifNotAdded = false;
                    sharedPreferences.edit().putString("friends",sharedPreferences.getString("friends", "")  +wykonawca+"-"+tytul+"%/%").apply();
                    Log.i("SHARED CONTAINS -----> ", sharedPreferences.getString("friends", "OOPS IT IS EMPTY") );
                    toIto = sharedPreferences.getString("friends", "AALO ALOO SWIRY");
                    Log.i("SHARED CONTAINS -----> ", toIto );
                    first.setText("ADDED TO");
                    Toast.makeText(MusicActivity.this, "Added to my songs!", Toast.LENGTH_SHORT).show();
                    first.setTextColor(getResources().getColor(R.color.twitter));

                }else
                {
                    imageView.setImageResource(R.drawable.dodaj);
                    ifNotAdded = true;
                    sharedPreferences.edit().putString("friends", sharedPreferences.getString("friends", "").replace(wykonawca+"-"+tytul+"%/%", "")).apply();
                    first.setText("ADD TO");
                    first.setTextColor(getResources().getColor(R.color.light_grey));
                }
            }
        });





        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(YTid);
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                youTubePlayer.setShowFullscreenButton(false);


            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };





        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubePlayerView.initialize(YouTubeConfig.getApiKEY(), onInitializedListener);
                Log.i("INFO", "YOUTUBE ID " + YTid);
                play.setVisibility(View.GONE);
                sharedPreferences.edit().putString("mainArtist", wykonawca).apply();
                sharedPreferences.edit().putString("mainTitle", tytul).apply();
                sharedPreferences.edit().putString("mainYT", YTid).apply();
                StartActivity.getTitleStart().setText(wykonawca);
                StartActivity.getSubtitle().setText(tytul);
                sharedPreferences.edit().putString("datePlaying", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(Calendar.getInstance().getTime())).apply();
                StartActivity.getDate().setText(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));

            }
        });

        showLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lyricsClicked) {
                    lyrics.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    lyricsClicked = true;
                    showLyrics.setText("Hide lyrics");
                }
                else {
                    lyrics.setVisibility(View.INVISIBLE);
                    scrollView.setVisibility(View.INVISIBLE);
                    showLyrics.setText("Show lyrics");
                    lyricsClicked = false;
                }
            }
        });


    }
}
