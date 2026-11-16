import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class alocacaoseq {

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

        public String getAno() { 
            return ano; 
        }
        public String getMes() { 
            return mes; 
        }
        public String getDia() {
            return dia; 
        }

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

    //Alocação sequencial
    public static class ListaVeiculos {
        private Veiculo[] array;
        private int n; // Controla a quantidade de elementos armazenados atualmente

        public ListaVeiculos(int tamanhoMaximo) {
            array = new Veiculo[tamanhoMaximo];
            n = 0;
        }

        // Insere no início e desloca tudo para a direita
        public void inserirInicio(Veiculo veiculo) {
            if (n >= array.length) return; // Segurança caso encha
            for (int i = n; i > 0; i--) {
                array[i] = array[i - 1]; // Remanejando para a direita
            }
            array[0] = veiculo;
            n++;
        }

        // Insere em uma posição específica e desloca o restante para a direita
        public void inserir(Veiculo veiculo, int posicao) {
            if (n >= array.length || posicao < 0 || posicao > n) return;
            for (int i = n; i > posicao; i--) {
                array[i] = array[i - 1]; // Remanejando para a direita
            }
            array[posicao] = veiculo;
            n++;
        }

        // Insere na última posição da Lista
        public void inserirFim(Veiculo veiculo) {
            if (n >= array.length) return;
            array[n] = veiculo;
            n++;
        }

        // Remove do início, desloca tudo para a esquerda e retorna o Veículo removido
        public Veiculo removerInicio() {
            if (n == 0) return null;
            Veiculo resp = array[0];
            n--;
            for (int i = 0; i < n; i++) {
                array[i] = array[i + 1]; // Remanejando para a esquerda
            }
            return resp;
        }

        // Remove de uma posição específica, desloca o restante para a esquerda e retorna
        public Veiculo remover(int posicao) {
            if (n == 0 || posicao < 0 || posicao >= n) return null;
            Veiculo resp = array[posicao];
            n--;
            for (int i = posicao; i < n; i++) {
                array[i] = array[i + 1]; // Remanejando para a esquerda
            }
            return resp;
        }

        // Remove do final da lista e retorna
        public Veiculo removerFim() {
            if (n == 0) return null;
            n--;
            return array[n]; // Ao diminuir 'n' o elemento fica isolado do acesso
        }

        // Função para mostrar todos os itens restantes na lista
        public void mostrar() {
            for (int i = 0; i < n; i++) {
                System.out.println(array[i].format());
            }
        }
    }

    // Função auxiliar para buscar veículo pelo ID no nosso "banco de dados" 
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
        Veiculo[] bancoVeiculos = new Veiculo[10000]; // Armazena todos os veículos do CSV
        Scanner sc = new Scanner(System.in);
        int quantidadeBd = 0;
        
        // Carregando dados do arquivo CSV
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

        // Cria a nossa Lista Sequencial
        ListaVeiculos lista = new ListaVeiculos(1000); 

        //  ids inseridos no final da lista até encontrar -1
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

        //quantidade de comandos e os comandos
        if (sc.hasNextInt()) {
            int numComandos = sc.nextInt();
            
            for (int i = 0; i < numComandos; i++) {
                String comando = sc.next();
                int idBusca;
                int pos;
                Veiculo v;

                switch (comando) {
                    case "II": // Inserir no Início
                        idBusca = sc.nextInt();
                        v = buscarVeiculo(bancoVeiculos, quantidadeBd, idBusca);
                        if (v != null) lista.inserirInicio(v);
                        break;
                        
                    case "I*": // Inserir em posição específica
                        pos = sc.nextInt();
                        idBusca = sc.nextInt();
                        v = buscarVeiculo(bancoVeiculos, quantidadeBd, idBusca);
                        if (v != null) lista.inserir(v, pos);
                        break;
                        
                    case "IF": // Inserir no Fim
                        idBusca = sc.nextInt();
                        v = buscarVeiculo(bancoVeiculos, quantidadeBd, idBusca);
                        if (v != null) lista.inserirFim(v);
                        break;
                        
                    case "RI": // Remover no Início
                        v = lista.removerInicio();
                        if (v != null) System.out.println("(R) " + v.getMarca() + " " + v.getModelo());
                        break;
                        
                    case "R*": // Remover em posição específica
                        pos = sc.nextInt();
                        v = lista.remover(pos);
                        if (v != null) System.out.println("(R) " + v.getMarca() + " " + v.getModelo());
                        break;
                        
                    case "RF": // Remover no Fim
                        v = lista.removerFim();
                        if (v != null) System.out.println("(R) " + v.getMarca() + " " + v.getModelo());
                        break;
                }
            }
        }

        // imprime todos os registros restantes na Lista do primeiro ao último
        lista.mostrar();
        
        sc.close();
    }
}