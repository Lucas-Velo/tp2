import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Leitor{

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
            // divide o texto no vetor a cada virgula
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

            // retorna o objeto com todos os atributos
            return this;
        }

        // get da classe Veiculo
        public int getId() {
            return id;
        }

        public String getMarca() {
            return marca;
        }

        public String getModelo() {
            return modelo;
        }

        public int getAno() {
            return ano;
        }

        public String getCategoria() {
            return categoria;
        }

        public String[] getCombustivel() {
            return combustivel;
        }

        public int getCilindros() {
            return cilindros;
        }

        public double getCilindrada() {
            return cilindrada;
        }

        public String getTransmissao() {
            return transmissao;
        }

        public String getTracao() {
            return tracao;
        }

        public double getConsumoCidade() {
            return consumoCidade;
        }

        public double getConsumoEstrada() {
            return consumoEstrada;
        }

        public double getCo2() {
            return co2;
        }

        public boolean getTurbo() {
            return turbo;
        }

        public Data getDataRegistro() {
            return dataRegistro;
        }
// format 
        public String format() {
            return "[" + id + " ## " + marca + " ## " + modelo + " ## " + ano + " ## "
                    + categoria + " ## [" + String.join(", ", combustivel) + "] ## " + cilindros + " ## " + cilindrada
                    + " ## "
                    + transmissao + " ## " + tracao + " ## " + consumoCidade + " ## "
                    + consumoEstrada + " ## " + co2 + " ## " + turbo + " ## " + dataRegistro.format() + "]";
        }

    }

    public static void main(String[] args) {
       // declaracoes
        String filePath = "/tmp/veiculos.csv";
        Veiculo[] veiculos = new Veiculo[1000];
        Scanner sc = new Scanner(System.in);
        int quantidade = 0;
        // lendo arquivo 
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
        // preenche o vetor com os objetos do csv
            while ((line = br.readLine()) != null) {
                // aloca espaço na memoria
                veiculos[quantidade] = new Veiculo();
                // preenche com os valores
                veiculos[quantidade].parseVeiculo(line);
                quantidade++;
            }
        // pesquisa sequencial
            while (sc.hasNextInt()) {
                int idBusca = sc.nextInt();

                if (idBusca == -1) {
                    break;
                }
                // procura os elementos certo e imprime na tela (break para sair do loop wquando achar)
                for (int i = 0; i < quantidade; i++) {
                    if (veiculos[i].getId() == idBusca) {
                        System.out.println(veiculos[i].format());
                        break;
                    }
                }
            }
           // se nao conseguir abrir o arquivo vai falar que deu erro
        } catch (IOException e) {
            System.err.println("houve um erro " + e.getMessage());
        }
        
            sc.close();
    }
}
