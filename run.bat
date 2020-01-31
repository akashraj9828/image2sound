
@echo off

cls
del img2sound.class
del wav.class
REM del out.wav
javac img2sound.java

java -Xmx1024m img2sound %1 %2 %3 %4 %5
REM java -Xmx1024m -Xprof -Xloggc:gc.txt img2sound
REM java -Xmx1000m -Xss1000m -Xprof -Xloggc:gc.txt  img2sound
REM java -Xmx1000m -Xss1000m -Xprof -Xloggc:gc.txt  img2sound
REM file out.wav

REM out.wav