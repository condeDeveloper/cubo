package br.com.conde.cubo;

import java.util.Random;

/**
 * A regra que decide, sem girar nada, se um cubo e montavel.
 *
 * <p>Toda torcao de canto que um giro de face faz, ela faz em grupo: num giro, as
 * torcoes somadas andam um multiplo de 3. Entao a soma das torcoes, vista modulo 3,
 * e a mesma do comecinho ate o fim, e no cubo montado ela vale zero.
 *
 * <p>Isso vira um juiz de uma linha: cubo com a soma das torcoes fora dos multiplos
 * de 3 nao sai do montado por giro nenhum, por mais que se gire. E o que acontece
 * quando alguem desmonta o cubo e remonta com um canto torto.
 */
public final class Juiz {

    private Juiz() {
    }

    /** Da para montar este cubo? */
    public static boolean daParaMontar(Cubo cubo) {
        return cubo.somaDasTorcoes() % 3 == 0;
    }

    /** Quantos arranjos de canto existem, contando os que nao se montam. */
    public static long arranjosQueExistem() {
        long torcoes = 1;
        for (int i = 0; i < Cubo.CANTOS; i++) {
            torcoes *= 3;
        }
        return (long) Indice.ORDENS * torcoes;
    }

    /** Quantos deles se montam, pela regra. */
    public static long arranjosQueSeMontam() {
        return arranjosQueExistem() / 3;
    }

    /**
     * Anda com o cubo ao acaso e confere a soma das torcoes a cada passo. E o teste
     * da regra contra os giros de verdade, sem passar pela varredura.
     *
     * @return quantas vezes a soma saiu dos multiplos de 3
     */
    public static int quebrasEmCaminhada(int passos, Random sorte) {
        Cubo cubo = Cubo.montado();
        int quebras = 0;
        for (int passo = 0; passo < passos; passo++) {
            cubo = cubo.gira(Giro.TODOS.get(sorte.nextInt(Giro.TODOS.size())));
            if (cubo.somaDasTorcoes() % 3 != 0) {
                quebras++;
            }
        }
        return quebras;
    }

    /** Um cubo com um canto torcido, que e o jeito classico de deixar o cubo impossivel. */
    public static Cubo comUmCantoTorto() {
        Cubo montado = Cubo.montado();
        int[] co = montado.co();
        co[0] = 1;
        return new Cubo(montado.cp(), co);
    }

    /** Um cubo embaralhado de verdade. */
    public static Cubo embaralhado(int giros, Random sorte) {
        Cubo cubo = Cubo.montado();
        for (int i = 0; i < giros; i++) {
            cubo = cubo.gira(Giro.TODOS.get(sorte.nextInt(Giro.TODOS.size())));
        }
        return cubo;
    }
}
