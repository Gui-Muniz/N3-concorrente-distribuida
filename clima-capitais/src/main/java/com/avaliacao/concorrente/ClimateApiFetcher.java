package com.avaliacao.concorrente;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Locale;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

public class ClimateApiFetcher {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // BASE_URL correta para dados históricos com start_date e end_date
    private static final String BASE_URL = "https://archive-api.open-meteo.com/v1/archive";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String fetchAndProcessClimateData(Capital capital, LocalDate startDate, LocalDate endDate) {
        String lat = String.format(Locale.US, "%.4f", capital.getLatitude());
        String lon = String.format(Locale.US, "%.4f", capital.getLongitude());

        String urlString = String.format("%s?latitude=%s&longitude=%s&daily=temperature_2m_max,temperature_2m_min&timezone=America/Sao_Paulo&start_date=%s&end_date=%s",
                BASE_URL, lat, lon, startDate.format(DATE_FORMATTER), endDate.format(DATE_FORMATTER));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .timeout(Duration.ofSeconds(20))
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RuntimeException(String.format("Falha ao buscar dados para %s. Código de status: %d, Resposta: %s, URL da Requisição: %s",
                                            capital.getName(), response.statusCode(), response.body(), urlString));
            }
        } catch (java.net.http.HttpTimeoutException e) {
            throw new RuntimeException("Timeout de HTTP ao buscar dados para " + capital.getName() + ": " + e.getMessage(), e);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Erro de I/O ao buscar dados para " + capital.getName() + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Requisição interrompida para " + capital.getName() + ": " + e.getMessage(), e);
        }
    }
}