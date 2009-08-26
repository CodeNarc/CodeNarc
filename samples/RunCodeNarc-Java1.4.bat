@rem Run CodeNarc using the supplied command-line parameters

@rem Make sure you have the following in your CLASSPATH:
@rem  (1) The Groovy jar
@rem  (2) The CodeNarc jar
@rem  (3) The Log4J jar
@rem  (4) The directory containing CodeNarc config files such as "codenarc.properties" or ruleset files.

@set GROOVY_JAR="%GROOVY_HOME%/embeddable/groovy-all-1.5.6.jar"

@java -classpath %GROOVY_JAR%;lib/CodeNarc-0.7.jar;lib/log4j-1.2.14.jar;lib;lib/jaxp-api-1.4.2.zip;lib/xercesImpl.jar org.codenarc.CodeNarc %*
