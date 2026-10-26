package br.com.conde.cubo;

import java.util.ArrayList;
import java.util.List;

/**
 * A varredura do cubo inteiro, a partir do cubo montado.
 *
 * <p>Sao 3.674.160 estados e cabem todos num {@code byte[]}, um por estado, com a
 * distancia ate o montado. Daí sai o numero de Deus do 2x2 sem citar ninguem: e o
 * maior valor da tabela.
 *
 * <p>O conjunto de giros entra por fora, porque "quantas jogadas" depende do que
 * conta como jogada: meia volta como uma so, ou meia volta como duas.
 */
public final class Varredura {

    private final List<Giro> giros;
    private final byte[] distancia;
    private final int[] porDistancia;
    private final int alcancados;

    public Varredura(List<Giro> giros) {
        this.giros = giros;
        this.distancia = new byte[Indice.TOTAL];
        java.util.Arrays.fill(distancia, (byte) -1);
        int[] fila = new int[Indice.TOTAL];
        int fim = 0;
        int comeco = Indice.de(Cubo.montado());
        distancia[comeco] = 0;
        fila[fim++] = comeco;
        int[][] girosCp = new int[giros.size()][];
        int[][] girosCo = new int[giros.size()][];
        for (int i = 0; i < giros.size(); i++) {
            girosCp[i] = giros.get(i).cp();
            girosCo[i] = giros.get(i).co();
        }
        int[] cp = new int[Cubo.CANTOS];
        int[] co = new int[Cubo.CANTOS];
        int[] novoCp = new int[Cubo.CANTOS];
        int[] novoCo = new int[Cubo.CANTOS];
        for (int cabeca = 0; cabeca < fim; cabeca++) {
            int atual = fila[cabeca];
            byte daqui = distancia[atual];
            System.arraycopy(Indice.ordem(atual / Indice.TORCOES), 0, cp, 0, Cubo.CANTOS);
            System.arraycopy(Indice.torcao(atual % Indice.TORCOES), 0, co, 0, Cubo.CANTOS);
            for (int g = 0; g < girosCp.length; g++) {
                for (int lugar = 0; lugar < Cubo.CANTOS; lugar++) {
                    int vem = girosCp[g][lugar];
                    novoCp[lugar] = cp[vem];
                    novoCo[lugar] = (co[vem] + girosCo[g][lugar]) % 3;
                }
                int vizinho = Indice.ordemDe(novoCp) * Indice.TORCOES + Indice.torcaoDe(novoCo);
                if (distancia[vizinho] < 0) {
                    distancia[vizinho] = (byte) (daqui + 1);
                    fila[fim++] = vizinho;
                }
            }
        }
        this.alcancados = fim;
        int maior = 0;
        for (byte quanto : distancia) {
            maior = Math.max(maior, quanto);
        }
        this.porDistancia = new int[maior + 1];
        for (byte quanto : distancia) {
            if (quanto >= 0) {
                porDistancia[quanto]++;
            }
        }
    }

    public List<Giro> giros() {
        return giros;
    }

    /** Quantos estados a varredura alcancou. */
    public int alcancados() {
        return alcancados;
    }

    /** A distancia ate o cubo montado, ou -1 se nao der para chegar com esses giros. */
    public int distancia(Cubo cubo) {
        return distancia[Indice.de(cubo)];
    }

    public int distancia(int indice) {
        return distancia[indice];
    }

    /** O numero de Deus deste conjunto de giros: o pior cubo que existe. */
    public int numeroDeDeus() {
        return porDistancia.length - 1;
    }

    /** Quantos cubos ha a cada distancia do montado. */
    public int[] porDistancia() {
        return porDistancia.clone();
    }

    public double media() {
        long soma = 0;
        for (int d = 0; d < porDistancia.length; d++) {
            soma += (long) d * porDistancia[d];
        }
        return (double) soma / alcancados;
    }

    /** Um dos cubos mais longe do montado que existem. */
    public int umDosPiores() {
        for (int indice = 0; indice < distancia.length; indice++) {
            if (distancia[indice] == numeroDeDeus()) {
                return indice;
            }
        }
        return -1;
    }

    /**
     * Monta o cubo descendo a tabela: a cada passo escolhe um giro que deixe o cubo
     * mais perto. Como a tabela e exata, nunca ha volta atras.
     */
    public List<Giro> monta(Cubo cubo) {
        if (distancia(cubo) < 0) {
            throw new IllegalArgumentException("este cubo nao se monta com estes giros");
        }
        List<Giro> passos = new ArrayList<>();
        Cubo atual = cubo;
        while (!atual.estaMontado()) {
            int daqui = distancia(atual);
            for (Giro giro : giros) {
                Cubo tentativa = atual.gira(giro);
                if (distancia(tentativa) == daqui - 1) {
                    passos.add(giro);
                    atual = tentativa;
                    break;
                }
            }
            if (passos.size() > numeroDeDeus()) {
                throw new IllegalStateException("a tabela nao esta descendo");
            }
        }
        return passos;
    }
}
