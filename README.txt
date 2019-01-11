Pour compiler : 
javac *.java

Installation : 
Créer un dossier "histories" au même niveau que le dossier clavardage.

Pour lancer le programme : 
Se placer dans le dossier au dessus des dossiers clavardage et histories et exécuter la commande :
"java clavardage/Run"

Nouvelle ligne de compilation (après inclusion des servlets) : 
javac -d classes src/presenceServer/ClavardageServlet.java

Pour lancer le serveur TomCat :
sudo $CATALINA_HOME/bin/startup.sh
Pour stopper le serveur TomCat :
sudo $CATALINA_HOME/bin/shutdown.sh

Pour inclure l'API Servlet (qui fait partie de JEE) :
Ajouter dans le .bashrc ; export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64
et
You could copy "servlet-api.jar" from "<CATALINA_HOME>\lib" to "<JAVA_HOME>\jre\lib\ext" (the JDK Extension Directory), or include the Servlet JAR file in your CLASSPATH.
