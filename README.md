# cubo

O cubo 2x2 varrido inteiro em Java 21: os **3.674.160** estados, a distância de cada um até o
cubo montado, e o número de Deus medido em vez de citado.

O 2x2 é só cantos: oito peças, nada de centro nem de meio. Como não há centro para servir de
referência, girar o cubo inteiro na mão não muda nada do problema, então um canto fica pregado
e sobram três faces para girar. Com isso o cubo todo cabe num `byte[]` de 3,6 milhões de
posições, e daí sai qualquer pergunta: o pior caso, a média, quantos cubos são fáceis, e o que
acontece quando você embaralha com poucos giros.

## O que tem aqui

- `Cubo`: onde está cada canto e quanto ele está torcido. Sete cantos, porque o oitavo está
  pregado.
- `Giro`: U, R e F, cada um de um quarto de volta, meia volta e três quartos. As meias voltas
  e as inversas saem da composição da base, não de tabelas escritas à mão.
- `Indice`: o cubo virado num número de 0 a 3.674.159, pelo código de Lehmer da ordem dos
  cantos mais as torções na base 3. Sem isso não há varredura.
- `Varredura`: a busca em largura que alcança o cubo inteiro, com a distância de cada estado.
- `Juiz`: a regra que decide, sem girar nada, se um cubo é montável.
- `Medidor`: as tabelas.

## Como rodar

```bash
mvn -B verify
mvn -q exec:java -Dexec.args="varredura"     # o cubo inteiro, nos dois jeitos de contar
mvn -q exec:java -Dexec.args="distribuicao"  # quantos cubos a cada distância
mvn -q exec:java -Dexec.args="regra"         # a regra da soma das torções
mvn -q exec:java -Dexec.args="duasfaces"     # o que dá para alcançar só com U e R
mvn -q exec:java -Dexec.args="montagem"      # montar o pior cubo, e embaralhar de verdade
```

## Seis achados

### 1. Onze giros, e isso está medido

```
jeito de contar    giros  estados  numero de Deus  media  piores
meia volta = 1         9  3674160              11  8.756    2644
meia volta = 2         6  3674160              14 10.666     276
```

Contando meia volta como um giro só, **nenhum cubo 2x2 está a mais de 11 giros do montado**, e
exatamente 2.644 estão a 11. Contando meia volta como dois, o pior está a 14, e são 276.

Não é um limite esperto nem uma estimativa: é o maior número da tabela, e a tabela tem uma
casa por cubo que existe.

### 2. Cubo fácil quase não existe

```
giros  meia volta = 1  meia volta = 2
    0               1               1
    1               9               6
    2              54              27
    3             321             120
    4            1847             534
    5            9992            2256
    6           50136            8969
    7          227536           33058
    8          870072          114149
    9         1887748          360508
   10          623800          930588
   11            2644         1350852
   12               -          782536
   13               -           90280
   14               -             276
```

**2.232** cubos estão a quatro giros ou menos. De 3.674.160. São 0,06%.

E 2.511.548 deles, ou **68%**, estão a exatamente 9 ou 10 giros. A distribuição não é uma
ladeira: é um paredão. Pegar um cubo embaralhado e achar que ele "parece quase montado" é quase
sempre ilusão de óptica.

### 3. Duas faces não bastam, e o pouco que sobra fica mais difícil

Girando só U e R, sem nunca tocar em F:

```
giros: 6 | estados alcancados: 29160 de 3674160 (0.79%) | numero de Deus: 14 | media: 9.794
```

Duas faces alcançam **0,79%** do cubo. O resto fica fora do alcance para sempre, não importa
quanto tempo se gire.

E repare no número de Deus: **14**, contra os 11 do cubo inteiro. Menos de um por cento dos
estados, e o pior caso é pior. Tirar uma ferramenta da caixa não encurta o caminho; alonga o
que ainda dá para fazer.

### 4. Uma linha de conta decide o que a varredura demorou 3,6 milhões de estados para dizer

```
arranjos de canto que existem: 11022480
a regra aceita: 3674160 (um terco)
a varredura alcancou: 3674160
iguais? sim

caminhada de 2000000 giros ao acaso: a soma das torcoes saiu dos multiplos de 3 0 vezes
```

Todo giro de face torce os cantos em grupo: a soma das torções anda sempre um múltiplo de 3.
Então essa soma, vista módulo 3, é a mesma do começo ao fim, e no cubo montado ela vale zero.

Isso dá um juiz de uma linha. Dos 11.022.480 jeitos de pôr sete cantos com sete torções,
exatamente **um terço** se monta, e é exatamente o que a varredura alcança. Dois milhões de
giros ao acaso depois, a soma nunca saiu da linha.

É o que acontece quando alguém desmonta o cubo e remonta com um canto torto:

```
cubo com um canto torcido: URF=URF1 UFL=UFL0 ULB=ULB0 UBR=UBR0 DFR=DFR0 DLF=DLF0 DRB=DRB0
  a regra diz: nao da
  a varredura diz: nem entra na tabela
```

### 5. Embaralhar não é sortear

```
giros no embaralho  media para montar  do pior caso  esperado se fosse sorteio
                 5              3.261             0                      14.4
                10              5.703             0                      14.4
                15              7.340             0                      14.4
                20              8.165             7                      14.4
                30              8.661             8                      14.4
                50              8.749            10                      14.4
               100              8.752            18                      14.4
```

Vinte mil cubos embaralhados por vez. A média sobre **todos** os cubos que existem é 8,756.

Com 20 giros ao acaso, a média é 8,165: o embaralho ainda está entregando cubo fácil demais.
Com 30, chega a 8,661, e mesmo assim só 8 dos 20.000 caem no pior caso, quando um sorteio de
verdade daria 14. Precisa de uns 50 giros para a média encostar, e de mais que isso para a
cauda encostar.

Quem embaralha um 2x2 com meia dúzia de giros não está sorteando um cubo: está sorteando um
cubo fácil.

### 6. Desembaralhar custa o mesmo que embaralhar

Nada na varredura garante isso. Ela só anda para a frente, a partir do cubo montado, e nunca
pergunta nada sobre o caminho de volta.

O teste pega os **3.674.160** cubos, um por um, calcula o inverso de cada um e compara as duas
distâncias. Nenhuma diferença. Se alguma das três tabelas de giro estivesse torta num canto,
essa conta denunciaria, e é por ela que eu confio no resto dos números deste README.

## Montando o pior cubo que existe

```
cubo: URF=URF1 UFL=UFL0 ULB=ULB1 UBR=UBR0 DFR=DFR2 DLF=DLF1 DRB=DRB1
solucao em 11 giros: U R U2 F R' F2 R' U R' U F2
conferido refazendo do zero: montou
```

A montagem não procura nada: com a tabela exata na mão, basta escolher a cada passo um giro
que deixe o cubo mais perto, e nunca há volta atrás. Onze giros, e o teste refaz os onze a
partir do cubo embaralhado para conferir que ele fecha.

## O que as medidas me corrigiram

**Escrevi a volta do código de Lehmer do jeito que parecia natural, e ela estava errada.**
Tirando os dígitos do número e preenchendo os cantos do fim para o começo, que é a ordem em
que os dígitos saem. Só que o dígito de Lehmer não é "qual canto", é "em que posição da lista
do que ainda não foi usado", e isso só funciona preenchendo do começo para o fim. O teste que
leva os 3.674.160 números a cubo e de volta existe por causa desse erro, e ele teria
contaminado todas as tabelas em silêncio, porque um índice trocado ainda é um índice válido.

**Eu achava que o subgrupo de duas faces fosse a versão fácil do problema.** É menor e é mais
difícil: 0,79% dos estados e o pior caso a 14 giros, contra 11 do cubo inteiro. Faz sentido
depois de medido: menos caminhos saindo de cada cubo significa caminhos mais longos, não mais
curtos. Antes de medir eu teria apostado no contrário.

**Eu embaralhava com 20 ou 30 giros achando que estava sorteando.** A média de 20 giros é
8,165 contra 8,756 do sorteio de verdade, e a cauda é pior: 7 em 20.000 no pior caso, contra
14 esperados. A tabela de convergência não estava no plano do repositório; entrou depois que o
número de 30 giros veio abaixo do esperado e eu fui ver se era erro meu.

**A conferência que mais vale é a que a varredura não podia ter acertado por sorte.** Comparar
o total de estados com 7! vezes 3^6 não prova nada: os dois saem da mesma conta que eu fiz na
cabeça. Comparar a distância de cada cubo com a distância do inverso dele prova, porque a
varredura nunca olhou para inverso nenhum.

## Como está organizado

```
src/main/java/br/com/conde/cubo/
  Cubo.java        onde esta cada canto e quanto ele esta torcido
  Giro.java        U, R e F, com as meias voltas saindo da composicao
  Indice.java      o cubo virado num numero de 0 a 3.674.159
  Varredura.java   a busca em largura que alcanca o cubo inteiro
  Juiz.java        a regra da soma das torcoes
  Medidor.java     as tabelas
src/test/java/br/com/conde/cubo/
  CuboTest, GiroTest, IndiceTest, VarreduraTest, JuizTest, MedidasTest
```

39 testes. O `MedidasTest` prende cada número deste README a uma medida, incluindo as duas
distribuições inteiras e as sete linhas da tabela de embaralho, que são determinísticas porque
a semente está fixada.

## Licença

MIT.
