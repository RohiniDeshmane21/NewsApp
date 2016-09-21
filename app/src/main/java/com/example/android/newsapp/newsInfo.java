package com.example.android.newsapp;

/**
 * Created by Rupali on 21-09-2016.
 */
public class newsInfo {
    private String NewsTitle;
    private String typeOfNews;
    private String webURL;

    public newsInfo(String newsTitile, String type,String web)
    {
        NewsTitle = newsTitile;
        typeOfNews = type;
        webURL = web;

    }

    public String getNewsTitle() {
        return NewsTitle;
    }

    public String getTypeOfNews() {
        return typeOfNews;
    }

    public String getWebURL() {
        return webURL;
    }
}
