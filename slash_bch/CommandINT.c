#include "Command.h"
void chpwd( char *new, char *old){
    if(new!=NULL)setenv("PWD",new,1);
    if(old!=NULL)setenv("OLDPWD",old,1);
}
void pathbuilder(char *path)
{

    char copyPath[MAX_ARGS_STRLEN];

    char tmp[MAX_ARGS_STRLEN];
    strcpy(tmp, path);

    char *tabdir[MAX_ARGS_NUMBER];
    char *tabtmp[MAX_ARGS_NUMBER];

    if (path != NULL)
    {
        if (path[0] == '/')
        {
            strcpy(copyPath, path);
            strcpy(path, "/");
        }
        else
        {
            strcpy(copyPath, getenv("PWD"));
            strcat(copyPath, "/");
            strcat(copyPath, path);
            strcpy(path, "/");
        }
        char *sep = "/";
        char *dirs = strtok(copyPath, sep);
        int i = 0;
        int j = 0;
        while (dirs != NULL)
        {
            tabdir[i] = dirs;
            i++;
            dirs = strtok(NULL, sep);
        }
        int fin = i;
        i = 0;
        while (i < fin)
        {
            if (strcmp(tabdir[i], "..") == 0)
            {
                if (j > 0)
                {
                    tabtmp[j] = NULL;
                    j--;
                }
            }
            else if (strcmp(tabdir[i], ".") != 0)
            {
                tabtmp[j] = tabdir[i];
                j++;
            }
            i++;
        }
        fin = j;
        j = 0;
        while (j < fin)
        {
            strcat(path, tabtmp[j]);
            if (j != (fin - 1))
            {
                strcat(path, "/");
            }
            j++;
        }
    }
}
int indexDerniereOcc(char *str){
    int i = 0;
    int res = 0;
    while (str[i]!= '\0')
    {
        if (str[i] == '/')
        {
            res = i;
        }
        i ++;
    }
    return res;
}
int cd_opt(char* opt, char* path){

    char *pwd = getenv("PWD"); //pwd actuel

    //copie du repertoir precedent
    char *oldpwd = getenv("OLDPWD"); //pwd ancien
    if (strcmp(opt, "-P") == 0){
        char res[MAX_ARGS_STRLEN];
        if (path != NULL){    //si l'argument existe
            if (strcmp(path,"-")==0){
                chdir(oldpwd);
                getcwd(res, MAX_ARGS_STRLEN);
                chpwd(res,pwd);
                RETVAL=0;
                return 0;
            }
            else if(chdir(path)== 0){   //si le repertoir est valide
                getcwd(res, MAX_ARGS_STRLEN);
                chpwd(res,pwd);
               
                RETVAL=0;
                return 0;
            }else{   //si aucun argument apres cd -P
               
                RETVAL=1;
                return 1;
            }  
        }else{  //si le repertoir n'est pas accessible
            RETVAL=1;
            return 1;
        };  
    }else{ //-L

        char *tmp = malloc(MAX_ARGS_STRLEN*sizeof(char));
        if(tmp==NULL)return 1;
        strcpy(tmp,path);

        if (tmp != NULL){
            pathbuilder(tmp);
            if (strcmp(path,"-")==0){
            chdir(oldpwd);
            chpwd(oldpwd,pwd);
            free(tmp);
            RETVAL=0;
            return 0;
            }else if (chdir(tmp)==0){
                chpwd(tmp,pwd);
                free(tmp);
                RETVAL=0;
                return 0;
            }else{
                free(tmp);
                cd_opt("-P",path);
                return 1;
            }
        }else{
            free(tmp);
            RETVAL=1;
            return 1;
        }
    } 
}
int get_pwd (char* opt, char* ret){
    if (strcmp(opt,"-P") == 0)
    {
        char res[MAX_ARGS_STRLEN];
        getcwd(res, MAX_ARGS_STRLEN);
        strcpy(ret,res);
        printf("%s\n",ret);
        RETVAL=0;
        return 0;
    }else if (strcmp(opt,"-L")== 0 || opt ==NULL)
    {
        strcpy(ret,getenv("PWD"));
        printf("%s\n",ret);
         RETVAL=0;
        return 0;
    }
    return 1;
}
void strCmdArg (char *s){ //____cmd_____arg___ -> cmd_arg
    int i = 0;
    int j = 0;
    int cmd1nonpasse = 1;
    int cmdnonpasse = 1;
    char *res = calloc(sizeof(char),strlen(s)+1);
    while (i<strlen(s))
    {
        if (s[i] == ' '){
            if(cmd1nonpasse){
                i ++;
            }else if (cmdnonpasse){
                res[j] = ' ';
                cmdnonpasse = 0;
                j ++;
                i ++;
            }else{
                i ++;
            }
        }else{
            cmd1nonpasse = 0;
            res[j] = s[i];
            i++;
            j++;
        }
    }
    res[j] = '\0';
    strcpy(s,res);
    free(res);
}

int chaine_espaces(char *p){
    for (int i = 0; i < strlen(p); i++){
        if (!isspace(p[i]))return 0;
    }
    return 1;
}
int is_numero(char *p){
    for (int i = 0; i < strlen(p); i++){
        if (!isdigit(p[i]))return 0;
    }
    return 1;
}

int exit1(char *command){
    if ((is_numero(command +5)==1) && strlen(command)>4){
            // exit avec Val          
            return atoi(command+5);
    }
    if (strlen(command) == 4 || (chaine_espaces(command + 4) == 1)){
        return RETVAL; // là on est dans le cas ou on a juste exit sans parametre(ou avec des espaces) :
                                    // val =la valeur de retour de la dernière commande exécutée en parametre.
                                    // atoi() de char a int(fonction prédéfinie)
    }else RETVAL=127;
    return RETVAL;
}