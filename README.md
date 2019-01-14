Ce dossier contient :
- L’ensemble des composants de l’application client de clavardage (les fichiers .java)
- Un dossier compressé (.zip) contenant les fichiers utiles au déploiement du serveur de présence

# APPLICATION CLIENT

Pour **compiler les fichiers sources de l’application client**, il suffit d’exécuter la commande suivante : 
>javac *.java

**Installation** : 
Créer un dossier "histories" au même niveau que le dossier clavardage.

Pour **lancer le programme** : 
Se placer dans le dossier au dessus des dossiers clavardage et histories et exécuter la commande :
>java clavardage/Run

## Présentation rapide du parcours de l’utilisateur dans notre application :

## 1) Fenêtre de connexion :

![Optional Text](../master/pictures/screenshot1.png)

Avant de se connecter à l’application de Chat, **l’utilisateur doit choisir** :

- _le mode de découverte du réseau_ : soit **basée sur UDP** (ce qui requiert que les différents utilisateurs soient sur le même domaine de broadcast et limite donc l’utilisation de ce mode aux réseaux locaux), soit **basée sur l’utilisation d’un serveur de présence HTTP** (ce qui requiert qu’un serveur de présence HTTP soit déployé et accessible, à une addresse et un numéro de port connus et à renseigner dans l’application).

- _le mode de stockage des historiques_ : soit **dans des fichiers** (requiert seulement la présence d’un dossier “histories” au même niveau que le dossier “clavardage”), soit **dans une base de données** (requiert une installation d’un logiciel de base de données, et la création de tables, comme décrit dans la section dédiée de ce README).
Au moment de se connecter (après le clic sur le bouton dédié), **l’application vérifie que le pseudo rentré par l’utilisateur est non vide et libre** (en fonction du mode de découverte du réseau choisi, soit avec une requête GET au serveur de présence soit par broadcast UDP et analyse des réponses).

## 2) Fenêtre principale :

![Optional Text](../master/pictures/screenshot1.png)

Après connection, l’utilisateur a, sur cette fenêtre principale, la possibilité de :
- _voir la liste des utilisateurs en ligne_ (seulement l’utilisateur Julien sur la capture d’écran par exemple)
- _changer son pseudo_. Si le changement n’est pas possible (pseudo déjà pris par exemple), l’application le signale à l’utilisateur et n’effectue pas le changement.
- _lancer une conversation avec un utilisateur_ (si une conversation n’est pas déjà lancée avec cet utilisateur, une nouvelle fenêtre s’ouvre alors, chez l’utilisateur distant et chez l’utilisateur local).
- _afficher la liste des historiques stockés sur cette machine_ (une nouvelle fenêtre s’ouvre, et l’utilisateur peut alors consulter l’historique de conversation de son choix).
- _se déconnecter_

*Notes :* 
- *Dans tous les cas, les historiques de conversation sont sauvegardés automatiquement par l’application.*
- *La réception d’un message dans une conversation déjà ouverte entraine la mise au premier plan de la fenêtre de conversation concernée.*
- *Les modes de découverte des utilisateurs en ligne et de stockage des historiques sont rappelés en bas de la fenêtre*
- *La liste des utilisateurs en ligne est mise à jour automatiquement grâce à une tâche périodique programmée sur un Timer (de cette manière on évite d'inonder le réseau de messages ou de surcharger le seveur de présence inutilement)*


# SERVEUR DE PRESENCE

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
C’est l’arborescence classique d’une servlet Java, pour TomCat. **Le répertoire “classes” contient le résultat de la compilation de la servlet, tandis que le répertoire “src” contient le code source de la servlet.**
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

**Le format général des requêtes (GET) que notre serveur de présence va traiter** est le suivant :
>**addresseDéploiementServeurTomcat/presenceserver/connect?display=”unBooléen”&pseudo=”unPseudo”&type=”unType”**

- _unBooléen_ peut être soit true (le serveur renvoie alors un affichage en HTML de plusieurs paramètres, notamment la liste des utilisateurs connectés, le nombre de requêtes reçues...etc), soit autre chose (auquel cas le serveur renvoie des données dans un format exploitable par l’application client).
- _unPseudo_ est le pseudo de l’utilisateur qui effectue la requête
- _unType_ est le type de requête que l’utilisateur effectue. Ce peut être :
	- *connect* : l’utilisateur effectue cette requête pour **signaler au serveur de présence qu’il est en ligne.**
	- *deconnect* : l’utilisateur effectue cette requête pour **signaler au serveur de présence qu’il n’est plus en ligne.**
	- *change* : l’utilisateur effectue cette requête pour **signaler au serveur de présence qu’il change de pseudo.** Le paramètre _unPseudo_ contient alors le nouveau pseudo, et c’est l’addresse du client qui est utilisée par le serveur pour l’identifier.
	- *info* : l’utilisateur effectue cette requête pour **demander au serveur la liste des utilisateurs connectés.**
	- *isfree* : l’utilisateur effectue cette requête pour **demander au serveur si le pseudo renseigné dans le champ _unPseudo_ est libre (ou s’il est déjà pris).**

Les différentes actions associées aux requêtes listées ci-dessus sont effectuées indépendamment de la valeur du paramètre display. Seul le format de la réponse du serveur change.

**Affichage dans un serveur web des informations du serveur**
Requête utilisée :
>localhost:8080/presenceserver/connect?display="true"&type="info"&pseudo="julien"

![Optional Text](../master/pictures/screenshot3.png)



