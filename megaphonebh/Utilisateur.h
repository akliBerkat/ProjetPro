#ifndef UTILISATEUR_H
#define UTILISATEUR_H
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#define MAXUSERS 100
typedef struct User{
    char nom [11];
    uint16_t id;
}User;
User * tab_inscrits ();
int addUser(User * tab ,char * nom);
uint16_t nb_Users(User * tab);
int containUser(User * tab,uint16_t id);

#endif