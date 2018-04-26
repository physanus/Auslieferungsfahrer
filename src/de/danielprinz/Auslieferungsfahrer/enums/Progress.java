package de.danielprinz.Auslieferungsfahrer.enums;

public enum Progress {

    LAT_LAN(1),
    RELATIONS(2),
    ELEVATION(3),
    CHEAPEST_ROUTES(4),
    FINISHED(5);


    int i;
    Progress(int i) {
        this.i = i;
    }

    public int getProgressNumber() {
        return i;
    }

}
