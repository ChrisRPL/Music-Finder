package com.music.finder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SwipeAdapter extends PagerAdapter {

    private int[] imageRes = {R.drawable.first, R.drawable.mysongs, R.drawable.about};
    private String[] textRes = {"FIND YOUR SONG!", "MY SONGS", "ABOUT THE APP"};
    private Context ctx;
    private LayoutInflater layoutInflater;

    public SwipeAdapter(Context ctx)
    {
        this.ctx = ctx;

    }

    @Override
    public int getCount() {
        return imageRes.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view==(RelativeLayout)o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);
        ImageButton imageButton = item_view.findViewById(R.id.imageView2);
        TextView textView = item_view.findViewById(R.id.textView2);
        imageButton.setImageResource(imageRes[position]);
        textView.setText(textRes[position]);
        container.addView(item_view);




        return item_view;


    }



    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
