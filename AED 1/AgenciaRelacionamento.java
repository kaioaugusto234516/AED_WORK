import java.io.*;
import java.util.Scanner;
import java.util.Random;

// --------------------------------------------------------
// TAD ListaIdentificadores (Com Sentinela e Tail)
// --------------------------------------------------------
class No {
    int valor;
    No proximo;

    public No(int valor) {
        this.valor = valor;
        this.proximo = null;
    }
}

class ListaIdentificadores {
    private No cabeca;
    private No cauda;
    private int tamanho;

    public ListaIdentificadores() {
        cabeca = new No(-1);
        cauda = cabeca;
        tamanho = 0;
    }

    public void inserir(int id) {
        No novo = new No(id);
        cauda.proximo = novo;
        cauda = novo;
        tamanho++;
    }

    public boolean contem(int id) {
        No atual = cabeca.proximo;
        while (atual != null) {
            if (atual.valor == id)
                return true;
            atual = atual.proximo;
        }
        return false;
    }

    public No getPrimeiro() {
        return cabeca.proximo;
    }

    public int getTamanho() {
        return tamanho;
    }
}

// --------------------------------------------------------
// TAD Candidato
// --------------------------------------------------------
class Candidato {
    private int id;
    private String nome;
    private String estado;
    private char sexo;
    private char sexoInteresse;
    private ListaIdentificadores interesses;
    private ListaIdentificadores potenciaisCompanheiros;

    public Candidato(int id, String nome, String estado, char sexo, char sexoInteresse,
            ListaIdentificadores interesses) {
        this.id = id;
        this.nome = nome;
        this.estado = estado;
        this.sexo = sexo;
        this.sexoInteresse = sexoInteresse;
        this.interesses = interesses;
        this.potenciaisCompanheiros = new ListaIdentificadores();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEstado() {
        return estado;
    }

    public char getSexo() {
        return sexo;
    }

    public char getSexoInteresse() {
        return sexoInteresse;
    }

    public ListaIdentificadores getInteresses() {
        return interesses;
    }

    public boolean verificaPotencialCompanheiro(Candidato v, int grauMinimo) {
        if (!this.estado.equals(v.estado))
            return false;

        boolean atendeSexoU = (this.sexoInteresse == 'I' || this.sexoInteresse == v.sexo);
        boolean atendeSexoV = (v.sexoInteresse == 'I' || v.sexoInteresse == this.sexo);
        if (!(atendeSexoU && atendeSexoV))
            return false;

        return calcularGrauInteresse(v) >= grauMinimo;
    }

    private int calcularGrauInteresse(Candidato v) {
        int grauTotal = 0;
        No atual = this.interesses.getPrimeiro();
        while (atual != null) {
            if (v.temInteresse(atual.valor)) {
                grauTotal += obterPeso(atual.valor);
            }
            atual = atual.proximo;
        }
        return grauTotal;
    }

    private int obterPeso(int area) {
        if (area == 1 || area == 3 || area == 4)
            return 1;
        if (area == 2 || area == 6 || area == 7)
            return 2;
        if (area == 5 || area == 8)
            return 3;
        return 0;
    }

    public void inserePotencialCompanheiro(Candidato v) {
        this.potenciaisCompanheiros.inserir(v.getId());
    }

    public int numeroCompanheiros() {
        return this.potenciaisCompanheiros.getTamanho();
    }

    public ListaIdentificadores listaCompanheiros() {
        return this.potenciaisCompanheiros;
    }

    public boolean temInteresse(int idArea) {
        return this.interesses.contem(idArea);
    }

    public boolean reside(String sigla) {
        return this.estado.equals(sigla);
    }
}

// --------------------------------------------------------
// Classe Principal: Interação, Algoritmos e Leitura/Escrita
// --------------------------------------------------------
public class AgenciaRelacionamento {
    private static Candidato[] candidatos;
    private static int totalCandidatos = 0;
    private static int grauDesejado = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Deseja gerar um novo ficheiro 'candidatos.txt' de teste? (S/N)");
        if (sc.nextLine().trim().toUpperCase().startsWith("S")) {
            gerarArquivoCandidatos();
        }

        carregarCandidatos();

        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- MENU DA AGÊNCIA ---");
            System.out.println("1. Inserir/atualizar o grau de interesse desejado");
            System.out.println("2. Imprimir potenciais companheiros para um candidato");
            System.out.println("3. Imprimir os K candidatos com mais potenciais companheiros");
            System.out.println("4. Imprimir candidatos sem nenhum companheiro encontrado");
            System.out.println("5. Imprimir candidatos com interesse numa área específica");
            System.out.println("6. Imprimir candidatos que residem num estado");
            System.out.println("7. Listar todos os candidatos (Visualização Formatada)");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());
                processarOpcao(opcao, sc);
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida.");
            }
        }
        sc.close();
    }

    private static void processarOpcao(int opcao, Scanner sc) {
        switch (opcao) {
            case 1:
                System.out.print("Introduza o novo grau de interesse mínimo: ");
                grauDesejado = Integer.parseInt(sc.nextLine());
                calcularTodosPares();
                System.out.println("Grau atualizado e pares recalculados.");
                break;
            case 2:
                System.out.print("Introduza o ID do candidato (0 a " + (totalCandidatos - 1) + "): ");
                int idBusca = Integer.parseInt(sc.nextLine());
                if (idBusca >= 0 && idBusca < totalCandidatos) {
                    Candidato c = candidatos[idBusca];
                    System.out.println("Potenciais companheiros para " + c.getNome() + ":");
                    No atual = c.listaCompanheiros().getPrimeiro();
                    while (atual != null) {
                        System.out.println("- ID: " + atual.valor + " | Nome: " + candidatos[atual.valor].getNome());
                        atual = atual.proximo;
                    }
                    System.out.println("Total encontrados: " + c.numeroCompanheiros());
                } else {
                    System.out.println("Candidato não encontrado.");
                }
                break;
            case 3:
                System.out.print("Introduza o valor de K: ");
                int k = Integer.parseInt(sc.nextLine());
                imprimirTopK(k);
                break;
            case 4:
                System.out.println("Candidatos sem potenciais companheiros:");
                for (int i = 0; i < totalCandidatos; i++) {
                    if (candidatos[i].numeroCompanheiros() == 0) {
                        System.out.println("ID: " + candidatos[i].getId() + " | Nome: " + candidatos[i].getNome());
                    }
                }
                break;
            case 5:
                System.out.print("Introduza o código da área de interesse (1 a 8): ");
                int area = Integer.parseInt(sc.nextLine());
                for (int i = 0; i < totalCandidatos; i++) {
                    if (candidatos[i].temInteresse(area)) {
                        System.out.println("ID: " + candidatos[i].getId() + " | Nome: " + candidatos[i].getNome());
                    }
                }
                break;
            case 6:
                System.out.print("Introduza a sigla do estado (ex: MG): ");
                String uf = sc.nextLine().toUpperCase();
                for (int i = 0; i < totalCandidatos; i++) {
                    if (candidatos[i].reside(uf)) {
                        System.out.println("ID: " + candidatos[i].getId() + " | Nome: " + candidatos[i].getNome());
                    }
                }
                break;
            case 7:
                listarCandidatosFormatados();
                break;
            case 0:
                System.out.println("A encerrar o sistema...");
                break;
            default:
                System.out.println("Opção inexistente.");
        }
    }

    private static void calcularTodosPares() {
        for (int i = 0; i < totalCandidatos; i++) {
            candidatos[i] = recriarCandidatoLimpo(candidatos[i]);
            for (int j = 0; j < totalCandidatos; j++) {
                if (i != j) {
                    if (candidatos[i].verificaPotencialCompanheiro(candidatos[j], grauDesejado)) {
                        candidatos[i].inserePotencialCompanheiro(candidatos[j]);
                    }
                }
            }
        }
    }

    private static Candidato recriarCandidatoLimpo(Candidato c) {
        ListaIdentificadores interessesCopia = new ListaIdentificadores();
        for (int a = 1; a <= 8; a++) {
            if (c.temInteresse(a))
                interessesCopia.inserir(a);
        }
        return new Candidato(c.getId(), c.getNome(), c.getEstado(), c.getSexo(), c.getSexoInteresse(), interessesCopia);
    }

    private static void imprimirTopK(int k) {
        if (totalCandidatos == 0) {
            System.out.println("Nenhum candidato carregado.");
            return;
        }

        int[] ids = new int[totalCandidatos];
        for (int i = 0; i < totalCandidatos; i++)
            ids[i] = i;

        for (int i = 0; i < totalCandidatos - 1; i++) {
            for (int j = 0; j < totalCandidatos - i - 1; j++) {
                if (candidatos[ids[j]].numeroCompanheiros() < candidatos[ids[j + 1]].numeroCompanheiros()) {
                    int temp = ids[j];
                    ids[j] = ids[j + 1];
                    ids[j + 1] = temp;
                }
            }
        }

        System.out.println("Top " + k + " candidatos com mais pares:");
        for (int i = 0; i < Math.min(k, totalCandidatos); i++) {
            System.out.println((i + 1) + "º - ID: " + candidatos[ids[i]].getId() + " | Nome: " +
                    candidatos[ids[i]].getNome() + " | Pares: " + candidatos[ids[i]].numeroCompanheiros());
        }
    }

    private static void carregarCandidatos() {
        File ficheiro = new File("candidatos.txt");
        if (!ficheiro.exists()) {
            System.out.println(
                    "AVISO: O ficheiro candidatos.txt não foi encontrado. Escreva 'S' ao iniciar o programa para o gerar.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(ficheiro))) {
            totalCandidatos = Integer.parseInt(br.readLine().trim());
            candidatos = new Candidato[totalCandidatos];

            for (int id = 0; id < totalCandidatos; id++) {
                br.readLine();
                String nome = br.readLine().trim();
                String estado = br.readLine().trim();
                char sexo = br.readLine().trim().charAt(0);
                char sexoInt = br.readLine().trim().charAt(0);
                int numAreas = Integer.parseInt(br.readLine().trim());

                ListaIdentificadores listaInt = new ListaIdentificadores();
                for (int j = 0; j < numAreas; j++) {
                    listaInt.inserir(Integer.parseInt(br.readLine().trim()));
                }
                candidatos[id] = new Candidato(id, nome, estado, sexo, sexoInt, listaInt);
            }
            System.out.println("Ficheiro lido com sucesso. Total cadastros: " + totalCandidatos);
        } catch (IOException e) {
            System.out.println("Erro na leitura de candidatos.txt: " + e.getMessage());
        }
    }

    private static void listarCandidatosFormatados() {
        System.out.println("\n--- LISTA DE CANDIDATOS (FORMATO HUMANO) ---");
        for (int i = 0; i < totalCandidatos; i++) {
            Candidato c = candidatos[i];
            System.out.println("ID: " + c.getId() + " | Nome: " + c.getNome());
            System.out.println(
                    "Estado: " + c.getEstado() + " | Sexo: " + c.getSexo() + " | Procura: " + c.getSexoInteresse());
            System.out.print("Áreas de Interesse: ");

            No atual = c.getInteresses().getPrimeiro();
            while (atual != null) {
                System.out.print(obterNomeArea(atual.valor) + (atual.proximo != null ? ", " : ""));
                atual = atual.proximo;
            }
            System.out.println("\n--------------------------------------------");
        }
    }

    private static String obterNomeArea(int idArea) {
        switch (idArea) {
            case 1:
                return "ESPORTES";
            case 2:
                return "ARTES";
            case 3:
                return "MÚSICA";
            case 4:
                return "CINEMA";
            case 5:
                return "TECNOLOGIA";
            case 6:
                return "ANIMAIS";
            case 7:
                return "GASTRONOMIA";
            case 8:
                return "CIÊNCIAS";
            default:
                return "DESCONHECIDO";
        }
    }

    private static void gerarArquivoCandidatos() {
        String[] nomesM = { "João", "Carlos", "Pedro", "Ricardo", "Tiago", "Miguel", "Bruno", "Rui" };
        String[] nomesF = { "Maria", "Ana", "Lúcia", "Sofia", "Beatriz", "Inês", "Catarina", "Rita" };
        String[] apelidos = { "Silva", "Santos", "Costa", "Oliveira", "Martins", "Ferreira", "Almeida" };
        String[] estados = { "MG", "SP", "RJ", "ES" };
        char[] interesses = { 'M', 'F', 'I' };
        Random rand = new Random();

        File ficheiro = new File("candidatos.txt");

        try (PrintWriter pw = new PrintWriter(new FileWriter(ficheiro))) {
            int n = 20;
            pw.println(n);
            for (int i = 0; i < n; i++) {
                pw.println("****");
                char sexo = rand.nextBoolean() ? 'M' : 'F';
                String nome = (sexo == 'M' ? nomesM[rand.nextInt(nomesM.length)] : nomesF[rand.nextInt(nomesF.length)])
                        + " " + apelidos[rand.nextInt(apelidos.length)];

                pw.println(nome);
                pw.println(estados[rand.nextInt(estados.length)]);
                pw.println(sexo);
                pw.println(interesses[rand.nextInt(interesses.length)]);

                int numAreas = rand.nextInt(4) + 1;
                pw.println(numAreas);
                int[] areasEscolhidas = new int[numAreas];
                for (int j = 0; j < numAreas; j++) {
                    int area;
                    boolean repetido;
                    do {
                        repetido = false;
                        area = rand.nextInt(8) + 1;
                        for (int k = 0; k < j; k++)
                            if (areasEscolhidas[k] == area)
                                repetido = true;
                    } while (repetido);
                    areasEscolhidas[j] = area;
                    pw.println(area);
                }
            }
            System.out.println("\n--------------------------------------------------");
            System.out.println("Ficheiro 'candidatos.txt' gerado (formato bruto obrigatório) com sucesso!");
            System.out.println("CAMINHO EXATO: " + ficheiro.getAbsolutePath());
            System.out.println("--------------------------------------------------\n");
        } catch (IOException e) {
            System.out.println("Erro ao gerar ficheiro: " + e.getMessage());
        }
    }
}