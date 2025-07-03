package com.avaliacao.concorrente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException; 

public class MultiThreadExperiment {

    public List<ResultadoCapital> execute(List<Capital> capitals, int numThreads, LocalDate startDate, LocalDate endDate) throws InterruptedException {
        List<ResultadoCapital> resultados = new ArrayList<>();
        // Usa um fixed thread pool que é o ideal para este cenário
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<ResultadoCapital>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        // Submissão de tarefas: uma tarefa por capital
        for (Capital capital : capitals) {
            Callable<ResultadoCapital> task = () -> {
                long capitalStartTime = System.currentTimeMillis();
                try {
                    String dados = ClimateApiFetcher.fetchAndProcessClimateData(capital, startDate, endDate);
                    long capitalEndTime = System.currentTimeMillis();
                    return new ResultadoCapital(capital, dados, capitalEndTime - capitalStartTime);
                } catch (RuntimeException e) {
                    long capitalEndTime = System.currentTimeMillis();
                    // Captura a RuntimeException e retorna um resultado com erro.
                    return new ResultadoCapital(capital, "Erro: " + e.getMessage(), capitalEndTime - capitalStartTime);
                }
            };
            futures.add(executor.submit(task));
        }

        executor.shutdown(); // Inicia o desligamento do executor
        // Espera as tarefas terminarem. Se o tempo limite expirar, InterruptedException é lançada.
        // O awaitTermination é fundamental para que o tempo total seja computado após o fim das threads.
        executor.awaitTermination(10, TimeUnit.MINUTES); 

        long endTime = System.currentTimeMillis();
        
        // Coleta os resultados de cada Future
        for (Future<ResultadoCapital> future : futures) {
            try {
                ResultadoCapital resultado = future.get(); // Bloqueia até a tarefa ser concluída
                if (resultado != null) {
                    resultados.add(resultado);
                }
            } catch (ExecutionException e) {
                // Esta exceção encapsula exceções lançadas dentro da tarefa Callable.
                // Registra o erro, mas não interrompe o fluxo principal de coleta de resultados.
                // Isso garante que mesmo que uma tarefa falhe, outras podem ser coletadas.
                System.err.println("Erro na tarefa de uma thread: " + e.getCause().getMessage());
            }
            // A InterruptedException é tratada no método 'execute' do Main.java, conforme ajustado.
        }
        
        System.out.println("Tempo total de execução Multi Thread (" + numThreads + " threads): " + (endTime - startTime) + "ms");
        
        return resultados;
    }
}