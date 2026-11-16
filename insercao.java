import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class insercao {

    //toLowerCase
    public static String toLowerCaseManual(String str) {
        char[] caracteres = str.toCharArray();
        for (int i = 0; i < caracteres.length; i++) {
            // Verifica se é uma letra maiúscula (entre A e Z)
            if (caracteres[i] >= 'A' && caracteres[i] <= 'Z') {
                // diferenca de minúscula e maúiscula
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

        // Construtor
        public Data(String data) {
            dataSeparada = data.split("-");
            this.ano = dataSeparada[0];
            this.mes = dataSeparada[1];
            this.dia = dataSeparada[2]; 
        }

        // Get da classe Data
        public String getAno() {
            return ano;
        }

        public String getMes() {
            return mes;
        }

        public String getDia() {
            return dia;
        }

        // data no modelo certo
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

        // Construtor vazio necessário para inicializar antes do parse
        public Veiculo() {
        }

        // Parse veiculo (trasnforma texto em objeto)
        public Veiculo parseVeiculo(String s) {
            // ignora as vírgulas dentro de aspas duplas
            String[] vei = s.split(",");

            this.id = Integer.parseInt(vei[0].trim());
            this.marca = vei[1].trim();
            this.modelo = vei[2].trim();
            this.ano = Integer.parseInt(vei[3].trim());
            this.categoria = vei[4].trim();
            // separa caso tenha mais de 1 combustivel com ; pq é String[]
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

        // gets da classe Veiculo
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

        // format 
        public String format() {
            return "[" + id + " ## " + marca + " ## " + modelo + " ## " + ano + " ## "
                    + categoria + " ## [" + String.join(", ", combustivel) + "] ## " + cilindros + " ## " + cilindrada
                    + " ## " + transmissao + " ## " + tracao + " ## " + consumoCidade + " ## "
                    + consumoEstrada + " ## " + co2 + " ## " + turbo + " ## " + dataRegistro.format() + "]";
        }
    }

    public static void main(String[] args) {
        // declaracoes
        String filePath = "/tmp/veiculos.csv";
        Veiculo[] veiculos = new Veiculo[10000];
        Scanner sc = new Scanner(System.in);
        int quantidade = 0;
        
        // Lendo arquivo e preenchendo o vetor geral
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Pula o cabeçalho
            
            while ((line = br.readLine()) != null) {
                veiculos[quantidade] = new Veiculo();
                veiculos[quantidade].parseVeiculo(line);
                quantidade++;
            }
        } catch (IOException e) {
            System.err.println("houve um erro " + e.getMessage());
        }

        // Vetor auxiliar para guardar apenas os veículos que o usuário digitar os ids
        Veiculo[] veiculosFiltrados = new Veiculo[1000];
        int qtdFiltrados = 0;

        // Leitura dos IDs na entrada padrão PS
        while (sc.hasNextInt()) {
            int idBusca = sc.nextInt();
            
            if (idBusca == -1) {
                break; // -1 indica o fim da entrada
            }
            
            // Procura o elemento e salva no vetor de filtrados em vez de imprimir direto
            for (int i = 0; i < quantidade; i++) {
                if (veiculos[i].getId() == idBusca) {
                    veiculosFiltrados[qtdFiltrados] = veiculos[i];
                    qtdFiltrados++;
                    break;
                }
            }
        }

        // Ordenação por Inserção pela "marca"
        for (int i = 1; i < qtdFiltrados; i++) {
            Veiculo temp = veiculosFiltrados[i];
            
            String marcaTemp = toLowerCaseManual(temp.getMarca());
            int j = i - 1;

            // Move os elementos que são MAIORES que a marcaTemp uma posição para frente
            // O compareTo devolve valor > 0 se a primeira string for alfabeticamente maior
            while (j >= 0 && toLowerCaseManual(veiculosFiltrados[j].getMarca()).compareTo(marcaTemp) > 0) {
                veiculosFiltrados[j + 1] = veiculosFiltrados[j];
                j--;
            }
            
            // Insere o elemento atual na sua posição correta
            veiculosFiltrados[j + 1] = temp;
        }

        for (int i = 0; i < qtdFiltrados; i++) {
            System.out.println(veiculosFiltrados[i].format());
        }

        sc.close();
    }
}
