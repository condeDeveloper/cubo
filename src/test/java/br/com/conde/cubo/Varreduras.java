package br.com.conde.cubo;

/**
 * As varreduras usadas pelos testes, montadas uma vez so.
 *
 * <p>Cada varredura leva alguns segundos e aparece em teste atras de teste. Sem
 * este guarda-tudo a bateria repete a mesma busca umas dez vezes, e o CI roda a
 * bateria tres vezes, uma por sistema.
 */
final class Varreduras {

    private static Varredura comMeiaVolta;
    private static Varredura soQuartos;
    private static Varredura duasFaces;

    private Varreduras() {
    }

    static synchronized Varredura comMeiaVolta() {
        if (comMeiaVolta == null) {
            comMeiaVolta = new Varredura(Giro.TODOS);
        }
        return comMeiaVolta;
    }

    static synchronized Varredura soQuartos() {
        if (soQuartos == null) {
            soQuartos = new Varredura(Giro.QUARTOS);
        }
        return soQuartos;
    }

    static synchronized Varredura duasFaces() {
        if (duasFaces == null) {
            duasFaces = new Varredura(Giro.DUAS_FACES);
        }
        return duasFaces;
    }
}
