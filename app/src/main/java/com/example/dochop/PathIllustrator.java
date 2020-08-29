package com.example.dochop;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Represents the PathIllustrator class used to draw the path linking coordinates extracted from
 * JSON data.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class PathIllustrator extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

    /**
     * Fragment to display the Google Map.
     */
    private GoogleMap googleMap;

    /**
     * Constructor for PathIllustrator.
     *
     * @param googleMap Fragment to display the Google Map.
     */
    PathIllustrator(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    /**
     * Passes the JSON data retrieved from the Google Directions API request for parsing.
     *
     * @param jsonData JSON data retrieved from the Google Directions API request.
     * @return A list of hash map containing the latitudes and longitudes of all steps
     * along the provided path.
     */
    @Override
    protected List<HashMap<String, String>> doInBackground(String... jsonData) {
        JSONObject jObject = null;
        List<HashMap<String, String>> path;

        try {
            jObject = new JSONObject(jsonData[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PathParser pathParser = new PathParser();

        assert jObject != null;
        path = pathParser.parse(jObject);

        return path;
    }

    /**
     * Draws the route by linking the coordinates extracted from the JSON data.
     *
     * @param result A list of hash map containing the latitudes and longitudes of all steps
     *               along the provided path.
     */
    @Override
    protected void onPostExecute(List<HashMap<String, String>> result) {
        ArrayList<LatLng> points = new ArrayList<>();
        PolylineOptions lineOptions = new PolylineOptions();

        for (int i = 0; i < result.size(); i++) {
            HashMap<String, String> point = result.get(i);

            double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
            double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
            LatLng position = new LatLng(lat, lng);

            points.add(position);
        }

        lineOptions.addAll(points);
        lineOptions.width(10);
        lineOptions.color(Color.BLUE);

        googleMap.addPolyline(lineOptions);
    }
}
