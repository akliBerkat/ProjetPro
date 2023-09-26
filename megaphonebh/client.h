#ifndef CLIENT_H
#define CLIENT_H

#include <stdint.h>

#define SIZE_MAX_ARG 100

#define LEN_PSEUDO 10
#define LEN_MESS_INSCR (2 + LEN_PSEUDO)

#define POST_BILLET_CODEREQ 2
#define DERNIER_BILLET_CODEREQ 3

typedef struct t {
    char * buf;
    int reste_a_lire; //Le nombre d'octets à lire
} derniers_billets;

void printBinary(uint16_t n);
derniers_billets* creer_struct_db(int taille);
int recv_db(int sock, derniers_billets* db, int max);
void free_db(derniers_billets* db);
int connect_to_server(const char* hostname, const char* port, int* sock, struct sockaddr_in6** addr, int* addrlen);
uint16_t encoder_entete(uint16_t codereq, uint16_t id);
u_int16_t get_id(uint16_t* reponse_ins);
uint16_t get_numfil_post_billet(uint16_t* reponse_post);
u_int16_t get_nb_dernier_billet(u_int16_t* reponse_dernier_billet);
u_int8_t get_datalen_dernier_billet(char* reponse_dernier_billet);
char* get_inscription_requette(char* pseudo);
char* get_post_billet_requette(uint16_t id, uint16_t numfil, uint16_t nb, uint8_t datalen, char* data);
char* get_dernier_billet_requette(uint16_t id, uint16_t numfil, uint16_t nb);
uint16_t inscription(int fd_sock, char* pseudo);
uint16_t poster_billet(int sock, uint16_t id, uint16_t numfil, uint8_t datalen, char* data);
int get_data_from_dernier_billet(char* data, int taille, int sock);
int demande_dernier_billet(int sock, u_int16_t id, uint16_t numfil, uint16_t nb);
int interaction_demande_dernier_billet(int sock);

#endif  /* CLIENT_H */
