package com.example.dochop;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Represents the CarparkAPI class used to identify the current lots available of the carparks.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class CarparkAPI extends AsyncTask<Void, Void, ArrayList<Carpark>> {

    /**
     * The string used to read each line passed from the CarparkAPI.
     */
    private String data = "";

    /**
     * The ArrayList of Carparks containing Carpark coordinates and their carpark number.
     */
    private ArrayList<Carpark> carparkRef = new ArrayList<>();

    /**
     * The ArrayList of Carparks containing the lots available, the carpark number and the carpark
     * coordinates.
     */
    private ArrayList<Carpark> carparkList = new ArrayList<>();

    /**
     * Passes data retrieved from the CarparkAPI and parses it.
     * The details of all the carparks and their coordinates are stored into carparkRef ArrayList.
     * The carparks with lots available have their coordinates obtained from carparkRef and are
     * added to the ArrayList carparkList.
     * The final ArrayList carparkList is then returned.
     *
     * @param voids This represents the voids
     * @return returns the carparkList containing the details of the carparks which have lots
     * available.
     */
    @Override
    protected ArrayList<Carpark> doInBackground(Void... voids) {
        try {
            URL url = new URL("https://api.data.gov.sg/v1/transport/carpark-availability");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            BufferedReader reader = new BufferedReader(new InputStreamReader(GpHomeUI.csvStream));
            try {
                String csvLine;
                while ((csvLine = reader.readLine()) != null) {
                    String[] ids = csvLine.split(",");
                    Carpark carpark = new Carpark();
                    carpark.setCarparkNumber(ids[0].replaceAll("\"", ""));
                    carpark.setLatitude(ids[5].replaceAll("\"", ""));
                    carpark.setLongitude(ids[6].replaceAll("\"", ""));
                    carparkRef.add(carpark);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            while ((line = bufferedReader.readLine()) != null) {
                data = data + line;
            }

            JSONObject JO1 = new JSONObject(data);
            JSONObject JO2 = (JSONObject) JO1.getJSONArray("items").get(0);
            JSONArray JA = JO2.getJSONArray("carpark_data");
            for (int j = 0; j < JA.length(); j++) {
                int a = -1;
                JSONObject JO3 = (JSONObject) JA.get(j);
                JSONArray JA1 = JO3.getJSONArray("carpark_info");
                for (int i = 0; i < JA1.length(); i++) {
                    JSONObject temp = JA1.getJSONObject(i);
                    if (temp.get("lot_type").equals("C")) {
                        if (temp.get("lots_available").equals("0")) {
                            break;
                        }
                        a = i;
                        break;
                    }
                }
                if (a >= 0) {
                    JSONObject JO4 = (JSONObject) JA1.get(a);
                    for (int i = 0; i < carparkRef.size(); i++) {
                        if (carparkRef.get(i).getCarparkNumber().equals(JO3.get("carpark_number").toString())) {
                            carparkRef.get(i).setLotsAvailable(JO4.get("lots_available").toString());
                            carparkList.add(carparkRef.get(i));
                            break;
                        }
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return carparkList;
    }

    /**
     * Executes after the background activity is performed.
     *
     * @param arrayList This represents the arrayList.
     */
    @Override
    protected void onPostExecute(ArrayList<Carpark> arrayList) {
        super.onPostExecute(arrayList);
    }
}
