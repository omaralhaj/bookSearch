
package com.example.android.booksearch;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.N;
import static com.example.android.booksearch.MainActivity.KEY;
import static com.example.android.booksearch.R.layout.books;

/**
 * Created by Omar on 8/25/17.
 */

public class BookList extends AppCompatActivity {


    private static int requestState = 0;
    private final String REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";
    String term1;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(books);
        BookAsyncTask b = new BookAsyncTask();
        b.execute();


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey(KEY)) {
                term1 = extras.getString(KEY);
            }
        } else {


            term1 = (String) savedInstanceState.getSerializable("");
        }


        if (isNetworkAvailable(this) == false) {
            ListView listView = (ListView) findViewById(R.id.mainbooks);
            listView.setVisibility(View.GONE);
            TextView textView = (TextView) findViewById(R.id.empty_list_view);
            textView.setVisibility(View.VISIBLE);
            textView.setText("Your Device is not connected to the internet");

        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("", term1);
    }

    private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {


        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            ArrayList<Book> books = new ArrayList<Book>();
            URL url = createUrl(REQUEST_URL + term1);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
                JSONObject baseJsonResponse = new JSONObject(jsonResponse);
                JSONArray featureArray = baseJsonResponse.getJSONArray("items");


                for (int i = 0; i < featureArray.length(); i++) {
                    JSONObject object = featureArray.getJSONObject(i);
                    JSONArray authorsArray = null;
                    JSONObject volumeInfo = object.getJSONObject("volumeInfo");

                    if (volumeInfo.has("authors") && volumeInfo.getJSONArray("authors").length() > 0) {
                        authorsArray = volumeInfo.getJSONArray("authors");
                        String[] authors = new String[authorsArray.length()];
                        for (int j = 0; j < authorsArray.length(); j++) {
                            authors[j] = authorsArray.getString(j);
                        }
                        if (volumeInfo.getString("title") != null) {
                            String title = volumeInfo.getString("title");
                            books.add(new Book(title, authors));
                        }


                    }


                }


            } catch (JSONException | IOException j) {
                j.printStackTrace();

            }


            return books;
        }


        protected void onPostExecute(ArrayList<Book> b) {
            if (b.isEmpty()) {

                ListView listView = (ListView) findViewById(R.id.mainbooks);
                listView.setVisibility(View.GONE);
                TextView textView = (TextView) findViewById(R.id.empty_list_view);
                textView.setVisibility(View.VISIBLE);
                if (isNetworkAvailable(BookList.this) == false) {
                    textView.setText("Your Device is not connected to the internet");
                    return;
                } else textView.setText("The list is empty, Try searching for a different term");
                return;
            }


            BooksAdapter booksAdapter = new BooksAdapter(BookList.this, b);
            ListView listView = (ListView) findViewById(R.id.mainbooks);
            listView.setAdapter(booksAdapter);


        }


        private URL createUrl(String stringUrl) {
            URL url = null;

            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                exception.printStackTrace();
                return null;


            }
            return url;
        }


        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                requestState = urlConnection.getResponseCode();

                if (requestState > 400 && requestState < 500) {
                    ListView listView = (ListView) findViewById(R.id.mainbooks);
                    listView.setVisibility(View.GONE);
                    TextView textView = (TextView) findViewById(R.id.empty_list_view);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Error: " + requestState + " (Bad Client");
                    return "";
                } else if (requestState > 500) {
                    ListView listView = (ListView) findViewById(R.id.mainbooks);
                    listView.setVisibility(View.GONE);
                    TextView textView = (TextView) findViewById(R.id.empty_list_view);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Error: " + requestState + " (Bad Server");
                    return "";

                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }


        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }


    }


}