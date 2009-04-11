set GROOVY="%GROOVY_HOME%\embeddable\groovy-all-1.5.6.jar"
set CODENARC_JAR=lib/CodeNarc-0.5.jar
set LOG4J_JAR=lib/log4j-1.2.14.jar
ant -lib %CODENARC_JAR% -lib %GROOVY% -lib %LOG4J_JAR% -lib lib runCodeNarc