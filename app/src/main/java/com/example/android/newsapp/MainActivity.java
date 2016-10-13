package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText searchString;
    Button btnSearch;
    TextView msg;
    ArrayList<newsInfo> newslist = new ArrayList<newsInfo>();
    ProgressBar progress;
    newsInfoAdapter newsAdapter;// = new newsInfoAdapter(MainActivity.this, newsInfo);

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private int EARTHQUAKE_LOADER_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
       // loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null,null);

        btnSearch = (Button)findViewById(R.id.buttonSearch);
        msg = (TextView) findViewById(R.id.textViewMsg);

        progress = (ProgressBar)findViewById(R.id.loading_indicator);
        progress.setMax(10);
        progress.setVisibility(View.GONE);
        msg.setVisibility(View.VISIBLE);

        searchString = (EditText) findViewById(R.id.editTextBookName);

        setTitle("Book List");

        final String enteredText = "";

        if(savedInstanceState != null)
        {
            msg.setVisibility(View.VISIBLE);
            newslist = (ArrayList<newsInfo>)savedInstanceState.getSerializable("Old");
            showData(newslist);
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                progress.setProgress(0);
                msg.setVisibility(View.GONE);

                newslist.clear();

                String enteredText = null;
                try {
                    enteredText = URLEncoder.encode(searchString.getText().toString(), "utf-8");
                    String url = "http://content.guardianapis.com/search?q=" + enteredText + "&api-key=test&show-tags=contributor";//https://www.googleapis.com/books/v1/volumes?q=" + enteredText + "&maxResults=10";

                    HttpAsyncTaskLoader loader = new HttpAsyncTaskLoader(MainActivity.this, url);
                    loader.startLoading();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        //savedInstanceState.putParcelableArrayList("newsInfo", (ArrayList<? extends Parcelable>) newslist);
       // ArrayList<newsInfo> news = new ArrayList<newsInfo>();
        savedInstanceState.putSerializable("Old", newslist);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
       newslist = (ArrayList<newsInfo>) savedInstanceState.get("Old");
    }

    public void showData(ArrayList<newsInfo> newsInfo)
    {
        newsAdapter = new newsInfoAdapter(MainActivity.this, newsInfo);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(newsAdapter);
        msg.setVisibility(View.GONE);
        newsAdapter.notifyDataSetChanged();
    }

    public String GET(String url){
        InputStream inputStream = null;
        String result = "";
        StringBuilder total = null;
        HttpURLConnection urlConnection = null;

        try
        {
            URL urlVar = new URL(url);
            urlConnection =(HttpURLConnection) urlVar.openConnection();
            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(20000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_ACCEPTED) {
            }

            InputStream in = new BufferedInputStream(
                    urlConnection.getInputStream());

            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }

            Log.d("InputStream", in.toString());
        }
         catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return total.toString();
    }

    public class HttpAsyncTaskLoader extends AsyncTaskLoader<ArrayList<newsInfo>>
    {
        private String murl;

        String contributorName = "";

        public HttpAsyncTaskLoader(Context context, String url) {
            super(context);
            murl = url;
           // onContentChanged();
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public ArrayList<newsInfo> loadInBackground() {
            if (murl == null) {
                return null;
            }

            // Perform the network request, parse the response, and extract a list of earthquakes.
            String result = GET(murl);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                JSONArray results = jsonObject.getJSONObject("response").getJSONArray("results");
                for(int i=0; i< results.length();i++)
                {
                    JSONObject news = results.getJSONObject(i);
                    String title = news.get("webTitle").toString();
                    String type = news.get("type").toString();
                    String webUrl = news.get("webUrl").toString();

                    if(type.equals("contributor"))
                    {
                        contributorName = news.get("firstName")+" "+news.get("lastName");
                    }
                    newslist.add(new newsInfo(title,type,webUrl,contributorName));
                }
                //showData(newslist);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return newslist;
        }

        @Override
        public void deliverResult(ArrayList<newsInfo> data) {
            super.deliverResult(data);
            progress.setVisibility(View.GONE);
            progress.setProgress(0);
            msg.setVisibility(View.GONE);
            showData(newslist);
        }

        @Override
        protected void onReset() {
            super.onReset();
            showData(newslist);
        }

    }
}