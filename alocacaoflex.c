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
    FILE *arq = fopen(caminhoArquivo, "r");

    if(arq==NULL){
        printf("Erro ao ler arquivo\n");
        exit(1);
    }

    char linha[2000];
    int n=0;

    fgets(linha, sizeof(linha), arq);

    while((fgets(linha, sizeof(linha), arq))!=NULL){
        n++; 
    }

    rewind(arq);
    fgets(linha, sizeof(linha), arq);
    
    Veiculo *retorno = malloc(n * sizeof(Veiculo)); 
    
    for(int i = 0; i < n; i++){
        fgets(linha, sizeof(linha), arq);

        for(int j = 0; linha[j] != '\0'; j++){
            if(linha[j] == '\n'){
                linha[j] = '\0';
                break;
            }
        }
        retorno[i] = *parseVeiculo(linha);
    }

    fclose(arq);
    return retorno;
}

// Estrutura do No (Celula) da lista encadeada
typedef struct Celula {
    Veiculo elemento;
    struct Celula *prox;
} Celula;

// Estrutura controladora da Lista
typedef struct {
    Celula *primeiro;
    Celula *ultimo;
    int tamanho;
} Lista;

// Inicia a lista alocando o "No Cabeca" para evitar verificacoes de ponteiro nulo
void iniciarLista(Lista *l) {
    l->primeiro = (Celula*) malloc(sizeof(Celula));
    l->primeiro->prox = NULL;
    l->ultimo = l->primeiro;
    l->tamanho = 0;
}

void inserirFim(Lista *l, Veiculo v) {
    Celula *nova = (Celula*) malloc(sizeof(Celula));
    nova->elemento = v;
    nova->prox = NULL;
    
    // Conecta o antigo ultimo na nova celula e atualiza o ponteiro ultimo
    l->ultimo->prox = nova;
    l->ultimo = nova;
    l->tamanho++;
}

void inserirInicio(Lista *l, Veiculo v) {
    Celula *nova = (Celula*) malloc(sizeof(Celula));
    nova->elemento = v;
    
    nova->prox = l->primeiro->prox;
    l->primeiro->prox = nova;
    
    if(l->primeiro == l->ultimo) {
        l->ultimo = nova;
    }
    l->tamanho++;
}

void inserirPosicao(Lista *l, Veiculo v, int pos) {
    if(pos < 0 || pos > l->tamanho) return;
    if(pos == 0) return inserirInicio(l, v);
    if(pos == l->tamanho) return inserirFim(l, v);
    
    // Caminha ate a celula imediatamente anterior a posicao de insercao
    Celula *i = l->primeiro;
    for(int j = 0; j < pos; j++) {
        i = i->prox;
    }
    
    Celula *nova = (Celula*) malloc(sizeof(Celula));
    nova->elemento = v;
    nova->prox = i->prox;
    i->prox = nova;
    l->tamanho++;
}

Veiculo removerInicio(Lista *l) {
    if(l->primeiro == l->ultimo) exit(1);
    
    Celula *removida = l->primeiro->prox;
    Veiculo resp = removida->elemento;
    
    // Pula a celula removida conectando o No Cabeca na proxima celula
    l->primeiro->prox = removida->prox;
    
    if(removida == l->ultimo) {
        l->ultimo = l->primeiro;
    }
    
    printf("(R) %s %s\n", resp.marca, resp.modelo);
    free(removida);
    l->tamanho--;
    return resp;
}

Veiculo removerFim(Lista *l) {
    if(l->primeiro == l->ultimo) exit(1);
    
    // Caminha ate a penultima celula para transforma-la na nova ultima
    Celula *i;
    for(i = l->primeiro; i->prox != l->ultimo; i = i->prox);
    
    Celula *removida = l->ultimo;
    Veiculo resp = removida->elemento;
    
    l->ultimo = i;
    l->ultimo->prox = NULL;
    
    printf("(R) %s %s\n", resp.marca, resp.modelo);
    free(removida);
    l->tamanho--;
    return resp;
}

Veiculo removerPosicao(Lista *l, int pos) {
    if(l->primeiro == l->ultimo || pos < 0 || pos >= l->tamanho) exit(1);
    if(pos == 0) return removerInicio(l);
    if(pos == l->tamanho - 1) return removerFim(l);
    
    Celula *i = l->primeiro;
    for(int j = 0; j < pos; j++) {
        i = i->prox;
    }
    
    Celula *removida = i->prox;
    Veiculo resp = removida->elemento;
    
    i->prox = removida->prox;
    
    printf("(R) %s %s\n", resp.marca, resp.modelo);
    free(removida);
    l->tamanho--;
    return resp;
}

Veiculo buscarVeiculo(Veiculo *bd, int id) {
    for(int i = 0; i < MAX_VEICULOS; i++){
        if(bd[i].id == id) return bd[i];
    }
    Veiculo vVazio = {0};
    return vVazio;
}

int main(){
    Veiculo* veiculos = lerCsv("/tmp/veiculos.csv");

    Lista lista;
    iniciarLista(&lista);

    char entrada[50];
    while(scanf(" %s", entrada) == 1) {
        if(strcmp(entrada, "FIM") == 0 || strcmp(entrada, "-1") == 0) {
            break;
        }
        int id = atoi(entrada);
        Veiculo v = buscarVeiculo(veiculos, id);
        if(v.id != 0) {
            inserirFim(&lista, v);
        }
    }

    int num_comandos;
    if(scanf("%d", &num_comandos) == 1) {
        
        for(int i = 0; i < num_comandos; i++){
            char cmd[5];
            scanf(" %s", cmd);
            
            if(strcmp(cmd, "II") == 0) {
                int id; scanf("%d", &id);
                inserirInicio(&lista, buscarVeiculo(veiculos, id));
            } 
            else if(strcmp(cmd, "IF") == 0) {
                int id; scanf("%d", &id);
                inserirFim(&lista, buscarVeiculo(veiculos, id));
            } 
            else if(strcmp(cmd, "I*") == 0) {
                int pos, id; scanf("%d %d", &pos, &id);
                inserirPosicao(&lista, buscarVeiculo(veiculos, id), pos);
            } 
            else if(strcmp(cmd, "RI") == 0) {
                removerInicio(&lista);
            } 
            else if(strcmp(cmd, "RF") == 0) {
                removerFim(&lista);
            } 
            else if(strcmp(cmd, "R*") == 0) {
                int pos; scanf("%d", &pos);
                removerPosicao(&lista, pos);
            }
        }
    }

    // Ignora o No Cabeca lista.primeiro e imprime a partir do prox
    int j = 0;
    for(Celula *i = lista.primeiro->prox; i != NULL; i = i->prox){
        char *str = format(&i->elemento);
        printf("[%d] %s\n", j, str); 
        free(str);
        j++;
    }

    free(veiculos);
    return 0;
}