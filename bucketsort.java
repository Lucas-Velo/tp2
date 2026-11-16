import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class bucketsort {

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

        // Construtor
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

    public static void main(String[] args) {
        String filePath = "/tmp/veiculos.csv";
        Veiculo[] veiculos = new Veiculo[10000];
        Scanner sc = new Scanner(System.in);
        int quantidade = 0;
        
        // Lendo arquivo e preenchendo o vetor geral
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); 
            
            while ((line = br.readLine()) != null) {
                veiculos[quantidade] = new Veiculo();
                veiculos[quantidade].parseVeiculo(line);
                quantidade++;
            }
        } catch (IOException e) {
            System.err.println("houve um erro " + e.getMessage());
        }

        Veiculo[] veiculosFiltrados = new Veiculo[1000];
        int qtdFiltrados = 0;

        // Leitura dos IDs na entrada padrao PS
        while (sc.hasNextInt()) {
            int idBusca = sc.nextInt();
            
            if (idBusca == -1) {
                break; 
            }
            
            for (int i = 0; i < quantidade; i++) {
                if (veiculos[i].getId() == idBusca) {
                    veiculosFiltrados[qtdFiltrados] = veiculos[i];
                    qtdFiltrados++;
                    break;
                }
            }
        }

        // bucketsort

        // Criando 10 baldes. Sendo conservador, cada balde pode ter até 'qtdFiltrados' elementos
        Veiculo[][] baldes = new Veiculo[10][qtdFiltrados];
        int[] contadorBaldes = new int[10]; // controlar quantos veículos em cada balde

        // distribuindo os veículos filtrados em seus respectivos baldes
        for (int i = 0; i < qtdFiltrados; i++) {
            Veiculo v = veiculosFiltrados[i];
            
            // Calculando a posição usando a conta que pedida
            int pos = (int) ((v.getCilindrada() / 8.1) * 10);
            
            // se for 8.1 da rpoblema, pois estoura o vetor (0a9)
            if (pos >= 10) {
                pos = 9; 
            }
            
            // Coloca o veículo no balde correto e aumenta a contagem daquele balde
            baldes[pos][contadorBaldes[pos]] = v;
            contadorBaldes[pos]++;
        }

        // Ordenando cada balde utilizando Ordenação por Inserção
        for (int b = 0; b < 10; b++) {
            int qtdNoBalde = contadorBaldes[b];
            
            // Inserção igual a questão anterior, mas olhando apenas para os itens deste balde e comparando 'cilindrada'
            for (int i = 1; i < qtdNoBalde; i++) {
                Veiculo temp = baldes[b][i];
                double cilTemp = temp.getCilindrada(); // Chave de comparação numérica
                int j = i - 1;
                
                while (j >= 0 && baldes[b][j].getCilindrada() > cilTemp) {
                    baldes[b][j + 1] = baldes[b][j];
                    j--;
                }
                baldes[b][j + 1] = temp;
            }
        }

        // Reagrupando (concatenando) todos os baldes de volta no vetor filtrado original
        int indiceRegresso = 0;
        for (int b = 0; b < 10; b++) {
            for (int i = 0; i < contadorBaldes[b]; i++) {
                veiculosFiltrados[indiceRegresso] = baldes[b][i];
                indiceRegresso++;
            }
        }

        // imprime os veículos já ordenados 
        for (int i = 0; i < qtdFiltrados; i++) {
            System.out.println(veiculosFiltrados[i].format());
        }

        sc.close();
    }
}