Pour compiler : 
javac *.java

Installation : 
Créer un dossier "histories" au même niveau que le dossier clavardage.

Pour lancer le programme : 
Se placer dans le dossier au dessus des dossiers clavardage et histories et exécuter la commande :
"java clavardage/Run"

We need the Servlet API library to compile this program. Servlet API is not part of JDK or Java SE (but belongs to Java EE). Tomcat provides a copy of servlet API called "servlet-api.jar" in "<CATALINA_HOME>\lib". You could copy "servlet-api.jar" from "<CATALINA_HOME>\lib" to "<JAVA_HOME>\jre\lib\ext" (the JDK Extension Directory), or include the Servlet JAR file in your CLASSPATH.

