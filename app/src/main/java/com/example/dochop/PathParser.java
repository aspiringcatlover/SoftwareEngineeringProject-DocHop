package com.example.dochop;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents the PathParser class used to extract the path coordinates from retrieved JSON data.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
class PathParser {

    /**
     * The data retrieved from the Google Directions API request is passed and parsed.
     * It reduces the data into a single route, followed by obtaining the route's legs and its
     * corresponding steps.
     * The steps are then converted into longitudes and latitudes and added into the List path,
     * before returning the whole path.
     *
     * @param jObject This represents the data retrieved from the Google Directions API, which was
     *                parsed as a JSONObject.
     * @return A list of hash map containing the latitudes and longitudes of all steps
     * along the provided path.
     */
    List<HashMap<String, String>> parse(JSONObject jObject) {
        List<HashMap<String, String>> path = new ArrayList<>();

        try {
            JSONArray jRoutes = jObject.getJSONArray("routes");

            JSONArray jLegs = ((JSONObject) jRoutes.get(0)).getJSONArray("legs");

            for (int i = 0; i < jLegs.length(); i++) {
                JSONArray jSteps = ((JSONObject) jLegs.get(i)).getJSONArray("steps");

                for (int j = 0; j < jSteps.length(); j++) {
                    String polyline;
                    polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(j)).get("polyline")).get("points");
                    List<LatLng> stepsLatLng = PolyUtil.decode(polyline);

                    for (int l = 0; l < stepsLatLng.size(); l++) {
                        HashMap<String, String> hm = new HashMap<>();
                        hm.put("lat", Double.toString((stepsLatLng.get(l)).latitude));
                        hm.put("lng", Double.toString((stepsLatLng.get(l)).longitude));
                        path.add(hm);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return path;
    }
}
