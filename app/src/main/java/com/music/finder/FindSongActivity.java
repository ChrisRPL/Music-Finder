package com.music.finder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class FindSongActivity extends AppCompatActivity {

    TextView czyTo;
    ProgressBar progressBar;
    Button button;
    EditText editText;
    TextView textView;
    Button getSong;
    CheckBox checkBox;
    CheckBox checkBox2;
    CheckBox checkBox3;
    Boolean isFirstChecked = false;
    Boolean isSecondChecked = false;
    Boolean isThirdChecked = false;

    private String title1;
    private String artist1;
    private String lyrics1;
    private String ytVideoId1;

    private String title2;
    private String artist2;
    private String lyrics2;
    private String ytVideoId2;

    private String title3;
    private String artist3;
    private String lyrics3;
    private String ytVideoId3;

    boolean connected = false;
    SharedPreferences sharedPreferences;

    public static JsonObject getJsonFromUrl(String url) throws IOException {
        URL urlObject = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        String inline = "";
        Scanner scanner = new Scanner(urlObject.openStream());

        while (scanner.hasNext()) {
            inline += scanner.nextLine();
        }

        scanner.close();

        JsonParser parse = new JsonParser();
        JsonObject data_obj = (JsonObject) parse.parse(inline);
        conn.disconnect();

        return data_obj;
    }

    public static JsonObject getJsonFromUrl(String url, String bearerToken) throws IOException {
        URL urlObject = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + bearerToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.connect();

        StringBuilder inline = new StringBuilder();
        Scanner scanner = new Scanner(conn.getInputStream());

        while (scanner.hasNext()) {
            inline.append(scanner.nextLine());
        }

        scanner.close();

        JsonParser parse = new JsonParser();
        JsonObject data_obj = (JsonObject) parse.parse(inline.toString());
        conn.disconnect();

        return data_obj;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editText);
        czyTo = findViewById(R.id.czyTo);
        textView = findViewById(R.id.textViewTytul1);
        getSong = findViewById(R.id.getSong);
        checkBox = findViewById(R.id.checkBox);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        progressBar = findViewById(R.id.progressBar);
        sharedPreferences = this.getSharedPreferences("com.music.finder", Context.MODE_PRIVATE);
        boolean czyMain = sharedPreferences.getBoolean("czyMain", true);
        String query = sharedPreferences.getString("query", "");
        MobileAds.initialize(this, "ca-app-pub-1029819886833987~7929650316");
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (czyMain)
            editText.setText("");
        else editText.setText(query);

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

                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        final Intent intent = new Intent(this, MusicActivity.class);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSong.setBackgroundResource(R.color.twitter);
                getSong.setTextColor(getResources().getColor(R.color.ic_launcher_background));
                getSong.setEnabled(true);

                if (!isFirstChecked) {
                    checkBox.setChecked(true);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                } else {
                    checkBox.setChecked(false);
                    isFirstChecked = false;
                }
            }
        });

        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSong.setBackgroundResource(R.color.twitter);
                getSong.setTextColor(getResources().getColor(R.color.ic_launcher_background));
                getSong.setEnabled(true);

                if (!isSecondChecked) {
                    checkBox.setChecked(false);
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(false);
                } else {
                    checkBox2.setChecked(false);
                    isSecondChecked = false;
                }
            }
        });

        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSong.setBackgroundResource(R.color.twitter);
                getSong.setTextColor(getResources().getColor(R.color.ic_launcher_background));
                getSong.setEnabled(true);

                if (!isThirdChecked) {
                    checkBox.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(true);
                } else {
                    isThirdChecked = false;
                    checkBox3.setChecked(false);
                }
            }
        });

        final InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                                          if (editText.getText().toString().equals(""))
                                              Toast.makeText(FindSongActivity.this, "Please, give me some text first", Toast.LENGTH_SHORT).show();
                                          else {
                                              new findSongTask().execute();
                                              czyTo.setVisibility(View.VISIBLE);
                                              button.setVisibility(View.INVISIBLE);
                                              editText.setVisibility(View.INVISIBLE);
                                              textView.setVisibility(View.INVISIBLE);
                                              getSong.setVisibility(View.VISIBLE);
                                              progressBar.setVisibility(View.VISIBLE);
                                              sharedPreferences.edit().putString("history", sharedPreferences.getString("history", "") + "Query: " + "\"" + editText.getText().toString() + "\"" + "\n" + "Date: " + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(Calendar.getInstance().getTime()) + "/").apply();
                                          }
                                      }


                                  }
        );

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_FORWARD)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (editText.getText().toString().equals(""))
                        Toast.makeText(FindSongActivity.this, "Please, give me some text first", Toast.LENGTH_SHORT).show();
                    else {
                        new findSongTask().execute();
                        czyTo.setVisibility(View.VISIBLE);
                        button.setVisibility(View.INVISIBLE);
                        editText.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        getSong.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        sharedPreferences.edit().putString("history", sharedPreferences.getString("history", "") + "Query: " + "\"" + editText.getText().toString() + "\"" + "\n" + "Date: " + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(Calendar.getInstance().getTime()) + "/").apply();
                    }
                }
                return false;
            }
        });

        getSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox3.isChecked()) {
                    String wykonawca = artist1;
                    String tytul = title1;
                    String tekst = lyrics1;
                    String YTid = ytVideoId1;
                    intent.putExtra("Wykonawca", wykonawca);
                    intent.putExtra("Tytul", tytul);
                    intent.putExtra("Tekst", tekst);
                    intent.putExtra("YTid", YTid);
                } else if (checkBox2.isChecked()) {
                    String wykonawca = artist2;
                    String tytul = title2;
                    String tekst = lyrics2;
                    String YTid = ytVideoId2;
                    intent.putExtra("Wykonawca", wykonawca);
                    intent.putExtra("Tytul", tytul);
                    intent.putExtra("Tekst", tekst);
                    intent.putExtra("YTid", YTid);
                } else if (checkBox.isChecked()) {
                    String wykonawca = artist3;
                    String tytul = title3;
                    String tekst = lyrics3;
                    String YTid = ytVideoId3;
                    intent.putExtra("Wykonawca", wykonawca);
                    intent.putExtra("Tytul", tytul);
                    intent.putExtra("Tekst", tekst);
                    intent.putExtra("YTid", YTid);
                }

                startActivity(intent);
            }
        });
    }

    public class findSongTask extends AsyncTask<Void, Void, Void> {
        Boolean ifSong(String titleAndAuthor) throws IOException { //SPRAWDZAM CZY ZNALEZIONY UTWÓR JEST PIOSENKĄ, SPRAWDZAJĄC CZY ISTNIEJE JEGO TEKST
            JsonObject musicObject = getJsonFromUrl("https://api.genius.com/search?q=" + titleAndAuthor, getString(R.string.genius_api_key));
            return musicObject.getAsJsonObject("response").getAsJsonArray("hits").size() > 0;
        }

        private void getResults() //JEZELI WPROWADZONA FRAZA DO SZUKANIA JEST KROTKA, WYSWIETLAM TYLKO JEDEN WYNIK
        {
            String ytApiKey = getString(R.string.yt_data_api_key);
            String musixmatchApiKey = getString(R.string.musixmatch_api_key);
            String geniusApiKey = getString(R.string.genius_api_key);

            String query = editText.getText().toString().replace(" ", "%20");
            try {
                if (ifSong(query)) {

                    String musixmatchUrl = "https://api.musixmatch.com/ws/1.1/matcher.lyrics.get?q_track=";
                    String ytUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=";
                    JsonObject results = getJsonFromUrl("https://api.genius.com/search?q=" + query, geniusApiKey);
                    JsonArray hits = results.getAsJsonObject("response").getAsJsonArray("hits");

                    if (hits.size() > 0) {
                        title1 = hits.get(0).getAsJsonObject().get("result").getAsJsonObject().get("title").getAsString();
                        artist1 = hits.get(0).getAsJsonObject().get("result").getAsJsonObject().get("primary_artist").getAsJsonObject().get("name").getAsString();

                        try {
                            JsonObject yt1VideoJson = getJsonFromUrl(ytUrl + title1.replace(" ", "%20") + artist1.replace(" ", "%20") + "&key=" + ytApiKey);
                            ytVideoId1 = yt1VideoJson.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("id").get("videoId").getAsString();
                        } catch (Exception e) {
                            ytVideoId1 = "";
                        }

                        JsonObject lyrics1Object = getJsonFromUrl(musixmatchUrl + title1.replace(" ", "%20") + "&q_artist=" + artist1.replace(" ", "%20") + "&apikey=" + musixmatchApiKey);
                        if (lyrics1Object.getAsJsonObject("message").getAsJsonObject("header").get("status_code").getAsInt() == 200)
                            lyrics1 = lyrics1Object.getAsJsonObject("message").getAsJsonObject("body").getAsJsonObject().getAsJsonObject("lyrics").get("lyrics_body").getAsString()
                                    .replace("******* This Lyrics is NOT for Commercial use *******", "").replace("(1409621981154)", "");
                        else
                            lyrics1 = "";
                    }

                    if (hits.size() > 1) {
                        title2 = hits.get(1).getAsJsonObject().get("result").getAsJsonObject().get("title").getAsString();
                        artist2 = hits.get(1).getAsJsonObject().get("result").getAsJsonObject().get("primary_artist").getAsJsonObject().get("name").getAsString();

                        JsonObject lyrics2Object = getJsonFromUrl(musixmatchUrl + title2.replace(" ", "%20") + "&q_artist=" + artist2.replace(" ", "%20") + "&apikey=" + musixmatchApiKey);
                        if (lyrics2Object.getAsJsonObject("message").getAsJsonObject("header").get("status_code").getAsInt() == 200)
                            lyrics2 = lyrics2Object.getAsJsonObject("message").getAsJsonObject("body").getAsJsonObject().getAsJsonObject("lyrics").get("lyrics_body").getAsString()
                                    .replace("******* This Lyrics is NOT for Commercial use *******", "").replace("(1409621981154)", "");
                        else
                            lyrics2 = "";

                        try {
                            JsonObject yt2VideoJson = getJsonFromUrl(ytUrl + title2.replace(" ", "%20") + artist2.replace(" ", "%20") + "&key=" + ytApiKey);
                            ytVideoId2 = yt2VideoJson.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("id").get("videoId").getAsString();
                        } catch (Exception e) {
                            ytVideoId2 = "";
                        }
                    }

                    if (hits.size() > 2) {
                        title3 = hits.get(2).getAsJsonObject().get("result").getAsJsonObject().get("title").getAsString();
                        artist3 = hits.get(2).getAsJsonObject().get("result").getAsJsonObject().get("primary_artist").getAsJsonObject().get("name").getAsString();

                        JsonObject lyrics3Object = getJsonFromUrl(musixmatchUrl + title3.replace(" ", "%20") + "&q_artist=" + artist3.replace(" ", "%20") + "&apikey=" + musixmatchApiKey);

                        if (lyrics3Object.getAsJsonObject("message").getAsJsonObject("header").get("status_code").getAsInt() == 200)
                            lyrics3 = lyrics3Object.getAsJsonObject("message").getAsJsonObject("body").getAsJsonObject().getAsJsonObject("lyrics").get("lyrics_body").getAsString()
                                    .replace("******* This Lyrics is NOT for Commercial use *******", "").replace("(1409621981154)", "");
                        else
                            lyrics3 = "";

                        try {
                            JsonObject yt3VideoJson = getJsonFromUrl(ytUrl + title3.replace(" ", "%20") + artist3.replace(" ", "%20") + "&key=" + ytApiKey);
                            ytVideoId3 = yt3VideoJson.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("id").get("videoId").getAsString();
                        } catch (Exception e) {
                            ytVideoId3 = "";
                        }
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (artist1 != null && title1 != null) {
                                checkBox3.setText("1. Artist: " + artist1 + "\n" + " Title: " + title1);
                                checkBox3.setVisibility(View.VISIBLE);
                            }
                            if (artist2 != null && title2 != null) {
                                checkBox2.setText("2. Artist: " + artist2 + "\n" + " Title: " + title2);
                                checkBox2.setVisibility(View.VISIBLE);
                            }
                            if (artist3 != null && title3 != null) {
                                checkBox.setText("3. Artist: " + artist3 + "\n" + " Title: " + title3);
                                checkBox.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else
                    throw new IllegalArgumentException("Not valid query");
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Toast.makeText(FindSongActivity.this, "An error occured, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
                cancel(true);
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            getResults();
            return null;
        }

    }
}
