@echo off
cls
del a.class
del out.wav
javac a.java

java a
file out.wav

out.wav