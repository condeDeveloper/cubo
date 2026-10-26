package br.com.conde.cubo;

import java.util.List;

/**
 * Os giros de face que mexem no cubo 2x2 com um canto pregado: U, R e F, cada um
 * de um quarto de volta, meia volta e tres quartos.
 *
 * <p>Tres faces bastam. As outras tres mexeriam o canto pregado, e o resultado
 * seria o mesmo cubo girado por inteiro na mao.
 */
public final class Giro {

    private static final int[][] BASE_CP = {
        {3, 0, 1, 2, 4, 5, 6},
        {4, 1, 2, 0, 6, 5, 3},
        {1, 5, 2, 3, 0, 4, 6},
    };

    private static final int[][] BASE_CO = {
        {0, 0, 0, 0, 0, 0, 0},
        {2, 0, 0, 1, 1, 0, 2},
        {1, 2, 0, 0, 2, 1, 0},
    };

    private static final String[] FACES = {"U", "R", "F"};
    private static final String[] SUFIXOS = {"", "2", "'"};

    /** Os nove giros de meia volta e de quarto de volta. */
    public static final List<Giro> TODOS = monta();

    /** So os seis de um quarto de volta, que e a outra maneira de contar jogada. */
    public static final List<Giro> QUARTOS = TODOS.stream()
            .filter(giro -> !giro.nome().endsWith("2")).toList();

    /** So os giros de U e de R, o famoso par de duas faces. */
    public static final List<Giro> DUAS_FACES = TODOS.stream()
            .filter(giro -> giro.face() < 2).toList();

    private final String nome;
    private final int face;
    private final int quartos;
    private final int[] cp;
    private final int[] co;

    private Giro(String nome, int face, int quartos, int[] cp, int[] co) {
        this.nome = nome;
        this.face = face;
        this.quartos = quartos;
        this.cp = cp;
        this.co = co;
    }

    private static List<Giro> monta() {
        Giro[] giros = new Giro[9];
        for (int face = 0; face < 3; face++) {
            int[] cp = BASE_CP[face];
            int[] co = BASE_CO[face];
            int[] acumuladoCp = cp;
            int[] acumuladoCo = co;
            for (int quartos = 1; quartos <= 3; quartos++) {
                if (quartos > 1) {
                    int[][] composto = compoe(acumuladoCp, acumuladoCo, cp, co);
                    acumuladoCp = composto[0];
                    acumuladoCo = composto[1];
                }
                giros[face * 3 + quartos - 1] = new Giro(FACES[face] + SUFIXOS[quartos - 1],
                        face, quartos, acumuladoCp, acumuladoCo);
            }
        }
        return List.of(giros);
    }

    /** O giro que e "primeiro A, depois B". */
    private static int[][] compoe(int[] cpA, int[] coA, int[] cpB, int[] coB) {
        int[] cp = new int[Cubo.CANTOS];
        int[] co = new int[Cubo.CANTOS];
        for (int lugar = 0; lugar < Cubo.CANTOS; lugar++) {
            cp[lugar] = cpA[cpB[lugar]];
            co[lugar] = (coA[cpB[lugar]] + coB[lugar]) % 3;
        }
        return new int[][] {cp, co};
    }

    public String nome() {
        return nome;
    }

    /** 0 para U, 1 para R, 2 para F. */
    public int face() {
        return face;
    }

    /** Quantos quartos de volta: 1, 2 ou 3. */
    public int quartos() {
        return quartos;
    }

    public int[] cp() {
        return cp;
    }

    public int[] co() {
        return co;
    }

    /** O giro que desfaz este. */
    public Giro inverso() {
        return TODOS.get(face * 3 + (4 - quartos) - 1);
    }

    public static Giro de(String nome) {
        for (Giro giro : TODOS) {
            if (giro.nome.equals(nome)) {
                return giro;
            }
        }
        throw new IllegalArgumentException("giro que nao existe: " + nome);
    }

    /** Uma sequencia escrita, do tipo "R U F' R2". */
    public static List<Giro> sequencia(String texto) {
        if (texto.isBlank()) {
            return List.of();
        }
        return java.util.Arrays.stream(texto.trim().split("\\s+")).map(Giro::de).toList();
    }

    public static String escrita(List<Giro> giros) {
        return String.join(" ", giros.stream().map(Giro::nome).toList());
    }

    @Override
    public String toString() {
        return nome;
    }
}
