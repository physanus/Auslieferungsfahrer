package de.danielprinz.Auslieferungsfahrer.containers;

public class BufferContainer {

    private RouteContainer routeContainer;
    private AddressContainer startpointCurrent;

    /**
     * Used for caching uncomputed data
     * @param routeContainer The route container
     * @param startpointCurrent The startpointCurrent
     */
    public BufferContainer(RouteContainer routeContainer, AddressContainer startpointCurrent) {
        this.routeContainer = routeContainer;
        this.startpointCurrent = startpointCurrent;
    }


    public RouteContainer getRouteContainer() {
        return routeContainer;
    }

    public AddressContainer getStartpointCurrent() {
        return startpointCurrent;
    }

}
