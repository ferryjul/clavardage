Ce dossier contient :
- L’ensemble des composants de l’application client de clavardage (les fichiers .java)
- Un dossier compressé (.zip) contenant les fichiers utiles au déploiement du serveur de présence
- Un driver JDBC (au format .jar)
- Un fichier SQL permettant la mise en place de la base de données utilisée (sur MySQL)
- Un rapport/guide utilisateur (au format .pdf)

*Note : Tous les diagrammes de conception sont accessibles à l'addresse suivante :* https://drive.google.com/open?id=1A7HP-Bph0FLwvJ8UnAnUXOJcFPdM0w4S

# APPLICATION CLIENT

*Un utilisateur correspond à une machine, on ne peut donc lancer qu'une seule session de l'application sur un ordinateur. Une conversation correspond au couple (pseudo de l'utilisateur distant, date de la conversation).*

Pour **compiler les fichiers sources de l’application client**, il suffit d’exécuter la commande suivante : 
>javac *.java

**Installation** : 
- En fonction du mode de découverte des utilisateurs en ligne choisi :  Avoir installé un conteneur de Servlet, et avoir placé correctement le dossier de la servlet fournie (voir la section "Serveur de présence").
- En fonction du mode de persistance des données choisi : Créer un dossier "histories" au même niveau que le dossier clavardage, ou avoir installé et configuré mySQL (voir la section "Historique et Persistance des données").
Pour **lancer le programme** : 
Se placer dans le dossier au dessus des dossiers clavardage et histories et exécuter la commande :
>java clavardage/Run

# DECOUVERTE DES UTILISATEURS EN LIGNE

**Il y a deux modes de découverte des utilisateurs en ligne disponibles**. Le premier ne nécessite pas d'installation particulière tandis que le deuxième nécessite l'installation d'un serveur de présence.

## 1. Découverte du réseau par broadcast UDP

Ce mode de découverte du réseau n'implique aucune installation particulière. Il correspond simplement à l'**échange de messages UDP d'une syntaxe particulière, analysés et exploités par tous les clients appartenant au domaine de broadcast correpondant.**

## 2. Serveur de présence

**Pour déployer le serveur de présence contenu dans le fichier “presenceserver.zip”**, l’installation d’un conteneur de servlet est un prérequis. Le dossier proposé ici a été constitué pour fonctionner avec le logiciel libre TomCat. **Voici son arborescence :**
```bash
presenceserver
	|_WEB_INF
		|_classes
			|_presenceServer
				|_ClavardageServlet.class
		|_src
			|_presenceServer
				|_ClavardageServlet.java
		|_web.xml
```
**Le fichier “web.xml” est le “descripteur de déploiement”.** Il contient, au format xml, les paramètres de déploiement de la servlet sur le serveur de TomCat. Par exemple, c’est ce fichier qui définit l’addresse URL par laquelle la servlet est accessible. Nous l’avons déjà rempli, et il n’est pas nécessaire de le modifier. Le serveur de présence est accessible à l’addresse :
>localhost:8080/presenceserver/connect

*(si le serveur TomCat est déployé localement et sur le port 8080 (le choix du port de déploiement est défini dans votre installation TomCat, et modifiable dans le fichier “server.xml” du répertoire “conf” dans le dossier d’installation de TomCat)).*

Pour **déployer le serveur de présence**, il faut copier/coller le dossier presenceserver (décompressé) dans le répertoire webapps de votre installation TomCat.
*Note : la servlet est ici déjà compilée, mais pour la (re)compiler, il est nécessaire d’inclure l’API Servlet (qui fait partie de JEE). Un moyen de le faire est de copier/coller la version de cette API fournie par TomCat (qui se trouve dans le répertoire d’installation de TomCat, dans le dossier “lib” (servlet-api.jar)) dans le répertoire d’installation de Java (sous jre/lib/ext). La commande à exécuter pour compiler la servlet et stocker le résultat dans le répertoire adéquat est alors :*

>*javac -d classes src/presenceServer/ClavardageServlet.java*

Une fois le dossier décompressé copié collé dans le repertoire webapps de TomCat, il suffit de **lancer le serveur TomCat** avec la commande :
>sudo $CATALINA_HOME/bin/startup.sh (où $CATALINA_HOME est le répertoire d’installation de TomCat)

Pour **arrêter le serveur**, la commande suivante peut être utilisée :
>sudo $CATALINA_HOME/bin/shutdown.sh

**Affichage dans un navigateur web des informations du serveur**

Requête utilisée :
>localhost:8080/presenceserver/connect?display="true"&type="info"&pseudo="julien"

# HISTORIQUE ET PERSISTANCE DES DONNEES :

 **Il y a deux modes de persistance de données disponibles**. Le premier mode nécessite l’installation de MySQL et l’utilisation de JDBC tandis que le deuxième n’a pas besoin d’utiliser MySQL et enregistre l’historique dans des fichiers texte. 
	
## 1) Historique avec base de données (MySQL et JDBC) :

Tout d’abord, **pour utiliser ce mode il faut avoir installé MySQL**. Puis, **il faut que le driver JDBC soit rajouté au classpath du projet** afin qu’il puisse être utilisé. Pour ce faire, dans notre git on retrouve un fichier.jar (mysql-connector-java-8.0.13.jar) qu’il suffit de rajouter au classpath du projet. Pour Eclipse, il faut faire un clic droit sur la base du projet Eclipse puis cliquer sur « Propriétés ». Dans le menu « Propriétés », il faut choisir « Java Build Path » puis sélectionner l’onglet « Libraries » puis « Add jars » pour pouvoir ajouter le fichier.jar au classpath.

Une fois le driver ajouté au classpath, il faut créer manuellement la base de données dans MySQL ainsi que l’utilisateur qui aura les droits sur cette base de données. Pour ce faire, il suffit de taper les commandes dans MySQL écrites dans le fichier « histo.sql » qui se trouve dans notre repository git ou alors d’exécuter le fichier (s’il se trouve dans le répertoire où a été lancé MySQL) avec la commande SQL « SOURCE histo.sql; ».

Une fois la base de données initialisée, il suffit de laisser tourner MySQL en fond puis on peut se servir de l’application avec ce mode de persistance.
	
## 2) Historique avec fichiers texte :

Pour utiliser ce mode de persistance de données, **il suffit de créer un dossier « histories » au même niveau que le dossier « clavardage »**. Dans ce dossier, les fichiers textes qui sauvegardent l’historique des messages seront automatiquement créés lors de l’utilisation de l’application.


