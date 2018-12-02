package com.example.peterboncheff.coursework;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.peterboncheff.coursework.MainActivity.BEGINNING_API_HTTP;
import static com.example.peterboncheff.coursework.MainActivity.END_OF_API_HTTP;

public class LocationsView extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private ForecastRecycleViewAdapter adapter;

    // progress bar spinner to indicate working in the background
    private ProgressBar progressBar;

    // TextView to display any error message
    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_view);

//        progressBar = findViewById(R.id.progressBar);
//        tvErrorMessage = findViewById(R.id.tv_errorMessage);
//        Button btnDownload = findViewById(R.id.btn_download);
//        btnDownload.setOnClickListener(this);

        // an initial, empty data set
        List<ForecastLocation> forecastLocations = new ArrayList<ForecastLocation>();

        // get a handle to the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // create an adapter with the mock data
        adapter = new ForecastRecycleViewAdapter(this, forecastLocations);

        // connect the adapter with the recycler view
        recyclerView.setAdapter(adapter);

        // give the recycler view a layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        HttpDownloaderTask task = new HttpDownloaderTask();
        URL downloadUrl = null;
        try {
            final String TEST_CITY = "London";
            downloadUrl = new URL(Uri.parse(BEGINNING_API_HTTP + TEST_CITY + END_OF_API_HTTP).toString());
            task.execute(downloadUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    class HttpDownloaderTask extends AsyncTask<URL, Void, List<ForecastLocation>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display the progress bar and hide the recycler view and error message
            recyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected List<ForecastLocation> doInBackground(URL... urls) {
            String forecastLocationsJson = null;
            URL downloadUrl = urls[0];
            forecastLocationsJson = new HttpConnection(downloadUrl.toString()).connectAndParseJson();
            // display an error message
            //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();


            List<ForecastLocation> forecastLocations = null;
            if (forecastLocationsJson != null) {
                forecastLocations = ForecastLocationsJsonParser.processJson(forecastLocationsJson);
            }
            return forecastLocations;
        }

        @Override
        protected void onPostExecute(List<ForecastLocation> forecastLocations) {
            super.onPostExecute(forecastLocations);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setData(forecastLocations);
//
//            if (forecastLocations != null) {
//                if (forecastLocations.size() == 0) {
//                    Toast.makeText(getApplicationContext(), "No locations", Toast.LENGTH_SHORT).show();
//                } else {
//                    recyclerView.setVisibility(View.VISIBLE);
//                    adapter.setData(forecastLocations);
//                }
//            }
        }
    }

}
