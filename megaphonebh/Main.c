// main pour lancer serveur
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/types.h>
#include <sys/stat.h>
#include "Utilisateur.h"
#include "fil.h"
#include "serveur.h"
#include <errno.h>

User *TabUser;
fils *fil_s;
pthread_mutex_t verrou = PTHREAD_MUTEX_INITIALIZER;

void *serve(void *arg)
{
    int sock = *((int *)arg);
    uint16_t debut = lecture_entete(sock);
    if (debut == 0){
        perror("erreur dans l'entete");
        return NULL;
    }
    int val;
    int recu = 0;
    uint8_t code =get_coderq(debut);

    if(code==1){
        pthread_mutex_lock(&verrou);
        recu = inscription_User(TabUser, sock);
        pthread_mutex_unlock(&verrou);
    }
    if(code==2){
        recu = poster_billet(sock, fil_s, TabUser, get_id_requete(debut));
        printf("Le billet est bien reçu et ajouté \n");
    }
        /*derniers n billets d'un fil*/
        /*if 3:
            recu = demander_des_nbillets(sock,TabUser,fil_s,get_id_requete(entete));
        
         if 4:
            recu = s'abonner_a_un_fil(sock,TabUser,fil_s,get_id_requete(debut));
            break;
        */
    if (recu==0){
        val = 0;
        if (sock >= 0)
            close(sock);
        pthread_exit(&val);
    }
    message_erreur(sock);//code requete 31
    val =1;
    if (sock >= 0)
        close(sock);
    pthread_exit(&val);
}

int main(int argc, char *argv[]){
    if (argc < 2){
        printf("TOO few arguments\n");
        return 1;
    }
    int sock = socket(PF_INET6, SOCK_STREAM, 0);
    struct sockaddr_in6 address_sock;
    memset(&address_sock, 0, sizeof(address_sock));
    address_sock.sin6_port = htons(atoi(argv[1]));
    address_sock.sin6_family = AF_INET6;
    address_sock.sin6_addr = in6addr_any;
    if (sock < 0)
    {
        perror("socket error");
        exit(1);
    }
    int opt = 0;
    int recu = setsockopt(sock, IPPROTO_IPV6, IPV6_V6ONLY, &opt, sizeof(opt));
    if (recu < 0)
        fprintf(stderr, "échec de setsockopt() : (%d)\n", errno);
    opt=1;
    recu = setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));
    if (recu < 0)
        fprintf(stderr, "échec de setsockopt() : (%d)\n", errno);
    recu = bind(sock, (struct sockaddr *)&address_sock, sizeof(address_sock));
    if (recu < 0)
    {
        perror("bind failed");
        exit(2);
    }
    recu = listen(sock, 0);
    if (recu < 0)
    {
        perror("le listen marche pas");
        exit(2);
    }
    TabUser = tab_inscrits();
    fil_s = creation_fils();
    while (1){
        struct sockaddr_in6 addrclient;
        socklen_t size = sizeof(addrclient);

        int sock;
        sock = accept(sock, (struct sockaddr *)&addrclient, &size);

        if (sock >= 0)
        {
            pthread_t thread;
            if (pthread_create(&thread, NULL, serve, &sock) == -1)
            {
                perror("thread pas creer");
                continue;
            }
            printf("le client est connecte a ce port là -> %d\n",htons(addrclient.sin6_port));
        }
    }
    close(sock);
    return 0;
}
