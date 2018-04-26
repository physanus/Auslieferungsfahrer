package de.danielprinz.Auslieferungsfahrer.containers;

import com.google.maps.model.*;
import de.danielprinz.Auslieferungsfahrer.Main;
import de.danielprinz.Auslieferungsfahrer.enums.Direction;

public class RelationContainer {

    private AddressContainer addressContainer1;
    private AddressContainer addressContainer2;
    private Duration duration;
    private Distance distance;
    private double cost = -1;
    private ElevationResult[] elevationResults; // elevation
    private EncodedPolyline encodedPolyline;
    private DirectionsStep[] directionsSteps;

    /**
     * Saves the relation data
     * @param addressContainer1 One of the addresses. Doesn't have to be the start point.
     * @param addressContainer2 The other address
     * @param duration The duration of the relation
     * @param distance The distance of the relation
     */
    public RelationContainer(AddressContainer addressContainer1, AddressContainer addressContainer2, Duration duration, Distance distance) {
        this.addressContainer1 = addressContainer1;
        this.addressContainer2 = addressContainer2;
        this.duration = duration;
        this.distance = distance;
    }

    public AddressContainer getAddressContainer1() {
        return addressContainer1;
    }

    public AddressContainer getAddressContainer2() {
        return addressContainer2;
    }

    public Duration getDuration() {
        return duration;
    }

    public Distance getDistance() {
        return distance;
    }

    public ElevationResult[] getElevationResults() {
        return elevationResults;
    }

    public void setElevationResults(ElevationResult[] elevationResults) {
        this.elevationResults = elevationResults;
    }

    public EncodedPolyline getEncodedPolyline() {
        return encodedPolyline;
    }

    public void setEncodedPolyline(EncodedPolyline encodedPolyline) {
        this.encodedPolyline = encodedPolyline;
    }

    public DirectionsStep[] getDirectionsSteps() {
        return directionsSteps;
    }

    public void setDirectionsSteps(DirectionsStep[] directionsSteps) {
        this.directionsSteps = directionsSteps;
    }

    /**
     * Calculates the energy consumption for this relation
     * @param start The startpoint. null if you only need the magnitude
     * @return The energy consumption in kW/h
     */
    public double getCost(AddressContainer start) {

        if(elevationResults == null) return -1;

        // allow caching of the costs
        if(start == null) {
            if(cost == -1) {
                start = addressContainer1;
            } else {
                return cost;
            }
        }

        if(!start.equals(addressContainer1) && !start.equals(addressContainer2)) throw new IllegalArgumentException("The specified addresscontainer is not contained in the given Relation container:\n" + start.toString() + "\n" + this.toString());
        AddressContainer end = getOtherAddressContainer(start);

        ///////////////////////////////////////////////////

        double elevation = 0.0;
        for(ElevationResult elevationResult : elevationResults) {
            elevation += elevationResult.elevation;
        }


        double slope = elevation / distance.inMeters; // Steigung in %

        if(slope < -3) {
            // energy retrieval
            return Main.SETTINGS_HANDLER.getConsumption(Direction.DOWNHILL) / 100 / 1000 * distance.inMeters;
        }
        if(slope > -3 && slope < 3) {
            // normal behaviour
            return Main.SETTINGS_HANDLER.getConsumption(Direction.NORMAL) / 100 / 1000 * distance.inMeters;
        } else {
            // higher energy consumption
            return Main.SETTINGS_HANDLER.getConsumption(Direction.UPHILL) / 100 / 1000 * distance.inMeters;
        }

    }

    /**
     * Gets the other AddressContainer than specified
     * @param addressContainer One of the two AddressContainers
     * @return The other AddressContainer
     */
    public AddressContainer getOtherAddressContainer(AddressContainer addressContainer) {
        return addressContainer1.equals(addressContainer) ? addressContainer2 : addressContainer1;
    }

    /**
     * Checks if the given AddressContainer is contained in the relation
     * @param addressContainer The AddressContainer
     * @return The result
     */
    public boolean containsAddressContainer(AddressContainer addressContainer) {
        return addressContainer1.equals(addressContainer) || addressContainer2.equals(addressContainer);
    }


    @Override
    public String toString() {
        return "RelationContainer{" +
                "addressContainer1=" + addressContainer1 +
                ", addressContainer2=" + addressContainer2 +
                ", duration=" + duration +
                ", distance=" + distance +
                ", cost=+-" + getCost(null) +
                '}';
    }
}
