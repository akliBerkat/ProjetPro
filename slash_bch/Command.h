#include<stdio.h>
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<unistd.h>
#include<readline/readline.h>
#include<readline/history.h>
#include<errno.h>
#include<signal.h> 
#include <sys/wait.h>
#ifndef COMMAND_H
#define COMMAND_H
extern int RETVAL;
#define MAX_ARGS_NUMBER 4096
#define MAX_ARGS_STRLEN 4096
#endif