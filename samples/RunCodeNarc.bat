@rem Run CodeNarc using the supplied command-line parameters 

@ java -classpath "%GROOVY_HOME%/embeddable/groovy-all-1.5.6.jar";lib;lib/CodeNarc-0.5.jar;lib/log4j-1.2.14.jar org.codenarc.CodeNarc %*
