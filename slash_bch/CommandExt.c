#include "Command.h"
#include <dirent.h>
#include <fcntl.h>

int contientetoile(char *p)
{
    int i = 0;
    while (i < strlen(p))
    {
        if (p[i] == '*')
            return 0;
        i++;
    }
    return 1;
}

int patternMatch(char *path, char *term)
{
    int x = strlen(term);
    int charAlire = strlen(path) - 1;
    return (strcmp(path + 1, term + x - charAlire) && path[0] != '.');
}

void pathfinder(char *path, char *res, char *tempores)
// path = le chemin qui reste a parcourir
// res = str de str de chemins qui satisfont les jokers
// tempores = resultat temporair = sorte de buffer pour permettre a la fonction de stocker le chemin tant que len(path) > 0
{
    // copie de tempores
    char tempocopy[MAX_ARGS_NUMBER];
    memset(tempocopy, 0, MAX_ARGS_NUMBER);
    strcpy(tempocopy, tempores);

    // sauvegarde du chemin initial
    char Trueinitmp[MAX_ARGS_NUMBER];
    getcwd(Trueinitmp, MAX_ARGS_NUMBER);

    char initmp[MAX_ARGS_NUMBER];
    if (tempores != NULL)
        strcpy(initmp, tempores);

    char initPath[MAX_ARGS_STRLEN];
    getcwd(initPath, MAX_ARGS_NUMBER);

    char copyPath[MAX_ARGS_STRLEN];
    strcpy(copyPath, path);

    char *tabdir[MAX_ARGS_NUMBER];
    int i = 0;
    if (path[0] == '/')
    {
        tabdir[i] = "/";
        i++;
        // strcpy(tempores,"/");
    }

    // couper notre path et stocker dns un tableau tabdir
    char *sep = "/";
    char *dirs = strtok(copyPath, sep);
    int j = 0;
    while (dirs != NULL)
    {
        tabdir[i] = dirs;
        i++;
        dirs = strtok(NULL, sep);
    }
    int fin = i;
    i = 0;

    // parcours du tableau
    while (i < fin)
    {
        if (strcmp(tabdir[i], "**") == 0)
        {
            if (i + 1 == fin)
            { // quand path = "**"

                char tmp[MAX_ARGS_NUMBER];
                strcpy(tmp, tempores);
                char newPath[MAX_ARGS_STRLEN];

                DIR *dir = opendir(".");
                if (dir == NULL)
                {
                    perror("opendir");
                    return;
                }
                struct dirent *entry;

                while ((entry = readdir(dir)) != NULL)
                {
                    if ((entry->d_name[0] == '.') | (entry->d_type == DT_REG))
                        continue;

                    if (entry->d_type == DT_DIR) // si c'est un repertoir continuer le parcours dedans
                    {
                        strcpy(tempores, tempocopy);
                        strcpy(newPath, entry->d_name);
                        strcat(newPath, "/**");
                        pathfinder(newPath, res, tempores);
                        if (initPath != NULL)
                            chdir(initPath);
                        strcpy(tempores, tmp);
                        strcpy(tempocopy, tempores);
                    }

                    // ajout du rep aux resultats
                    if (strlen(tempocopy) > 0)
                        strcat(tempocopy, "/");
                    strcat(tempocopy, entry->d_name);
                    strcat(res, " ");
                    strcat(res, tempocopy);
                    strcat(res, "/");
                    strcpy(tempocopy, tempores);
                }
                // revenir au rep courant et free des allocs
                if (Trueinitmp != NULL)
                    chdir(Trueinitmp);
                free(entry);
                free(dir);
                return;
            }
            else
            { // cas **/path
                char suitepath[MAX_ARGS_NUMBER];
                char tmp[MAX_ARGS_NUMBER];
                strcpy(tmp, tempores);
                char newPath[MAX_ARGS_STRLEN];

                DIR *dir = opendir(".");
                if (dir == NULL)
                {
                    perror("opendir");
                    return;
                }

                j = i + 1;
                memset(suitepath, 0, MAX_ARGS_NUMBER);
                while (j < fin)
                {
                    strcat(suitepath, tabdir[j]);
                    if (j != (fin - 1))
                    {
                        strcat(suitepath, "/");
                    }
                    j++;
                }

                struct dirent *entry;
                while ((entry = readdir(dir)) != NULL)
                {

                    if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0 || (entry->d_type != DT_DIR) || entry->d_name[0] == '.')
                        continue;

                    if (entry->d_type != DT_REG)
                    {

                        strcpy(tempores, tempocopy);
                        strcpy(newPath, entry->d_name);
                        strcat(newPath, "/");
                        strcat(newPath, suitepath);
                        pathfinder(newPath, res, tempores);
                        if (initPath != NULL)
                            chdir(initPath);
                        strcpy(tempores, tempocopy);
                        strcpy(newPath, entry->d_name);
                        strcat(newPath, "/**/");
                        strcat(newPath, suitepath);
                        pathfinder(newPath, res, tempores);
                        if (initPath != NULL)
                            chdir(initPath);
                        strcpy(tempores, tmp);
                        strcpy(tempocopy, tempores);
                    }
                }
                if (Trueinitmp != NULL)
                    chdir(Trueinitmp);
                free(entry);
                free(dir);
                return;
            }
        }
        else if (strcmp(tabdir[i], "*") == 0)
        {
            if (i + 1 == fin)
            { // .../*

                // char newPath[MAX_ARGS_STRLEN];

                DIR *dir = opendir(".");
                if (dir == NULL)
                {
                    perror("opendir");
                    return;
                }

                struct dirent *entry;
                while ((entry = readdir(dir)) != NULL)
                {
                    // getcwd(newPath,MAX_ARGS_NUMBER);
                    if (entry->d_name[0] == '.')
                        continue;

                    if (strlen(tempocopy) > 0)
                    {
                        strcat(tempocopy, "/");
                        strcat(tempocopy, entry->d_name);
                        strcat(res, " ");
                        strcat(res, tempocopy);
                        strcpy(tempocopy, tempores);
                    }
                    else
                    {
                        strcat(tempocopy, entry->d_name);
                        strcat(res, " ");
                        strcat(res, tempocopy);
                        strcpy(tempocopy, tempores);
                    }
                }
                free(entry);
                free(dir);
                return;
            }
            else
            { // .../*/...
                char tmp[MAX_ARGS_NUMBER];
                strcpy(tmp, tempores);
                char newPath[MAX_ARGS_STRLEN];
                memset(newPath, 0, MAX_ARGS_NUMBER);

                DIR *dir = opendir(".");
                if (dir == NULL)
                {
                    perror("opendir");
                    return;
                }

                struct dirent *entry;
                while ((entry = readdir(dir)) != NULL)
                {
                    if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0 || (entry->d_type == DT_REG) || entry->d_name[0] == '.')
                        continue;

                    strcpy(tempores, tempocopy);

                    strcpy(newPath, entry->d_name);
                    strcat(newPath, "/");
                    j = i + 1;
                    while (j < fin)
                    {
                        strcat(newPath, tabdir[j]);
                        if (j != (fin - 1))
                        {
                            strcat(newPath, "/");
                        }
                        j++;
                    }
                    pathfinder(newPath, res, tempores);
                    if (initPath != NULL)
                        chdir(initPath);
                    strcpy(tempores, tmp);
                    strcpy(tempocopy, tempores);
                }
                if (Trueinitmp != NULL)
                    chdir(Trueinitmp);
                free(entry);
                free(dir);
                return;
            }
        }
        else if (tabdir[i][0] == '*')
        {
            if (i + 1 == fin)
            { // .../*_

                DIR *dir = opendir(".");
                if (dir == NULL)
                {
                    perror("opendir");
                    return;
                }

                struct dirent *entry;
                while ((entry = readdir(dir)) != NULL)
                {
                    if (strcmp(entry->d_name, ".") != 0 && strcmp(entry->d_name, "..") != 0 && entry->d_name[0] != '.' && patternMatch(tabdir[i], entry->d_name) == 00)
                    {

                        if (strlen(tempocopy) > 0)
                        {
                            strcat(tempocopy, "/");
                            strcat(tempocopy, entry->d_name);
                            strcat(res, " ");
                            strcat(res, tempocopy);
                            strcpy(tempocopy, tempores);
                        }
                        else
                        {
                            strcat(tempocopy, entry->d_name);
                            strcat(res, " ");
                            strcat(res, tempocopy);
                            strcpy(tempocopy, tempores);
                        }
                    }
                    else
                        continue;
                }
                if (Trueinitmp != NULL)
                    chdir(Trueinitmp);
                free(entry);
                free(dir);
                return;
            }
            else
            { // .../*_/...
                char newPath[MAX_ARGS_STRLEN];
                memset(newPath, 0, MAX_ARGS_STRLEN);
                char tmp[MAX_ARGS_NUMBER];
                memset(tmp, 0, MAX_ARGS_NUMBER);
                if (tempores != NULL)
                    strcpy(tmp, tempores);
                DIR *dir = opendir(".");
                if (dir == NULL)
                {
                    perror("opendir");
                    return;
                }

                struct dirent *entry;
                while ((entry = readdir(dir)) != NULL)
                {
                    if (entry->d_name[0] != '.' && (entry->d_type == DT_DIR) && patternMatch(tabdir[i], entry->d_name) == 0)
                    {

                        strcpy(tempores, tempocopy);

                        if (entry->d_name[0] == '*')
                        {
                            chdir(entry->d_name);

                            strcat(tempores, "/");
                            strcat(tempores, entry->d_name);

                            j = i + 1;
                            while (j < fin)
                            {
                                strcat(newPath, tabdir[j]);
                                if (j != (fin - 1))
                                {
                                    strcat(newPath, "/");
                                }
                                j++;
                            }

                            pathfinder(newPath, res, tempores);
                            if (initPath != NULL)
                                chdir(initPath);
                            strcpy(tempores, tmp);
                            memset(newPath, 0, MAX_ARGS_STRLEN);
                            continue;
                        }
                        else
                        {
                            strcpy(newPath, entry->d_name);
                            strcat(newPath, "/");
                            j = i + 1;
                            while (j < fin)
                            {
                                strcat(newPath, tabdir[j]);
                                if (j != (fin - 1))
                                {
                                    strcat(newPath, "/");
                                }
                                j++;
                            }
                            pathfinder(newPath, res, tempores);
                            if (initPath != NULL)
                                chdir(initPath);
                            strcpy(tempores, tmp);
                        }
                    }
                    else
                        continue;
                }
                free(entry);
                free(dir);
                return;
            }
        }
        else
        {

            if (i + 1 == fin)
            {

                DIR *dir = opendir(".");
                if (dir == NULL)
                {
                    perror("opendir");
                    return;
                }
                int cond = 0;
                struct dirent *entry;

                while ((entry = readdir(dir)) != NULL)
                {
                    if (strcmp(entry->d_name, ".") != 0 && strcmp(entry->d_name, "..") != 0)
                    {

                        if (strcmp(tabdir[i], entry->d_name) == 0)
                            cond = 1;
                    }
                    else
                        continue;
                }
                if (cond == 0)
                {
                    free(entry);
                    free(dir);
                    return;
                }
                else
                {
                    if (strlen(tempocopy) > 0)
                    {
                        strcat(tempocopy, "/");
                        strcat(tempocopy, tabdir[i]);
                        strcpy(tempores, tempocopy);
                    }
                    else
                    {
                        strcat(tempocopy, tabdir[i]);
                        strcpy(tempores, tempocopy);
                    }
                }

                free(entry);
                free(dir);
            }
            else
            {
                if (chdir(tabdir[i]) == 0)
                {
                    memset(initPath, 0, MAX_ARGS_NUMBER);
                    getcwd(initPath, MAX_ARGS_NUMBER);
                    if (strlen(tempocopy) > 0 && strcmp(tempocopy, "/") != 0)
                    {
                        strcat(tempocopy, "/");
                        strcat(tempocopy, tabdir[i]);
                        strcpy(tempores, tempocopy);
                    }
                    else
                    {
                        strcat(tempocopy, tabdir[i]);
                        strcpy(tempores, tempocopy);
                    }
                }
                else
                {
                    strcpy(tempores, initmp);
                    return;
                }
            }
        }
        i++;
    }

    // arrivé a la fin si len du resultat temporaire > 0 l'ajouter aux resultats finaux
    if (strlen(tempocopy) > 0)
    {
        strcat(res, " ");
        strcat(res, tempocopy);
    }
    if (Trueinitmp != NULL)
        chdir(Trueinitmp);
    return;
}

void Auxpathfinder(char *path, char *res, char *tempores)
{
    char tmp[MAX_ARGS_NUMBER];
    getcwd(tmp, MAX_ARGS_NUMBER);
    if (strstr(path, "**") != NULL)
    {
        pathfinder(path + 3, res, tempores);
        chdir(tmp);
    }
    pathfinder(path, res, tempores);
    chdir(tmp);
}

void pathtostr(char *path, char *res)
{

    char *tabdir[MAX_ARGS_NUMBER];
    char copyPath[MAX_ARGS_NUMBER];
    char *respathfinder = calloc((MAX_ARGS_NUMBER * MAX_ARGS_STRLEN), sizeof(char));
    char *tempores = calloc(MAX_ARGS_NUMBER, sizeof(char));
    if (path != NULL)
    {
        strcpy(copyPath, path);
    }
    else
        return;
    int i = 0;
    char *sep = " ";
    char *dirs = strtok(copyPath, sep);
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
        if (contientetoile(tabdir[i]) == 0)
        {
            Auxpathfinder(tabdir[i], respathfinder, tempores);
            strcat(res, respathfinder);
            strcat(res, " ");
            memset(respathfinder, 0, MAX_ARGS_NUMBER * MAX_ARGS_STRLEN);
            memset(tempores, 0, MAX_ARGS_NUMBER);
        }
        else
        {
            strcpy(tempores, tabdir[i]);
            strcat(res, tempores);
            strcat(res, " ");
            memset(tempores, 0, MAX_ARGS_NUMBER);
        }
        i++;
    }

    free(respathfinder);
    free(tempores);
}

char **pathtotab(char *src)
{
    //   char *str = calloc(sizeof(char),MAX_ARGS_STRLEN*MAX_ARGS_NUMBER);
    //   strcpy(str,src);
    if (src == NULL)
        return NULL;
    char **res = NULL;
    char *p = strtok(src, " ");
    int n_spaces = 0;

    /* split string and append tokens to 'res' */
    while (p)
    {
        // printf("p = %p\n");
        res = realloc(res, sizeof(char *) * ++n_spaces);
        if (res == NULL)
            exit(-1); /* memory allocation failed */

        res[n_spaces - 1] = p;
        p = strtok(NULL, " ");
    }
    // free(str);
    /* realloc one extra element for the last NULL */
    res = realloc(res, sizeof(char *) * (n_spaces + 1));
    res[n_spaces] = 0;

    return res;
}

int find_index(char *haystack, char *needle)
{
    char *p = strstr(haystack, needle);
    if (p)
    {
        return p - haystack;
    }
    return -1;
}

int get_first_redirection_index(char *str)
{
    char *needles[] = {">>", ">", ">|", "2>", "2>|", "2>>", "<"};

    // Initialize the minimum index to a large number
    int min_index = 100000;
    for (int i = 0; i < 7; i++)
    {
        int index = find_index(str, needles[i]);
        if (index != -1 && index < min_index)
        {
            // If the index is not -1 and is smaller than the current minimum,
            // update the minimum index
            min_index = index;
        }
    }

    // Return the minimum index, or -1 if no index was found
    return min_index == 100000 ? -1 : min_index;
}

void removeSpaces(char *str)
{
    int index = 0;
    for (int i = 0; str[i]; i++)
    {
        if (str[i] != ' ')
        {
            str[index++] = str[i];
        }
    }
    str[index] = '\0';
}

void apply_redirection(char *redirection, char *fic, int *arr)
{
    char *treated_file_name = malloc(sizeof(char) * MAX_ARGS_STRLEN * MAX_ARGS_STRLEN);
    char *CopyFileName = malloc(sizeof(char) * MAX_ARGS_STRLEN * MAX_ARGS_STRLEN);
    char *tempores = malloc(sizeof(char) * MAX_ARGS_STRLEN * MAX_ARGS_STRLEN);
    strcpy(treated_file_name, fic);
    Auxpathfinder(CopyFileName, treated_file_name, tempores);
    int fd = 0;
    if (strcmp(redirection, ">>") == 0)
    {
        fd = open(treated_file_name, O_CREAT | O_APPEND | O_WRONLY, 0644);
        if (fd == -1)
        {
            RETVAL = 1;
            arr[0] = 1;
        }
        else
        {
            dup2(fd, STDOUT_FILENO);
        }
        // Treatment for case >>
    }
    else if (strcmp(redirection, ">") == 0)
    {
        fd = open(treated_file_name, O_CREAT | O_EXCL | O_WRONLY, 0644);
        if (fd == -1)
        {
            RETVAL = 1;
            arr[0] = 1;
        }
        else
        {
            dup2(fd, STDOUT_FILENO);
        }
        // Treatment for case >
    }
    else if (strcmp(redirection, "<") == 0)
    {
        fd = open(treated_file_name, O_RDONLY);
        if (fd == -1)
        {
            RETVAL = 1;
            arr[0] = 1;
        }
        else
        {
            dup2(fd, STDIN_FILENO);
        }
        // Treatment for case <
    }
    else if (strcmp(redirection, "2>") == 0)
    {
        fd = open(treated_file_name, O_CREAT | O_EXCL | O_WRONLY, 0644);
        if (fd == -1)
        {
            RETVAL = 1;
            arr[0] = 1;
        }
        else
        {
            dup2(fd, STDOUT_FILENO);
        }
        // Treatment for case 2>
    }
    else if (strcmp(redirection, "2>|") == 0)
    {
        fd = open(treated_file_name, O_CREAT | O_TRUNC | O_WRONLY, 0644);
        if (fd == -1)
        {
            RETVAL = 1;
            arr[0] = 1;
        }
        else
        {
            dup2(fd, STDOUT_FILENO);
        }
        // Treatment for case 2>|
    }
    else if (strcmp(redirection, ">|") == 0)
    {
        fd = open(treated_file_name, O_CREAT | O_TRUNC | O_WRONLY, 0644);
        if (fd == -1)
        {
            RETVAL = 1;
            arr[0] = 1;
        }
        else
        {
            dup2(fd, STDOUT_FILENO);
        }
        // Treatment for case >|
    }
    else if (strcmp(redirection, "2>>") == 0)
    {
        fd = open(treated_file_name, O_CREAT | O_APPEND | O_WRONLY, 0644);
        if (fd == -1)
        {
            RETVAL = 1;
            arr[0] = 1;
        }
        else
        {
            dup2(fd, STDOUT_FILENO);
        }
        // Treatment for case 2>>
    }
    close(fd);
    free(treated_file_name);
    free(CopyFileName);
    free(tempores);
}

void arrange_each_redirection(char *strOriginal, int *arr)
{
    char *str = malloc(sizeof(char) * MAX_ARGS_STRLEN);
    strcpy(str, strOriginal);
    char **tokens = pathtotab(str);
    int i = 0;
    while (tokens[i] != NULL && tokens[i + 1] != NULL)
    {
        // printf(":%s: : :%s:", tokens[i],tokens[i+1]);
        apply_redirection(tokens[i], tokens[i + 1], arr);
        i = i + 2;
    }
    free(str);
}

void redirections(char *line, int *arr, char *command)
{
    char *redir = calloc(sizeof(char), MAX_ARGS_NUMBER);
    int index = get_first_redirection_index(line);
    if (index != -1)
    {
        // printf("%d \n",index);
        strncpy(command, line, index - 1);
        command[index - 1] = '\0';
        // printf("commande  :%s:\n",command);
        strcpy(redir, line + index);
        arrange_each_redirection(redir, arr);
        free(redir);
    }
    else
    {
        strcpy(command, line);
        free(redir);
    }
}
int hasRedirection(char *commande)
{
    if (get_first_redirection_index(commande) == -1)
        return 0;
    else
        return 1;
}

void sig_action(int sig)
{
    RETVAL = 255;
    printf("<completé> \n");
    exit(255);
}

int exe(char *command, char *arguments, int *tube)
{
    // int ret=0;
    pid_t pid = 0;
    int status = 0;

    char *commandeExe = malloc(sizeof(char) * (strlen(command) + 1));
    strcpy(commandeExe, command);
    char *res = calloc(sizeof(char), MAX_ARGS_STRLEN * MAX_ARGS_NUMBER);
    strcpy(res, arguments);
    char **arg = pathtotab(res);
    struct sigaction action_int;
    // free(res);
    switch (pid = fork())
    {
    case 0:
        action_int.sa_handler = SIG_DFL;
        sigemptyset(&action_int.sa_mask);
        action_int.sa_flags = 0;
        sigaction(SIGTERM, &action_int, NULL);
        sigaction(SIGINT, &action_int, NULL);

        if (tube[0])
        {
            perror("erreur de redirection"), exit(1);
        }

        RETVAL = execvp(commandeExe, arg);
        free(commandeExe);
        free(arg);
        free(res);
        exit(WEXITSTATUS(status));
    case -1:
        perror("ERREUR DE FORK\n");
        free(commandeExe);
        free(arg);
        free(res);
        return 1;
    default:

        wait(&status);
        if (WIFEXITED(status))
            RETVAL = WEXITSTATUS(status);
        else if (WIFSIGNALED(status))
            RETVAL = 255;
        free(commandeExe);
        free(arg);
        free(res);
        return RETVAL;
    }

    return 1;
}