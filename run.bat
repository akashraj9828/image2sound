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
del img2sound.class
REM del out.wav
javac img2sound.java

java -Xmx1024m img2sound
file out.wav

REM out2.wav