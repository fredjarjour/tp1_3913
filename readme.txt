Allen Pinchuk (20217816)  &  Frédéric Jarjour (20220974)

github : https://github.com/fredjarjour/tp1_3913


## Comment Executer
Ouvrir en ligne de commande le root du projet. Vous devriez voir ceci comme le path courant : 

 path/to/project/<nom du dossier zip> 

Pour executer tloc.jar, il faut utiliser la commande suivante : 

java -jar tloc.jar <path vers un fichier>


Pour executer tassert.jar, il faut utiliser la commande suivante : 

java -jar tassert.jar <path vers un fichier>


Pour executer tls.jar, il faut utiliser la commande suivante : 

java -jar tls.jar <path vers un dossier ou fichier> 

Il est possible d'ajouter l'option -o pour créer un fichier de sortie. 

<csvPath> est le path pour un fichier qui va etre creer et il doit se termine avec .csv 

ex:

java -jar tls.jar -o <csvPath> <path vers un dossier ou fichier>


Pour executer tropcomp.jar, il faut utiliser la commande suivante : 

java -jar tropcomp.jar <path vers un fichier> <seuil>

Il est possible d'ajouter l'option -o pour créer un fichier de sortie.

<csvPath> est le path pour un fichier qui va etre creer et il doit se termine avec .csv 

ex:

java -jar tropcomp.jar -o <csvPath> <path vers un fichier> <seuil>


Vous pouvez également exécuter les fichiers java dictement en clonant le projet git:

git clone https://github.com/fredjarjour/tp1_3913


Ensuite, vous pouvez exécuter une fonction avec:

java src\main\java\com\example\<nom du fichier>.java <arguments>
