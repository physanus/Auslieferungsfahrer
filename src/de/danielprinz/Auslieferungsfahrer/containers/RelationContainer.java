package de.danielprinz.Auslieferungsfahrer.containers;

import com.google.maps.model.Distance;
import com.google.maps.model.Duration;

public class RelationContainer {

    private final static double CONSUMPTION_DOWNHILL =  90.0;    // kW/h per 100 km
    private final static double CONSUMPTION_NORMAL   = 100.0;    // kW/h per 100 km
    private final static double CONSUMPTION_UPHILL   = 120.0;    // kW/h per 100 km

    private AddressContainer addressContainer1;
    private AddressContainer addressContainer2;
    private Duration duration;
    private Distance distance;

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
        return null;
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

    /**
     * Calculates the energy consumption for this relation
     * @param start The startpoint
     * @return The energy consumption in kW/h
     */
    public double getCost(AddressContainer start) {

        if(!start.equals(addressContainer1) && !start.equals(addressContainer2)) throw new IllegalArgumentException("The specified addresscontainer is not contained in the given Relation container:\n" + start.toString() + "\n" + this.toString());
        AddressContainer end = getOtherAddressContainer(start);

        double altitude = end.getElevation() - start.getElevation();
        double slope = altitude / distance.inMeters; // Steigung in %

        if(slope < -3) {
            // energy retrieval
            return CONSUMPTION_DOWNHILL / 100 / 1000 * distance.inMeters;
        }
        if(slope > -3 && slope < 3) {
            // normal behaviour
            return CONSUMPTION_NORMAL / 100 / 1000 * distance.inMeters;
        } else {
            // higher energy consumption
            return CONSUMPTION_UPHILL / 100 / 1000 * distance.inMeters;
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
                ", cost=+-" + getCost(addressContainer1) +
                '}';
    }
}
