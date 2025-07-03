package com.avaliacao.concorrente;

import java.time.LocalDate;

public class DailyClimateRecord {
    private LocalDate date;
    private double dailyAverageTemperature;
    private double dailyMinTemperature;
    private double dailyMaxTemperature;

    public DailyClimateRecord(LocalDate date, double dailyAverageTemperature, double dailyMinTemperature, double dailyMaxTemperature) {
        this.date = date;
        this.dailyAverageTemperature = dailyAverageTemperature;
        this.dailyMinTemperature = dailyMinTemperature;
        this.dailyMaxTemperature = dailyMaxTemperature;
    }

    // Getters para acessar os dados
    public LocalDate getDate() {
        return date;
    }

    public double getDailyAverageTemperature() {
        return dailyAverageTemperature;
    }

    public double getDailyMinTemperature() {
        return dailyMinTemperature;
    }

    public double getDailyMaxTemperature() {
        return dailyMaxTemperature;
    }

    @Override
    public String toString() {
        return String.format("  Data: %s | Média: %.2f°C | Mínima: %.2f°C | Máxima: %.2f°C",
                             date, dailyAverageTemperature, dailyMinTemperature, dailyMaxTemperature);
    }
}
