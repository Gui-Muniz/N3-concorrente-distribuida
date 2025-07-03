package com.avaliacao.concorrente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SingleThreadExperiment {

    public List<ResultadoCapital> execute(List<Capital> capitals, LocalDate startDate, LocalDate endDate) {
        List<ResultadoCapital> resultados = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();

        for (Capital capital : capitals) {
            long capitalStartTime = System.currentTimeMillis();
            try {
                String dados = ClimateApiFetcher.fetchAndProcessClimateData(capital, startDate, endDate);
                long capitalEndTime = System.currentTimeMillis();
                ResultadoCapital resultado = new ResultadoCapital(capital, dados, capitalEndTime - capitalStartTime);
                resultados.add(resultado);
            } catch (RuntimeException e) {
                long capitalEndTime = System.currentTimeMillis();
                // Apenas registra o erro no resultado, não imprime aqui.
                resultados.add(new ResultadoCapital(capital, "Erro: " + e.getMessage(), capitalEndTime - capitalStartTime));
            }
        }

        long endTime = System.currentTimeMillis();
        // A impressão do tempo total do experimento (uma rodada)
        System.out.println("Tempo total de execução Single Thread: " + (endTime - startTime) + "ms");
        
        // Retorna os resultados brutos para que o Main possa decidir como exibir.
        return resultados;
    }
}