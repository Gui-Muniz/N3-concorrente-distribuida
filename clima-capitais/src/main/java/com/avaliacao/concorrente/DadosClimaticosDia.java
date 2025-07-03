package com.avaliacao.concorrente;

public class DadosClimaticosDia {
    private String data; // Formato YYYY-MM-DD
    private double temperaturaMedia;
    private double temperaturaMinima;
    private double temperaturaMaxima;

    public DadosClimaticosDia(String data, double temperaturaMedia, double temperaturaMinima, double temperaturaMaxima) {
        this.data = data;
        this.temperaturaMedia = temperaturaMedia;
        this.temperaturaMinima = temperaturaMinima;
        this.temperaturaMaxima = temperaturaMaxima;
    }

    // Getters
    public String getData() { return data; }
    public double getTemperaturaMedia() { return temperaturaMedia; }
    public double getTemperaturaMinima() { return temperaturaMinima; }
    public double getTemperaturaMaxima() { return temperaturaMaxima; }

    @Override
    public String toString() {
        return "  Data: " + data +
               " | Média: " + String.format("%.2f", temperaturaMedia) + "°C" +
               " | Mínima: " + String.format("%.2f", temperaturaMinima) + "°C" +
               " | Máxima: " + String.format("%.2f", temperaturaMaxima) + "°C";
    }
}