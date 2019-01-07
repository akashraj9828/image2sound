
@echo off

cls
del %1.class
javac %1.java

java -Xmx1024m %1