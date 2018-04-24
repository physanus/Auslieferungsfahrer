package de.danielprinz.Auslieferungsfahrer;

import com.google.gson.*;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleAPI {

    public static final String API_KEY_GEOCODING = "AIzaSyDvfT13x4r95aAEDWbSKmisylcsCqVw6Xs";
    public static final String API_KEY_ELEVATION = "AIzaSyDLP9QNf7wJjcQ3kW03IFEuFywEJ9KFDmM";
    public static final String API_KEY_DISTANCE_MATRIX = "AIzaSyC-uWBs6zByu5WvBUe5PrF8BfQA59z_5aI";

    /**
     * Returns altitude and distance data
     * @param waypoints A list of addresses
     */
    public static void analyze(ArrayList<String> waypoints) throws IOException {

        // convert addresses to lat|lang
        ArrayList<AddressContainer> addressContainers = getLatLang(waypoints);
        if(Main.DEBUG) for(AddressContainer addressContainer : addressContainers) System.out.println(addressContainer);
        if(Main.DEBUG) System.out.println("");

        // get elevation
        addressContainers = getElevation(addressContainers);
        if(Main.DEBUG) for(AddressContainer addressContainer : addressContainers) System.out.println(addressContainer);
        if(Main.DEBUG) System.out.println("");

        // get all possible relations
        ArrayList<RelationContainer> relationContainers = getRelations(addressContainers);
        if(Main.DEBUG) for(RelationContainer relationContainer : relationContainers) System.out.println(relationContainer);
        if(Main.DEBUG) System.out.println("total: " + relationContainers.size());
        if(Main.DEBUG) System.out.println("");

        getRoutes(addressContainers, relationContainers);

    }


    /**
     * Converts addresses into lat|lng pairs
     * @param waypoints A list of addresses
     * @return The list of addresses containing lat|lng pairs
     * @throws IOException
     */
    private static ArrayList<AddressContainer> getLatLang(ArrayList<String> waypoints) throws IOException {

        ArrayList<AddressContainer> addressContainers = new ArrayList<>();
        GeoApiContext context = new GeoApiContext.Builder().apiKey(API_KEY_GEOCODING).build();

        for(String waypoint : waypoints) {
            GeocodingResult[] results = new GeocodingResult[0];
            try {
                results = GeocodingApi.geocode(context, waypoint).await();
            } catch (ApiException | InterruptedException e) {
                e.printStackTrace();
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            LatLng latLng = results[0].geometry.location;

            addressContainers.add(new AddressContainer(waypoint, latLng));
        }

        return addressContainers;
    }

    /**
     * Retrieves the elevation (= height) of the places
     * @param addressContainers The addresses
     * @return The addresses
     * @throws IOException
     */
    private static ArrayList<AddressContainer> getElevation(ArrayList<AddressContainer> addressContainers) throws IOException {

        GeoApiContext context = new GeoApiContext.Builder().apiKey(API_KEY_ELEVATION).build();

        for(AddressContainer addressContainer : addressContainers) {

            ElevationResult result = new ElevationResult();
            try {
                result = ElevationApi.getByPoint(context, addressContainer.getLatLng()).await();
            } catch (ApiException | InterruptedException e) {
                e.printStackTrace();
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            double elevation = result.elevation;

            addressContainer.setElevation(elevation);
        }

        return addressContainers;
    }

    /**
     * Finds all relations between the addresses
     * @param addressContainers The addresses
     * @return The relations
     * @throws IOException
     */
    private static ArrayList<RelationContainer> getRelations(ArrayList<AddressContainer> addressContainers) throws IOException {
        // 0 | 1 | 2 | 3

        ArrayList<RelationContainer> relationContainers = new ArrayList<>();
        GeoApiContext context = new GeoApiContext.Builder().apiKey(API_KEY_DISTANCE_MATRIX).build();

        for(int i = 0; i < addressContainers.size(); i++) {
            for(int j = i+1; j < addressContainers.size(); j++) {
                AddressContainer addressContainer1 = addressContainers.get(i);
                AddressContainer addressContainer2 = addressContainers.get(j);

                DistanceMatrix result = null;
                try {
                    result = DistanceMatrixApi.newRequest(context).origins(addressContainer1.getLatLng()).destinations(addressContainer2.getLatLng()).await();
                } catch (ApiException | InterruptedException e) {
                    e.printStackTrace();
                }

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                DistanceMatrixElement distanceMatrixElement = result.rows[0].elements[0];

                relationContainers.add(new RelationContainer(addressContainers.get(i), addressContainers.get(j), distanceMatrixElement.duration, distanceMatrixElement.distance));
            }
        }

        return relationContainers;
    }

    private static ArrayList<List<RelationContainer>> getRoutes(ArrayList<AddressContainer> addressContainers, ArrayList<RelationContainer> relationContainers) {

        // 0 | 1 | 2 | 3
        // 01 | 02 | 03 | 12 | 13 | 23

        // startpoint is the point where the final route should start at
        // addressContainer is only used to check the cost

        AddressContainer startpoint = addressContainers.get(0);


        ArrayList<RouteContainer> routeContainers = new ArrayList<>();
        for(AddressContainer startpointCurrent : addressContainers) {
            RouteContainer routeContainer = new RouteContainer(startpointCurrent);
            while(true) {
                List<AddressContainer> acs = addressContainers.stream().filter(ac -> !routeContainer.contains(ac)).collect(Collectors.toList());
                if(acs.isEmpty()) break;

                // get costs
                HashMap<AddressContainer, Double> costs = new HashMap<>();
                for(AddressContainer possibleRelationDestination : acs) {
                    RelationContainer possibleRelationContainer = MethodProvider.getRelationByAddresses(relationContainers, startpointCurrent, possibleRelationDestination);
                    costs.put(possibleRelationDestination, possibleRelationContainer.getCost(startpointCurrent));
                }
                List<AddressContainer> smallestValues = MethodProvider.getCheapestAddress(costs);
                // TODO handle multiple results. For now we will discard them
                routeContainer.addElement(smallestValues.get(0), costs.get(smallestValues.get(0)));
                startpointCurrent = smallestValues.get(0);
            }
            //routeContainer.finish(relationContainers);
            routeContainers.add(routeContainer);
            System.out.println(routeContainer);
        }
        System.out.println("");

        // lets find the route with the lowest cost
        // TODO check for other routes as cheap as this one and suggest them as alternative routes
        RouteContainer cheapest = MethodProvider.getCheapestRoute(routeContainers);

        // lets order it correctly since we wanted to
        cheapest.shift(startpoint);
        cheapest.finish(relationContainers);

        System.out.println(cheapest);






        return null;

    }


    private static JsonObject getURLContent(URL url) throws IOException {
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        return root.getAsJsonObject();
    }
}
