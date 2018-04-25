package de.danielprinz.Auslieferungsfahrer.handlers;

public class DistanceHandler {

    double distance;

    public DistanceHandler(double distance) {
        this.distance = distance;
    }


    @Override
    public String toString() {
        double km = distance / 1000;
        return km + " km";
    }

}
