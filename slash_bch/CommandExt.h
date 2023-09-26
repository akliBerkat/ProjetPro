#include<stdio.h>
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<unistd.h>
#include<readline/readline.h>
#include<readline/history.h>
#include<sys/types.h>
#include <sys/wait.h>

#ifndef COMMANDEXT_H
#define COMMANDEXT_H
int exe(char * p,char *r,int *s);
int contientetoile(char *p);
int patternMatch(char * path, char *term);
void pathtostr(char *path, char *res);
void Auxpathfinder(char *path, char *res, char *tempores);
void removeSpaces(char *str);
int hasRedirection(char *commande);
void redirections(char *line, int *arr, char *command);
#endif