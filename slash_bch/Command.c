#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <readline/readline.h>
#include <readline/history.h>
#include "CommandExt.h"
#include "CommandINT.h"
#include "Command.h"
#include <signal.h>
#define BLUE "\001\e[1;34m\002"
#define GREEN "\001\e[1;32m\002"
#define RED "\001\e[1;31m\002"
#define RESET "\001\e[0m\002"
int RETVAL = 0;

char *prompt(char *i)
{
    char *pro = malloc((sizeof(char) * 32));
    int x = strlen(getenv("PWD"));
    char *val = malloc(5);
    memset(val, 0, 5);
    sprintf(val, "[%s]", i);
    int v = strlen(val);

    if (x <= 23)
    {
        memmove(pro, val, v);
        memmove(pro + v, getenv("PWD"), x);
        memmove(pro + x + v, "$ ", 2);
        memmove(pro + x + v + 2, "\0", 2);
    }
    else
    {
        memcpy(pro, val, v);
        memcpy(pro + v, "...", 3);
        memcpy(pro + v + 3, getenv("PWD") + (x - (28 - v - 3)), 28 - v - 3);
        memcpy(pro + 28, "$ ", sizeof(char) * 2);
        memcpy(pro + 30, "\0", 2);
    }
    free(val);
    return pro;
}

int interpreteur(char *c, char *ret)
{

    int t[1] = {0};
    char *x = malloc(MAX_ARGS_STRLEN * sizeof(char));
    char *path = malloc(MAX_ARGS_STRLEN * sizeof(char));
    char *sep = " ";
    char *opt = malloc(MAX_ARGS_STRLEN * sizeof(char));
    if (hasRedirection(c))
    {
        redirections(c, t, x);
        strcpy(c, x);
    }
    strcpy(x, c);

    char *token = strtok(x, sep);
    if (strlen(c) > 0)
    {
        if (strcmp(token, "cd") == 0)
        {
            token = strtok(NULL, sep);
            if (token != NULL)
            {
                if (strcmp(token, "-P") == 0 || strcmp(token, "-L") == 0)
                {
                    strcpy(opt, token);
                    token = strtok(NULL, sep);
                    if (token != NULL)
                    {
                        strcpy(path, token);
                        int r = cd_opt(opt, path);
                        free(path);
                        free(opt);
                        free(x);
                        return r;
                    }
                    else
                    {
                        free(path);
                        free(opt);
                        free(x);
                        return 1;
                    }
                }
                else
                {
                    int w = cd_opt("-L", token);
                    free(path);
                    free(opt);
                    free(x);
                    return w;
                }
            }
            else
            {
                int m = cd_opt("-P", getenv("HOME"));
                free(path);
                free(opt);
                free(x);
                return m;
            }
        }
        else if (strcmp(token, "pwd") == 0)
        {
            token = strtok(NULL, sep);
            if (token != NULL && strcmp(token, "-P") == 0)
            {

                int s = get_pwd("-P", ret);
                free(path);
                free(opt);
                free(x);
                return s;
            }
            int m = get_pwd("-L", ret);
            free(path);
            free(opt);
            free(x);
            return m;
        }
        else if ((strcmp(token, "exit") == 0))
        {
            free(path);
            free(opt);
            free(x);
            exit(exit1(c));
        }
        else
        {
            char *commande = calloc(sizeof(char), MAX_ARGS_STRLEN);
            char *commandeExe = calloc(sizeof(char), MAX_ARGS_STRLEN);
            char *res = calloc(sizeof(char), MAX_ARGS_STRLEN * MAX_ARGS_NUMBER);
            // strcpy(commandeExe,c);
            // printf("token = |%s|\n",token);

            if (strstr(token, "*") != NULL)
            { // *ho ././.
                // printf("la commande contient *\n");
                char *tempores = calloc(sizeof(char), MAX_ARGS_NUMBER);
                Auxpathfinder(token, commande, tempores);
                removeSpaces(commande);
                free(tempores);
                // printf("commande = |%s|\n",commande);
                strcpy(commandeExe, c);
                pathtostr(commandeExe, res);
                if (strcmp(commande, "exit") == 0)
                {
                    // printf("c'est exit\n");
                    // printf("res avant: %s\n",res);
                    strCmdArg(res);
                    // printf("cas exit res = |%s|\n",res);
                    int val = interpreteur(res, ret);
                    free(res);
                    free(commande);
                    free(commandeExe);
                    free(x);
                    free(path);
                    free(opt);
                    // return 0;
                    return val;
                }
                else
                {
                    // printf("c'est pas exit\n");
                    // printf("res : %s\n",res);
                    int ex = exe(commande, res, t);
                    free(res);
                    free(commande);
                    free(commandeExe);
                    free(x);
                    free(path);
                    free(opt);
                    // return 0;
                    return ex;
                }
            }
            else
            { //  cmd */*/*
                strcpy(commande, token);
                strcpy(commandeExe, c);
                pathtostr(commandeExe, res);
                // printf("res : %s\n",res);
                int ex = exe(commande, res, t);
                free(res);
                free(commande);
                free(commandeExe);
                free(x);
                free(path);
                free(opt);
                return ex;
            }
        }
    }
    free(x);
    free(path);
    free(opt);
    return (1);
}

int main(int argc, char const *argv[])
{

    struct sigaction action_int;

    int std[3] = {0, 1, 2};
    std[0] = dup(0);
    std[1] = dup(1);
    std[2] = dup(2);

    rl_outstream = stderr;

    while (1)
    {
        action_int.sa_handler = SIG_IGN;
        sigemptyset(&action_int.sa_mask);
        action_int.sa_flags = 0;
        sigaction(SIGINT, &action_int, NULL);
        sigaction(SIGTERM, &action_int, NULL);

        dup2(std[0], 0);
        dup2(std[1], 1);
        dup2(std[2], 2);

        char *ret = malloc(MAX_ARGS_STRLEN);
        char *p;
        char *i = malloc(2);
        char *prom;
        sprintf(i, "%d", RETVAL);
        if (RETVAL == 255)
        {
            prom = prompt("SIG");
        }
        else
        {
            prom = prompt(i);
        }
        free(i);
        p = readline(prom);
        if (p != NULL)
            add_history(p);
        if (p != NULL)
        {
            interpreteur(p, ret);
        }
        else
        {
            // free(action_int.sa_handler);
            free(p);
            free(ret);
            exit(RETVAL);
        }
        // free(action_int.sa_handler);
        free(prom);
        free(p);
        free(ret);
    }

    return 0;
}