package br.com.conde.cubo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GiroTest {

    @Test
    @DisplayName("sao nove giros, tres por face")
    void osNove() {
        assertThat(Giro.TODOS).hasSize(9);
        assertThat(Giro.QUARTOS).hasSize(6);
        assertThat(Giro.DUAS_FACES).hasSize(6);
        assertThat(Giro.TODOS.stream().map(Giro::nome).toList())
                .containsExactly("U", "U2", "U'", "R", "R2", "R'", "F", "F2", "F'");
    }

    @Test
    @DisplayName("o inverso troca um quarto por tres quartos e deixa a meia volta quieta")
    void inverso() {
        assertThat(Giro.de("U").inverso().nome()).isEqualTo("U'");
        assertThat(Giro.de("U'").inverso().nome()).isEqualTo("U");
        assertThat(Giro.de("R2").inverso().nome()).isEqualTo("R2");
        for (Giro giro : Giro.TODOS) {
            assertThat(giro.inverso().inverso()).isSameAs(giro);
        }
    }

    @Test
    @DisplayName("a sequencia escrita vira giros e volta a ser texto")
    void sequencia() {
        String texto = "R U F' R2 U'";
        assertThat(Giro.escrita(Giro.sequencia(texto))).isEqualTo(texto);
        assertThat(Giro.sequencia("")).isEmpty();
        assertThatThrownBy(() -> Giro.de("X")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("as duas faces sao U e R, e nenhuma delas e F")
    void duasFaces() {
        assertThat(Giro.DUAS_FACES.stream().map(Giro::nome).toList())
                .containsExactly("U", "U2", "U'", "R", "R2", "R'");
    }

    @Test
    @DisplayName("cada giro leva canto em canto, sem perder nem repetir nenhum")
    void aTabelaEUmaPermutacao() {
        for (Giro giro : Giro.TODOS) {
            int[] contados = new int[Cubo.CANTOS];
            for (int canto : giro.cp()) {
                contados[canto]++;
            }
            for (int quantos : contados) {
                assertThat(quantos).as(giro.nome()).isEqualTo(1);
            }
            int soma = 0;
            for (int torcao : giro.co()) {
                assertThat(torcao).isBetween(0, 2);
                soma += torcao;
            }
            assertThat(soma % 3).as("a torcao de " + giro + " fecha em multiplo de 3").isZero();
        }
    }
}
