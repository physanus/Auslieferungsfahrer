package de.danielprinz.Auslieferungsfahrer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import de.danielprinz.Auslieferungsfahrer.containers.AddressContainer;
import de.danielprinz.Auslieferungsfahrer.containers.BufferContainer;
import de.danielprinz.Auslieferungsfahrer.containers.RelationContainer;
import de.danielprinz.Auslieferungsfahrer.containers.RouteContainer;
import de.danielprinz.Auslieferungsfahrer.handlers.DistanceHandler;
import de.danielprinz.Auslieferungsfahrer.handlers.DurationHandler;
import de.danielprinz.Auslieferungsfahrer.handlers.EnergyHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleAPI {

    private static final String API_KEY_GEOCODING = "AIzaSyDvfT13x4r95aAEDWbSKmisylcsCqVw6Xs";
    private static final String API_KEY_ELEVATION = "AIzaSyDLP9QNf7wJjcQ3kW03IFEuFywEJ9KFDmM";
    private static final String API_KEY_DISTANCE_MATRIX = "AIzaSyC-uWBs6zByu5WvBUe5PrF8BfQA59z_5aI";

    /**
     * Returns altitude and distance data
     * @param waypoints A list of addresses
     */
    public static void analyze(ArrayList<String> waypoints) throws IOException {

        // convert addresses to lat|lang
        ArrayList<AddressContainer> addressContainers = getLatLang(waypoints);
        if(Main.DEBUG) System.out.println("LatLang:");
        if(Main.DEBUG) for(AddressContainer addressContainer : addressContainers) System.out.println(addressContainer);
        if(Main.DEBUG) System.out.println("total: " + addressContainers.size());
        if(Main.DEBUG) System.out.println("");

        // get elevation
        addressContainers = getElevation(addressContainers);
        if(Main.DEBUG) System.out.println("Elevation:");
        if(Main.DEBUG) for(AddressContainer addressContainer : addressContainers) System.out.println(addressContainer);
        if(Main.DEBUG) System.out.println("total: " + addressContainers.size());
        if(Main.DEBUG) System.out.println("");

        // get all possible relations
        ArrayList<RelationContainer> relationContainers = getRelations(addressContainers);
        if(Main.DEBUG) System.out.println("Relations:");
        if(Main.DEBUG) for(RelationContainer relationContainer : relationContainers) System.out.println(relationContainer);
        if(Main.DEBUG) System.out.println("total: " + relationContainers.size());
        if(Main.DEBUG) System.out.println("");

        ArrayList<RouteContainer> cheapestRoutes = getCheapestRoutes(addressContainers, relationContainers);
        if(Main.DEBUG) System.out.println("Cheapest route(s):");
        if(Main.DEBUG) for(RouteContainer routeContainer : cheapestRoutes) System.out.println(routeContainer);
        if(Main.DEBUG) System.out.println("total: " + cheapestRoutes.size());
        if(Main.DEBUG) System.out.println("");

        RouteContainer routeContainer = cheapestRoutes.get(0);
        System.out.println("Duration : " + new DurationHandler(routeContainer.getDuration()));
        System.out.println("Distance : " + new DistanceHandler(routeContainer.getDistance()));
        System.out.println("Energy   : " + new EnergyHandler(routeContainer.getCost()));
        // Output: time, distance, energy

    }


    /**
     * Converts addresses into lat|lng pairs
     * @param waypoints A list of addresses
     * @return The list of addresses containing lat|lng pairs
     * @throws IOException On GoogleAPI error
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
     * @throws IOException On GoogleAPI error
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
     * @throws IOException On GoogleAPI error
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

    /**
     * Calculates the cheapest routes
     * @param addressContainers The addresses
     * @param relationContainers The relations
     * @return A list of the cheapest routes. Usually contains one element; if more, these are as cheap routes as the first one (alternative routes)
     */
    private static ArrayList<RouteContainer> getCheapestRoutes(ArrayList<AddressContainer> addressContainers, ArrayList<RelationContainer> relationContainers) {

        // 0 | 1 | 2 | 3
        // 01 | 02 | 03 | 12 | 13 | 23

        // startpoint is the point where the final route should start at
        AddressContainer startpoint = addressContainers.get(0);


        ArrayList<RouteContainer> routeContainers = new ArrayList<>(); // stores the final routes
        for(AddressContainer startpointCurrent : addressContainers) {

            //HashMap<RouteContainer, AddressContainer> buffer = new HashMap<>();
            ArrayList<BufferContainer> buffer = new ArrayList<>();// in case we get equal relation-costs, put them into the buffer and calculate them later // routeContainer, startpointCurrent

            RouteContainer routeContainer = new RouteContainer(relationContainers, startpointCurrent); // stores the route currently being worked on
            while(true) { // as long as there are not-visited-places left
                RouteContainer finalRouteContainer = routeContainer;
                List<AddressContainer> acs = addressContainers.stream().filter(ac -> !finalRouteContainer.contains(ac)).collect(Collectors.toList());
                if(acs.isEmpty()) {
                    // current route is done, save it
                    routeContainers.add(routeContainer);
                    if(buffer.isEmpty()) {
                        // nothing in the buffer, continue
                        break;
                    } else {
                        // we need to calculate something, load it
                        BufferContainer bufferContainer = buffer.get(0);
                        buffer.remove(0);
                        routeContainer = bufferContainer.getRouteContainer();
                        startpointCurrent = bufferContainer.getStartpointCurrent();
                        continue;
                    }
                }

                // get costs of all possible relations (to not-visited-places)
                HashMap<AddressContainer, Double> costs = new HashMap<>();
                for(AddressContainer possibleRelationDestination : acs) {
                    RelationContainer possibleRelationContainer = MethodProvider.getRelationByAddresses(relationContainers, startpointCurrent, possibleRelationDestination);
                    costs.put(possibleRelationDestination, possibleRelationContainer.getCost(startpointCurrent));
                }
                List<AddressContainer> cheapestAddresses = MethodProvider.getCheapestAddresses(costs);
                if(cheapestAddresses.size() > 1) {
                    // multiple relations have the same cost
                    // TODO not yet tested, I couldn't find any locations having the same cost. Pretty unlikely anyways.
                    // In case something crashes here, don't blame me but the earth for having too many odd numbers.
                    System.out.println("======================================================================================");
                    System.out.println("= Untested state. Please send the locations entered to plan the route to the author: =");
                    System.out.println("================== https://github.com/physanus/Auslieferungsfahrer ===================");
                    System.out.println("===================================== Thank you! =====================================");
                    System.out.println("======================================================================================");
                    for(int i = 1; i < cheapestAddresses.size(); i++) {
                        buffer.add(new BufferContainer(routeContainer.copy(), cheapestAddresses.get(i)));
                    }
                }

                routeContainer.addElement(relationContainers, cheapestAddresses.get(0), costs.get(cheapestAddresses.get(0)));
                startpointCurrent = cheapestAddresses.get(0);
            }
        }

        for(RouteContainer routeContainer : routeContainers) {
            routeContainer.shift(startpoint);
            routeContainer.finish(relationContainers);
        }

        // lets find the route with the lowest cost
        ArrayList<RouteContainer> cheapestRoutes = MethodProvider.getCheapestRoutes(routeContainers);

        return cheapestRoutes;
    }

}
