import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class pilha {
    //tolowercase
    public static String toLowerCaseManual(String str) {
        char[] caracteres = str.toCharArray();
        for (int i = 0; i < caracteres.length; i++) {
            if (caracteres[i] >= 'A' && caracteres[i] <= 'Z') {
                caracteres[i] = (char) (caracteres[i] + 32);
            }
        }
        return new String(caracteres);
    }

    public static class Data {
        private String ano;
        private String mes;
        private String dia;
        private String[] dataSeparada;

        public Data(String data) {
            dataSeparada = data.split("-");
            this.ano = dataSeparada[0];
            this.mes = dataSeparada[1];
            this.dia = dataSeparada[2];
        }

        public String getAno() { return ano; }
        public String getMes() { return mes; }
        public String getDia() { return dia; }

        public String format() {
            return String.format("%s/%s/%s", dia, mes, ano);
        }
    }

    public static class Veiculo {
        private int id;
        private String marca;
        private String modelo;
        private int ano;
        private String categoria;
        private String[] combustivel;
        private int cilindros;
        private double cilindrada;
        private String transmissao;
        private String tracao;
        private double consumoCidade;
        private double consumoEstrada;
        private double co2;
        private boolean turbo;
        private Data dataRegistro;

        public Veiculo() {
        }

        public Veiculo parseVeiculo(String s) {
            String[] vei = s.split(",");

            this.id = Integer.parseInt(vei[0].trim());
            this.marca = vei[1].trim();
            this.modelo = vei[2].trim();
            this.ano = Integer.parseInt(vei[3].trim());
            this.categoria = vei[4].trim();
            this.combustivel = vei[5].split(";");
            this.cilindros = Integer.parseInt(vei[6].trim());
            this.cilindrada = Double.parseDouble(vei[7].trim());
            this.transmissao = vei[8].trim();
            this.tracao = vei[9].trim();
            this.consumoCidade = Double.parseDouble(vei[10].trim());
            this.consumoEstrada = Double.parseDouble(vei[11].trim());
            this.co2 = Double.parseDouble(vei[12].trim());
            this.turbo = Boolean.parseBoolean(vei[13].trim());
            this.dataRegistro = new Data(vei[14].trim());

            return this;
        }

        public int getId() { return id; }
        public String getMarca() { return marca; }
        public String getModelo() { return modelo; }
        public int getAno() { return ano; }
        public String getCategoria() { return categoria; }
        public String[] getCombustivel() { return combustivel; }
        public int getCilindros() { return cilindros; }
        public double getCilindrada() { return cilindrada; }
        public String getTransmissao() { return transmissao; }
        public String getTracao() { return tracao; }
        public double getConsumoCidade() { return consumoCidade; }
        public double getConsumoEstrada() { return consumoEstrada; }
        public double getCo2() { return co2; }
        public boolean getTurbo() { return turbo; }
        public Data getDataRegistro() { return dataRegistro; }

        public String format() {
            return "[" + id + " ## " + marca + " ## " + modelo + " ## " + ano + " ## "
                    + categoria + " ## [" + String.join(", ", combustivel) + "] ## " + cilindros + " ## " + cilindrada
                    + " ## " + transmissao + " ## " + tracao + " ## " + consumoCidade + " ## "
                    + consumoEstrada + " ## " + co2 + " ## " + turbo + " ## " + dataRegistro.format() + "]";
        }
    }

    // Celula pra pilha
    public static class Celula {
        public Veiculo elemento; // O dado
        public Celula prox;      // O ponteiro para o elemento debaixo na pilha

        public Celula(Veiculo elemento) {
            this.elemento = elemento;
            this.prox = null;
        }
    }

    public static class PilhaFlexivel {
        private Celula topo; // pilha só precisa conhecer quem é o topo

        public PilhaFlexivel() {
            topo = null;
        }

        // Empilhar (Inserir no Topo)
        public void empilhar(Veiculo x) {
            Celula tmp = new Celula(x); // Cria novo nó
            tmp.prox = topo;           
            topo = tmp;                
            tmp = null;
        }

        // Desempilhar
        public Veiculo desempilhar() {
            if (topo == null) return null; // Se a pilha estiver vazia
            
            Veiculo resp = topo.elemento; // Salva o veículo que será removido para poder retornar
            Celula tmp = topo;
            topo = topo.prox;           
            tmp.prox = null;           
            tmp = null;
            
            return resp;
        }

        // Mostra os elementos da pilha, começando do topo até chegar na base
        public void mostrar() {
            // Um laço simples que vai 'descendo' os ponteiros até achar nulo
            for (Celula i = topo; i != null; i = i.prox) {
                System.out.println(i.elemento.format());
            }
        }
    }

    // Função para buscar veículo pelo ID
    public static Veiculo buscarVeiculo(Veiculo[] bd, int total, int id) {
        for (int i = 0; i < total; i++) {
            if (bd[i].getId() == id) {
                return bd[i];
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String filePath = "/tmp/veiculos.csv";
        Veiculo[] bancoVeiculos = new Veiculo[10000];
        Scanner sc = new Scanner(System.in);
        int quantidadeBd = 0;
        
        // Carregando dados do arquivo CSV para a memória
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                bancoVeiculos[quantidadeBd] = new Veiculo();
                bancoVeiculos[quantidadeBd].parseVeiculo(line);
                quantidadeBd++;
            }
        } catch (IOException e) {
            System.err.println("houve um erro " + e.getMessage());
        }

        // Criando a nossa nova estrutura
        PilhaFlexivel pilha = new PilhaFlexivel();

        // Lendo IDs e empilhando até achar o -1
        while (sc.hasNext()) {
            String entrada = sc.next();
            if (entrada.equals("-1")) {
                break;
            }
            int idBusca = Integer.parseInt(entrada);
            Veiculo encontrado = buscarVeiculo(bancoVeiculos, quantidadeBd, idBusca);
            if (encontrado != null) {
                pilha.empilhar(encontrado);
            }
        }

        if (sc.hasNextInt()) {
            int numComandos = sc.nextInt();
            
            for (int i = 0; i < numComandos; i++) {
                String comando = sc.next();

                if (comando.equals("I")) { // Empilhar (Inserir)
                    int idBusca = sc.nextInt();
                    Veiculo v = buscarVeiculo(bancoVeiculos, quantidadeBd, idBusca);
                    if (v != null) {
                        pilha.empilhar(v);
                    }
                } 
                else if (comando.equals("R")) { // Desempilhar (Remover)
                    Veiculo removido = pilha.desempilhar();
                    if (removido != null) {
                        System.out.println("(R) " + removido.getMarca() + " " + removido.getModelo());
                    }
                }
            }
        }

        // imprime todos os registros da Pilha a partir do topo
        pilha.mostrar();
        
        sc.close();
    }
}