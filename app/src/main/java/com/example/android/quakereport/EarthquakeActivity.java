/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {


    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    /**
     * URL to query the USGS dataset for earthquake information
     */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&updatedafter=2019-08-21&minmagnitude=3";

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        //Toast.makeText(this, "initial toast", Toast.LENGTH_SHORT).show();

//        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
//        task.execute();

        //Toast.makeText(this, "final toast", Toast.LENGTH_SHORT).show();

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);


    }

    EarthquakeAdapter adapter = null;


    public void updateUi(List<Earthquake> earthquakes) {

        if (earthquakes == null) {
            return;
        }
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes the list of earthquakes as input
        adapter = new EarthquakeAdapter(EarthquakeActivity.this, earthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Earthquake currentEarthquake = adapter.getItem(i);

                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {

        // Create a new loader for the given URL
        return new EarthquakeLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {

        updateUi(earthquakes);
        // Clear the adapter of previous earthquake data
        adapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            adapter.addAll(earthquakes);
        }

        updateUi(earthquakes);
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
    }



//    public class EarthquakeAsyncTask extends AsyncTask<URL, Void, ArrayList<Earthquake>> {
//
//        @Override
//        protected ArrayList<Earthquake> doInBackground(URL... urls) {
//
//            // Create URL object
//            URL url = createUrl(USGS_REQUEST_URL);
//
//            // Perform HTTP request to the URL and receive a JSON response back
//            String jsonResponse = "";
//            try {
//                jsonResponse = makeHttpRequest(url);
//            } catch (IOException e) {
//                // TODO Handle the IOException
//            }
//
//            // Extract relevant fields from the JSON response and create an {@link Event} object
//            ArrayList<Earthquake> earthquakes = QueryUtils.extractEarthquakes(jsonResponse);
//
//
//            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
//            return earthquakes;
//        }
//
//        /**
//         * Update the screen with the given earthquake (which was the result of the
//         * {@link EarthquakeAsyncTask}).
//         */
//        @Override
//        protected void onPostExecute(ArrayList<Earthquake> earthquake) {
//            if (earthquake == null) {
//                return;
//            }
//
//            updateUi(earthquake);
//        }
//
//        /**
//         * Returns new URL object from the given string URL.
//         */
//        private URL createUrl(String stringUrl) {
//
//            URL url = null;
//            try {
//                url = new URL(stringUrl);
//            } catch (MalformedURLException exception) {
//                return null;
//            }
//            return url;
//        }
//
//        /**
//         * Make an HTTP request to the given URL and return a String as the response.
//         */
//        private String makeHttpRequest(URL url) throws IOException {
//            String jsonResponse = "";
//
//            if (url == null) {
//                return jsonResponse;
//            }
//            HttpURLConnection urlConnection = null;
//            InputStream inputStream = null;
//            try {
//
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.setReadTimeout(10000 /* milliseconds */);
//                urlConnection.setConnectTimeout(15000 /* milliseconds */);
//                urlConnection.connect();
//
//                if (urlConnection.getResponseCode() == 200) {
//                    inputStream = urlConnection.getInputStream();
//                    jsonResponse = readFromStream(inputStream);
//                } else {
//                    Log.e("MakeHttpRequest", "error ");
//                }
//            } catch (IOException e) {
//                // TODO: Handle the exception
//                Log.e("makeHttpRequest catch ", "error ", e);
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (inputStream != null) {
//                    // function must handle java.io.IOException here
//                    inputStream.close();
//                }
//            }
//            return jsonResponse;
//        }
//
//        /**
//         * Convert the {@link InputStream} into a String which contains the
//         * whole JSON response from the server.
//         */
//        private String readFromStream(InputStream inputStream) throws IOException {
//            StringBuilder output = new StringBuilder();
//            if (inputStream != null) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
//                BufferedReader reader = new BufferedReader(inputStreamReader);
//                String line = reader.readLine();
//                while (line != null) {
//                    output.append(line);
//                    line = reader.readLine();
//                }
//            }
//            return output.toString();
//        }
//    }
}
