package com.music.finder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MySongsActivity extends AppCompatActivity {

    ListView lv_languages;
    ArrayAdapter<String> language_adapter;
    ArrayList<String> languagesarraylist;
    Bundle extras;
    String wykonawcaTytul;
    SharedPreferences sharedPreferences;
    Intent intent, loading;
    ListView listView;
    boolean connected = false;
    String[] notSegmented = new String[2];
    ArrayList<String> artist = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    String[] artistArray;
    String[] titleArray;
    SongsListView songsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_songs);

        sharedPreferences = this.getSharedPreferences("com.music.finder", Context.MODE_PRIVATE);
        intent = new Intent(this, MusicActivity.class);
        loading = new Intent(this, LoadingActivity.class);

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
                            finishActivity(1);
                        }
                    })

                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        extras = getIntent().getExtras();
        languagesarraylist = new ArrayList<>();

        listView = findViewById(R.id.songsList);
        wykonawcaTytul = sharedPreferences.getString("friends", "");


        if (wykonawcaTytul.contains("%/%") && notSegmented.length != 1) {
            init();
            lv_languages.setAdapter(songsListView);

            SwipeDismissListViewTouchListener touchListener =
                    new SwipeDismissListViewTouchListener(
                            lv_languages,
                            new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                @Override
                                public boolean canDismiss(int position) {
                                    return true;
                                }

                                @Override
                                public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                    for (int position : reverseSortedPositions) {
                                        sharedPreferences.edit().putString("friends", sharedPreferences.getString("friends", "").replace(languagesarraylist.get(position) + "%/%", "")).apply();
                                        languagesarraylist.remove(position);
                                        artist.remove(position);
                                        title.remove(position);
                                        songsListView.notifyDataSetChanged();
                                    }
                                }
                            });
            lv_languages.setOnTouchListener(touchListener);

            lv_languages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String rozdziel = languagesarraylist.get(position);
                    String[] jeden = rozdziel.split("-");
                    sharedPreferences.edit().putString("artist", jeden[1]).apply();
                    sharedPreferences.edit().putString("title", jeden[0]).apply();
                    startActivity(loading);
                    loading.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    new loadData().execute();
                }
            });
        } else
            Toast.makeText(this, "Your library is empty, add some songs first", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MySongsActivity.this, "An error occured, please try again", Toast.LENGTH_SHORT).show();
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

    private void init() {

        String[] dataSplit = wykonawcaTytul.split("%/%");

        languagesarraylist.addAll(Arrays.asList(dataSplit));
        for (int i = 0; i < languagesarraylist.size(); i++) {
            notSegmented = languagesarraylist.get(i).split("-");
            artist.add(notSegmented[0]);
            title.add(notSegmented[1]);
        }

        artistArray = new String[artist.size()];
        titleArray = new String[title.size()];

        for (int i = 0; i < artist.size(); i++) {
            artistArray[i] = artist.get(i);
        }

        for (int i = 0; i < title.size(); i++) {
            titleArray[i] = title.get(i);
        }

        songsListView = new SongsListView(this, artist, title);
        language_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, languagesarraylist);
        lv_languages = findViewById(R.id.songsList);
    }
}
