package com.adammcneilly.helloworld.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.adammcneilly.helloworld.data.HWContract;
import com.adammcneilly.helloworld.datatransferobjects.HWLocation;

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
import java.util.List;

/**
 * A service that pulls Hello World locations and inserts them into the database.
 *
 * Created by adammcneilly on 2/22/16.
 */
public class LocationService extends IntentService {
    /**
     * Log tag in case there are any issues running the service.
     */
    private static final String TAG = LocationService.class.getSimpleName();

    /**
     * The URL of all Hello World locations.
     */
    private static final String LOCATION_URL = "https://www.helloworld.com/helloworld_locations.json";

    /**
     * Constant string for a GET request.
     */
    private static final String GET = "GET";

    /**
     * Key for the locations array in the JSON object.
     */
    private static final String LOCATION_ARRAY = "locations";

    public LocationService() {
        super("Location Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "OnHandleIntent Called");

        // A URL connection and a reader for that connection.
        // These exist outside of try so they can be accessed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL locationUrl = new URL(LOCATION_URL);

            // Connect to URL and "GET" the locations.
            urlConnection = (HttpURLConnection) locationUrl.openConnection();
            urlConnection.setRequestMethod(GET);
            urlConnection.connect();

            // Read the locations stream.
            InputStream inputStream = urlConnection.getInputStream();
            if(inputStream == null) {
                // We don't have a stream, just return.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Read input and add to a builder
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            // If buffer was empty, just return and don't format it.
            if(builder.length() == 0) {
                return;
            }

            // Format and insert into database
            insertLocations(builder.toString());
        } catch(IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            // Disconnect if we can
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch(IOException ioe) {
                    Log.e(TAG, ioe.getMessage());
                }
            }
        }
    }

    /**
     * Inserts the locations in the given json string into the database.
     * @param locationJsonString A JSON string containing an array of each hello world location to
     *                           be inserted.
     */
    private void insertLocations(String locationJsonString) {
        try {
            // Get array
            JSONObject locationJson = new JSONObject(locationJsonString);
            JSONArray locationArray = locationJson.getJSONArray(LOCATION_ARRAY);

            // The current JSON string has a comma after the last element that shouldn't be there.
            // As such, the network request assumes we have one more element than we actually do.
            // To account for this, we create a List of content values, and convert it to an array
            // when calling bulk insert.
            List<ContentValues> locationContentValues = new ArrayList<>();

            for(int i = 0; i < locationArray.length(); i++) {
                // If we got null here, just skip it.
                // Not a permanent solution, but necessary to make the Hello World app work.
                if(locationArray.isNull(i)) {
                    continue;
                }

                // Get current location
                HWLocation hwLocation = new HWLocation(locationArray.getJSONObject(i));
                locationContentValues.add(hwLocation.getContentValues());
            }

            ContentValues[] values = new ContentValues[locationContentValues.size()];
            getContentResolver().bulkInsert(HWContract.LocationEntry.CONTENT_URI, locationContentValues.toArray(values));
            // Print Json
            //TODO: Remove
            Log.v(TAG, locationJsonString);
        } catch(JSONException je) {
            Log.e(TAG, je.getMessage());
        }
    }
}
