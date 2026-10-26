package br.com.conde.cubo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CuboTest {

    @Test
    @DisplayName("o cubo montado tem cada canto no lugar e sem torcao")
    void montado() {
        Cubo cubo = Cubo.montado();
        assertThat(cubo.estaMontado()).isTrue();
        assertThat(cubo.somaDasTorcoes()).isZero();
        for (int lugar = 0; lugar < Cubo.CANTOS; lugar++) {
            assertThat(cubo.canto(lugar)).isEqualTo(lugar);
            assertThat(cubo.torcao(lugar)).isZero();
        }
    }

    @Test
    @DisplayName("quatro quartos de volta da mesma face voltam ao cubo de antes")
    void quatroQuartos() {
        for (Giro giro : Giro.QUARTOS) {
            Cubo cubo = Cubo.montado();
            for (int vez = 1; vez <= 4; vez++) {
                cubo = cubo.gira(giro);
                assertThat(cubo.estaMontado()).as(giro + " vezes " + vez).isEqualTo(vez == 4);
            }
        }
    }

    @Test
    @DisplayName("meia volta e dois quartos, e tres quartos e a volta ao contrario")
    void meiaVolta() {
        for (int face = 0; face < 3; face++) {
            Giro quarto = Giro.TODOS.get(face * 3);
            Giro meia = Giro.TODOS.get(face * 3 + 1);
            Giro tresQuartos = Giro.TODOS.get(face * 3 + 2);
            Cubo montado = Cubo.montado();
            assertThat(montado.gira(quarto).gira(quarto)).isEqualTo(montado.gira(meia));
            assertThat(montado.gira(meia).gira(quarto)).isEqualTo(montado.gira(tresQuartos));
            assertThat(montado.gira(quarto).gira(tresQuartos)).isEqualTo(montado);
        }
    }

    @Test
    @DisplayName("nenhum giro mexe no canto pregado, e a soma das torcoes nunca sai dos multiplos de 3")
    void oCantoPregadoEASoma() {
        Random sorte = new Random(1);
        Cubo cubo = Cubo.montado();
        for (int passo = 0; passo < 20_000; passo++) {
            cubo = cubo.gira(Giro.TODOS.get(sorte.nextInt(9)));
            assertThat(cubo.somaDasTorcoes() % 3).as("passo " + passo).isZero();
            assertThat(cubo.cp()).hasSize(Cubo.CANTOS);
        }
    }

    @Test
    @DisplayName("desfazer a sequencia ao contrario volta para o cubo de antes")
    void inverterASequencia() {
        Random sorte = new Random(2);
        for (int teste = 0; teste < 200; teste++) {
            List<Giro> ida = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                ida.add(Giro.TODOS.get(sorte.nextInt(9)));
            }
            List<Giro> volta = new ArrayList<>();
            for (int i = ida.size() - 1; i >= 0; i--) {
                volta.add(ida.get(i).inverso());
            }
            assertThat(Cubo.montado().gira(ida).gira(volta).estaMontado()).isTrue();
        }
    }

    @Test
    @DisplayName("o inverso do cubo desfaz o cubo")
    void oInversoDoCubo() {
        Random sorte = new Random(3);
        for (int teste = 0; teste < 500; teste++) {
            Cubo cubo = Juiz.embaralhado(15, sorte);
            Cubo inverso = cubo.inverso();
            assertThat(inverso.inverso()).isEqualTo(cubo);
            assertThat(Indice.de(inverso)).isNotNegative();
        }
        assertThat(Cubo.montado().inverso()).isEqualTo(Cubo.montado());
    }

    @Test
    @DisplayName("cubo com numero errado de cantos e recusado")
    void cantosDeMenos() {
        assertThatThrownBy(() -> new Cubo(new int[3], new int[3]))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("dois cubos com os mesmos cantos e as mesmas torcoes sao o mesmo cubo")
    void igualdade() {
        Cubo um = Juiz.embaralhado(7, new Random(4));
        Cubo outro = Juiz.embaralhado(7, new Random(4));
        assertThat(um).isEqualTo(outro);
        assertThat(um.hashCode()).isEqualTo(outro.hashCode());
        assertThat(um).isNotEqualTo(Cubo.montado());
    }
}
