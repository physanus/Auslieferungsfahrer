package de.danielprinz.Auslieferungsfahrer.containers;

import de.danielprinz.Auslieferungsfahrer.MethodProvider;

import java.util.ArrayList;

public class RouteContainer {

    private ArrayList<AddressContainer> route = new ArrayList<>();
    private double cost = 0.0;
    private double duration = 0.0;
    private double distance = 0.0;

    /**
     * Stores the whole route
     * @param startpoint The startpoint of the route
     */
    public RouteContainer(ArrayList<RelationContainer> relationContainers, AddressContainer startpoint) {
        this.addElement(relationContainers, startpoint, 0);
    }

    /**
     * Used for internal copy only
     */
    private RouteContainer() {
    }

    /**
     * Adds an element to the route
     * @param addressContainer The AddressContainer to be added
     * @param cost The cost for travelling from the current address to AddressContainer
     */
    public void addElement(ArrayList<RelationContainer> relationContainers, AddressContainer addressContainer, double cost) {
        this.route.add(addressContainer);
        this.cost += cost;
        calcTimeAndDistance(relationContainers);
    }

    /**
     * Gets the first element of the route
     * @return The element
     */
    public AddressContainer getFirstElement() {
        return this.route.get(0);
    }

    /**
     * Gets the last element of the route
     * @return The element
     */
    public AddressContainer getLastElement() {
        return this.route.get(this.route.size() - 1);
    }

    public double getCost() {
        return cost;
    }

    public double getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    /**
     * Checks if the route contains a specific Addresscontainer
     * @param addressContainer The AddressContainer
     * @return The result
     */
    public boolean contains(AddressContainer addressContainer) {
        return this.route.contains(addressContainer);
    }

    /**
     * Closes the route so that the endpoint is equal to the startpoint
     * @param relationContainers The relation containers
     */
    public void finish(ArrayList<RelationContainer> relationContainers) {
        RelationContainer relationContainer = MethodProvider.getRelationByAddresses(relationContainers, this.getLastElement(), this.getFirstElement());
        this.addElement(relationContainers, this.getFirstElement(), relationContainer.getCost(this.getLastElement()));
    }

    /**
     * Makes a copy of the routeContainer
     * @return The copy
     */
    public RouteContainer copy() {
        RouteContainer newRouteContainer = new RouteContainer();
        newRouteContainer.route.addAll(this.route);
        newRouteContainer.cost = this.cost;

        return newRouteContainer;
    }

    public void calcTimeAndDistance(ArrayList<RelationContainer> relationContainers) {
        AddressContainer prev = this.route.get(0);
        double duration = 0.0;
        double distance = 0.0;
        for(int i = 1; i < this.route.size(); i++) {
            AddressContainer current = this.route.get(i);
            RelationContainer relationContainer = MethodProvider.getRelationByAddresses(relationContainers, prev, current);
            duration += relationContainer.getDuration().inSeconds;
            distance += relationContainer.getDistance().inMeters;

            prev = current;
        }
        this.duration = duration;
        this.distance = distance;
    }

    /**
     * Shifts the addresses until out startpoint is the startpoint
     * @param startpoint The startpoint of the route
     */
    public void shift(AddressContainer startpoint) {
        while(!this.getFirstElement().equals(startpoint)) {
            this.route.add(0, this.getLastElement());
            this.route.remove(this.route.size() - 1);
        }
    }

    @Override
    public String toString() {
        return "RouteContainer{" +
                "cost=" + cost +
                ", route=" + route +
                '}';
    }
}
