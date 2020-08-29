package com.example.dochop;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents the DirectionsAPI class used to retrieve various routes between specified points on
 * the map.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class DirectionsAPI extends AsyncTask<String, Void, String> {

    /**
     * The Google Map fragment used to display the route between various points.
     */
    private GoogleMap googleMap;

    /**
     * Creates a new directionsAPI object.
     *
     * @param googleMap This represents the Google Map fragment used to display the route.
     */
    DirectionsAPI(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    /**
     * Passes the Google Directions API request as a URL to obtain route data.
     * The details of all the routes are fetched from the Google Directions API, before being stored
     * and returned as the String data.
     *
     * @param url This represents the Google Directions API request to be queried, passed as a URL.
     * @return This represents the data of the multiple routes between the specified points on the
     * map.
     */
    @Override
    protected String doInBackground(String... url) {

        // For storing data from Google Directions API
        String data = "";

        // Fetching the data from Google Directions API
        try {
            data = downloadUrl(url[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Parses the data which is retrieved from the Google Directions API.
     * The final route will then be displayed on the provided Google Map fragment.
     *
     * @param result This represents the data of the multiple routes between specified points on the
     *               map.
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        PathIllustrator pathIllustrator = new PathIllustrator(googleMap);
        pathIllustrator.execute(result);
    }

    /**
     * Creates a HTTP connection to communicate with the Google Directions API request,
     * which is passed as a URL.
     * Data is read from the URL and returned as a String data.
     *
     * @param strUrl This represents the Google Directions API request to be queried, passed as a
     *               URL.
     * @return This represents the data of the multiple routes between the specified points on the
     * map.
     * @throws IOException This represents the IOException that may occur when data is being read
     *                     from the URL request.
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data;
        InputStream iStream;
        HttpURLConnection urlConnection;
        URL url = new URL(strUrl);

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        iStream = urlConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(iStream));

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        data = stringBuilder.toString();

        bufferedReader.close();
        iStream.close();
        urlConnection.disconnect();

        return data;
    }
}