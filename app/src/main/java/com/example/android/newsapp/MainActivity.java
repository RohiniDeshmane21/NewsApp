package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.content.pm.ActivityInfo;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<newsInfo>> {

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
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private String API_URL ="http://content.guardianapis.com/search?q=android&api-key=test&show-tags=contributor";
    @Override
    public Loader<ArrayList<newsInfo>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL

        return (new HttpAsyncTaskLoader(this,API_URL));

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<newsInfo>> loader, ArrayList<newsInfo> data) {
        // Clear the adapter of previous earthquake data
        newsAdapter.clear();
        newslist = data;
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            //newsAdapter.addAll(data);
            showData(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<newsInfo>> loader) {
        // Loader reset, so we can clear out our existing data.
       // newsAdapter.clear();
       showData(newslist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        btnSearch = (Button)findViewById(R.id.buttonSearch);
        msg = (TextView) findViewById(R.id.textViewMsg);

        progress = (ProgressBar)findViewById(R.id.loading_indicator);
        progress.setMax(10);
        progress.setVisibility(View.GONE);
        msg.setVisibility(View.VISIBLE);

        searchString = (EditText) findViewById(R.id.editTextBookName);

        setTitle("Book List");

        final String enteredText = "";

       /* btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                progress.setProgress(0);
                msg.setVisibility(View.GONE);


               /* if (!newslist.isEmpty()) {
                    newslist.clear();
                }
                try {
                    String enteredText = URLEncoder.encode(searchString.getText().toString(), "utf-8");

                    String url = "http://content.guardianapis.com/search?q=" + enteredText + "&api-key=test&show-tags=contributor";//https://www.googleapis.com/books/v1/volumes?q=" + enteredText + "&maxResults=10";

                    ConnectivityManager cm =
                            (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();

                    if (isConnected == true) {
                      //  new HttpAsyncTask().execute(url);
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
            /*}
        });*/
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

   /* private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }*/

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

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return newslist;
        }
    }
    /*private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        String contributorName = "";

        @Override
        protected String doInBackground(String... urls) {

            String result = GET(urls[0]);

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

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            showData(newslist);
            msg.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
            msg.setVisibility(View.GONE);
        }
    }*/
}