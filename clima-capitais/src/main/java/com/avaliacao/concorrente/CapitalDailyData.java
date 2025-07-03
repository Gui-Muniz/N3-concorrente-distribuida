package com.avaliacao.concorrente;

import java.util.List;

public class CapitalDailyData {
    private String capitalName;
    private List<DailyClimateRecord> dailyRecords;

    public CapitalDailyData(String capitalName, List<DailyClimateRecord> dailyRecords) {
        this.capitalName = capitalName;
        this.dailyRecords = dailyRecords;
    }

    public String getCapitalName() {
        return capitalName;
    }

    public List<DailyClimateRecord> getDailyRecords() {
        return dailyRecords;
    }
}