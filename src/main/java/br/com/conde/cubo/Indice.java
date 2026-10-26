package br.com.conde.cubo;

/**
 * Transforma um cubo num numero de 0 a 3.674.159, e de volta.
 *
 * <p>Sem isso nao ha varredura: guardar 3,6 milhoes de objetos num mapa custa
 * memoria e tempo demais. Com isso, o cubo inteiro cabe num {@code byte[]}.
 *
 * <p>O numero tem duas partes. A ordem dos sete cantos vira um numero de 0 a 5039
 * pelo codigo de Lehmer: quantos cantos menores ainda nao usados vem depois de
 * cada um. As torcoes dos seis primeiros viram um numero de 0 a 728 na base 3, e a
 * do setimo nao precisa ser guardada, porque a soma fecha em multiplo de 3.
 */
public final class Indice {

    /** Quantas ordens de canto existem: 7 fatorial. */
    public static final int ORDENS = 5040;

    /** Quantas torcoes existem: 3 elevado a 6, com a setima saindo da soma. */
    public static final int TORCOES = 729;

    /** O tamanho do cubo 2x2 inteiro. */
    public static final int TOTAL = ORDENS * TORCOES;

    private Indice() {
    }

    public static int de(Cubo cubo) {
        if (cubo.somaDasTorcoes() % 3 != 0) {
            throw new IllegalArgumentException(
                    "este cubo nao existe: a soma das torcoes deu " + cubo.somaDasTorcoes()
                            + ", que nao e multiplo de 3");
        }
        return ordemDe(cubo.cp()) * TORCOES + torcaoDe(cubo.co());
    }

    /** O numero da ordem dos cantos, de 0 a 5039. */
    public static int ordemDe(int[] cp) {
        int numero = 0;
        for (int i = 0; i < Cubo.CANTOS; i++) {
            int menores = 0;
            for (int j = i + 1; j < Cubo.CANTOS; j++) {
                if (cp[j] < cp[i]) {
                    menores++;
                }
            }
            numero = numero * (Cubo.CANTOS - i) + menores;
        }
        return numero;
    }

    /** O numero das torcoes, de 0 a 728. */
    public static int torcaoDe(int[] co) {
        int numero = 0;
        for (int i = 0; i < Cubo.CANTOS - 1; i++) {
            numero = numero * 3 + co[i];
        }
        return numero;
    }

    public static Cubo cubo(int indice) {
        return new Cubo(ordem(indice / TORCOES), torcao(indice % TORCOES));
    }

    /** A ordem dos cantos a partir do numero dela. */
    public static int[] ordem(int numero) {
        // Primeiro os digitos de Lehmer, que saem do fim para o comeco porque as
        // bases sao 1, 2, 3... Depois os cantos, que entram do comeco para o fim:
        // o digito da posicao e o lugar do canto na lista do que ainda nao foi usado.
        int[] digitos = new int[Cubo.CANTOS];
        int resto = numero;
        for (int i = Cubo.CANTOS - 1; i >= 0; i--) {
            int quantos = Cubo.CANTOS - i;
            digitos[i] = resto % quantos;
            resto /= quantos;
        }
        int[] sobrando = new int[Cubo.CANTOS];
        for (int i = 0; i < Cubo.CANTOS; i++) {
            sobrando[i] = i;
        }
        int[] cp = new int[Cubo.CANTOS];
        int quantosSobram = Cubo.CANTOS;
        for (int i = 0; i < Cubo.CANTOS; i++) {
            int escolhido = digitos[i];
            cp[i] = sobrando[escolhido];
            System.arraycopy(sobrando, escolhido + 1, sobrando, escolhido,
                    quantosSobram - escolhido - 1);
            quantosSobram--;
        }
        return cp;
    }

    /** As torcoes a partir do numero delas, com a setima fechando a soma. */
    public static int[] torcao(int numero) {
        int[] co = new int[Cubo.CANTOS];
        int resto = numero;
        int soma = 0;
        for (int i = Cubo.CANTOS - 2; i >= 0; i--) {
            co[i] = resto % 3;
            resto /= 3;
            soma += co[i];
        }
        co[Cubo.CANTOS - 1] = (3 - soma % 3) % 3;
        return co;
    }
}
