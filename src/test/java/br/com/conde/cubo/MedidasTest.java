package br.com.conde.cubo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Os numeros que estao escritos no README, cada um preso a uma medida. */
class MedidasTest {

    @Test
    @DisplayName("README: a distribuicao contando meia volta como um giro")
    void aDistribuicaoComMeiaVolta() {
        int[] esperado = {1, 9, 54, 321, 1847, 9992, 50136, 227536, 870072, 1887748, 623800, 2644};
        assertThat(Varreduras.comMeiaVolta().porDistancia()).isEqualTo(esperado);
    }

    @Test
    @DisplayName("README: a distribuicao contando meia volta como dois giros")
    void aDistribuicaoSoComQuartos() {
        int[] esperado = {1, 6, 27, 120, 534, 2256, 8969, 33058, 114149, 360508, 930588,
            1350852, 782536, 90280, 276};
        assertThat(Varreduras.soQuartos().porDistancia()).isEqualTo(esperado);
    }

    @Test
    @DisplayName("README: 11 e 14 giros, com media 8,756 e 10,666")
    void osNumerosDeDeus() {
        assertThat(Varreduras.comMeiaVolta().numeroDeDeus()).isEqualTo(11);
        assertThat(Varreduras.comMeiaVolta().media()).isCloseTo(8.756, within(0.0005));
        assertThat(Varreduras.soQuartos().numeroDeDeus()).isEqualTo(14);
        assertThat(Varreduras.soQuartos().media()).isCloseTo(10.666, within(0.0005));
    }

    @Test
    @DisplayName("README: so com U e R sobram 29.160 cubos, 0,79% do total")
    void asDuasFaces() {
        Varredura duas = Varreduras.duasFaces();
        assertThat(duas.alcancados()).isEqualTo(29_160);
        assertThat(100.0 * duas.alcancados() / Indice.TOTAL).isCloseTo(0.79, within(0.01));
        assertThat(duas.numeroDeDeus()).isEqualTo(14);
        assertThat(duas.media()).isCloseTo(9.794, within(0.001));
        assertThat(duas.porDistancia()).isEqualTo(new int[] {1, 6, 18, 53, 148, 400, 910, 1882,
            3276, 4628, 6198, 6325, 4352, 941, 22});
    }

    @Test
    @DisplayName("README: 11.022.480 arranjos, um terco deles montavel")
    void aRegra() {
        assertThat(Juiz.arranjosQueExistem()).isEqualTo(11_022_480L);
        assertThat(Juiz.arranjosQueSeMontam()).isEqualTo(3_674_160L);
        assertThat(Varreduras.comMeiaVolta().alcancados()).isEqualTo(3_674_160);
        assertThat(Juiz.quebrasEmCaminhada(2_000_000, new Random(5))).isZero();
    }

    @Test
    @DisplayName("README: a solucao de 11 giros do pior cubo monta mesmo")
    void oPiorCubo() {
        Varredura varredura = Varreduras.comMeiaVolta();
        Cubo pior = Indice.cubo(varredura.umDosPiores());
        List<Giro> passos = varredura.monta(pior);
        assertThat(passos).hasSize(11);
        assertThat(Giro.escrita(passos)).isEqualTo("U R U2 F R' F2 R' U R' U F2");
        assertThat(pior.gira(passos).estaMontado()).isTrue();
    }

    @Test
    @DisplayName("README: embaralhar com 30 giros nao e sortear um cubo")
    void embaralharNaoESortear() {
        Varredura varredura = Varreduras.comMeiaVolta();
        int quantos = 20_000;
        double[] mediaEsperada = {3.261, 5.703, 7.340, 8.165, 8.661, 8.749, 8.752};
        int[] pioresEsperados = {0, 0, 0, 7, 8, 10, 18};
        int[] embaralhos = {5, 10, 15, 20, 30, 50, 100};
        for (int i = 0; i < embaralhos.length; i++) {
            Random sorte = new Random(11);
            long soma = 0;
            int piores = 0;
            for (int teste = 0; teste < quantos; teste++) {
                int quanto = varredura.distancia(Juiz.embaralhado(embaralhos[i], sorte));
                soma += quanto;
                if (quanto == varredura.numeroDeDeus()) {
                    piores++;
                }
            }
            assertThat((double) soma / quantos).as("media com " + embaralhos[i] + " giros")
                    .isCloseTo(mediaEsperada[i], within(0.001));
            assertThat(piores).as("piores com " + embaralhos[i] + " giros")
                    .isEqualTo(pioresEsperados[i]);
        }
        double esperadoSeFosseSorteio = (double) quantos * 2644 / Indice.TOTAL;
        assertThat(esperadoSeFosseSorteio).isCloseTo(14.4, within(0.1));
    }
}
