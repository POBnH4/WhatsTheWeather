package com.example.peterboncheff.coursework;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.peterboncheff.coursework.MainActivity.API_FORECAST_LIST;

public class ForecastLocationsJsonParser {

    public static List<ForecastLocation> processJson(String json){
        List<ForecastLocation> locations = new ArrayList<ForecastLocation>();
        locations.add(new ForecastLocation("Hallo 1"));
        locations.add(new ForecastLocation("Hallo 2"));
        locations.add(new ForecastLocation("Hallo 3"));
        locations.add(new ForecastLocation("Hallo 4"));
        locations.add(new ForecastLocation("Hallo 5"));
        locations.add(new ForecastLocation("Hallo 6"));
        try {
            // Create the top level, root JSON Object from the json String.
            JSONObject locationsObj = new JSONObject(json);
            JSONArray locationObj = locationsObj.getJSONArray(API_FORECAST_LIST);
            // Get the array of locations that is the value of the locationObj's "Location" key
            JSONArray locationArray = locationObj.getJSONArray(0);
            // Loop through each JSON object in the locationArray, i.e. each site that a forecast is available for
            for (int i = 0; i < locationArray.length(); i++){
                // for each JSON Object in locationsArray
                // create a new ForecastLocation object for this JSON Object
                //ForecastLocation forecastLocation = new ForecastLocation();
                // add forecastLocation to the List that will be returned by this method
                //locations.add(forecastLocation);

                // Get the JSON Object at position i of the locationArray
                JSONObject location = locationArray.getJSONObject(i);


                // for this location, gee the String value of the "id" key, which defines the ID of the location.
                //String id = location.getString(KEY_ID);
                //forecastLocation.setId(id);

                // for this location, gee the String value of the "name" key, which defines the ID of the location.
                //String name = location.getString(KEY_NAME);
                //forecastLocation.setName(name);

                // not every location has a key called "elevation", so check first
                //if (location.has(KEY_ELEVATION)) {
                    // this location has an key called "elevation", so get its double value
                //    double elevation = location.getDouble(KEY_ELEVATION);
                //    forecastLocation.setElevation(elevation);
                //}
                // get the double value of the location's "latitude" key
                //double latitude = location.getDouble(KEY_LATITUDE);
                //forecastLocation.setLatitude(latitude);

                // get the double value of teh location's "longitude" key
                //double longitude = location.getDouble(KEY_LONGITUDE);
                //forecastLocation.setLongitude(longitude);

                // not every location has a "region" key, so check
                //if (location.has(KEY_REGION)) {
                    // this location does, so get it
                //    String region = location.getString(KEY_REGION);
                //    forecastLocation.setRegion(region);
                }

                // not every location has a "unitaryAuthArea" key, so check
                //if (location.has(KEY_UNITARY_AUTH_AREA)) {
                //    String uAuthArea = location.getString(KEY_UNITARY_AUTH_AREA);
                //    forecastLocation.setUnitaryAuthArea(uAuthArea);
                //}
            //}
        } catch (JSONException e) {
            // there was an exception processing the json, possibly with the json String, so print the stack trace.
            e.printStackTrace();
        }

        return locations;
    }
}
