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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
    String[] autorItytulTabCzyste = new String[2];
    String[] autorItytulTabCzysteDwa = new String[2];
    String[] autorItytulTabCzysteTrzy = new String[2];
    String lyrics1;
    String lyrics2;
    String lyrics3;
    String ytID1;
    String ytID2;
    String ytID3;

    String lyrics1Czyste;
    String lyrics2Czyste;
    String lyrics3Czyste;
    String ytID1Czyste;
    String ytID2Czyste;
    String ytID3Czyste;
    Boolean firstFound = false;
    Boolean secondFound = false;
    Boolean thirdFound = false;
    boolean connected = false;
    SharedPreferences sharedPreferences;

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
        MobileAds.initialize(this,"ca-app-pub-1029819886833987~7929650316");
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (czyMain)
            editText.setText("");
        else if (!czyMain)
            editText.setText(query);

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
                }else {
                    checkBox.setChecked(false);
                    isFirstChecked=false;
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
                }else {
                    checkBox2.setChecked(false);
                    isSecondChecked=false;
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
                }else {
                    isThirdChecked=false;
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
                    sharedPreferences.edit().putString("history", sharedPreferences.getString("history", "") + "Query: " + "\"" +editText.getText().toString() +"\"" + "\n"+ "Date: " +new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(Calendar.getInstance().getTime()) + "/").apply();
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
                        sharedPreferences.edit().putString("history", sharedPreferences.getString("history", "") + "Query: " + "\"" +editText.getText().toString() +"\"" + "\n"+ "Date: " +new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(Calendar.getInstance().getTime()) + "/").apply();
                    }
                }
                return false;
            }
        });

        getSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox3.isChecked()){
                    String wykonawca = autorItytulTabCzyste[0];
                    String tytul = autorItytulTabCzyste[1];
                    String tekst = lyrics1Czyste;
                    String YTid = ytID1Czyste;
                    intent.putExtra("Wykonawca", wykonawca);
                    intent.putExtra("Tytul", tytul);
                    intent.putExtra("Tekst", tekst);
                    intent.putExtra("YTid", YTid);
                }else if (checkBox2.isChecked()){
                    String wykonawca = autorItytulTabCzysteDwa[0];
                    String tytul = autorItytulTabCzysteDwa[1];
                    String tekst = lyrics2Czyste;
                    String YTid = ytID2Czyste;
                    intent.putExtra("Wykonawca", wykonawca);
                    intent.putExtra("Tytul", tytul);
                    intent.putExtra("Tekst", tekst);
                    intent.putExtra("YTid", YTid);
                }else if (checkBox.isChecked()) {
                    String wykonawca = autorItytulTabCzysteTrzy[0];
                    String tytul = autorItytulTabCzysteTrzy[1];
                    String tekst = lyrics3Czyste;
                    String YTid = ytID3Czyste;
                    intent.putExtra("Wykonawca", wykonawca);
                    intent.putExtra("Tytul", tytul);
                    intent.putExtra("Tekst", tekst);
                    intent.putExtra("YTid", YTid);
                }

                startActivity(intent);
            }
        });
    }

    public class findSongTask extends AsyncTask<Void, Void, Void>
    {
        public Boolean ifSong(Document doc, int a){ //SPRAWDZAM CZY ZNALEZIONY UTWÓR JEST PIOSENKĄ, SPRAWDZAJĄC CZY ISTNIEJE JEGO TEKST
            String wyraz = doc.select("a").eq(a).text();
            String[] rozdzielone = wyraz.split("-");
            Document html1Get;
            String zawartosc = null;
            try {
               html1Get = Jsoup.connect("https://www.tekstowo.pl/szukaj,wykonawca," + rozdzielone[0] +",tytul,"+rozdzielone[1]+".html").get();
                zawartosc = html1Get.body().text();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Toast.makeText(FindSongActivity.this, "An error occured during loading data", Toast.LENGTH_SHORT).show();
                    }
                });
                cancel(true);
            }

            if (zawartosc!=null)
            {
                return !zawartosc.contains("brak wyników wyszukiwania");
            }
            else
                return false;

        }

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

        public void zalaczJedenWynik() //JEZELI WPROWADZONA FRAZA DO SZUKANIA JEST KROTKA, WYSWIETLAM TYLKO JEDEN WYNIK
        {
            pytanie = editText.getText().toString();
            pytanieRozdzielone = pytanie.split(" ");
            if (pytanieRozdzielone.length==2) {
                zapytanieDoYT[0] = pytanieRozdzielone[0];
                zapytanieDoYT[1] = "+";
                zapytanieDoYT[2] = pytanieRozdzielone[1];

                for (String noweZapytanie: zapytanieDoYT) {
                    zapytanieDoYTwStringu+=noweZapytanie;
                }
            }
            else
                zapytanieDoYTwStringu=pytanie;
            try{
                doc = Jsoup.connect("https://www.youtube.com/results?search_query=" + zapytanieDoYTwStringu).get();
                while(!firstFound)
                {
                    if (doc.select("a").eq(a_int).text().contains("-")){
                        if (ifSong(doc, a_int))
                            firstFound = true;
                        else {
                            a_int++;
                        }
                    }
                    else
                        a_int++;
                }
                autorItyutl = doc.select("a").eq(a_int).text();

                autorItytulTab = autorItyutl.split("-");

                if (autorItytulTab[0].contains("(Official Video)")||autorItytulTab[0].contains("[OFFICIAL VIDEO]")||autorItytulTab[0].contains("(Official Music Video)")||autorItytulTab[0].contains("[Official Music Video]")||autorItytulTab[0].contains("(Video)"))
                {
                    autorItytulTabCzyste[1] = autorItytulTab[0].replace("(Official Video)", "").replace("[OFFICIAL VIDEO]", "").replace("(Official Music Video)", "").replace("[Official Music Video]", "").replace("(Video)", "");
                    autorItytulTabCzyste[0] = autorItytulTab[1];
                } else {

                    autorItytulTabCzyste[0] = autorItytulTab[0];

                    autorItytulTabCzyste[1] = autorItytulTab[1].replace("(Official Video)", "").replace("[OFFICIAL VIDEO]", "").replace("(Official Music Video)", "").replace("[Official Music Video]", "").replace("(Video)", "");
                }
                String[] wykonawcaRozdzielony = autorItytulTabCzyste[0].split(" ");
                String[] tytulRozdzielony = autorItytulTabCzyste[1].split(" ");
                String wykonawcaString = "";
                String tytulString = "";
                String verify = "not found";

                for (String wykoanwca: wykonawcaRozdzielony)
                {
                    wykonawcaString+=wykoanwca + "+";
                }

                for (String tytul: tytulRozdzielony)
                {
                    tytulString+= tytul + "+";
                }
                htmlGet = Jsoup.connect("https://www.tekstowo.pl/szukaj,wykonawca," + wykonawcaString +",tytul,"+tytulString+".html").get();
                while (!ifLyricsOfThatSong(autorItytulTabCzyste[0], autorItytulTabCzyste[1], muzyka1, htmlGet)&&muzyka1<91) {
                    muzyka1++;
                    if(ifLyricsOfThatSong(autorItytulTabCzyste[0], autorItytulTabCzyste[1], muzyka1, htmlGet))
                    {
                        verify="found";
                        break;
                    }
                }

                if (!verify.equals("not found")) {
                    String htmlTag = htmlGet.select("a").eq(muzyka1).attr("href");

                    tekst = Jsoup.connect("https://www.tekstowo.pl" + htmlTag).get();
                    lyrics1 = tekst.select("div.song-text").outerHtml();
                    lyrics1Czyste = lyrics1.replace("<h2>Tekst piosenki:</h2>", "").replace("<br>", "\n").replace("<div class=\"song-text\">", "").split("<p>")[0];
                }else if (!htmlGet.body().text().contains("brak wyników wyszukiwania"))
                {
                    String htmlTag = htmlGet.select("a").eq(76).attr("href");

                    tekst = Jsoup.connect("https://www.tekstowo.pl" + htmlTag).get();
                    lyrics1 = tekst.select("div.song-text").outerHtml();
                    lyrics1Czyste = lyrics1.replace("<h2>Tekst piosenki:</h2>", "").replace("<br>", "\n").replace("<div class=\"song-text\">", "").split("<p>")[0];
                }
                else
                    lyrics1Czyste="Lyrics not found";

                ytID1 = doc.select("a").eq(a_int).attr("href");
                ytID1Czyste = ytID1.replace("https://www.youtube.com/watch?v=", "").replace("/watch?v=", "");

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        checkBox3.setText("1. Artist: " + autorItytulTabCzyste[0] +"\n"+ " Title: " + autorItytulTabCzyste[1]);
                        checkBox3.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }catch (IOException e){
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

        public void zalaczPozostaleDwaWyniki() //JEZELI WPISANA FRAZA JEST DLUZSZA, WYSWIETLAM OPROCZ JEDNEGO POZOSTALE DWA WYNIKI
        {
            pytanie = editText.getText().toString();
            pytanieRozdzielone = pytanie.split(" ");

            if (pytanieRozdzielone.length%2==0)
            {
                for (int i=0; i<pytanieRozdzielone.length; i++)
                {
                    if (i>(pytanieRozdzielone.length/2) - 1)
                        pytanieTrzyTab.add(pytanieRozdzielone[i]);
                    else
                        pytanieDwaTab.add(pytanieRozdzielone[i]);
                }
            }
            else
            {
                for (int i=0; i<pytanieRozdzielone.length; i++)
                {
                    if (i>(pytanieRozdzielone.length + 1)/2 - 1)
                        pytanieTrzyTab.add(pytanieRozdzielone[i]);
                    else
                        pytanieDwaTab.add(pytanieRozdzielone[i]);
                }
            }

            for (int i=0; i<pytanieDwaTab.size(); i++)
            {
                pytanieYTdwa.add(pytanieDwaTab.get(i));
                pytanieYTdwa.add("+");
            }

            for (int i=0; i<pytanieTrzyTab.size(); i++)
            {
                pytanieYTtrzy.add(pytanieTrzyTab.get(i));
                pytanieYTtrzy.add("+");
            }

            for (int i=0; i<pytanieYTdwa.size(); i++)
            {
                zapytanieDoYTwStringuDwa+=pytanieYTdwa.get(i);
            }

            for (int i=0; i<pytanieYTtrzy.size(); i++)
            {
                zapytanieDoYTwStringuTrzy+=pytanieYTtrzy.get(i);
            }

            try{
                doc = Jsoup.connect("https://www.youtube.com/results?search_query=" + zapytanieDoYTwStringuDwa).get();
                while(!secondFound)
                {
                    if (doc.select("a").eq(a_int_trzeci).text().contains("-")){
                        if (ifSong(doc, a_int_trzeci))
                            secondFound = true;
                        else {
                            a_int_trzeci++;
                            continue;
                        }
                    }
                    else
                        a_int_trzeci++;
                }
                autorItyutlDwa = doc.select("a").eq(a_int_trzeci).text();

                autorItytulTabDwa = autorItyutlDwa.split("-");

                if (autorItytulTabDwa[0].contains("(Official Video)")||autorItytulTabDwa[0].contains("[OFFICIAL VIDEO]")||autorItytulTabDwa[0].contains("(Official Music Video)")||autorItytulTabDwa[0].contains("[Official Music Video]")||autorItytulTabDwa[0].contains("(Video)"))
                {
                    autorItytulTabCzysteDwa[1] = autorItytulTabDwa[0].replace("(Official Video)", "").replace("[OFFICIAL VIDEO]", "").replace("(Official Music Video)", "").replace("[Official Music Video]", "").replace("(Video)", "");
                    autorItytulTabCzysteDwa[0] = autorItytulTabDwa[1];
                } else {
                    autorItytulTabCzysteDwa[0] = autorItytulTabDwa[0];
                    autorItytulTabCzysteDwa[1] = autorItytulTabDwa[1].replace("(Official Video)", "").replace("[OFFICIAL VIDEO]", "").replace("(Official Music Video)", "").replace("[Official Music Video]", "").replace("(Video)", "");
                }

                String[] wykonawcaRozdzielony = autorItytulTabCzysteDwa[0].split(" ");
                String[] tytulRozdzielony = autorItytulTabCzysteDwa[1].split(" ");
                String wykonawcaString = "";
                String tytulString = "";
                String verify = "not found";

                for (String wykoanwca: wykonawcaRozdzielony)
                {
                    wykonawcaString+=wykoanwca + "+";
                }

                for (String tytul: tytulRozdzielony)
                {
                    tytulString+= tytul + "+";
                }
                htmlGet = Jsoup.connect("https://www.tekstowo.pl/szukaj,wykonawca," + wykonawcaString +",tytul,"+tytulString+".html").get();


                while (!ifLyricsOfThatSong(autorItytulTabCzysteDwa[0], autorItytulTabCzysteDwa[1], muzyka2, htmlGet)&&muzyka2<91) {
                    muzyka2++;
                    if(ifLyricsOfThatSong(autorItytulTabCzysteDwa[0], autorItytulTabCzysteDwa[1], muzyka2, htmlGet))
                    {
                        verify="found";
                        break;
                    }
                }

                if (!verify.equals("not found")) {
                    String htmlTag = htmlGet.select("a").eq(muzyka2).attr("href");

                    tekst = Jsoup.connect("https://www.tekstowo.pl" + htmlTag).get();
                    lyrics2 = tekst.select("div.song-text").outerHtml();
                    lyrics2Czyste = lyrics2.replace("<h2>Tekst piosenki:</h2>", "").replace("<br>", "\n").replace("<div class=\"song-text\">", "").split("<p>")[0];
                }else if (!htmlGet.body().text().contains("brak wyników wyszukiwania"))
                {
                    String htmlTag = htmlGet.select("a").eq(76).attr("href");

                    tekst = Jsoup.connect("https://www.tekstowo.pl" + htmlTag).get();
                    lyrics2 = tekst.select("div.song-text").outerHtml();
                    lyrics2Czyste = lyrics2.replace("<h2>Tekst piosenki:</h2>", "").replace("<br>", "\n").replace("<div class=\"song-text\">", "").split("<p>")[0];
                }
                else
                    lyrics2Czyste="Lyrics not found";


                ytID2 = doc.select("a").eq(a_int_trzeci).attr("href");
                ytID2Czyste = ytID2.replace("https://www.youtube.com/watch?v=", "").replace("/watch?v=", "");

                doc = Jsoup.connect("https://www.youtube.com/results?search_query=" + zapytanieDoYTwStringuTrzy).get();
                while(!thirdFound)
                {
                    if (doc.select("a").eq(a_int_drugi).text().contains("-")){
                        if (ifSong(doc, a_int_drugi))
                            thirdFound = true;
                        else {
                            a_int_drugi++;
                        }
                    }
                    else
                        a_int_drugi++;
                }
                autorItyutlTrzy = doc.select("a").eq(a_int_drugi).text();

                if (!autorItyutlTrzy.contains("https")||!autorItyutlTrzy.contains("http")) {
                    autorItytulTabTrzy = autorItyutlTrzy.split("-");
                    if (autorItytulTabTrzy[0].contains("(Official Video)") || autorItytulTabTrzy[0].contains("[OFFICIAL VIDEO]") || autorItytulTabTrzy[0].contains("(Official Music Video)") || autorItytulTabTrzy[0].contains("[Official Music Video]")||autorItytulTabTrzy[0].contains("(Video)")) {
                        autorItytulTabCzysteTrzy[1] = autorItytulTabTrzy[0].replace("(Official Video)", "").replace("[OFFICIAL VIDEO]", "").replace("(Official Music Video)", "").replace("[Official Music Video]", "").replace("(Video)", "");
                        autorItytulTabCzysteTrzy[0] = autorItytulTabTrzy[1];
                    } else {
                        autorItytulTabCzysteTrzy[0] = autorItytulTabTrzy[0];
                        autorItytulTabCzysteTrzy[1] = autorItytulTabTrzy[1].replace("(Official Video)", "").replace("[OFFICIAL VIDEO]", "").replace("(Official Music Video)", "").replace("[Official Music Video]", "").replace("(Video)", "");
                    }

                    String[] wykonawcaRozdzielony1 = autorItytulTabCzysteTrzy[0].split(" ");
                    String[] tytulRozdzielony1 = autorItytulTabCzysteTrzy[1].split(" ");
                    String wykonawcaString1 = "";
                    String tytulString1 = "";

                    for (String wykoanwca : wykonawcaRozdzielony1) {
                        wykonawcaString1 += wykoanwca + "+";
                    }

                    for (String tytul : tytulRozdzielony1) {
                        tytulString1 += tytul + "+";
                    }
                    htmlGet = Jsoup.connect("https://www.tekstowo.pl/szukaj,wykonawca," + wykonawcaString1 + ",tytul," + tytulString1 + ".html").get();
                    verify = "not found";

                    while (!ifLyricsOfThatSong(autorItytulTabCzysteTrzy[0], autorItytulTabCzysteTrzy[1], muzyka3, htmlGet)&&muzyka3<91) {
                        muzyka3++;
                        if (ifLyricsOfThatSong(autorItytulTabCzysteTrzy[0], autorItytulTabCzysteTrzy[1], muzyka3, htmlGet)) {
                            verify = "found";
                            break;
                        }
                    }

                    if (!verify.equals("not found")) {
                        String htmlTag = htmlGet.select("a").eq(muzyka3).attr("href");

                        tekst = Jsoup.connect("https://www.tekstowo.pl" + htmlTag).get();
                        lyrics3 = tekst.select("div.song-text").outerHtml();
                        lyrics3Czyste = lyrics3.replace("<h2>Tekst piosenki:</h2>", "").replace("<br>", "\n").replace("<div class=\"song-text\">", "").split("<p>")[0];
                    } else if (!htmlGet.body().text().contains("brak wyników wyszukiwania")) {
                        String htmlTag = htmlGet.select("a").eq(76).attr("href");

                        tekst = Jsoup.connect("https://www.tekstowo.pl" + htmlTag).get();
                        lyrics3 = tekst.select("div.song-text").outerHtml();
                        lyrics3Czyste = lyrics3.replace("<h2>Tekst piosenki:</h2>", "").replace("<br>", "\n").replace("<div class=\"song-text\">", "").split("<p>")[0];
                    } else
                        lyrics3Czyste = "Lyrics not found";


                    ytID3 = doc.select("a").eq(a_int_drugi).attr("href");
                    ytID3Czyste = ytID3.replace("https://www.youtube.com/watch?v=", "").replace("/watch?v=", "");
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        checkBox2.setText("2. Artist: " + autorItytulTabCzysteDwa[0] + "\n"+ " Title: " + autorItytulTabCzysteDwa[1]);
                        if (!autorItyutlTrzy.contains("https")||!autorItyutlTrzy.contains("http")) {
                            checkBox.setText("3. Artist: " + autorItytulTabCzysteTrzy[0] + "\n" + " Title: " + autorItytulTabCzysteTrzy[1]);
                            checkBox.setVisibility(View.VISIBLE);
                        }
                        checkBox2.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
                pytanieYTdwa.clear();
                pytanieYTtrzy.clear();

            }catch (IOException e){
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Toast.makeText(FindSongActivity.this, "An error occured during loading data", Toast.LENGTH_SHORT).show();
                    }
                });
                cancel(true);
            }
        }


        String pytanie;
        ArrayList<String> pytanieDwaTab = new ArrayList<>(); // STRING ZAWIERAJĄCY PIERWSZĄ POŁOWĘ ZAPYTANIA
        ArrayList<String> pytanieTrzyTab = new ArrayList<>(); // STRING ZAWIERAJĄCY DRUGĄ POŁOWĘ ZAPYTANIA
        String[] zapytanieDoYT= new String[3];
        ArrayList<String> pytanieYTdwa = new ArrayList<>();
        ArrayList<String> pytanieYTtrzy = new ArrayList<>();
        String zapytanieDoYTwStringu;
        String zapytanieDoYTwStringuDwa;
        String zapytanieDoYTwStringuTrzy;
        String autorItyutl;
        String autorItyutlDwa;
        String autorItyutlTrzy;
        String[] autorItytulTab = new String[2];
        String[] autorItytulTabDwa;
        String[] autorItytulTabTrzy;

        String[] pytanieRozdzielone; // TABLICA ZAWIERAJĄCA FRAZĘ WYNOSZĄCĄ WIĘCEJ NIŻ DWA SŁOWA
        Document doc;
        Document htmlGet;
        Document tekst;
        int a_int = 47;
        int a_int_drugi = 47;
        int a_int_trzeci = 47;
        int muzyka1 = 76;
        int muzyka2 = 76;
        int muzyka3 = 76;


        @Override
        protected Void doInBackground(Void... voids) {
            String[] tab = editText.getText().toString().split(" ");
            if (tab.length>2) {
                zalaczJedenWynik();
                zalaczPozostaleDwaWyniki();
            }else
                zalaczJedenWynik();

            return null;
        }

    }
}
