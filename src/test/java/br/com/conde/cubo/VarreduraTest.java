package br.com.conde.cubo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VarreduraTest {

    @Test
    @DisplayName("contando meia volta como um giro, o pior cubo esta a 11 giros")
    void comMeiaVolta() {
        Varredura varredura = Varreduras.comMeiaVolta();
        assertThat(varredura.alcancados()).isEqualTo(3_674_160);
        assertThat(varredura.numeroDeDeus()).isEqualTo(11);
        assertThat(varredura.media()).isCloseTo(8.756, within(0.001));
        assertThat(varredura.porDistancia()[11]).isEqualTo(2644);
        assertThat(varredura.porDistancia()[0]).isEqualTo(1);
        assertThat(varredura.porDistancia()[1]).isEqualTo(9);
    }

    @Test
    @DisplayName("contando meia volta como dois giros, o pior esta a 14")
    void soQuartos() {
        Varredura varredura = Varreduras.soQuartos();
        assertThat(varredura.alcancados()).isEqualTo(3_674_160);
        assertThat(varredura.numeroDeDeus()).isEqualTo(14);
        assertThat(varredura.media()).isCloseTo(10.666, within(0.001));
        assertThat(varredura.porDistancia()[14]).isEqualTo(276);
        assertThat(varredura.porDistancia()[1]).isEqualTo(6);
    }

    @Test
    @DisplayName("a soma da distribuicao fecha com o cubo inteiro")
    void aDistribuicaoFecha() {
        for (Varredura varredura : List.of(Varreduras.comMeiaVolta(), Varreduras.soQuartos())) {
            long soma = 0;
            for (int quantos : varredura.porDistancia()) {
                soma += quantos;
            }
            assertThat(soma).isEqualTo(Indice.TOTAL);
        }
    }

    @Test
    @DisplayName("desembaralhar custa o mesmo que embaralhar, nos 3,6 milhoes de cubos")
    void oInversoCustaOMesmo() {
        // Nada na varredura garante isso: ela so anda para a frente. Se a tabela
        // estivesse torta em algum canto, esta conta denunciaria.
        Varredura varredura = Varreduras.comMeiaVolta();
        for (int indice = 0; indice < Indice.TOTAL; indice++) {
            int daIda = varredura.distancia(indice);
            int daVolta = varredura.distancia(Indice.de(Indice.cubo(indice).inverso()));
            if (daIda != daVolta) {
                throw new AssertionError("o cubo " + indice + " custa " + daIda
                        + " e o inverso dele custa " + daVolta);
            }
        }
    }

    @Test
    @DisplayName("vizinho de um cubo esta a um giro de distancia, para mais ou para menos")
    void osVizinhosSaoVizinhos() {
        Varredura varredura = Varreduras.comMeiaVolta();
        Random sorte = new Random(6);
        for (int teste = 0; teste < 5000; teste++) {
            Cubo cubo = Juiz.embaralhado(sorte.nextInt(20), sorte);
            int daqui = varredura.distancia(cubo);
            for (Giro giro : Giro.TODOS) {
                int dali = varredura.distancia(cubo.gira(giro));
                assertThat(Math.abs(dali - daqui)).as("de " + daqui + " para " + dali)
                        .isLessThanOrEqualTo(1);
            }
        }
    }

    @Test
    @DisplayName("a montagem tem exatamente o tamanho da distancia, e monta mesmo")
    void aMontagem() {
        Varredura varredura = Varreduras.comMeiaVolta();
        Random sorte = new Random(7);
        for (int teste = 0; teste < 2000; teste++) {
            Cubo cubo = Juiz.embaralhado(sorte.nextInt(25), sorte);
            List<Giro> passos = varredura.monta(cubo);
            assertThat(passos).hasSize(varredura.distancia(cubo));
            assertThat(cubo.gira(passos).estaMontado()).isTrue();
        }
        assertThat(varredura.monta(Cubo.montado())).isEmpty();
    }

    @Test
    @DisplayName("com so duas faces, 29.160 cubos sao alcancaveis e o resto nao")
    void duasFaces() {
        Varredura varredura = Varreduras.duasFaces();
        assertThat(varredura.alcancados()).isEqualTo(29_160);
        assertThat(varredura.numeroDeDeus()).isEqualTo(14);
        assertThat(varredura.porDistancia()[14]).isEqualTo(22);
        Random sorte = new Random(8);
        int foraDoAlcance = 0;
        for (int teste = 0; teste < 2000; teste++) {
            if (varredura.distancia(Juiz.embaralhado(20, sorte)) < 0) {
                foraDoAlcance++;
            }
        }
        assertThat(foraDoAlcance).as("quase todo cubo fica fora").isGreaterThan(1900);
    }

    @Test
    @DisplayName("cubo fora do alcance nao se monta, e a varredura diz isso")
    void foraDoAlcance() {
        Varredura varredura = Varreduras.duasFaces();
        Cubo fora = Cubo.montado().gira(Giro.de("F"));
        assertThat(varredura.distancia(fora)).isNegative();
        assertThatThrownBy(() -> varredura.monta(fora))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nao se monta");
    }
}
