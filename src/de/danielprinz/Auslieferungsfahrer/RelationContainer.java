package de.danielprinz.Auslieferungsfahrer;

import com.google.maps.model.Distance;
import com.google.maps.model.Duration;

public class RelationContainer {

    private final static double CONSUMPTION_DOWNHILL =  90.0;    // kW/h per 100 km
    private final static double CONSUMPTION_NORMAL   = 100.0;    // kW/h per 100 km
    private final static double CONSUMPTION_UPHILL   = 120.0;    // kW/h per 100 km


    AddressContainer addressContainer1;
    AddressContainer addressContainer2;
    Duration duration;
    Distance distance;

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
     * Calculates the energy consumption
     * @return The energy consumption
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

    public AddressContainer getOtherAddressContainer(AddressContainer addressContainer) {
        return addressContainer1.equals(addressContainer) ? addressContainer2 : addressContainer1;
    }



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
