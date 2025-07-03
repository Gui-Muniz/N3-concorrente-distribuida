package com.avaliacao.concorrente;

import java.util.ArrayList;
import java.util.List;

public class CapitalData {

    public static List<Capital> getBrazilianCapitals() {
        List<Capital> capitals = new ArrayList<>();

        // Coordenadas arredondadas para melhor compatibilidade com a API Open-Meteo
        capitals.add(new Capital("Rio Branco", -9.9750, -67.8000));
        capitals.add(new Capital("Maceió", -9.6750, -35.7500));
        capitals.add(new Capital("Macapá", 0.0330, -51.0500));
        capitals.add(new Capital("Manaus", -3.1250, -60.0000));
        capitals.add(new Capital("Salvador", -12.9750, -38.4750));
        capitals.add(new Capital("Fortaleza", -3.7250, -38.5250));
        capitals.add(new Capital("Brasília", -15.7750, -47.8750));
        capitals.add(new Capital("Vitória", -20.2750, -40.3000));
        capitals.add(new Capital("Goiânia", -16.6750, -49.2500));
        capitals.add(new Capital("São Luís", -2.5250, -44.3000));
        capitals.add(new Capital("Cuiabá", -15.6000, -56.1000));
        capitals.add(new Capital("Campo Grande", -20.4750, -54.6250));
        capitals.add(new Capital("Belo Horizonte", -19.9250, -43.9250));
        capitals.add(new Capital("Belém", -1.4500, -48.5000));
        capitals.add(new Capital("João Pessoa", -7.1250, -34.8750));
        capitals.add(new Capital("Curitiba", -25.4250, -49.2750));
        capitals.add(new Capital("Recife", -8.0500, -34.9000));
        capitals.add(new Capital("Teresina", -5.0750, -42.8000));
        capitals.add(new Capital("Natal", -5.7750, -35.2000));
        capitals.add(new Capital("Porto Alegre", -30.0250, -51.2250));
        capitals.add(new Capital("Porto Velho", -8.7750, -63.9000));
        capitals.add(new Capital("Boa Vista", 2.8250, -60.6750));
        capitals.add(new Capital("Florianópolis", -27.5750, -48.5750));
        capitals.add(new Capital("Palmas", -10.1600, -48.3300));
        capitals.add(new Capital("Aracaju", -10.9000, -37.0500));
        capitals.add(new Capital("São Paulo", -23.5500, -46.6300));
        capitals.add(new Capital("Rio de Janeiro", -22.9000, -43.2200));

        return capitals;
    }
}