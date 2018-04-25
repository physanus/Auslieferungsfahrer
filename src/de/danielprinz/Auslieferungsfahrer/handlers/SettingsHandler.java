package de.danielprinz.Auslieferungsfahrer.handlers;

import de.danielprinz.Auslieferungsfahrer.enums.Direction;

public class SettingsHandler {

    private double consumption;
    private double decrementValue;
    private double incrementValue;
    private int samples;

    /**
     * Handles all the settings methods
     * @param consumptionDefault The default value for the consumtion of the truck. =100
     * @param decrementValueDefault The default value for the decrease of the truck when going downhill. =0.9
     * @param incrementValueDefault The default value for the increse of the consumtion when going uphill. =1.2
     * @param samplesDefault Specifies how many points are generated between two addressContainers for determining the height differende. Higher value = better results
     */
    public SettingsHandler(double consumptionDefault, double decrementValueDefault, double incrementValueDefault, int samplesDefault) {
        this.consumption = consumptionDefault;
        this.decrementValue = decrementValueDefault;
        this.incrementValue = incrementValueDefault;
        this.samples = samplesDefault;
    }


    public double getConsumption(Direction direction) {
        if(direction.equals(Direction.DOWNHILL))
            return consumption * decrementValue;
        else if(direction.equals(Direction.NORMAL))
            return consumption;
        else
            return consumption * incrementValue;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public double getDecrementValue() {
        return decrementValue;
    }

    public void setDecrementValue(double decrementValue) {
        this.decrementValue = decrementValue;
    }

    public double getIncrementValue() {
        return incrementValue;
    }

    public void setIncrementValue(double incrementValue) {
        this.incrementValue = incrementValue;
    }

    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }
}
