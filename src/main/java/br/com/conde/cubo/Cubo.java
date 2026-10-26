package br.com.conde.cubo;

import java.util.Arrays;

/**
 * Um cubo 2x2 pelos cantos, com um canto pregado.
 *
 * <p>O cubo 2x2 e so cantos: oito peças, nada de centro nem de meio. Como ele nao
 * tem centro para servir de referencia, girar o cubo inteiro nao muda nada do
 * problema; entao um canto fica pregado no lugar, o de tras-embaixo-esquerda, e
 * sobram tres giros de face para mexer no resto: U, R e F.
 *
 * <p>O estado e isto: onde esta cada um dos sete cantos que andam ({@code cp}) e
 * quanto cada um esta torcido ({@code co}, de 0 a 2). A soma das torcoes e sempre
 * multipla de 3, e e so isso que separa um cubo embaralhado de um cubo montado
 * errado de fabrica.
 */
public final class Cubo {

    /** Quantos cantos andam: sete, porque o oitavo esta pregado. */
    public static final int CANTOS = 7;

    /** Os nomes dos cantos que andam, na ordem dos indices. */
    public static final String[] NOMES = {"URF", "UFL", "ULB", "UBR", "DFR", "DLF", "DRB"};

    private final int[] cp;
    private final int[] co;

    public Cubo(int[] cp, int[] co) {
        if (cp.length != CANTOS || co.length != CANTOS) {
            throw new IllegalArgumentException("o cubo 2x2 tem " + CANTOS + " cantos que andam");
        }
        this.cp = cp.clone();
        this.co = co.clone();
    }

    /** O cubo montado. */
    public static Cubo montado() {
        int[] cp = new int[CANTOS];
        for (int i = 0; i < CANTOS; i++) {
            cp[i] = i;
        }
        return new Cubo(cp, new int[CANTOS]);
    }

    public int[] cp() {
        return cp.clone();
    }

    public int[] co() {
        return co.clone();
    }

    public int canto(int lugar) {
        return cp[lugar];
    }

    public int torcao(int lugar) {
        return co[lugar];
    }

    public boolean estaMontado() {
        for (int i = 0; i < CANTOS; i++) {
            if (cp[i] != i || co[i] != 0) {
                return false;
            }
        }
        return true;
    }

    /** A soma das torcoes, que num cubo de verdade sempre fecha em multiplo de 3. */
    public int somaDasTorcoes() {
        int soma = 0;
        for (int torcao : co) {
            soma += torcao;
        }
        return soma;
    }

    /** O cubo depois de um giro. */
    public Cubo gira(Giro giro) {
        int[] novoCp = new int[CANTOS];
        int[] novoCo = new int[CANTOS];
        int[] doGiroCp = giro.cp();
        int[] doGiroCo = giro.co();
        for (int lugar = 0; lugar < CANTOS; lugar++) {
            int deOndeVem = doGiroCp[lugar];
            novoCp[lugar] = cp[deOndeVem];
            novoCo[lugar] = (co[deOndeVem] + doGiroCo[lugar]) % 3;
        }
        return new Cubo(novoCp, novoCo);
    }

    /** O cubo depois de uma sequencia de giros. */
    public Cubo gira(Iterable<Giro> giros) {
        Cubo atual = this;
        for (Giro giro : giros) {
            atual = atual.gira(giro);
        }
        return atual;
    }

    /** O embaralho que desfaz este: o que estava no lugar i volta para o lugar i. */
    public Cubo inverso() {
        int[] novoCp = new int[CANTOS];
        int[] novoCo = new int[CANTOS];
        for (int lugar = 0; lugar < CANTOS; lugar++) {
            novoCp[cp[lugar]] = lugar;
            novoCo[cp[lugar]] = (3 - co[lugar]) % 3;
        }
        return new Cubo(novoCp, novoCo);
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Cubo cubo
                && Arrays.equals(cp, cubo.cp) && Arrays.equals(co, cubo.co);
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(cp) + Arrays.hashCode(co);
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder();
        for (int lugar = 0; lugar < CANTOS; lugar++) {
            if (lugar > 0) {
                texto.append(' ');
            }
            texto.append(NOMES[lugar]).append('=').append(NOMES[cp[lugar]]).append(co[lugar]);
        }
        return texto.toString();
    }
}
