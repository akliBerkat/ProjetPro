#include "fil.h"
pthread_mutex_t vr = PTHREAD_MUTEX_INITIALIZER;
billet copier_billet(billet bilt)
{
    billet tmp = {0};
    memmove(tmp.nom, bilt.nom, 10);
    tmp.nom[10] = '\0';
    tmp.taille = bilt.taille;
    bilt.contenu = malloc(sizeof(char) * bilt.taille + 1);
    memmove(tmp.contenu, bilt.contenu, bilt.taille);
    tmp.contenu[bilt.taille] = '\0';
    return tmp;
}
fil copier_fil(fil fil_init)
{
    fil tmp = {0};
    tmp.num = fil_init.num;
    tmp.nb_billets = fil_init.nb_billets;
    tmp.taille = sizeof(fil_init.billets);
    //tmp.billets = malloc(sizeof(billet) * MaxBILLETS);
    int i = 0;
    while (i < fil_init.nb_billets)
    {
        tmp.billets[i] = copier_billet(fil_init.billets[i]);
        i++;
    }
    return tmp;
}

fils *creation_fils()
{ // creation une structure de fils pour ranger les fils

    fils *fils_tmp = (fils *)malloc(sizeof(fils));
    if (fils_tmp == NULL)
        perror("echec de malloc");
    memset(fils_tmp, 0, sizeof(fils));
    fils_tmp->fils_t = malloc(MaxFils * sizeof(fil));
    if (fils_tmp == NULL)
    {
        free(fils_tmp);
        return NULL;
    }
    fils_tmp->taille = MaxFils;
    fils_tmp->nbfils = 0;
    return fils_tmp;
}
void free_fils(fils *fils)
{
    for (int i = 0; i < fils->nbfils; i++)
    {
        free_fil(fils->fils_t + i);
    }
    free(fils);
}
void free_fil(fil *fil)
{
    int i = 0;
    while (i < fil->nb_billets)
    {
        free(fil->billets[i].contenu); // free contenu des billets
        i++;
    }
    free(fil->billets); // free tableau de billets
}
fil *ajouter_fil(fils *fils,char * origine)
{ // ajouter un fil a la strucure de fils
    if (fils->nbfils == MaxFils)
    {
        perror("la structure de fils est pleine");
        return NULL;
    }
    fil fil_tmp;
    memset(&fil_tmp, 0, sizeof(fil_tmp));
    memmove(fil_tmp.origine, origine, 10* sizeof(char));
    fil_tmp.origine[10]='\0';
    fil_tmp.num = fils->nbfils;
    fil_tmp.taille = MaxBILLETS;
    /*fil_tmp.billets = malloc(MaxBILLETS * sizeof(billet));
    if (fil_tmp.billets = NULL)
    {
        perror("echec de malloc billet");
        return NULL;
    }*/
    fil_tmp.nb_billets = 0;
    fils->fils_t[fils->nbfils] = fil_tmp;
    fils->nbfils++;
    fil *tmp=fils->fils_t + (fils->nbfils - 1);
    pthread_mutex_unlock(&vr);
    return tmp;
}
int ajouter_billet(fil *fil, uint8_t taille, fils *fils, char *nom, char *texte)
{
    pthread_mutex_lock(&vr); // car tout les clients peuvent effectuer l'ajout au meme temps
    if (fil->taille == fil->nb_billets)
    { // donc le fil est plein on créer un nouveau fil et on rappelle la fonction
        printf("Le fil est plein vous etes redirigé vers un nouveau fil \n");
        ajouter_billet(ajouter_fil(fils,nom), taille, fils, nom, texte);//creation d'un nouveau fil puis ajout du billet au fil
    }
    billet *b=malloc(sizeof(billet));
    b->contenu = malloc(sizeof(char) * taille + 1);
    if (b->contenu == NULL)
    {
        perror("malloc failed");
        pthread_mutex_unlock(&vr);
        return 1;
    }
    memmove(b->contenu, texte, taille);
    memmove(b->nom, nom, 10);
    int r=fil->nb_billets;
    fil ->billets[r]=*b;
    fil->nb_billets += 1;
    pthread_mutex_unlock(&vr);
    return 0;
}

int ajouter_aunmfl(fils * fls,uint16_t numf, uint8_t taille, char* text,char * nom){
    if(numf>(fls->nbfils)){//le fil n'existe pas
        return 1;
    }
    return ajouter_billet(fls->fils_t+numf-1,taille,fls,nom,text);
}

