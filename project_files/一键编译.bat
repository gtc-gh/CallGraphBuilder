@echo off
echo д��.rc�ļ�
echo A ICON "./icon.ico" > res.rc
echo ����.res�ļ�
windres -i res.rc --input-format=rc -o my.res -O coff
echo ����
gcc -c -o main.o main.c
gcc -c -o rainbow_CORE.o rainbow_CORE.c
gcc -c -o rainbow_UI.o rainbow_UI.c
gcc -c -o rainbow_IO.o rainbow_IO.c
echo ����
gcc main.o rainbow_CORE.o rainbow_UI.o rainbow_IO.o my.res -o Rainbow.exe
echo ɾ����ʱ�ļ�
del res.rc
del my.res
del main.o
del rainbow_CORE.o
del rainbow_UI.o
del rainbow_IO.o