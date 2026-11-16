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
    sscanf(s, "%d-%d-%d", &d.ano, &d.mes, &d.dia);
    return d;
}

// data no modelo pedido xx/xx/xxxx
 void formatData(Data d, char* buffer){
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

    int quantComb = 0;
    char *comb = strtok(combustivel, ";");

    while(comb != NULL && quantComb < MAX_COMBUSTIVEL){
        sprintf(v->combustivel[quantComb], "%s", comb);
        quantComb++;
        comb = strtok(NULL, ";");
    }

    return v;
}

char* format(Veiculo *v){
    char* str = malloc(2000 * sizeof(char));
    int pos = 0;

    pos += sprintf(str + pos, "[%d ## %s ## %s ## %d ## %s ## [", v->id, v->marca, v->modelo, v->ano, v->categoria);

    for(int i = 0; i < MAX_COMBUSTIVEL && v->combustivel[i][0] != '\0'; i++){
        if(i > 0){
            pos += sprintf(str + pos, ", "); // A virgula e espaco do combustivel
        }
        pos += sprintf(str + pos, "%s", v->combustivel[i]);
    }

    pos += sprintf(str + pos, "] ## %d ## %.1f ## %s ## %s ## %.2f ## %.2f ## %.1f ## %s ## ", 
                   v->cilindros, v->cilindrada, v->transmissao, v->tracao, 
                   v->consumoCidade, v->consumoEstrada, v->co2, v->turbo ? "true" : "false");

    char buffer[20];
    formatData(v->dataRegistro, buffer);
    pos += sprintf(str + pos, "%s]", buffer);

    return str;
}

Veiculo* lerCsv(char* caminhoArquivo){
    FILE *arq = fopen(caminhoArquivo, "r");

    if(arq == NULL){
        printf("Erro ao ler arquivo\n");
        exit(1);
    }

    char linha[2000];
    int n = 0;

    fgets(linha, sizeof(linha), arq);

    while((fgets(linha, sizeof(linha), arq)) != NULL){
        n++; 
    }

    rewind(arq);
    fgets(linha, sizeof(linha), arq);
    
    // Usando calloc e limitando pelo MAX_VEICULOS
    Veiculo *retorno = calloc(MAX_VEICULOS, sizeof(Veiculo)); 
    
    for(int i = 0; i < n && i < MAX_VEICULOS; i++){
        fgets(linha, sizeof(linha), arq);

        for(int j = 0; linha[j] != '\0'; j++){
            if(linha[j] == '\n' || linha[j] == '\r'){
                linha[j] = '\0';
                break;
            }
        }
        
        Veiculo *v_temp = parseVeiculo(linha);
        retorno[i] = *v_temp;
        free(v_temp);
    }

    fclose(arq);
    return retorno;
}

//lower case
void toLowerCase(char* origem, char* destino) {
    int i = 0;
    while (origem[i] != '\0') {
        if (origem[i] >= 'A' && origem[i] <= 'Z') {
            destino[i] = origem[i] + 32;
        } else {
            destino[i] = origem[i];
        }
        i++;
    }
    destino[i] = '\0';
}

#define TAM_FILA 6

typedef struct {
    Veiculo elementos[TAM_FILA];
    int inicio;
    int fim;
} FilaCircular;

void iniciarFila(FilaCircular *f) {
    f->inicio = 0;
    f->fim = 0;
}

Veiculo removerFila(FilaCircular *f) {
    if (f->inicio == f->fim) {
        exit(1); // Fila vazia
    }
    Veiculo removido = f->elementos[f->inicio];
    
    // Avanca o inicio de forma circular
    f->inicio = (f->inicio + 1) % TAM_FILA;
    
    printf("(R)%s %s\n", removido.marca, removido.modelo);
    
    return removido;
}

void inserirFila(FilaCircular *f, Veiculo v) {
    // Verifica lotacao maxima: se o fim avancar 1 e bater no inicio, esta cheia
    if (((f->fim + 1) % TAM_FILA) == f->inicio) {
        removerFila(f);
    }
    
    f->elementos[f->fim] = v;
    
    // Avanca o fim de forma circular
    f->fim = (f->fim + 1) % TAM_FILA;
}

int main(){
    Veiculo* veiculos = lerCsv("/tmp/veiculos.csv");

    FilaCircular fila;
    iniciarFila(&fila);

    char entrada[50];
    char entradaMin[50];
    
    // Leitura Inicial
    while(scanf(" %s", entrada) == 1) {
        toLowerCase(entrada, entradaMin);
        
        if(strcmp(entradaMin, "fim") == 0 || strcmp(entrada, "-1") == 0) {
            break;
        }
        
        int id = atoi(entrada);
        
        for(int i = 0; i < MAX_VEICULOS; i++){
            // Com o calloc na lerCsv, os veiculos invalidos terao id 0 e serao ignorados seguramente
            if(veiculos[i].id == id){
                inserirFila(&fila, veiculos[i]);
                break;
            }
        }
    }

    int num_comandos;
    if(scanf("%d", &num_comandos) == 1) {
        
        for(int i = 0; i < num_comandos; i++){
            char cmd[10];
            char cmdMin[10];
            scanf(" %s", cmd);
            toLowerCase(cmd, cmdMin);
            
            if(strcmp(cmdMin, "i") == 0) {
                int id;
                scanf("%d", &id);
                
                for(int j = 0; j < MAX_VEICULOS; j++){
                    if(veiculos[j].id == id){
                        inserirFila(&fila, veiculos[j]);
                        break;
                    }
                }
            } 
            else if (strcmp(cmdMin, "r") == 0) {
                // Remove apenas se tiver elementos (inicio diferente de fim)
                if(fila.inicio != fila.fim) {
                    removerFila(&fila);
                }
            }
        }
    }

    // imprimir no formato da fila
    int indiceAtual = fila.inicio;
    while(indiceAtual != fila.fim){
        char *str = format(&fila.elementos[indiceAtual]);
        printf("%s\n", str);
        free(str);
        
        // Caminha ate alcancar o fim
        indiceAtual = (indiceAtual + 1) % TAM_FILA;
    }

    free(veiculos);
    return 0;
}