package de.danielprinz.Auslieferungsfahrer.handlers;

public class DurationHandler {

    double duration; // seconds

    public DurationHandler(double duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        int seconds = (int) (duration % 60);
        int minutes = (int) (duration / 60);
        if(minutes > 59) {
            int hours = minutes / 60;
            minutes -= hours * 60;
            return hours + ":" + minutes + ":" + seconds + " h";
        }
        return minutes + ":" + seconds + " min";
    }

}
