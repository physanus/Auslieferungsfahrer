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
     * Finds the routecontainers with the lowest costs
     * @param routeContainers The routes
     * @return The routes
     */
    public static ArrayList<RouteContainer> getCheapestRoutes(ArrayList<RouteContainer> routeContainers) {
        double lowestCost = Double.MAX_VALUE;
        for(RouteContainer routeContainer : routeContainers) {
            if(routeContainer.getCost() < lowestCost)
                lowestCost = routeContainer.getCost();
        }

        ArrayList<RouteContainer> cheapestRoutes = new ArrayList<>();
        for(RouteContainer routeContainer : routeContainers) {
            if(routeContainer.getCost() == lowestCost)
                cheapestRoutes.add(routeContainer);
        }

        return cheapestRoutes;
    }

}
