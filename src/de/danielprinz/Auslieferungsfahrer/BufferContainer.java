package de.danielprinz.Auslieferungsfahrer;

public class BufferContainer {

    private RouteContainer routeContainer;
    private AddressContainer startpointCurrent;

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
