#include "serveur.h"

#define HEADER_SIZE 6
uint16_t encoder_entete(uint16_t codereq, uint16_t id)
{
    uint16_t entete = (id << 5) | codereq;
    return htons(entete);
}
char * Mserveur(uint16_t codereq, uint16_t id, uint16_t nfil, uint16_t NB)//message du serveur
{
    char *tmp = malloc(6);
    if (tmp == NULL)
    {
        perror("malloc");
        return NULL;
    }

    // Remplir l'entête
    *(uint16_t *)(tmp) = encoder_entete(codereq, id);

    // Remplir les autres champs
    *(uint16_t *)(tmp + 2) = htons(nfil);
    *(uint16_t *)(tmp + 4) = htons(NB);
    return tmp;
}
void message_erreur(int sock)//gestion d'erreur avec CodeReq
{
    char *erreur = Mserveur(31, 0, 0, 0);
    if (erreur == NULL)
    {
        return;
    }
    int env=send(sock, erreur,sizeof(erreur), 0);
    if(env==0)perror("send echoué");
    free(erreur);
}
char *Forme_Billet(uint16_t numfil, char* origine, char* pseudo, uint8_t datalen, char* data){
    size_t tmp_size = 23 + datalen;
    char *tmp = malloc(tmp_size * sizeof(char));
    if (tmp == NULL) {
        perror("malloc");
        return NULL;
    }
    uint16_t numfil_network = htons(numfil);
    memcpy(tmp, &numfil_network, sizeof(uint16_t));
    
    memcpy(tmp + sizeof(uint16_t), origine, 10* sizeof(char));
    
    memcpy(tmp + sizeof(uint16_t) + 10, pseudo, 10* sizeof(char));
    
    tmp[sizeof(uint16_t) + 2 * 10] = datalen;
    
    memcpy(tmp + 23, data, datalen * sizeof(char));

    return tmp;

}
uint8_t get_coderq(uint16_t entete) {
    uint16_t masque = 0x001F;
    uint8_t cod_req = (uint8_t)(entete & masque);
    return cod_req;
}

uint16_t get_id_requete(uint16_t entete) {
    uint16_t masque = 0x001F;
    uint16_t id = (entete & (~masque)) >> 5;
    return id;
}

int myread(int sock,char *buf){
    int lu=0;
    lu=recv(sock,buf,sizeof(buf),0);
    printf("lu %d\n",lu);
    return 0;
}
uint16_t lecture_entete(int sock)
{
    char * buf =malloc(sizeof(char)*2);
    if (buf == NULL)
        return 0;
    int l = recv(sock,buf,2,0);
    if (l==0){
        free(buf);
        return 0;
    }
    uint16_t entete = *((uint16_t *)buf);
    free(buf);
    return ntohs(entete);
}
int inscription_User(User * tab,int sock){
    //donc on lit d'abord l'entree 
    char *b =malloc(10*sizeof(char));
    if (b == NULL){
        perror("malloc a echoué");
        return 1;
    }
    //int r = myread(sock, buf);
    int r=recv(sock,b,10,0);
    if (r==0){
        free(b);
        return 1;
    }
    char *Nom = malloc(11 * sizeof(char));
    if (Nom == NULL){
        perror("malloc a echoué");
        return 1;
    }
    memmove(Nom, b, 10);
    free(b);

    //on ajoute l'utilisateur
    int idU=addUser(tab,Nom);
    if(idU==-1){
        perror("Tableau d'utilisateur est plein\n");
        message_erreur(sock);
        return 1;
    }
    
    //envoie de la reponse que l'utilsateur est inscrit
    printf("le client {%s}est inscrit avec id %d\n", Nom,idU);
    char *m= Mserveur(1, (uint16_t)idU, 0, 0);
    if (m==NULL)
    {
        perror("erreur malloc");
        return 1;
    }
    send(sock,m,6, 0); 
    free(m);
    return 0;
}
char * lecture(int sock,uint16_t *nfil,uint16_t *nb,uint8_t *datalen){
    char buf[5];
    int r = recv(sock,buf,5,0);
    if (r<0){return NULL;}
    *nfil = ntohs(*((uint16_t *)buf));
    *nb = ntohs(*((uint16_t *)(buf + 2)));
    *datalen = *((uint8_t *)(buf + 4));

    //lecture de data
    char *data=malloc(sizeof(char)*(*datalen));
    memset(data,0,*datalen);
    int s=recv(sock,data,*datalen,0);
    if(s<0)return NULL;
    return data;
}
int poster_billet(int sock,fils *fls,User *tab,uint16_t id){
    if(containUser(tab,id)>0)return 1;//l'user est pas inscrit
    uint16_t nb;
    uint16_t nmfil;
    uint8_t datalen;
    char *data=lecture(sock,&nmfil,&nb,&datalen);
    if(data==NULL)return 1;
    if(nmfil<=0){
        fil *fil=ajouter_fil(fls,tab[id-1].nom);
        if(fil==NULL)return 1;
        int r=ajouter_billet(fil,datalen,fls,tab[id-1].nom,data);
        if(r==1)return 1;
        nmfil=fls->nbfils;
    }else{
        int r=ajouter_aunmfl(fls,nmfil,datalen,data,tab[id-1].nom);
        if(r==1)return 1;
    }
  
    //reponse du serveur
    char *reponse=Mserveur(2,id,nmfil,0);
    if(reponse==NULL)return 1;
    int s=send(sock,reponse,6,0);//sizeof(reponse)=6
    if(s!=6){
        free(reponse);
        return 1;
    }
    free(reponse);
    return 0;
}
/*int nb_message_a_envoyer(fils *fls, uint16_t nfil, uint16_t nb, uint16_t* numfil_rep, uint16_t* nb) {
    if (nfil > 0) {
        if (nfil > fls->nbfils) {
            return 1; // Le fil demandé n'existe pas
        }
        
        fil fil = fls->fils_t[nfil - 1];
        if (nb == 0 || nb >= fil.nb_billets) {
            *numfil_rep = nfil;
            *nb = fil.nb_billets;
        } else {
            *numfil_rep = nfil;
            *nb = nb;
        }
    } else {
        *numfil_rep = fls->nbfils;
        *nb = 0;
        uint16_t nb_i;
        
        for (int i = 0; i < fls->nbfils; i++) {
            nb_derniers_billet_du_fil(fls, i + 1, nb, &nb_i); // Le fil existe forcément
            *nb += nb_i;
        }
    }
    
    return 0;
}
int get_messages(fils *fls, uint16_t nfil, uint16_t nb, char ***message, uint16_t *nfil, uint16_t *nbp) {
    if (!nb_message_a_envoyer(fils, nfil, nb, nfil, nbp)) {
        return 0;
    }
    if (*nbp == 0) {
        return 0;
    }
    *message = (char **)malloc(*nbp * sizeof(char *));
    if (*message == NULL) {
        perror("malloc");
        return 0;
    }
    memset(*message, 0, *nbp * sizeof(char *));
    int indice = 0;
    if (nfil > 0) {
        if (!get_messages_fil(fls, *message, indice, nfil, nb, NULL)) {
            free_messages_billets(*message, *nbp);
            return 0;
        }
        indice++;
    } else {
        int i=0;
        while (i < fls->nbfils) {
            uint16_t nb_cop = 0;
            if (!get_messages_fil(fls, *message, indice, i + 1, nb, &nb_cop)) {
                free_messages_billets(*message, *nbp);
                return 0;
            }
            indice += nb_cop;
            i++;
        }
    }
    return 1;
}

*/