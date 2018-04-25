package de.danielprinz.Auslieferungsfahrer.enums;

public enum Progress {

    LAT_LAN(1),
    RELATIONS(2),
    CHEAPEST_ROUTES(3),
    FINISHED(4);


    int i;
    Progress(int i) {
        this.i = i;
    }

    public int getProgressNumber() {
        return i;
    }

}
