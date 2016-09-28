package com.example.android.newsapp;

import java.io.Serializable;

/**
 * Created by Rupali on 21-09-2016.
 */
public class newsInfo implements Serializable {
    private String NewsTitle;
    private String typeOfNews;
    private String webURL;
    private String contribute;

    public newsInfo(String newsTitile, String type,String web,String Contributor)
    {
        NewsTitle = newsTitile;
        typeOfNews = type;
        webURL = web;
        contribute = Contributor;
    }

    public newsInfo() {

    }


    public String getContribute() {
        return contribute;
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
