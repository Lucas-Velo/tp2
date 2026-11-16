import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class listadupla {

    //toLowerCase
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

    // lista dupla
    public static class CelulaDupla {
        public Veiculo elemento;
        public CelulaDupla prox, ant;

        // Construtor para o nó "cabeça"
        public CelulaDupla() {
            this(null);
        }

        public CelulaDupla(Veiculo elemento) {
            this.elemento = elemento;
            this.prox = this.ant = null;
        }
    }

    public static class ListaDupla {
        private CelulaDupla primeiro, ultimo;
        private int tamanho; // Ajuda no controle das posições

        public ListaDupla() {
            // Inicializa com uma célula cabeça vazia. Isso facilita inserções/remoções
            primeiro = new CelulaDupla();
            ultimo = primeiro;
            tamanho = 0;
        }

        public void inserirInicio(Veiculo veiculo) {
            CelulaDupla tmp = new CelulaDupla(veiculo);
            tmp.ant = primeiro;
            tmp.prox = primeiro.prox;
            primeiro.prox = tmp;
            
            if (primeiro == ultimo) {
                ultimo = tmp;
            } else {
                tmp.prox.ant = tmp;
            }
            tmp = null;
            tamanho++;
        }

        public void inserirFim(Veiculo veiculo) {
            ultimo.prox = new CelulaDupla(veiculo);
            ultimo.prox.ant = ultimo;
            ultimo = ultimo.prox;
            tamanho++;
        }

        public void inserir(Veiculo veiculo, int pos) {
            if (pos < 0 || pos > tamanho) return;
            
            if (pos == 0) {
                inserirInicio(veiculo);
            } else if (pos == tamanho) {
                inserirFim(veiculo);
            } else {
                // "Caminha" na lista até chegar na posição desejada
                CelulaDupla i = primeiro;
                for (int j = 0; j < pos; j++, i = i.prox);
                
                CelulaDupla tmp = new CelulaDupla(veiculo);
                tmp.ant = i;
                tmp.prox = i.prox;
                tmp.ant.prox = tmp.prox.ant = tmp;
                tmp = i = null;
                tamanho++;
            }
        }

        public Veiculo removerInicio() {
            if (primeiro == ultimo) return null; // Lista vazia
            
            CelulaDupla tmp = primeiro.prox; // Elemento real a ser removido
            Veiculo resp = tmp.elemento;
            
            primeiro.prox = tmp.prox; // Pula o elemento removido
            if (primeiro.prox != null) {
                primeiro.prox.ant = primeiro;
            } else {
                ultimo = primeiro; // Se a lista ficou vazia
            }
            
            tmp.prox = tmp.ant = null; // Isola o nó 
            tmp = null;
            tamanho--;
            return resp;
        }

        public Veiculo removerFim() {
            if (primeiro == ultimo) return null; // Lista vazia
            
            Veiculo resp = ultimo.elemento;
            ultimo = ultimo.ant;         
            ultimo.prox.ant = null;       
            ultimo.prox = null;
            tamanho--;
            return resp;
        }

        public Veiculo remover(int pos) {
            if (primeiro == ultimo || pos < 0 || pos >= tamanho) return null;
            if (pos == 0) return removerInicio();
            if (pos == tamanho - 1) return removerFim();
            
            CelulaDupla i = primeiro.prox;
            for (int j = 0; j < pos; j++, i = i.prox); // Caminha até o alvo
            
            Veiculo resp = i.elemento;
            // Refaz a ligação saltando o elemento 'i'
            i.ant.prox = i.prox;
            i.prox.ant = i.ant;
            i.prox = i.ant = null;
            i = null;
            tamanho--;
            return resp;
        }

        public void mostrar() {
            // Começa no 'primeiro.prox' pois o 'primeiro' é o nosso nó fictício cabeça
            for (CelulaDupla i = primeiro.prox; i != null; i = i.prox) {
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
        
        // Carregando o arquivo
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

        // Nova Estrutura Flexível Dupla
        ListaDupla lista = new ListaDupla();

        //Inserção no fim até achar o -1
        while (sc.hasNext()) {
            String entrada = sc.next();
            if (entrada.equals("-1")) {
                break;
            }
            int idBusca = Integer.parseInt(entrada);
            Veiculo encontrado = buscarVeiculo(bancoVeiculos, quantidadeBd, idBusca);
            if (encontrado != null) {
                lista.inserirFim(encontrado);
            }
        }

        // processando os variados comandos
        if (sc.hasNextInt()) {
            int numComandos = sc.nextInt();
            
            for (int i = 0; i < numComandos; i++) {
                String comando = sc.next();
                int idBusca;
                int pos;
                Veiculo v;

                switch (comando) {
                    case "II":
                        idBusca = sc.nextInt();
                        v = buscarVeiculo(bancoVeiculos, quantidadeBd, idBusca);
                        if (v != null) lista.inserirInicio(v);
                        break;
                        
                    case "I*":
                        pos = sc.nextInt();
                        idBusca = sc.nextInt();
                        v = buscarVeiculo(bancoVeiculos, quantidadeBd, idBusca);
                        if (v != null) lista.inserir(v, pos);
                        break;
                        
                    case "IF":
                        idBusca = sc.nextInt();
                        v = buscarVeiculo(bancoVeiculos, quantidadeBd, idBusca);
                        if (v != null) lista.inserirFim(v);
                        break;
                        
                    case "RI":
                        v = lista.removerInicio();
                        if (v != null) System.out.println("(R) " + v.getMarca() + " " + v.getModelo());
                        break;
                        
                    case "R*":
                        pos = sc.nextInt();
                        v = lista.remover(pos);
                        if (v != null) System.out.println("(R) " + v.getMarca() + " " + v.getModelo());
                        break;
                        
                    case "RF":
                        v = lista.removerFim();
                        if (v != null) System.out.println("(R) " + v.getMarca() + " " + v.getModelo());
                        break;
                }
            }
        }

        // Exibindo os elementos restantes
        lista.mostrar();
        
        sc.close();
    }
}