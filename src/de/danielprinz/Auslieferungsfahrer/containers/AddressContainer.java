package de.danielprinz.Auslieferungsfahrer.containers;

import com.google.maps.model.LatLng;

public class AddressContainer {

    private String address;
    private LatLng latLng;

    /**
     * Stores an address with its corresponding data, e.g. coordinates, name, elevation, etc.
     * @param address The address entered into the text field
     * @param latLng The coordinates of the address
     */
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressContainer that = (AddressContainer) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        return latLng != null ? latLng.equals(that.latLng) : that.latLng == null;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (latLng != null ? latLng.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AddressContainer{" +
                "address='" + address + '\'' +
                ", latLng=" + latLng +
                '}';
    }
}
