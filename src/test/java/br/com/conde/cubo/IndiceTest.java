package br.com.conde.cubo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IndiceTest {

    @Test
    @DisplayName("as 5040 ordens de canto vao e voltam, e nenhuma repete numero")
    void asOrdens() {
        Set<String> vistas = new HashSet<>();
        for (int numero = 0; numero < Indice.ORDENS; numero++) {
            int[] cp = Indice.ordem(numero);
            assertThat(Indice.ordemDe(cp)).as("ordem " + numero).isEqualTo(numero);
            assertThat(vistas.add(java.util.Arrays.toString(cp))).as("ordem repetida").isTrue();
            int[] contados = new int[Cubo.CANTOS];
            for (int canto : cp) {
                contados[canto]++;
            }
            for (int quantos : contados) {
                assertThat(quantos).as("cada canto aparece uma vez").isEqualTo(1);
            }
        }
        assertThat(vistas).hasSize(5040);
    }

    @Test
    @DisplayName("as 729 torcoes vao e voltam, e a setima sempre fecha a soma")
    void asTorcoes() {
        for (int numero = 0; numero < Indice.TORCOES; numero++) {
            int[] co = Indice.torcao(numero);
            assertThat(Indice.torcaoDe(co)).as("torcao " + numero).isEqualTo(numero);
            int soma = 0;
            for (int torcao : co) {
                assertThat(torcao).isBetween(0, 2);
                soma += torcao;
            }
            assertThat(soma % 3).as("soma da torcao " + numero).isZero();
        }
    }

    @Test
    @DisplayName("todo numero de 0 a 3.674.159 vira um cubo e volta a ser o mesmo numero")
    void todosOsNumeros() {
        for (int indice = 0; indice < Indice.TOTAL; indice++) {
            if (Indice.de(Indice.cubo(indice)) != indice) {
                throw new AssertionError("o indice " + indice + " nao voltou igual");
            }
        }
        assertThat(Indice.TOTAL).isEqualTo(3_674_160);
    }

    @Test
    @DisplayName("o cubo montado e o numero zero")
    void oMontadoEZero() {
        assertThat(Indice.de(Cubo.montado())).isZero();
        assertThat(Indice.cubo(0).estaMontado()).isTrue();
    }

    @Test
    @DisplayName("cubo com a soma das torcoes torta nao entra na tabela")
    void cuboImpossivel() {
        assertThatThrownBy(() -> Indice.de(Juiz.comUmCantoTorto()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nao e multiplo de 3");
    }
}
