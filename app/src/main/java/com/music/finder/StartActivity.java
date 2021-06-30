package com.music.finder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.AlertDialog;
import android.os.AsyncTask;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {


    ViewPager viewPager;
    SwipeAdapter swipeAdapter;
    boolean connected = false;
    int position;
    Intent first, mysongs, about;
    YouTubePlayerSupportFragment youTubePlayerView;

    SharedPreferences sharedPreferences;
    static TextView title, subtitle, date;

    ImageButton imageButton;
    int x = 0;
    static String artysta, tytul, ytID, data;
    Intent intent;
    ImageView playVideo;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    public static TextView getTitleStart() {
        return title;
    }

    public static TextView getSubtitle() {
        return subtitle;
    }

    public static TextView getDate() {
        return date;
    }


    public void onBackPressed() {
        x++;
        if (x == 2) {
            finish();
            System.exit(0);
        } else
            Toast.makeText(this, "Press again to exit app", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        MobileAds.initialize(this, "ca-app-pub-1029819886833987~7929650316");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1029819886833987/9584015908");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                startActivity(mysongs);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        TextView search = findViewById(R.id.textView11);
        TabLayout tabLayout = findViewById(R.id.indicator);
        playVideo = findViewById(R.id.imageView9);
        intent = new Intent(this, MusicActivity.class);
        viewPager = findViewById(R.id.view_pager);
        youTubePlayerView = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.view2);
        swipeAdapter = new SwipeAdapter(this);
        viewPager.setAdapter(swipeAdapter);
        tabLayout.setupWithViewPager(viewPager, true);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 3000, 3000);
        first = new Intent(this, FindSongActivity.class);
        mysongs = new Intent(this, MySongsActivity.class);
        about = new Intent(this, AboutActivity.class);
        sharedPreferences = this.getSharedPreferences("com.music.finder", Context.MODE_PRIVATE);
        artysta = sharedPreferences.getString("mainArtist", "ARTIST");
        tytul = sharedPreferences.getString("mainTitle", "TITLE");
        ytID = sharedPreferences.getString("mainYT", "");
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        date = findViewById(R.id.textView15);
        imageButton = findViewById(R.id.imageButton);
        data = sharedPreferences.getString("datePlaying", "");
        date.setText(data);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, FindSongActivity.class));
            }
        });

        playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
                new loadData().execute();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchHistoryActivity.class));
            }
        });

        title.setText(artysta);
        subtitle.setText(tytul);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            connected = true;
        } else {
            connected = false;

            new AlertDialog.Builder(this)
                    .setTitle("NO INTERNET CONNECTION")
                    .setMessage("Please check your internet connection")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })

                    .setIcon(android.R.drawable.ic_dialog_alert).create()
                    .show();
        }
    }

    public void activityStart(View view) {
        position = viewPager.getCurrentItem();

        switch (position) {
            case 0:
                startActivity(first);
                sharedPreferences.edit().putBoolean("czyMain", true).apply();
                break;
            case 1:
                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
                else
                    startActivity(mysongs);
                break;
            case 2:
                startActivity(about);
                break;
        }
    }

    public class loadData extends AsyncTask<Void, Void, Void> {

        String title = StartActivity.subtitle.getText().toString(), artist = StartActivity.title.getText().toString(),
                lyrics, ytVideoId;

        @Override
        protected Void doInBackground(Void... voids) {
            String ytApiKey = getString(R.string.yt_data_api_key);
            String musixmatchApiKey = getString(R.string.musixmatch_api_key);
            String geniusApiKey = getString(R.string.genius_api_key);

            String musixmatchUrl = "https://api.musixmatch.com/ws/1.1/matcher.lyrics.get?q_track=";
            String ytUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=";
            try {
                JsonObject results = FindSongActivity.getJsonFromUrl("https://api.genius.com/search?q=" + title.replace(" ", "%20") + artist.replace(" ", "%20"), geniusApiKey);
                JsonArray hits = results.getAsJsonObject("response").getAsJsonArray("hits");

                title = hits.get(0).getAsJsonObject().get("result").getAsJsonObject().get("title").getAsString();
                artist = hits.get(0).getAsJsonObject().get("result").getAsJsonObject().get("primary_artist").getAsJsonObject().get("name").getAsString();

                try {
                    JsonObject yt1VideoJson = FindSongActivity.getJsonFromUrl(ytUrl + title.replace(" ", "%20") + artist.replace(" ", "%20") + "&key=" + ytApiKey);
                    ytVideoId = yt1VideoJson.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("id").get("videoId").getAsString();
                } catch (Exception e) {
                    ytVideoId = "";
                }

                JsonObject lyrics1Object = FindSongActivity.getJsonFromUrl(musixmatchUrl + title.replace(" ", "%20") + "&q_artist=" + artist.replace(" ", "%20") + "&apikey=" + musixmatchApiKey);
                if (lyrics1Object.getAsJsonObject("message").getAsJsonObject("header").get("status_code").getAsInt() == 200)
                    lyrics = lyrics1Object.getAsJsonObject("message").getAsJsonObject("body").getAsJsonObject().getAsJsonObject("lyrics").get("lyrics_body").getAsString()
                            .replace("******* This Lyrics is NOT for Commercial use *******", "").replace("(1409621981154)", "");
                else
                    lyrics = "";

                intent.putExtra("Wykonawca", artist);
                intent.putExtra("Tytul", title);
                intent.putExtra("Tekst", lyrics);
                intent.putExtra("YTid", ytVideoId);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Toast.makeText(StartActivity.this, "An error occured, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            StartActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < swipeAdapter.getCount() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    } else {
                        viewPager.setCurrentItem(0, true);
                    }
                }
            });
        }
    }
}



