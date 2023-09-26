#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<unistd.h>
#ifndef COMMANDEINT_H
#define COMMANDEINT_H
int cd_opt(char *opt, char *path);
int get_pwd (char *c,char *ret);
int exit1(char *command);
void pathbuilder(char *path);
void strCmdArg (char *s);
#endif