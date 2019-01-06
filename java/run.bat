REM @echo off
REM cls
REM del a.class
REM del out.wav
REM javac a.java

REM java a
REM file out.wav

REM REM out.wav

@echo off

cls
del b.class
REM del out.wav
javac b.java

java -Xmx1024m b
file out.wav

REM out2.wav