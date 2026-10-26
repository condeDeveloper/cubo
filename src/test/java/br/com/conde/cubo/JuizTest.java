package br.com.conde.cubo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JuizTest {

    @Test
    @DisplayName("a regra aceita exatamente um terco dos arranjos que existem")
    void umTerco() {
        assertThat(Juiz.arranjosQueExistem()).isEqualTo(11_022_480L);
        assertThat(Juiz.arranjosQueSeMontam()).isEqualTo(3_674_160L);
        assertThat(Juiz.arranjosQueSeMontam() * 3).isEqualTo(Juiz.arranjosQueExistem());
    }

    @Test
    @DisplayName("a regra bate com a varredura: todo arranjo aceito e alcancavel")
    void aRegraBateComAVarredura() {
        assertThat(Juiz.arranjosQueSeMontam())
                .isEqualTo(Varreduras.comMeiaVolta().alcancados());
    }

    @Test
    @DisplayName("dois milhoes de giros ao acaso e a soma das torcoes nao sai dos multiplos de 3")
    void aCaminhada() {
        assertThat(Juiz.quebrasEmCaminhada(2_000_000, new Random(5))).isZero();
    }

    @Test
    @DisplayName("cubo com um canto torcido e recusado pela regra e nao entra na tabela")
    void cantoTorto() {
        Cubo torto = Juiz.comUmCantoTorto();
        assertThat(Juiz.daParaMontar(torto)).isFalse();
        assertThat(torto.somaDasTorcoes()).isEqualTo(1);
        assertThatThrownBy(() -> Varreduras.comMeiaVolta().distancia(torto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("todo cubo embaralhado passa pela regra")
    void oEmbaralhadoPassa() {
        Random sorte = new Random(9);
        for (int teste = 0; teste < 5000; teste++) {
            assertThat(Juiz.daParaMontar(Juiz.embaralhado(sorte.nextInt(40), sorte))).isTrue();
        }
    }

    @Test
    @DisplayName("torcer um canto para um lado e outro para o outro volta a valer")
    void duasTorcoesQueSeAnulam() {
        Cubo montado = Cubo.montado();
        int[] co = montado.co();
        co[0] = 1;
        co[1] = 2;
        Cubo cubo = new Cubo(montado.cp(), co);
        assertThat(Juiz.daParaMontar(cubo)).isTrue();
        assertThat(Varreduras.comMeiaVolta().distancia(cubo)).isPositive();
    }
}
