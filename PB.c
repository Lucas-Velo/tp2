#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_VEICULOS 1000
#define TAM_STRING 100
#define MAX_COMBUSTIVEL 5

// cria a struct data
typedef struct{
    int dia;
    int mes;
    int ano;
} Data;

// converte a data os atributos do struct data
Data parseData(char* s){
    Data d;
    // sscanf lê de uma string na memória, le os numeros da string pulando os traços e armazena os valores nos atributos do struct data
    sscanf(s, "%d-%d-%d", &d.ano, &d.mes, &d.dia);
    return d;
}

// data no modelo pedido xx/xx/xxxx , buffer para a string nao sumir quando a funcao acaba 
 void formatData(Data d, char* buffer){
    // armazena dados na string ao inves de mostrar na tela (ano com 4 digitos para evitar erros)
    sprintf(buffer, "%02d/%02d/%04d", d.dia, d.mes, d.ano);
}

// cria a struct veiculo
typedef struct{
    int id;
    char marca[TAM_STRING];
    char modelo[TAM_STRING];
    int ano;
    char categoria[TAM_STRING];
    char combustivel[MAX_COMBUSTIVEL][TAM_STRING];
    int cilindros;
    float cilindrada;
    char transmissao[TAM_STRING];
    char tracao[TAM_STRING];
    double consumoCidade;
    double consumoEstrada;
    double co2;
    bool turbo;
    Data dataRegistro;
} Veiculo;

// funcao para criar um Veiculo com as informacoes do arquivo
Veiculo* parseVeiculo(char* s){
    Veiculo *v = malloc(sizeof(Veiculo));

    char* parte;
    char combustivel[TAM_STRING];

    // passa todas as informacoes para o Veiculo
    parte = strtok(s, ",");
    v->id = atoi(parte);

    parte = strtok(NULL, ",");
    sprintf(v->marca, "%s", parte);

    parte = strtok(NULL, ",");
    sprintf(v->modelo, "%s", parte);

    parte = strtok(NULL, ",");
    v->ano = atoi(parte);

    parte = strtok(NULL, ",");
    sprintf(v->categoria, "%s", parte);

    parte = strtok(NULL, ",");
    sprintf(combustivel, "%s", parte);

    parte = strtok(NULL, ",");
    v->cilindros = atoi(parte);

    parte = strtok(NULL, ",");
    v->cilindrada = atof(parte);

    parte = strtok(NULL, ",");
    strcpy(v->transmissao, parte);

    parte = strtok(NULL, ",");
    strcpy(v->tracao, parte);

    parte = strtok(NULL, ",");
    v->consumoCidade = atof(parte);

    parte = strtok(NULL, ",");
    v->consumoEstrada = atof(parte);

    parte = strtok(NULL, ",");
    v->co2 = atof(parte);

    parte = strtok(NULL, ",");
    v->turbo = (strcmp(parte, "true") == 0);

    parte = strtok(NULL, ",");
    v->dataRegistro = parseData(parte);

    //separa os combustíveis
    int quantComb = 0;
    char *comb = strtok(combustivel, ";");

    while(comb != NULL && quantComb < MAX_COMBUSTIVEL){
        sprintf(v->combustivel[quantComb], "%s", comb);
        quantComb++;
        //identifica a parte separada por ; em combustivel para trocar
        comb = strtok(NULL, ";");
    }

    return v;
}

char* format(Veiculo *v){
    // cria uma string para formatar
    char* str = malloc(2000 * sizeof(char));
    int pos=0;

    //adiciona a string quantos caracteres que ela acabou de escrever acelerando o processo
    pos+=sprintf(str+pos, "[%d ## ", v->id);
    pos+=sprintf(str+pos, "%s ## ", v->marca);
    pos+=sprintf(str+pos, "%s ## ", v->modelo);
    pos+=sprintf(str+pos, "%d ## ", v->ano);
    pos+=sprintf(str+pos, "%s ## ", v->categoria);

    pos+=sprintf(str+pos, "[");

    //Separa os combustiveis com , se tiver mais de um
    for(int i=0; i<MAX_COMBUSTIVEL && v->combustivel[i][0] !='\0'; i++){
        if(i > 0){
            pos += sprintf(str + pos, ",");
        }
        pos += sprintf(str + pos, "%s", v->combustivel[i]);
    }

    pos += sprintf(str + pos, "] ## ");

    pos += sprintf(str + pos, "%d ## ", v->cilindros);
    pos += sprintf(str + pos, "%.1f ## ", v->cilindrada);
    pos += sprintf(str + pos, "%s ## ", v->transmissao);
    pos += sprintf(str + pos, "%s ## ", v->tracao);
    pos += sprintf(str + pos, "%.2f ## ", v->consumoCidade);
    pos += sprintf(str + pos, "%.2f ## ", v->consumoEstrada);
    pos += sprintf(str + pos, "%.1f ## ", v->co2);
    pos += sprintf(str + pos, "%s ## ", v->turbo ? "true" : "false");

    //chama a funcao para formatar a data
    char buffer[20];
    formatData(v->dataRegistro, buffer);

    pos += sprintf(str + pos, "%s]", buffer);

    return str;
}

Veiculo* lerCsv(char* caminhoArquivo){
    //abre o arquivo para leitura
    FILE *arq = fopen(caminhoArquivo, "r");

    if(arq==NULL){
        printf("Erro ao ler arquivo\n");
        exit(1);
    }

    char linha[2000];
    int n=0;

    // pula cabeçalho
    fgets(linha, sizeof(linha), arq);

    //Conta a quantidade de linhas do arquivo
    while((fgets(linha, sizeof(linha), arq))!=NULL){
        n++; 
    }

    rewind(arq);
    //pula cabeçalho
    fgets(linha, sizeof(linha), arq);
    
    //Cria um vetor de arquivos com alocacao dinamica
    Veiculo *retorno = malloc(n * sizeof(Veiculo)); 
    
    //percorre as linhas preenchidas do arquivo
    for(int i = 0; i < n; i++){
        fgets(linha, sizeof(linha), arq);

        //tirar o /n
        for(int j = 0; linha[j] != '\0'; j++){
            if(linha[j] == '\n'){
                linha[j] = '\0';
                break;
            }
        }

        // Adiciona o veiculo ja pronto ao vetor
        retorno[i] = *parseVeiculo(linha);
    }

    fclose(arq);
    return retorno;
}

// Algoritmo de Selecao com desempate por ID 
void ordenacaoSelecao(Veiculo *vetor, int tamanho){
    for(int i = 0; i < tamanho - 1; i++){
        int indiceMenor = i;
        
        for(int j = i + 1; j < tamanho; j++){
            int comparacao = strcmp(vetor[j].modelo, vetor[indiceMenor].modelo);
            
            // Desempate: Se o modelo for igual, prioriza o menor ID
            if(comparacao < 0 || (comparacao == 0 && vetor[j].id < vetor[indiceMenor].id)){
                indiceMenor = j; 
            }
        }
        
        if(indiceMenor != i){
            Veiculo temp = vetor[i];
            vetor[i] = vetor[indiceMenor];
            vetor[indiceMenor] = temp;
        }
    }
}

// Pesquisa Binaria: divide o vetor ao meio a cada iteracao
bool pesquisaBinaria(Veiculo *vetor, int tamanho, char *chaveModelo){
    int inicio = 0;
    int fim = tamanho - 1;

    while(inicio <= fim){
        int meio = (inicio + fim) / 2;
        int comparacao = strcmp(vetor[meio].modelo, chaveModelo);

        if(comparacao == 0){
            return true; // Achou
        } 
        else if(comparacao < 0){
            inicio = meio + 1; // Descarta a metade esquerda
        } 
        else {
            fim = meio - 1; // Descarta a metade direita
        }
    }
    return false; // Nao achou
}

int main(){
    Veiculo* veiculos = lerCsv("/tmp/veiculos.csv");

    Veiculo selecionados[MAX_VEICULOS];
    int qtdSelecionados = 0;

    int id;
    scanf("%d", &id);

    // Leitura dos IDs
    while(id != -1){
        for(int i = 0; i < MAX_VEICULOS; i++){
            if(veiculos[i].id == id){
                selecionados[qtdSelecionados] = veiculos[i];
                qtdSelecionados++;
                break;
            }
        }
        scanf("%d", &id);
    }

    // Ordena antes da pesquisa
    ordenacaoSelecao(selecionados, qtdSelecionados);

    char modeloBusca[TAM_STRING];

    // le ate o fim
    // O espaco no scanf para nao dar problema
    while(scanf(" %[^\r\n]", modeloBusca) != EOF){
        
        if(strcmp(modeloBusca, "FIM") == 0){
            break;
        }

        if(pesquisaBinaria(selecionados, qtdSelecionados, modeloBusca)){
            printf("SIM\n");
        } else {
            printf("NAO\n");
        }
    }

    free(veiculos);
    return 0;
}