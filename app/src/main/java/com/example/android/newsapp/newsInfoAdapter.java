package com.example.android.newsapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rupali on 14-09-2016.
 */
public class newsInfoAdapter extends ArrayAdapter<newsInfo> {
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final newsInfo currentNews = getItem(position);
        //check if existing view is being reused, otherwise inflate the new
        View listitemView = convertView;

        if (listitemView == null)
        {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.news_info, parent, false);
        }

        ViewHolder holder = new ViewHolder();

        holder.NewsTitle = (TextView) listitemView.findViewById(R.id.textViewNewsName);
        holder.NewsType = (TextView) listitemView.findViewById(R.id.textViewType);
        holder.contributor = (TextView)listitemView.findViewById(R.id.textViewContributer);
        listitemView.setTag(holder);

        holder.NewsTitle.setText(currentNews.getNewsTitle());
        holder.NewsType.setText(currentNews.getTypeOfNews());

        if(!currentNews.getContribute().equals(null))
        {
            holder.contributor.setText(currentNews.getContribute());
            holder.contributor.setVisibility(View.VISIBLE);
        }
        else
            holder.contributor.setVisibility(View.GONE);

        final String webURL = currentNews.getWebURL();

        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webURL));
                getContext().startActivity(browserIntent);
            }
        });

        return listitemView;
    }

    public newsInfoAdapter(Activity context, ArrayList<newsInfo> bookList)
    {
        super(context,0,bookList);
    }
}