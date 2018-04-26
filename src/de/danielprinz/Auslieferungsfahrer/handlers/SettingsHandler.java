package de.danielprinz.Auslieferungsfahrer.handlers;

import de.danielprinz.Auslieferungsfahrer.enums.Direction;

public class SettingsHandler {

    private double consumption;
    private double decrementValue;
    private double incrementValue;

    /**
     * Handles all the settings methods
     * @param consumptionDefault The default value for the consumtion of the truck. =100
     * @param decrementValueDefault The default value for the decrease of the truck when going downhill. =0.9
     * @param incrementValueDefault The default value for the increse of the consumtion when going uphill. =1.2
     */
    public SettingsHandler(double consumptionDefault, double decrementValueDefault, double incrementValueDefault) {
        this.consumption = consumptionDefault;
        this.decrementValue = decrementValueDefault;
        this.incrementValue = incrementValueDefault;
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
}
