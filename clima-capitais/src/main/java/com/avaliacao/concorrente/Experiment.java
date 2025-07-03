package com.avaliacao.concorrente;

import java.time.LocalDate;
import java.util.List;

public interface Experiment {
    // Método que executa o experimento, recebendo as capitais e o período
    List<ResultadoCapital> execute(List<Capital> capitals, LocalDate startDate, LocalDate endDate) throws Exception;

    // Método para retornar o número de threads usadas na versão do experimento
    int getNumThreads();
}