OBJS	= CommandExt.o Command.o CommandINT.o
SOURCE	= CommandExt.c Command.c CommandINT.c
HEADER	= CommandEXT.h Command.h CommandINT.h
OUT	= slash
CC	 = gcc
FLAGS	 = -g -c -Wall
LFLAGS	 = -lreadline

all: $(OBJS)
	$(CC) -g $(OBJS) -o $(OUT) $(LFLAGS)

CommandExt.o: CommandExt.c
	$(CC) $(FLAGS) CommandExt.c 

Command.o: Command.c
	$(CC) $(FLAGS) Command.c 

CommandINT.o: CommandINT.c
	$(CC) $(FLAGS) CommandINT.c 


clean:
	rm -f $(OBJS) $(OUT)