package de.danielprinz.Auslieferungsfahrer.containers;

import com.google.maps.model.LatLng;

public class AddressContainer {

    private String address;
    private LatLng latLng;
    private double elevation;

    public AddressContainer(String address, LatLng latLng) {
        this.address = address;
        this.latLng = latLng;
    }


    public String getAddress() {
        return address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressContainer that = (AddressContainer) o;

        if (Double.compare(that.elevation, elevation) != 0) return false;
        if (!address.equals(that.address)) return false;
        return latLng.equals(that.latLng);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = address.hashCode();
        result = 31 * result + latLng.hashCode();
        temp = Double.doubleToLongBits(elevation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "AddressContainer{" +
                "address='" + address + '\'' +
                ", latLng=" + latLng +
                ", elevation=" + elevation +
                '}';
    }
}
