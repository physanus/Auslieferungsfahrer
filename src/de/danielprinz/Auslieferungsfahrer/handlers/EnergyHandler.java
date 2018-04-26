package de.danielprinz.Auslieferungsfahrer.handlers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class EnergyHandler {

    private double energy;

    public EnergyHandler(double energy) {
        this.energy = energy;
    }


    @Override
    public String toString() {
        String energyString = new DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(energy);
        return energyString + " kW/h";
    }

}
