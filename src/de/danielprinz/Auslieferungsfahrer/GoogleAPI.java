package de.danielprinz.Auslieferungsfahrer;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.*;
import de.danielprinz.Auslieferungsfahrer.containers.AddressContainer;
import de.danielprinz.Auslieferungsfahrer.containers.BufferContainer;
import de.danielprinz.Auslieferungsfahrer.containers.RelationContainer;
import de.danielprinz.Auslieferungsfahrer.containers.RouteContainer;
import de.danielprinz.Auslieferungsfahrer.enums.Progress;
import de.danielprinz.Auslieferungsfahrer.gui.AlertBox;
import javafx.application.Platform;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GoogleAPI {

    private static final String API_KEY_GEOCODING = "AIzaSyDvfT13x4r95aAEDWbSKmisylcsCqVw6Xs";
    private static final String API_KEY_ELEVATION = "AIzaSyDLP9QNf7wJjcQ3kW03IFEuFywEJ9KFDmM";
    private static final String API_KEY_DISTANCE_MATRIX = "AIzaSyC-uWBs6zByu5WvBUe5PrF8BfQA59z_5aI";
    private static final String API_KEY_DIRECTIONS = "AIzaSyB5PhpvEPJh5kZdmXsT5cZyu1uzXznmUwA";
    private static final String API_KEY_STATIC_MAPS = "AIzaSyCh9Rudd3tEeAQng-_i38nCJiW5RWq6b6g";

    private static final GeoApiContext CONTEXT_GEOCODING = new GeoApiContext.Builder().apiKey(API_KEY_GEOCODING).build();
    private static final GeoApiContext CONTEXT_ELEVATION = new GeoApiContext.Builder().apiKey(API_KEY_ELEVATION).build();
    private static final GeoApiContext CONTEXT_DISTANCE_MATRIX = new GeoApiContext.Builder().apiKey(API_KEY_DISTANCE_MATRIX).build();
    private static final GeoApiContext CONTEXT_DISTANCE_DIRECTIONS = new GeoApiContext.Builder().apiKey(API_KEY_DIRECTIONS).build();
    private static final GeoApiContext CONTEXT_STATIC_MAPS = new GeoApiContext.Builder().apiKey(API_KEY_STATIC_MAPS).build();

    private static final String[] ABC = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    /**
     * Returns altitude and distance data
     * @param waypoints A list of addresses
     */
    public static ArrayList<RouteContainer> analyze(ArrayList<String> waypoints) throws IOException, ApiException, InterruptedException {

        // convert addresses to lat|lang
        Main.setProgress(Progress.LAT_LAN);
        ArrayList<AddressContainer> addressContainers = getLatLng(waypoints);
        if(Main.DEBUG) System.out.println("LatLang:");
        if(Main.DEBUG) for(AddressContainer addressContainer : addressContainers) System.out.println(addressContainer);
        if(Main.DEBUG) System.out.println("total: " + addressContainers.size());
        if(Main.DEBUG) System.out.println("");

        // get all possible relations
        Main.setProgress(Progress.RELATIONS);
        ArrayList<RelationContainer> relationContainers = getRelations(addressContainers);
        if(Main.DEBUG) System.out.println("Relations:");
        if(Main.DEBUG) for(RelationContainer relationContainer : relationContainers) System.out.println(relationContainer);
        if(Main.DEBUG) System.out.println("total: " + relationContainers.size());
        if(Main.DEBUG) System.out.println("");

        // get elevation
        Main.setProgress(Progress.ELEVATION);
        relationContainers = generateElevation(relationContainers);
        if(Main.DEBUG) System.out.println("Elevation:");
        if(Main.DEBUG) for(RelationContainer relationContainer : relationContainers) System.out.println(relationContainer);
        if(Main.DEBUG) System.out.println("total: " + relationContainers.size());
        if(Main.DEBUG) System.out.println("");

        Main.setProgress(Progress.CHEAPEST_ROUTES);
        ArrayList<RouteContainer> cheapestRoutes = getCheapestRoutes(addressContainers, relationContainers);
        if(Main.DEBUG) System.out.println("Cheapest route(s):");
        if(Main.DEBUG) for(RouteContainer routeContainer : cheapestRoutes) System.out.println(routeContainer);
        if(Main.DEBUG) System.out.println("total: " + cheapestRoutes.size());
        if(Main.DEBUG) System.out.println("");

        //RouteContainer routeContainer = cheapestRoutes.get(0);
        //if(Main.DEBUG) System.out.println("Duration : " + new DurationHandler(routeContainer.getDuration()));
        //if(Main.DEBUG) System.out.println("Distance : " + new DistanceHandler(routeContainer.getDistance()));
        //if(Main.DEBUG) System.out.println("Energy   : " + new EnergyHandler(routeContainer.getCost()));

        Main.setProgress(Progress.FINISHED);
        return cheapestRoutes;

    }


    /**
     * Converts addresses into lat|lng pairs
     * @param waypoints A list of addresses
     * @return The list of addresses containing lat|lng pairs
     * @throws IOException On GoogleAPI error
     * @throws InterruptedException On GoogleAPI error
     * @throws ApiException On GoogleAPI error
     */
    private static ArrayList<AddressContainer> getLatLng(ArrayList<String> waypoints) throws IOException, InterruptedException, ApiException {

        ArrayList<AddressContainer> addressContainers = new ArrayList<>();

        for(String waypoint : waypoints) {
            GeocodingResult[] results = GeocodingApi.geocode(CONTEXT_GEOCODING, waypoint).await();
            if(results.length == 0) {
                Platform.runLater(() -> {
                    AlertBox.display("Fehler: Adresse", "Die Adresse wurde nicht erkannt. Bitte überprüfen und erneut versuchen!\n" + waypoint);
                    Main.releaseUILocks();
                });
            }
            LatLng latLng = results[0].geometry.location;
            addressContainers.add(new AddressContainer(waypoint, latLng));
        }

        return addressContainers;
    }

    /**
     * Finds all relations between the addresses
     * @param addressContainers The addresses
     * @return The relations
     * @throws IOException On GoogleAPI error
     * @throws InterruptedException On GoogleAPI error
     * @throws ApiException On GoogleAPI error
     */
    private static ArrayList<RelationContainer> getRelations(ArrayList<AddressContainer> addressContainers) throws IOException, InterruptedException, ApiException {
        // 0 | 1 | 2 | 3

        ArrayList<RelationContainer> relationContainers = new ArrayList<>();


        for(int i = 0; i < addressContainers.size(); i++) {
            for(int j = i+1; j < addressContainers.size(); j++) {
                AddressContainer addressContainer1 = addressContainers.get(i);
                AddressContainer addressContainer2 = addressContainers.get(j);

                DistanceMatrix result = DistanceMatrixApi.newRequest(CONTEXT_DISTANCE_MATRIX).origins(addressContainer1.getLatLng()).destinations(addressContainer2.getLatLng()).await();
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
     * @throws InterruptedException On GoogleAPI error
     * @throws ApiException On GoogleAPI error
     * @throws IOException On GoogleAPI error
     */
    private static ArrayList<RouteContainer> getCheapestRoutes(ArrayList<AddressContainer> addressContainers, ArrayList<RelationContainer> relationContainers) throws InterruptedException, ApiException, IOException {

        // startpoint is the point where the final route should start at
        AddressContainer startpoint = addressContainers.get(0);


        ArrayList<RouteContainer> routeContainers = new ArrayList<>(); // stores the final routes
        for (AddressContainer startpointCurrent : addressContainers) {

            //HashMap<RouteContainer, AddressContainer> buffer = new HashMap<>();
            ArrayList<BufferContainer> buffer = new ArrayList<>();// in case we get equal relation-costs, put them into the buffer and calculate them later // routeContainer, startpointCurrent

            RouteContainer routeContainer = new RouteContainer(startpointCurrent); // stores the route currently being worked on
            while (true) { // as long as there are not-visited-places left
                RouteContainer finalRouteContainer = routeContainer;
                List<AddressContainer> acs = addressContainers.stream().filter(ac -> !finalRouteContainer.contains(ac)).collect(Collectors.toList());
                if (acs.isEmpty()) {
                    // current route is done, save it
                    routeContainers.add(routeContainer);
                    if (buffer.isEmpty()) {
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
                for (AddressContainer possibleRelationDestination : acs) {
                    RelationContainer possibleRelationContainer = MethodProvider.getRelationByAddresses(relationContainers, startpointCurrent, possibleRelationDestination);
                    costs.put(possibleRelationDestination, possibleRelationContainer.getCost(startpointCurrent));
                }
                List<AddressContainer> cheapestAddresses = MethodProvider.getCheapestAddresses(costs);
                if (cheapestAddresses.size() > 1) {
                    // multiple relations have the same cost
                    // TODO not yet tested, I couldn't find any locations having the same cost. Pretty unlikely anyways.
                    // In case something crashes here, don't blame me but the earth for having too many odd numbers.
                    System.out.println("======================================================================================");
                    System.out.println("= Untested state. Please send the locations entered to plan the route to the author: =");
                    System.out.println("================== https://github.com/physanus/Auslieferungsfahrer ===================");
                    System.out.println("===================================== Thank you! =====================================");
                    System.out.println("======================================================================================");
                    for (int i = 1; i < cheapestAddresses.size(); i++) {
                        buffer.add(new BufferContainer(routeContainer.copy(), cheapestAddresses.get(i)));
                    }
                }

                RelationContainer relationContainer = MethodProvider.getRelationByAddresses(relationContainers, startpointCurrent, cheapestAddresses.get(0));
                routeContainer.addElement(cheapestAddresses.get(0), relationContainer);
                startpointCurrent = cheapestAddresses.get(0);
            }
        }

        for (RouteContainer routeContainer : routeContainers) {
            routeContainer.shift(startpoint);
        }

        // remove other routes which are the same after they got shifted to equal
        Iterator iterator = routeContainers.iterator();
        while(iterator.hasNext()) {
            RouteContainer routeContainer = (RouteContainer) iterator.next();
            Iterator it = routeContainers.iterator();
            boolean found = false;
            while(it.hasNext()) {
                RouteContainer route = (RouteContainer) it.next();
                if(route.equals(routeContainer)) {
                    if(found) {
                        iterator.remove();
                        break;
                    }
                    found = true;
                }
            }
        }

        System.out.println("======================================");
        System.out.println("======================================");
        System.out.println("======================================");
        System.out.println("======================================");
        System.out.println("======================================");
        System.out.println("======================================");
        for(RouteContainer routeContainer : routeContainers) {
            routeContainer.finish(relationContainers);
        }

        // lets find the route with the lowest cost
        ArrayList<RouteContainer> cheapestRoutes = MethodProvider.getCheapestRoutes(routeContainers);
        for(RouteContainer routeContainer : cheapestRoutes) {
            routeContainer.setMap();
            System.out.println(Arrays.asList(routeContainer.getRelations().get(routeContainer.getRelations().size() - 1).getElevationResults()));
            System.out.println(routeContainer.getRelations().get(routeContainer.getRelations().size() - 1).getElevationResults()[routeContainer.getRelations().get(routeContainer.getRelations().size() - 1).getElevationResults().length - 1].location);
        }

        return cheapestRoutes;
    }

    /**
     * Generates elevation data by retrieving a route for each relation and saving its polyline data
     * @param relationContainers The relations to be processed
     * @return The relations
     * @throws IOException On GoogleAPI error
     * @throws InterruptedException On GoogleAPI error
     * @throws ApiException On GoogleAPI error
     */
    public static ArrayList<RelationContainer> generateElevation(ArrayList<RelationContainer> relationContainers) throws IOException, InterruptedException, ApiException {
        for(RelationContainer relationContainer : relationContainers) {
            generateElevation(relationContainer);
        }

        return relationContainers;
    }

    /**
     * Generates elevation data by retrieving a route for the relation and saving its polyline data
     * @param relationContainer The relation to be processed
     * @return The relations
     * @throws IOException On GoogleAPI error
     * @throws InterruptedException On GoogleAPI error
     * @throws ApiException On GoogleAPI error
     */
    public static void generateElevation(RelationContainer relationContainer) throws InterruptedException, ApiException, IOException {
        DirectionsResult result = DirectionsApi.getDirections(CONTEXT_DISTANCE_DIRECTIONS, relationContainer.getAddressContainer1().getAddress(), relationContainer.getAddressContainer2().getAddress())
                .alternatives(false)
                .departureTime(new DateTime().plus(Duration.standardMinutes(2))) // TODO in die Einstellungen übernehmen
                .mode(TravelMode.DRIVING)
                .language("de")
                .trafficModel(TrafficModel.BEST_GUESS)
                .await();
        EncodedPolyline encodedPolyline = result.routes[0].overviewPolyline;
        List<LatLng> latLngs = encodedPolyline.decodePath();

        DirectionsStep[] directionsSteps = result.routes[0].legs[0].steps;
        relationContainer.setDirectionsSteps(directionsSteps);

        ElevationResult[] elevationResults = ElevationApi.getByPoints(CONTEXT_ELEVATION, (LatLng[]) (latLngs.toArray(new LatLng[0]))).await();
        relationContainer.setElevationResults(elevationResults);
        relationContainer.setEncodedPolyline(encodedPolyline);
    }


    /**
     * Get the image of the map containing the route
     * @param routeContainer The route
     * @return The image
     * @throws InterruptedException On GoogleAPI error
     * @throws ApiException On GoogleAPI error
     * @throws IOException On GoogleAPI error
     */
    public static BufferedImage getMap(RouteContainer routeContainer) throws InterruptedException, ApiException, IOException {

        ArrayList<LatLng> latLngs = new ArrayList<>();
        for(RelationContainer relationContainer : routeContainer.getRelations()) {
            for(ElevationResult elevationResult : relationContainer.getElevationResults()) {
                latLngs.add(elevationResult.location);
            }
        }
        String encodedPolyline = PolylineEncoding.encode(latLngs);
        System.out.println("encodedPolyline: " + encodedPolyline);
        System.out.println("containing " + latLngs.size() + " items");

        StaticMapsRequest.Path path = new StaticMapsRequest.Path();
        path.addPoint("enc:" + encodedPolyline);
        path.color("0x0000ff");
        path.weight(5);

        StaticMapsRequest request = StaticMapsApi.newRequest(CONTEXT_STATIC_MAPS, new Size(640, 640)).path(path);

        int i = 0;
        for(AddressContainer addressContainer : routeContainer.getRoute()) {
            //if(i > 9) break;
            StaticMapsRequest.Markers markers = new StaticMapsRequest.Markers();
            markers.size(StaticMapsRequest.Markers.MarkersSize.normal);
            markers.color("blue");
            markers.label(ABC[i]);
            markers.addLocation(addressContainer.getLatLng());
            request.markers(markers);
            i++;
        }

        try {
            ImageResult imageResult = request.await();
            ByteArrayInputStream bais = new ByteArrayInputStream(imageResult.imageData);
            return ImageIO.read(bais);
        } catch (IOException e) {
            return ImageIO.read(Main.class.getResourceAsStream("staticmapserror.png"));
        }

    }


}
