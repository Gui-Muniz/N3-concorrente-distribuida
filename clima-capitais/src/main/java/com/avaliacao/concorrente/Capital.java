// Capital.java
package com.avaliacao.concorrente;

public class Capital {
    private String name;
    private double latitude;
    private double longitude;

    public Capital(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // Opcional: Adicione um método toString para depuração, se quiser ver os valores
    @Override
    public String toString() {
        return "Capital{" +
               "name='" + name + '\'' +
               ", latitude=" + latitude +
               ", longitude=" + longitude +
               '}';
    }
}