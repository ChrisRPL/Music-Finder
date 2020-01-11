package com.music.finder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Handler;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {


    ViewPager viewPager;
    SwipeAdapter swipeAdapter;
    boolean connected = false;
    ImageView play;
    RelativeLayout relativeLayout;
    int position;
    Intent first, mysongs, about;
    YouTubePlayerSupportFragment youTubePlayerView;

    SharedPreferences sharedPreferences;
    static TextView title, subtitle, date;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    ImageButton imageButton;
    int x = 0;
    static TextView przechwyc;
    static String artysta, tytul, ytID, data;
    YouTubePlayer youTubePlayer1;
    Intent intent;
    ImageView playVideo;
    private Timer timer;
    private int current_pos = 0;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;



    public static TextView getTitleStart() {
        return title;
    }

    public static TextView getSubtitle()
    {
        return subtitle;
    }

    public static TextView getDate(){ return date;}


    public void onBackPressed()
    {

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

        MobileAds.initialize(this,"ca-app-pub-1029819886833987~7929650316");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1029819886833987/9584015908");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
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
        first = new Intent(this, MainActivity.class);
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
                startActivity(new Intent(StartActivity.this, MainActivity.class));
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
                startActivity(new Intent(getApplicationContext(), Activity.class));
            }
        });






        title.setText(artysta);
        subtitle.setText(tytul);


















        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
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

                    .setIcon(android.R.drawable.ic_dialog_alert).create()
                    .show();
        }


    }



    public void activityStart(View view)
    {
        position = viewPager.getCurrentItem();




        switch (position){
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
            default:
                Log.i("AAAAAAAAAAAAAAAAAAAAAAA","AAAAAAAAAAAAAAAAAAAAAAAA" );
        }
    }

    private void slideShow()
    {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
    }



    public class loadData extends AsyncTask<Void, Void, Void>
    {

        String lyrics3 = "", ytID3 = "", ytID3Czyste = "", lyrics3Czyste = "", title= StartActivity.subtitle.getText().toString(), artist = StartActivity.title.getText().toString();


        Document htmlGet, tekst, doc;

        int a_int_drugi = 47;
        int muzyka1 = 76;

        public Boolean ifLyricsOfThatSong(String artysta, String tytul, int x, Document songHTML)
        {

            String htmlTag1 = songHTML.select("a").eq(x).text();



            if (htmlTag1.contains("-")) {
                if (htmlTag1.split("-")[0].replace(" ", "").toUpperCase().equals(artysta.replace(" ", "").toUpperCase()) && htmlTag1.split("-")[1].replace(" ", "").toUpperCase().equals(tytul.replace(" ", "").toUpperCase()))
                    return true;
                else
                    return false;
            }
            else
                return false;


        }




        @Override
        protected Void doInBackground(Void... voids) {

            Log.i("DASDSADSADSDSADSADSA", artist);
            Log.i("DASDSADSADSDSADSADSA", title);

            try {
                doc = (Document) Jsoup.connect("https://www.youtube.com/results?search_query=" + artist+"-"+title).get();
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Toast.makeText(StartActivity.this, "An error occured, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
            while(!doc.select("a").eq(a_int_drugi).text().contains("-"))
            {
                a_int_drugi++;
            }

            String[] wykonawcaRozdzielony = artist.split(" ");
            String[] tytulRozdzielony = title.split(" ");
            String wykonawcaString = "";
            String tytulString = "";
            String verify = "not found";
            int muzyka1 = 76;

            for (String wykoanwca: wykonawcaRozdzielony)
            {
                wykonawcaString+=wykoanwca + "+";
            }

            for (String tytul: tytulRozdzielony)
            {
                tytulString+= tytul + "+";
            }

            try {
                htmlGet = Jsoup.connect("https://www.tekstowo.pl/szukaj,wykonawca," + wykonawcaString +",tytul,"+tytulString+".html").get();
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Toast.makeText(StartActivity.this, "An error occured, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
            while (!ifLyricsOfThatSong(artist, title, muzyka1, htmlGet)&&muzyka1<91) {
                muzyka1++;
                if(ifLyricsOfThatSong(artist, title, muzyka1, htmlGet))
                {
                    verify="found";
                    Log.i("PRZERWANO", "PRZERWANO");
                    break;
                }
            }

            if (!verify.equals("not found")) {
                String htmlTag = htmlGet.select("a").eq(muzyka1).attr("href");

                try {
                    tekst = Jsoup.connect("https://www.tekstowo.pl" + htmlTag).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            Toast.makeText(StartActivity.this, "An error occured, please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                lyrics3 = tekst.select("div.song-text").outerHtml();
                lyrics3Czyste = lyrics3.replace("<h2>Tekst piosenki:</h2>", "").replace("<br>", "\n").replace("<div class=\"song-text\">", "").split("<p>")[0];
            }else if (!htmlGet.body().text().contains("brak wyników wyszukiwania"))
            {
                String htmlTag = htmlGet.select("a").eq(76).attr("href");

                try {
                    tekst = Jsoup.connect("https://www.tekstowo.pl" + htmlTag).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            Toast.makeText(StartActivity.this, "An error occured, please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                lyrics3 = tekst.select("div.song-text").outerHtml();
                lyrics3Czyste = lyrics3.replace("<h2>Tekst piosenki:</h2>", "").replace("<br>", "\n").replace("<div class=\"song-text\">", "").split("<p>")[0];
            }
            else
                lyrics3Czyste="Lyrics not found";
            lyrics3 = tekst.select("div.song-text").outerHtml();
            lyrics3Czyste = lyrics3.replace("<h2>Tekst piosenki:</h2>", "").replace("<br>", "\n").replace("<div class=\"song-text\">", "").split("<p>")[0];
            Log.i("TEEEEEKKSSTT AUUUU", lyrics3Czyste);
            ytID3 = doc.select("a").eq(a_int_drugi).attr("href");
            ytID3Czyste = ytID3.replace("https://www.youtube.com/watch?v=", "").replace("/watch?v=", "");
            Log.i("IIIII AAAJJDDIII AUU", ytID3Czyste);
            intent.putExtra("Wykonawca", artist);
            intent.putExtra("Tytul", title);
            intent.putExtra("Tekst", lyrics3Czyste);
            intent.putExtra("YTid", ytID3Czyste);



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



