package br.com.conde.cubo;

import java.util.List;
import java.util.Locale;
import java.util.Random;

/** As tabelas do README, uma por argumento. */
public final class Medidor {

    private Medidor() {
    }

    public static void main(String[] args) {
        String qual = args.length == 0 ? "tudo" : args[0];
        switch (qual) {
            case "varredura" -> varredura();
            case "distribuicao" -> distribuicao();
            case "regra" -> regra();
            case "duasfaces" -> duasFaces();
            case "montagem" -> montagem();
            default -> {
                varredura();
                distribuicao();
                regra();
                duasFaces();
                montagem();
            }
        }
    }

    private static Varredura comMeiaVolta;
    private static Varredura soQuartos;

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

    static void varredura() {
        System.out.println("== o cubo inteiro ==");
        System.out.println("jeito de contar    giros  estados  numero de Deus  media  piores");
        for (Object[] caso : new Object[][] {
                {"meia volta = 1", comMeiaVolta()}, {"meia volta = 2", soQuartos()}}) {
            Varredura varredura = (Varredura) caso[1];
            int[] porDistancia = varredura.porDistancia();
            System.out.printf(Locale.ROOT, "%-18s %5d %8d %15d %6.3f %7d%n",
                    caso[0], varredura.giros().size(), varredura.alcancados(),
                    varredura.numeroDeDeus(), varredura.media(),
                    porDistancia[porDistancia.length - 1]);
        }
        System.out.println();
        System.out.println("o cubo tem " + Indice.ORDENS + " ordens de canto e " + Indice.TORCOES
                + " torcoes, ou seja " + Indice.TOTAL + " estados, e a varredura alcancou todos");
    }

    static void distribuicao() {
        System.out.println("== quantos cubos a cada distancia do montado ==");
        int[] cheio = comMeiaVolta().porDistancia();
        int[] quartos = soQuartos().porDistancia();
        System.out.println("giros  meia volta = 1  meia volta = 2");
        for (int d = 0; d < Math.max(cheio.length, quartos.length); d++) {
            String um = d < cheio.length ? String.valueOf(cheio[d]) : "-";
            String outro = d < quartos.length ? String.valueOf(quartos[d]) : "-";
            System.out.printf(Locale.ROOT, "%5d %15s %15s%n", d, um, outro);
        }
    }

    static void regra() {
        System.out.println("== a regra da soma das torcoes ==");
        System.out.printf(Locale.ROOT, "arranjos de canto que existem: %d%n",
                Juiz.arranjosQueExistem());
        System.out.printf(Locale.ROOT, "a regra aceita: %d (um terco)%n",
                Juiz.arranjosQueSeMontam());
        System.out.printf(Locale.ROOT, "a varredura alcancou: %d%n", comMeiaVolta().alcancados());
        System.out.printf(Locale.ROOT, "iguais? %s%n",
                Juiz.arranjosQueSeMontam() == comMeiaVolta().alcancados() ? "sim" : "NAO");
        System.out.println();

        int passos = 2_000_000;
        int quebras = Juiz.quebrasEmCaminhada(passos, new Random(5));
        System.out.printf(Locale.ROOT,
                "caminhada de %d giros ao acaso: a soma das torcoes saiu dos multiplos de 3"
                        + " %d vezes%n", passos, quebras);

        Cubo torto = Juiz.comUmCantoTorto();
        System.out.println();
        System.out.println("cubo com um canto torcido: " + torto);
        System.out.println("  a regra diz: " + (Juiz.daParaMontar(torto) ? "da" : "nao da"));
        String daVarredura;
        try {
            daVarredura = "distancia " + comMeiaVolta().distancia(torto);
        } catch (IllegalArgumentException erro) {
            daVarredura = "nem entra na tabela (" + erro.getMessage() + ")";
        }
        System.out.println("  a varredura diz: " + daVarredura);
    }

    static void duasFaces() {
        System.out.println("== so com duas faces, U e R ==");
        Varredura duas = new Varredura(Giro.DUAS_FACES);
        System.out.printf(Locale.ROOT,
                "giros: %d | estados alcancados: %d de %d (%.2f%%) | numero de Deus: %d"
                        + " | media: %.3f%n",
                duas.giros().size(), duas.alcancados(), Indice.TOTAL,
                100.0 * duas.alcancados() / Indice.TOTAL, duas.numeroDeDeus(), duas.media());
        System.out.println();
        System.out.println("giros  cubos");
        int[] porDistancia = duas.porDistancia();
        for (int d = 0; d < porDistancia.length; d++) {
            System.out.printf(Locale.ROOT, "%5d %6d%n", d, porDistancia[d]);
        }
        System.out.println();
        System.out.println("com duas faces da para alcancar " + duas.alcancados()
                + " cubos, e o resto do cubo fica fora do alcance para sempre");
    }

    static void montagem() {
        Varredura varredura = comMeiaVolta();
        int pior = varredura.umDosPiores();
        Cubo cubo = Indice.cubo(pior);
        List<Giro> passos = varredura.monta(cubo);
        System.out.println("== montando o pior cubo que existe ==");
        System.out.println("cubo: " + cubo);
        System.out.println("solucao em " + passos.size() + " giros: " + Giro.escrita(passos));
        System.out.println("conferido refazendo do zero: "
                + (cubo.gira(passos).estaMontado() ? "montou" : "NAO MONTOU"));
        System.out.println();

        int quantos = 20_000;
        int piores = varredura.porDistancia()[varredura.numeroDeDeus()];
        System.out.println("== embaralhar nao e sortear ==");
        System.out.println("giros no embaralho  media para montar  do pior caso  esperado se fosse sorteio");
        double esperado = (double) quantos * piores / Indice.TOTAL;
        for (int embaralho : new int[] {5, 10, 15, 20, 30, 50, 100}) {
            Random sorte = new Random(11);
            long soma = 0;
            int pioresAchados = 0;
            for (int i = 0; i < quantos; i++) {
                Cubo embaralhado = Juiz.embaralhado(embaralho, sorte);
                int quanto = varredura.distancia(embaralhado);
                soma += quanto;
                if (quanto == varredura.numeroDeDeus()) {
                    pioresAchados++;
                }
            }
            System.out.printf(Locale.ROOT, "%18d %18.3f %13d %25.1f%n",
                    embaralho, (double) soma / quantos, pioresAchados, esperado);
        }
        System.out.println();
        System.out.printf(Locale.ROOT, "a media sobre TODOS os cubos que existem e %.3f%n",
                varredura.media());
    }
}
