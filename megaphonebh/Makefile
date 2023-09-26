OBJS	= serveur.o Utilisateur.o fil.o Main.o 
SOURCE	= serveur.c Utilisateur.c fil.c Main.c 
HEADER	= serveur.h Utilisateur.h fil.h
OUT	= Serveur
CC	 = gcc
FLAGS	 = -g -c -Wall
LFLAGS	 = -pthread

all: $(OBJS)
	$(CC) -g $(OBJS) -o $(OUT) $(LFLAGS)

serveur.o: serveur.c
	$(CC) $(FLAGS) serveur.c 

Utilisateur.o: Utilisateur.c
	$(CC) $(FLAGS) Utilisateur.c 

fil.o: fil.c
	$(CC) $(FLAGS) fil.c 

Main.o: Main.c
	$(CC) $(FLAGS) Main.c 


clean:
	rm -f $(OBJS) $(OUT)