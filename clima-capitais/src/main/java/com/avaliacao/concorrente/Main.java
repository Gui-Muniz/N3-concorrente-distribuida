package com.avaliacao.concorrente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Comparator;

// Importações para o parsing JSON
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    // Delay de 10 segundos (10000 ms) entre as rodadas
    private static final long DELAY_BETWEEN_ROUNDS_MS = 10 * 1000;

    // Mapa para armazenar os tempos médios de cada versão
    private static final Map<String, Double> averageExecutionTimes = new LinkedHashMap<>();

    public static void main(String[] args) {
        System.out.println("Iniciando avaliação substitutiva de Programação Concorrente e Distribuída...");

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        System.out.println("Período de dados: " + startDate + " a " + endDate);

        // --- Execução da Versão de Referência (Single Thread) ---
        System.out.println("\n--- Executando Versão de Referência (Single Thread) ---");
        runSingleThreadVersion(startDate, endDate);

        // --- Execução da Versão Multi Thread (27 threads - 1 capital por thread) ---
        System.out.println("\n--- Executando Versão Multi Thread (27 threads - 1 capital/thread) ---");
        runMultiThreadVersion27Threads(startDate, endDate);

        // --- Execução da Versão Multi Thread (9 threads - 3 capitais por thread) ---
        System.out.println("\n--- Executando Versão Multi Thread (9 threads - 3 capitais/thread) ---");
        runMultiThreadVersion9Threads(startDate, endDate);

        // --- Execução da Versão Multi Thread (3 threads - 9 capitais por thread) ---
        System.out.println("\n--- Executando Versão Multi Thread (3 threads - 9 capitais/thread) ---");
        runMultiThreadVersion3Threads(startDate, endDate);

        System.out.println("\n--- Avaliação substitutiva concluída ---");

        // Imprime o resumo ordenado dos tempos médios
        printSortedAverageTimes();
    }

    private static void runSingleThreadVersion(LocalDate startDate, LocalDate endDate) {
        System.out.println("Iniciando 10 rodadas para Single Thread...");
        long totalExecutionTimeSingleThread = 0;

        List<Capital> capitals = CapitalData.getBrazilianCapitals();

        for (int i = 1; i <= 10; i++) {
            System.out.println("\nRodada " + i + " de 10 para Single Thread...");
            long roundStartTime = System.currentTimeMillis();

            for (Capital capital : capitals) {
                try {
                    String jsonData = ClimateApiFetcher.fetchAndProcessClimateData(capital, startDate, endDate);
                    List<DailyClimateRecord> dailyRecords = processClimateDataJson(jsonData, capital.getName());

                    // Apenas para reduzir a verbosidade do console no relatório final,
                    // mantemos a impressão de resumo apenas na primeira rodada ou em caso de erro.
                    // Para ver detalhes de todas as capitais em todas as rodadas, remova este IF.
                    if (i == 1 || dailyRecords.isEmpty()) {
                        System.out.println("--- Dados para " + capital.getName() + " ---");
                        if (dailyRecords.isEmpty()) {
                            System.out.println("Nenhum dado diário encontrado ou erro ao processar. (Pode ser limite da API)");
                        } else {
                            displayMonthlySummary(dailyRecords);
                        }
                        System.out.println("--- Fim dos dados para " + capital.getName() + " ---");
                    }


                } catch (RuntimeException e) {
                    System.out.println("Erro ao buscar/processar dados para " + capital.getName() + ": " + e.getMessage());
                }
            }
            long roundEndTime = System.currentTimeMillis();
            long roundDuration = roundEndTime - roundStartTime;
            totalExecutionTimeSingleThread += roundDuration;
            System.out.println("  Tempo da Rodada " + i + ": " + roundDuration + "ms");
            System.out.println("--- Fim dos Dados da " + i + "ª Rodada para Single Thread ---");

            if (i < 10) {
                System.out.println("Aguardando " + (DELAY_BETWEEN_ROUNDS_MS / 1000) + " segundos antes da próxima rodada...");
                try {
                    Thread.sleep(DELAY_BETWEEN_ROUNDS_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("A espera foi interrompida.");
                }
            }
        }
        double avgTime = totalExecutionTimeSingleThread / 10.0;
        System.out.println("\nTempo total de execução Single Thread: " + totalExecutionTimeSingleThread + "ms");
        System.out.println("Tempo médio de execução Single Thread: " + avgTime + "ms");
        averageExecutionTimes.put("Single Thread", avgTime);
    }

    private static void runMultiThreadVersion27Threads(LocalDate startDate, LocalDate endDate) {
        System.out.println("Iniciando 10 rodadas para Multi Thread (27 threads)...");
        long totalExecutionTimeMultiThread = 0;
        int numThreads = 27;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Capital> capitals = CapitalData.getBrazilianCapitals();

        for (int i = 1; i <= 10; i++) {
            System.out.println("\nRodada " + i + " de 10 para Multi Thread (27 threads)...");
            long roundStartTime = System.currentTimeMillis();

            List<Future<List<DailyClimateRecord>>> futures = new ArrayList<>();

            for (Capital capital : capitals) {
                Callable<List<DailyClimateRecord>> task = () -> {
                    try {
                        String jsonData = ClimateApiFetcher.fetchAndProcessClimateData(capital, startDate, endDate);
                        List<DailyClimateRecord> dailyRecords = processClimateDataJson(jsonData, capital.getName());
                        return dailyRecords;
                    } catch (RuntimeException e) {
                        System.err.println(String.format("Erro na thread para %s: %s", capital.getName(), e.getMessage()));
                        return new ArrayList<>();
                    }
                };
                futures.add(executor.submit(task));
            }

            for (int j = 0; j < futures.size(); j++) {
                Capital currentCapital = capitals.get(j);
                try {
                    List<DailyClimateRecord> dailyRecords = futures.get(j).get();
                    // Apenas para reduzir a verbosidade do console no relatório final,
                    // mantemos a impressão de resumo apenas na primeira rodada ou em caso de erro.
                    if (i == 1 || dailyRecords.isEmpty()) {
                        System.out.println("--- Dados para " + currentCapital.getName() + " ---");
                        if (dailyRecords.isEmpty()) {
                            System.out.println("Nenhum dado diário encontrado ou erro ao processar.");
                        } else {
                            displayMonthlySummary(dailyRecords);
                        }
                        System.out.println("--- Fim dos dados para " + currentCapital.getName() + " ---");
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao obter resultado para " + currentCapital.getName() + ": " + e.getMessage());
                }
            }

            long roundEndTime = System.currentTimeMillis();
            long roundDuration = roundEndTime - roundStartTime;
            totalExecutionTimeMultiThread += roundDuration;
            System.out.println("  Tempo da Rodada " + i + ": " + roundDuration + "ms");
            System.out.println("--- Fim dos Dados da " + i + "ª Rodada para Multi Thread (27 threads) ---");

            if (i < 10) {
                System.out.println("Aguardando " + (DELAY_BETWEEN_ROUNDS_MS / 1000) + " segundos antes da próxima rodada...");
                try {
                    Thread.sleep(DELAY_BETWEEN_ROUNDS_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("A espera foi interrompida.");
                }
            }
        }

        shutdownExecutor(executor);
        double avgTime = totalExecutionTimeMultiThread / 10.0;
        System.out.println("\nTempo total de execução Multi Thread (27 threads): " + totalExecutionTimeMultiThread + "ms");
        System.out.println("Tempo médio de execução Multi Thread (27 threads): " + avgTime + "ms");
        averageExecutionTimes.put("Multi Thread (27 threads)", avgTime);
    }

    private static void runMultiThreadVersion9Threads(LocalDate startDate, LocalDate endDate) {
        System.out.println("Iniciando 10 rodadas para Multi Thread (9 threads)...");
        long totalExecutionTimeMultiThread = 0;
        int numThreads = 9;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Capital> capitals = CapitalData.getBrazilianCapitals();
        int capitalsPerThread = (int) Math.ceil((double) capitals.size() / numThreads);

        for (int i = 1; i <= 10; i++) {
            System.out.println("\nRodada " + i + " de 10 para Multi Thread (9 threads)...");
            long roundStartTime = System.currentTimeMillis();

            List<Future<List<CapitalDailyData>>> futures = new ArrayList<>();

            for (int t = 0; t < numThreads; t++) {
                final int threadId = t;
                Callable<List<CapitalDailyData>> task = () -> {
                    List<CapitalDailyData> threadResults = new ArrayList<>();
                    int startIndex = threadId * capitalsPerThread;
                    int endIndex = Math.min(startIndex + capitalsPerThread, capitals.size());

                    for (int k = startIndex; k < endIndex; k++) {
                        Capital capital = capitals.get(k);
                        try {
                            String jsonData = ClimateApiFetcher.fetchAndProcessClimateData(capital, startDate, endDate);
                            List<DailyClimateRecord> dailyRecords = processClimateDataJson(jsonData, capital.getName());
                            threadResults.add(new CapitalDailyData(capital.getName(), dailyRecords));
                        } catch (RuntimeException e) {
                            System.err.println(String.format("Erro na thread %d para %s: %s", threadId, capital.getName(), e.getMessage()));
                            threadResults.add(new CapitalDailyData(capital.getName(), new ArrayList<>()));
                        }
                    }
                    return threadResults;
                };
                futures.add(executor.submit(task));
            }

            for (Future<List<CapitalDailyData>> future : futures) {
                try {
                    List<CapitalDailyData> results = future.get();
                    for (CapitalDailyData data : results) {
                        // Apenas para reduzir a verbosidade do console no relatório final,
                        // mantemos a impressão de resumo apenas na primeira rodada ou em caso de erro.
                        if (i == 1 || data.getDailyRecords().isEmpty()) {
                            System.out.println("--- Dados para " + data.getCapitalName() + " ---");
                            if (data.getDailyRecords().isEmpty()) {
                                System.out.println("Nenhum dado diário encontrado ou erro ao processar.");
                            } else {
                                displayMonthlySummary(data.getDailyRecords());
                            }
                            System.out.println("--- Fim dos dados para " + data.getCapitalName() + " ---");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao obter resultado de uma thread: " + e.getMessage());
                }
            }

            long roundEndTime = System.currentTimeMillis();
            long roundDuration = roundEndTime - roundStartTime;
            totalExecutionTimeMultiThread += roundDuration;
            System.out.println("  Tempo da Rodada " + i + ": " + roundDuration + "ms");
            System.out.println("--- Fim dos Dados da " + i + "ª Rodada para Multi Thread (9 threads) ---");

            if (i < 10) {
                System.out.println("Aguardando " + (DELAY_BETWEEN_ROUNDS_MS / 1000) + " segundos antes da próxima rodada...");
                try {
                    Thread.sleep(DELAY_BETWEEN_ROUNDS_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("A espera foi interrompida.");
                }
            }
        }

        shutdownExecutor(executor);
        double avgTime = totalExecutionTimeMultiThread / 10.0;
        System.out.println("\nTempo total de execução Multi Thread (9 threads): " + totalExecutionTimeMultiThread + "ms");
        System.out.println("Tempo médio de execução Multi Thread (9 threads): " + avgTime + "ms");
        averageExecutionTimes.put("Multi Thread (9 threads)", avgTime);
    }

    private static void runMultiThreadVersion3Threads(LocalDate startDate, LocalDate endDate) {
        System.out.println("Iniciando 10 rodadas para Multi Thread (3 threads)...");
        long totalExecutionTimeMultiThread = 0;
        int numThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Capital> capitals = CapitalData.getBrazilianCapitals();
        int capitalsPerThread = (int) Math.ceil((double) capitals.size() / numThreads);

        for (int i = 1; i <= 10; i++) {
            System.out.println("\nRodada " + i + " de 10 para Multi Thread (3 threads)...");
            long roundStartTime = System.currentTimeMillis();

            List<Future<List<CapitalDailyData>>> futures = new ArrayList<>();

            for (int t = 0; t < numThreads; t++) {
                final int threadId = t;
                Callable<List<CapitalDailyData>> task = () -> {
                    List<CapitalDailyData> threadResults = new ArrayList<>();
                    int startIndex = threadId * capitalsPerThread;
                    int endIndex = Math.min(startIndex + capitalsPerThread, capitals.size());

                    for (int k = startIndex; k < endIndex; k++) {
                        Capital capital = capitals.get(k);
                        try {
                            String jsonData = ClimateApiFetcher.fetchAndProcessClimateData(capital, startDate, endDate);
                            List<DailyClimateRecord> dailyRecords = processClimateDataJson(jsonData, capital.getName());
                            threadResults.add(new CapitalDailyData(capital.getName(), dailyRecords));
                        } catch (RuntimeException e) {
                            System.err.println(String.format("Erro na thread %d para %s: %s", threadId, capital.getName(), e.getMessage()));
                            threadResults.add(new CapitalDailyData(capital.getName(), new ArrayList<>()));
                        }
                    }
                    return threadResults;
                };
                futures.add(executor.submit(task));
            }

            for (Future<List<CapitalDailyData>> future : futures) {
                try {
                    List<CapitalDailyData> results = future.get();
                    for (CapitalDailyData data : results) {
                        // Apenas para reduzir a verbosidade do console no relatório final,
                        // mantemos a impressão de resumo apenas na primeira rodada ou em caso de erro.
                        if (i == 1 || data.getDailyRecords().isEmpty()) {
                            System.out.println("--- Dados para " + data.getCapitalName() + " ---");
                            if (data.getDailyRecords().isEmpty()) {
                                System.out.println("Nenhum dado diário encontrado ou erro ao processar.");
                            } else {
                                displayMonthlySummary(data.getDailyRecords());
                            }
                            System.out.println("--- Fim dos dados para " + data.getCapitalName() + " ---");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao obter resultado de uma thread: " + e.getMessage());
                }
            }

            long roundEndTime = System.currentTimeMillis();
            long roundDuration = roundEndTime - roundStartTime;
            totalExecutionTimeMultiThread += roundDuration;
            System.out.println("  Tempo da Rodada " + i + ": " + roundDuration + "ms");
            System.out.println("--- Fim dos Dados da " + i + "ª Rodada para Multi Thread (3 threads) ---");

            if (i < 10) {
                System.out.println("Aguardando " + (DELAY_BETWEEN_ROUNDS_MS / 1000) + " segundos antes da próxima rodada...");
                try {
                    Thread.sleep(DELAY_BETWEEN_ROUNDS_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("A espera foi interrompida.");
                }
            }
        }

        shutdownExecutor(executor);
        double avgTime = totalExecutionTimeMultiThread / 10.0;
        System.out.println("\nTempo total de execução Multi Thread (3 threads): " + totalExecutionTimeMultiThread + "ms");
        System.out.println("Tempo médio de execução Multi Thread (3 threads): " + avgTime + "ms");
        averageExecutionTimes.put("Multi Thread (3 threads)", avgTime);
    }

    /**
     * Processa a string JSON retornada pela API e calcula as temperaturas média, mínima e máxima por dia.
     * @param jsonData A string JSON bruta.
     * @param cityName O nome da cidade para fins de logging de erro.
     * @return Uma lista de DailyClimateRecord com os dados diários, ou uma lista vazia em caso de erro.
     */
    public static List<DailyClimateRecord> processClimateDataJson(String jsonData, String cityName) {
        List<DailyClimateRecord> dailyRecords = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(jsonData);
            if (!obj.has("daily") || obj.get("daily").equals(JSONObject.NULL)) {
                System.err.println(String.format("Erro para %s: Seção 'daily' não encontrada ou é nula no JSON.", cityName));
                return dailyRecords;
            }

            JSONObject daily = obj.getJSONObject("daily");

            if (!daily.has("time") || daily.get("time").equals(JSONObject.NULL) ||
                !daily.has("temperature_2m_max") || daily.get("temperature_2m_max").equals(JSONObject.NULL) ||
                !daily.has("temperature_2m_min") || daily.get("temperature_2m_min").equals(JSONObject.NULL)) {
                System.err.println(String.format("Erro para %s: Dados de tempo ou temperatura (máx/min) incompletos/inválidos no JSON.", cityName));
                return dailyRecords;
            }

            JSONArray dates = daily.getJSONArray("time");
            JSONArray maxTemps = daily.getJSONArray("temperature_2m_max");
            JSONArray minTemps = daily.getJSONArray("temperature_2m_min");

            if (dates.length() == 0 || maxTemps.length() == 0 || minTemps.length() == 0 ||
                !(dates.length() == maxTemps.length() && dates.length() == minTemps.length())) {
                System.err.println(String.format("Erro para %s: Arrays de tempo ou temperatura vazios ou com tamanhos inconsistentes.", cityName));
                return dailyRecords;
            }

            for (int i = 0; i < dates.length(); i++) {
                LocalDate date = LocalDate.parse(dates.getString(i));
                double maxTemp = maxTemps.getDouble(i);
                double minTemp = minTemps.getDouble(i);

                double avgTemp = (maxTemp + minTemp) / 2.0;

                dailyRecords.add(new DailyClimateRecord(date, avgTemp, minTemp, maxTemp));
            }

        } catch (org.json.JSONException e) {
            System.err.println(String.format("Erro ao processar JSON para %s: %s", cityName, e.getMessage()));
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println(String.format("Erro inesperado ao processar dados para %s: %s", cityName, e.getMessage()));
            return new ArrayList<>();
        }
        return dailyRecords;
    }

    // Método auxiliar para exibir o resumo mensal no console
    private static void displayMonthlySummary(List<DailyClimateRecord> dailyRecords) {
        if (dailyRecords.isEmpty()) {
            System.out.println("  Nenhum dado diário para resumir.");
            return;
        }

        double monthSumAvg = 0;
        double monthMinOverall = Double.MAX_VALUE;
        double monthMaxOverall = Double.MIN_VALUE;

        if (!dailyRecords.isEmpty()) {
            DailyClimateRecord firstDay = dailyRecords.get(0);
            System.out.println(String.format("  Primeiro dia (%s): Média: %.2f°C | Mínima: %.2f°C | Máxima: %.2f°C",
                                firstDay.getDate(), firstDay.getDailyAverageTemperature(),
                                firstDay.getDailyMinTemperature(), firstDay.getDailyMaxTemperature()));
        }

        if (dailyRecords.size() > 1) {
            DailyClimateRecord lastDay = dailyRecords.get(dailyRecords.size() - 1);
            System.out.println(String.format("  Último dia (%s): Média: %.2f°C | Mínima: %.2f°C | Máxima: %.2f°C",
                                lastDay.getDate(), lastDay.getDailyAverageTemperature(),
                                lastDay.getDailyMinTemperature(), lastDay.getDailyMaxTemperature()));
        }

        for (DailyClimateRecord record : dailyRecords) {
            monthSumAvg += record.getDailyAverageTemperature();
            if (record.getDailyMinTemperature() < monthMinOverall) {
                monthMinOverall = record.getDailyMinTemperature();
            }
            if (record.getDailyMaxTemperature() > monthMaxOverall) {
                monthMaxOverall = record.getDailyMaxTemperature();
            }
        }
        double monthAvgOverall = monthSumAvg / dailyRecords.size();

        System.out.println(String.format("  Resumo Mensal (Total de %d dias): Média Geral: %.2f°C | Mínima do Mês: %.2f°C | Máxima do Mês: %.2f°C",
                            dailyRecords.size(), monthAvgOverall, monthMinOverall, monthMaxOverall));
    }

    private static void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                System.err.println("Algumas threads não terminaram em 60 segundos. Forçando shutdown.");
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Novo método para imprimir os tempos médios ordenados
    private static void printSortedAverageTimes() {
        System.out.println("\n--- RESUMO FINAL DOS TEMPOS MÉDIOS DE EXECUÇÃO ---");
        System.out.println("--------------------------------------------------");

        // Converte o mapa para uma lista de entradas e ordena pelo tempo médio (valor)
        averageExecutionTimes.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .forEach(entry ->
                System.out.printf("%-30s: %.2fms%n", entry.getKey(), entry.getValue())
            );
        System.out.println("--------------------------------------------------");
    }
}