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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
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
    ArrayList<String> artist = new ArrayList<String>();
    ArrayList<String> title = new ArrayList<String>();
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

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        else{
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
        languagesarraylist = new ArrayList<String>();

        listView = findViewById(R.id.songsList);
        wykonawcaTytul = sharedPreferences.getString("friends", "");


        if (wykonawcaTytul.contains("%/%")&&notSegmented.length!=1){
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
                                        sharedPreferences.edit().putString("friends",sharedPreferences.getString("friends", "").replace(languagesarraylist.get(position)+ "%/%", "")).apply();
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
        }
        else
            Toast.makeText(this, "Your library is empty, add some songs first", Toast.LENGTH_SHORT).show();
    }

    public class loadData extends AsyncTask<Void, Void, Void>
    {

        String lyrics3 = "", ytID3 = "", ytID3Czyste = "", lyrics3Czyste = "", title= sharedPreferences.getString("artist",""), artist = sharedPreferences.getString("title", "");
        Document htmlGet, tekst, doc;
        int a_int_drugi = 47;

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

            try {
                doc = (Document) Jsoup.connect("https://www.youtube.com/results?search_query=" + artist+"-"+title).get();
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Toast.makeText(MySongsActivity.this, "An error occured, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
                cancel(true);
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
                        Toast.makeText(MySongsActivity.this, "An error occured, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
                cancel(true);
            }

            while (!ifLyricsOfThatSong(artist, title, muzyka1, htmlGet)&&muzyka1<91) {
                muzyka1++;
                if(ifLyricsOfThatSong(artist, title, muzyka1, htmlGet))
                {
                    verify="found";
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
                            Toast.makeText(MySongsActivity.this, "An error occured, please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                    cancel(true);
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
                            Toast.makeText(MySongsActivity.this, "An error occured, please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                    cancel(true);
                }
                lyrics3 = tekst.select("div.song-text").outerHtml();
                lyrics3Czyste = lyrics3.replace("<h2>Tekst piosenki:</h2>", "").replace("<br>", "\n").replace("<div class=\"song-text\">", "").split("<p>")[0];
            }
            else
                lyrics3Czyste="Lyrics not found";

            ytID3 = doc.select("a").eq(a_int_drugi).attr("href");
            ytID3Czyste = ytID3.replace("https://www.youtube.com/watch?v=", "").replace("/watch?v=", "");
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

    private void init() {

        String[] dataSplitted = wykonawcaTytul.split("%/%");

        languagesarraylist.addAll(Arrays.asList(dataSplitted));
        for (int i=0; i<languagesarraylist.size(); i++)
        {
            notSegmented=languagesarraylist.get(i).split("-");
            artist.add(notSegmented[0]);
            title.add(notSegmented[1]);
        }

        artistArray = new String[artist.size()];
        titleArray = new String[title.size()];

        for (int i=0; i<artist.size(); i++)
        {
            artistArray[i]=artist.get(i);
        }

        for (int i=0; i<title.size(); i++)
        {
            titleArray[i]=title.get(i);
        }

        songsListView = new SongsListView(this, artist, title);
        language_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,languagesarraylist);
        lv_languages = (ListView) findViewById(R.id.songsList);
    }
}
