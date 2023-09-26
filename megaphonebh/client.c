#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/time.h>
// Network-related includes
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include<readline/readline.h>

#include "client.h"


void printBinary(uint16_t n) {
    uint16_t mask = 0x8000;  // masque pour extraire chaque bit
    int i;
    for (i = 0; i < 16; i++){
        if (n & mask) {
            printf("1");
        } else {
            printf("0");
        }
        // Décale le masque de 1 bit vers la droite pour passer au bit suivant
        mask = mask >> 1;
    }
    printf("\n");
}

//

derniers_billets * creer_struct_db (int taille){
    derniers_billets * db = malloc(sizeof(derniers_billets));
    if(db==NULL){
        perror("malloc");
        return NULL;
    }
    db->reste_a_lire = taille;
    db->buf = malloc(db->reste_a_lire); 
    if(db->buf==NULL){
        perror("malloc");
        free(db);
        return NULL;
    }
    memset(db->buf, 0, db->reste_a_lire);
    return db;
}


int recv_db(int sock, derniers_billets* db, int max){
    int len;
    while(((len = recv(sock, db->buf+(max - db->reste_a_lire), db->reste_a_lire, 0)) < db->reste_a_lire) && len>0 ){
        db->reste_a_lire -= len;

    }
    db->reste_a_lire -= len;
    //si on a lu tout les billets
    if(db->reste_a_lire!= 0){
        return 0;}
    else
    return 1;
}


void free_db(derniers_billets * db){
    free(db->buf);
    free(db);
}


// Function to connect to the server
int connect_to_server(const char *hostname, const char *port, int *sock, struct sockaddr_in6 **addr, int *addrlen) {
    struct addrinfo hints, *res, *p;
    int ret;
    memset(&hints, 0, sizeof(hints));
    hints.ai_family = AF_INET6;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_V4MAPPED | AI_ALL;

    if ((ret = getaddrinfo(hostname, port, &hints, &res)) != 0 || res == NULL) {
        perror("Error getaddrinfo\n");
        return -1;
    }

    *addrlen = sizeof(struct sockaddr_in6);
    p = res;
    while (p != NULL) {
        if ((*sock = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) > 0) {
            if (connect(*sock, p->ai_addr, *addrlen) == 0)
                break;
            close(*sock);
        }

        p = p->ai_next;
    }

    if (p == NULL){
        perror("No addr found\n");
        return -2;
    }

    *addr = (struct sockaddr_in6 *)p->ai_addr;
    freeaddrinfo(res);
    return 0;
}

// Function to encode the header
uint16_t encoder_entete(uint16_t codereq, uint16_t id) {
    uint16_t entete = (id << 5) | codereq;
    return htons(entete);
}

// reponses du serveur 

// Function to extract the ID from the response instruction
u_int16_t get_id(uint16_t *reponse_ins) {
    u_int16_t numfil = ntohs(reponse_ins[1]);
    u_int16_t nb = ntohs(reponse_ins[2]);
    u_int16_t entete = ntohs(reponse_ins[0]);
    u_int16_t masque_codereq = 0x001F;
    uint16_t codreq = entete & masque_codereq;
    uint16_t masque_id = ~masque_codereq;
    uint16_t id = (entete & masque_id) >> 5;

    if (codreq != 1 || numfil != 0 || nb != 0) {
        if (codreq == 31){
            perror("Le serveur ne peut pas répondre à la requête");
            return 0;
        }
        perror("Message reçu par le serveur erroné");
        return 0;
    }

    return id;
}
//reponse du serveur apres post billet
uint16_t get_numfil_post_billet(uint16_t * reponse_post){
    u_int16_t numfil = ntohs(reponse_post[1]);
    u_int16_t nb = ntohs(reponse_post[2]);
    u_int16_t entete = ntohs(reponse_post[0]);
    u_int16_t masque_codereq = 0x001F;
    uint16_t codreq = entete & masque_codereq;
    uint16_t masque_id = ~masque_codereq;
    uint16_t id = (entete & masque_id) >> 5;

    if(codreq!=2 || nb!=0){
         if (codreq == 31) {
            perror("Le serveur ne peut pas répondre à la requête");
            return 0;
        }
        perror("Message reçu par le serveur erroné");
        return 0;
    }
    return numfil;
}
//reponse n billets (a faire)
u_int16_t get_nb_dernier_billet(u_int16_t * reponse_dernier_billet){

	u_int16_t numfil = ntohs(reponse_dernier_billet[1]);
    u_int16_t nb = ntohs(reponse_dernier_billet[2]);
    u_int16_t entete = ntohs(reponse_dernier_billet[0]);
    u_int16_t masque_codereq = 0x001F;
    uint16_t codreq = entete & masque_codereq;
    uint16_t masque_id = ~masque_codereq;
    uint16_t id = (entete & masque_id) >> 5;

	if(codreq!= DERNIER_BILLET_CODEREQ){
        perror("Message reçu par le serveur erroné");
		return 0;
	}
	printf("Le nombre de billets a recevoir est :%u\n",nb);
	return nb;
}

u_int8_t get_datalen_dernier_billet(char* reponse_dernier_billet){
    uint16_t *cast_rep = (uint16_t *)reponse_dernier_billet;
	u_int16_t numfil = ntohs( cast_rep[0]);
    printf("Numfil : %u\n",numfil);
	char  origine[11];
	memmove(origine,reponse_dernier_billet+2,10);
    printf("Origine : %s\n",origine);
	origine[10] = '\0';
    char pseudo[11];
	memmove(pseudo,reponse_dernier_billet+12,10);
	pseudo[10] = '\0';
	printf("Pseudo : %s\n",pseudo);
	uint8_t datalen = ((uint8_t *)reponse_dernier_billet)[22];
	return datalen;
}

//creer les requettes

// Function to generate the inscription request message
char *get_inscription_requette(char *pseudo) {
    char *inscription_requette = malloc(LEN_MESS_INSCR * sizeof(char));
    if (inscription_requette == NULL) {
        perror("malloc failed");
        return NULL;
    }

    uint16_t entete = encoder_entete(1, 0);
    *(uint16_t *)(inscription_requette) = entete;
    int len = strlen(pseudo);

    assert(len <= LEN_PSEUDO);

    memmove(inscription_requette + 2, pseudo, len);

    for (int i = 0; i < LEN_PSEUDO - len; i++) {
        inscription_requette[2 + len + i] = '#';
    }

    return inscription_requette;
}
//
char * get_post_billet_requette(uint16_t id, uint16_t numfil, uint16_t nb, uint8_t datalen, char * data){
	char * post_billet_requette = malloc(7 + (datalen * sizeof(char)));
	if(post_billet_requette == NULL){
		perror("malloc");
		return NULL;
	}
    //
    uint16_t* post_billet_requette_ptr = (uint16_t*)post_billet_requette;
    post_billet_requette_ptr[0] = encoder_entete(POST_BILLET_CODEREQ, id);
    post_billet_requette_ptr[1] = htons(numfil);
    post_billet_requette_ptr[2] = htons(nb);
    //
    u_int8_t* post_billet_requette_byte_ptr = (u_int8_t*)post_billet_requette;
    post_billet_requette_byte_ptr[6] = datalen;


	if(datalen > 0){
		memmove(post_billet_requette+7, data, datalen);    
	}

	return post_billet_requette;
}
//
char * get_dernier_billet_requette(uint16_t id, uint16_t numfil, uint16_t nb){

    char * dernier_billet_requette = malloc(7);
	if(dernier_billet_requette == NULL){
		perror("malloc");
		return NULL;
	}
    //
    uint16_t* dernier_billet_requette_ptr = (uint16_t*)dernier_billet_requette;
    dernier_billet_requette_ptr[0] = encoder_entete(DERNIER_BILLET_CODEREQ, id);
    dernier_billet_requette_ptr[1] = htons(numfil);
    dernier_billet_requette_ptr[2] = htons(nb);
    //
    u_int8_t* dernier_billet_requette_byte_ptr = (u_int8_t*)dernier_billet_requette;
    dernier_billet_requette_byte_ptr[6] = 0;

	return dernier_billet_requette;
}
// Function to perform the inscription process
uint16_t inscription(int fd_sock, char *pseudo) {
    char *inscription_requette = get_inscription_requette(pseudo);
    if (send(fd_sock, inscription_requette, LEN_MESS_INSCR, 0) != LEN_MESS_INSCR) {
        perror("Erreur d'envoi de la requette inscription");
        free(inscription_requette);
        return 0;
    }
    free(inscription_requette);

    // Response from the server
    u_int16_t reponse_ins[3];
    int len = recv(fd_sock, reponse_ins, sizeof(reponse_ins), 0);
    if (len != sizeof(reponse_ins)) {
        perror("Erreur de réception lors de l'inscription");
        return 0;
    }

    uint16_t id = get_id(reponse_ins);
    if (id) {
        printf("ID attribué :%d\n", id);
        return id;
    }

    printf("Echec de l'insciption, Probléme serveur\n");
    return 0;
}
//
uint16_t poster_billet(int sock,uint16_t id, uint16_t numfil, uint8_t datalen, char * data){
    char * poster_requette = get_post_billet_requette(id,numfil,0,datalen,data);
    int lnret = send(sock,poster_requette,7+datalen,0);
    if(lnret!=7+datalen){
        free(poster_requette);
        return 0;
    }
    free(poster_requette);
    //réponse du serveur
    u_int16_t reponse_poster_billet[3];
    int len = recv(sock,reponse_poster_billet, sizeof(reponse_poster_billet) ,0);
    printf("[%d]\n",len);
    if( (len  != sizeof(reponse_poster_billet))){
        return 0;
    }
    return get_numfil_post_billet(reponse_poster_billet);
}



int get_data_from_dernier_billet(char *data,int taille,int sock){
    derniers_billets *db = creer_struct_db(taille);
    if (db == NULL){
        perror("La structure de reception de billets est pas créée");
        return 0;
    }
    int r =recv_db(sock, db, taille);
    if (r == 0){
        free(db);
        return 0;
    }
    memmove(data, db->buf, taille);
    free_db(db);
    return 1;
}

int demande_dernier_billet(int sock,u_int16_t id,uint16_t numfil, uint16_t nb){

    char * dernier_billet_requette = get_dernier_billet_requette(id,numfil,nb);
    if(send(sock, dernier_billet_requette,7,0) != 7){
        free(dernier_billet_requette);
        return 0;
    } 
    free(dernier_billet_requette);

    //premiere reponse du serveur 
    u_int16_t prem_rep_serv[3];
    if((recv(sock,prem_rep_serv, sizeof(prem_rep_serv) ,0)) != sizeof(prem_rep_serv)){
        perror("Premiere reponse du serveur incoherente !");
        return 0;
    }

    uint16_t nbbillets = get_nb_dernier_billet(prem_rep_serv);
    //l'affichage se fait dans nbbillets et ca annonce le nombre de billets qui arrivent

    for(int i = 0; i < nbbillets; i++){
        char data[23];
        memset(data,0,2+10+10+1);
        if (!get_data_from_dernier_billet(data,23,sock))
            return 0;
        uint8_t datalen = get_datalen_dernier_billet(data);

        char buf_reception[datalen+1];
        memset(buf_reception,0,datalen+1);
        if (!get_data_from_dernier_billet(buf_reception,datalen,sock))
            return 0;
        printf("Billet : %s\n",buf_reception);
    }

    return 1;
}

int interaction_demande_dernier_billet(int sock){
    int id, numfil, nbbillets;
    char * line ;
    line = readline("Entrez l'id :\n");
    if (line != NULL)
    {
        id = atoi(line);
        free(line);
        
    }
    line = readline("Entrez le numéro de fil :\n");
    if (line != NULL)
    {
        numfil = atoi(line);
        free(line);
        
    }
    line = readline("Entrez le nombre de billet voulu :\n");
    if (line != NULL)
    {
        nbbillets = atoi(line);
        free(line);
    }
    int ret = demande_dernier_billet(sock, id, numfil, nbbillets);
    if(ret==0){
        perror("Erreur demande derniers billets");
        return 0;
    }
    return 1;
}


int dem_poster_billet(int sock){
    uint16_t id, numfil;
    uint8_t datalen;
    char billet[256]; //255+1
    memset(billet,0,sizeof(billet));
    char * line;
    line = readline("Entrez l'id :\n");
    if(line != NULL){
        id = atoi(line);
        free(line);
    }
    line = readline("Entrez le numéro de fil :\n");
    if(line != NULL){
        numfil = atoi(line);
        free(line);
    }
    line = readline("Entrez le contenu du billet :\n");
    if(line!=NULL){
        memmove(billet,line,strlen(line)<=255);
        datalen = strlen(billet);
        free(line);
    }
    int ret = poster_billet(sock, id, numfil, datalen, billet);
    if(!ret)
        perror("Erreur post_billet");
    else
        printf("Billet posté, Numfil :%u\n", ret);
    return ret;
}



int main(int argc, char const *argv[]) {
    char hostname[SIZE_MAX_ARG];
    char port[SIZE_MAX_ARG];
    if (argc < 3) {
        sprintf(hostname,"%s","lulu");
        sprintf(port,"%s","7777");
    }else{
        sprintf(hostname,"%s",argv[1]);
        sprintf(port,"%s",argv[2]);
    }
    struct sockaddr_in6 *server_addr;
    int fdsock, adrlen;

    if(connect_to_server(hostname,port, &fdsock, &server_addr, &adrlen)==0){
        printf("Connecté !\n"); 
    }else
    {
        perror("Echec de la connexion !\n");
        exit(1);
    }

    int ret=1;
    char pseudo[11];
    memset(pseudo,'#',10);
    pseudo[10]='\0';
    memmove(pseudo, "aaaa", strlen("aaaa")<=10 ? strlen("aaaa") : 10);

    ret = inscription(fdsock,pseudo);
    close(fdsock);
    if(connect_to_server(hostname,port, &fdsock, &server_addr, &adrlen)==0){
        printf("Connecté !\n"); 
    }else
    {
        perror("Echec de la connexion !\n");
        exit(1);
    }

    ret = dem_poster_billet(fdsock);

    close(fdsock);
    if(connect_to_server(hostname,port, &fdsock, &server_addr, &adrlen)==0){
        printf("Connecté !\n"); 
    }else
    {
        perror("Echec de la connexion !\n");
        exit(1);
    }

    ret = interaction_demande_dernier_billet(fdsock);

    close(fdsock);
    return ret;
}


