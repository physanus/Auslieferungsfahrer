package de.danielprinz.Auslieferungsfahrer.containers;

import com.google.maps.errors.ApiException;
import de.danielprinz.Auslieferungsfahrer.GoogleAPI;
import de.danielprinz.Auslieferungsfahrer.MethodProvider;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RouteContainer {

    private ArrayList<AddressContainer> route = new ArrayList<>();
    private ArrayList<RelationContainer> relations = new ArrayList<>();
    private double cost = 0.0;
    private double duration = 0.0;
    private double distance = 0.0;
    private BufferedImage map;

    /**
     * Stores the whole route
     * @param startpoint The startpoint of the route
     */
    public RouteContainer(AddressContainer startpoint) {
        this.route.add(startpoint);
    }

    /**
     * Used for internal copy only
     */
    private RouteContainer() {
    }

    /**
     * Adds an element to the route
     * @param addressContainer The AddressContainer to be added
     * @param relationContainer The corresponding relationContainer
     */
    public void addElement(AddressContainer addressContainer, RelationContainer relationContainer) {
        this.route.add(addressContainer);
        if(!this.relations.contains(relationContainer))
            this.relations.add(relationContainer);
        this.cost += relationContainer.getCost(getForelastElement());
        this.calcTimeAndDistance();
    }

    public ArrayList<AddressContainer> getRoute() {
        return route;
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

    /**
     * Gets the forelast element of the route
     * @return The element
     */
    public AddressContainer getForelastElement() {
        return this.route.get(this.route.size() - 2);
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

    public BufferedImage getMap() {
        return map;
    }

    public void setMap(ArrayList<RelationContainer> relationContainers) throws InterruptedException, ApiException, IOException {
        System.out.println(this);
        map = GoogleAPI.getMap(relationContainers, this);
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
     * @param relationContainers The relations
     */
    public void finish(List<RelationContainer> relationContainers) {
        RelationContainer relationContainer = MethodProvider.getRelationByAddresses(relationContainers, this.getLastElement(), this.getFirstElement());
        this.addElement(this.getFirstElement(), relationContainer);
    }

    public ArrayList<RelationContainer> getRelations() {
        return relations;
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

    public void calcTimeAndDistance() {
        for(RelationContainer relationContainer : relations) {
            this.duration += relationContainer.getDuration().inSeconds;
            this.distance += relationContainer.getDistance().inMeters;
        }
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteContainer that = (RouteContainer) o;

        if (Double.compare(that.cost, cost) != 0) return false;
        if (Double.compare(that.duration, duration) != 0) return false;
        if (Double.compare(that.distance, distance) != 0) return false;
        if (route != null ? !route.equals(that.route) : that.route != null) return false;
        if (relations != null ? !relations.equals(that.relations) : that.relations != null) return false;
        return map != null ? map.equals(that.map) : that.map == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = route != null ? route.hashCode() : 0;
        result = 31 * result + (relations != null ? relations.hashCode() : 0);
        temp = Double.doubleToLongBits(cost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(duration);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(distance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (map != null ? map.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RouteContainer{" +
                "cost=" + cost +
                ", route=" + route +
                '}';
    }
}
