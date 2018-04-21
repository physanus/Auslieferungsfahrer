package de.danielprinz.Auslieferungsfahrer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GoogleAPI {

    public static final String API_KEY = "AIzaSyC-uWBs6zByu5WvBUe5PrF8BfQA59z_5aI";
    public static final String API_KEY_GEOCODING = "AIzaSyDvfT13x4r95aAEDWbSKmisylcsCqVw6Xs";

    /**
     * Returns altitude and distance data
     * @param waypoints A list of addresses
     */
    public static void analyze(ArrayList<String> waypoints) throws IOException {

        // convert addresses to lat|lang
        ArrayList<AddressContainer> addressContainers = getLatLang(waypoints);
        if(Main.DEBUG) for(AddressContainer addressContainer : addressContainers) System.out.println(addressContainer);


    }


    /**
     * Converts addresses into lat|lng pairs
     * @param waypoints A list of addresses
     * @return The list of addresses containing lat|lng pairs
     * @throws IOException
     */
    private static ArrayList<AddressContainer> getLatLang(ArrayList<String> waypoints) throws IOException {
        // convert address into lat|long using Google's Geocoding API
        String requestTemplate = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s";

        ArrayList<AddressContainer> addressContainers = new ArrayList<>();
        for(String waypoint : waypoints) {
            String waypointEncoded = URLEncoder.encode(waypoint, "UTF-8");
            String request = String.format(requestTemplate, waypointEncoded, API_KEY_GEOCODING);

            JsonObject root = getURLContent(new URL(request));
            JsonObject latlng = root.get("results").getAsJsonArray().get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject();

            double lat = latlng.get("lat").getAsDouble();
            double lng = latlng.get("lng").getAsDouble();

            addressContainers.add(new AddressContainer(waypoint, lat, lng));
        }

        return addressContainers;
    }




    private static JsonObject getURLContent(URL url) throws IOException {
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        return root.getAsJsonObject();
    }
}
