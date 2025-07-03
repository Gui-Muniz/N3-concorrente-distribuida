package com.avaliacao.concorrente;

public class ResultadoCapital {
    private Capital capital;
    private String dadosBrutos; // Nome da variável que guarda os dados JSON
    private long tempoExecucaoMs;

    public ResultadoCapital(Capital capital, String dadosBrutos, long tempoExecucaoMs) {
        this.capital = capital;
        this.dadosBrutos = dadosBrutos;
        this.tempoExecucaoMs = tempoExecucaoMs;
    }

    // --- Getters ---
    public Capital getCapital() {
        return capital;
    }

    public String getDadosBrutos() {
        return dadosBrutos; // Este é o método que faltava ou estava incorreto!
    }

    public long getTempoExecucaoMs() {
        return tempoExecucaoMs;
    }

    // Você pode adicionar setters se precisar modificar esses valores,
    // mas para este projeto, apenas os getters devem ser suficientes.
}