package de.danielprinz.Auslieferungsfahrer;

import java.util.ArrayList;

public class RouteContainer {

    private ArrayList<AddressContainer> route = new ArrayList<>();
    private double cost = 0.0;

    public RouteContainer(AddressContainer startpoint) {
        this.addElement(startpoint, 0);
    }

    private RouteContainer() {
    }

    public void addElement(AddressContainer addressContainer, double cost) {
        this.route.add(addressContainer);
        this.cost += cost;
    }

    public AddressContainer getLastElement() {
        return this.route.get(this.route.size() - 1);
    }
    public AddressContainer getFirstElement() {
        return this.route.get(0);
    }

    public double getCost() {
        return cost;
    }

    public boolean contains(AddressContainer addressContainer) {
        return this.route.contains(addressContainer);
    }

    /**
     * Closes the route so that the endpoint is equal to the start point
     * @param relationContainers
     */
    public void finish(ArrayList<RelationContainer> relationContainers) {
        RelationContainer relationContainer = MethodProvider.getRelationByAddresses(relationContainers, this.getLastElement(), this.getFirstElement());
        this.addElement(this.getFirstElement(), relationContainer.getCost(this.getLastElement()));
    }

    /**
     * Makes a copy of the routeContainer
     * @return The copy
     */
    public RouteContainer copy() {
        RouteContainer newRouteContainer = new RouteContainer();
        for(AddressContainer addressContainer : this.route) {
            newRouteContainer.route.add(addressContainer);
        }
        newRouteContainer.cost = this.cost;

        return newRouteContainer;
    }

    /**
     * Shifts the addresses until out startpoint is the startpoint
     * @param startpoint
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
