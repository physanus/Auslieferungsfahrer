package de.danielprinz.Auslieferungsfahrer;

import java.util.*;

public class MethodProvider {

    public static RelationContainer getRelationByAddresses(List<RelationContainer> relationContainers, AddressContainer addressContainer1, AddressContainer addressContainer2) {
        for(RelationContainer relationContainer : relationContainers) {
            if(relationContainer.containsAddressContainer(addressContainer1) && relationContainer.containsAddressContainer(addressContainer2)) {
                return relationContainer;
            }
        }
        return null;
    }

    /**
     * Finds the addresscontainers with the lowest costs, even if multiple addresses have the same cost
     * @param costs The map
     * @return The Map
     */
    public static List<AddressContainer> getCheapestAddress(HashMap<AddressContainer, Double> costs) {
        double minValue = Collections.min(costs.values());

        List<AddressContainer> smallestValues = new ArrayList<>();
        for(Map.Entry<AddressContainer, Double> e : costs.entrySet()) {
            if(e.getValue() == minValue) smallestValues.add(e.getKey());
        }
        return smallestValues;
    }

    /**
     * Finds the routecontainer with the lowest cost
     * @param routeContainers The routes
     * @return The route
     */
    public static RouteContainer getCheapestRoute(ArrayList<RouteContainer> routeContainers) {
        double lowestCost = Double.MAX_VALUE;
        RouteContainer cheapestRoute = null;
        for(RouteContainer routeContainer : routeContainers) {
            if(routeContainer.getCost() < lowestCost) {
                lowestCost = routeContainer.getCost();
                cheapestRoute = routeContainer;
            }
        }
        return cheapestRoute;
    }

}
