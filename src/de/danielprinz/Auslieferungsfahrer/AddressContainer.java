package de.danielprinz.Auslieferungsfahrer;

public class AddressContainer {

    public AddressContainer(String address, double lat, double lng) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    String address;
    double lat, lng;

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }


    @Override
    public String toString() {
        return "AddressContainer{" +
                "address='" + address + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }

}
