package com.music.finder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchHistoryActivity extends AppCompatActivity {
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    SharedPreferences sharedPreferences;
    String tabelaPrzedPodzialem;
    String[] tabPomoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);

        sharedPreferences = this.getSharedPreferences("com.music.finder", Context.MODE_PRIVATE);
        tabelaPrzedPodzialem = sharedPreferences.getString("history", "");
        tabPomoc = tabelaPrzedPodzialem.split("/");
        arrayList= new ArrayList<String>();

        arrayList.addAll(Arrays.asList(tabPomoc));

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, arrayList);
        listView = findViewById(R.id.history);
        listView.setAdapter(arrayAdapter);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    sharedPreferences.edit().putString("history",sharedPreferences.getString("history", "").replace(arrayList.get(position) + "/", "")).apply();
                                    arrayList.remove(position);
                                    arrayAdapter.notifyDataSetChanged();

                                }
                            }
                        });
        listView.setOnTouchListener(touchListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sharedPreferences.edit().putBoolean("czyMain", false).apply();
                sharedPreferences.edit().putString("query", arrayList.get(position).split("/")[0].split("Date")[0].replace("Query: ", "").replace("\"", "")).apply();

                startActivity(new Intent(getApplicationContext(), FindSongActivity.class));
            }
        });

    }
}
