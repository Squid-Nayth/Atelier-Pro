# Application de gestion des employés des ligues – M2L

Application Java en ligne de commande permettant la gestion des employés des ligues de la M2L.

## ⬇️ Pour commencer

### Prérequis

* **Java JDK 17** ou supérieur.
* **Eclipse IDE**, il est fortement recommandé d'utiliser cette IDE car le projet est configuré pour Eclipse, néamoins vous pouvez l'importer sur un autre IDE mais vous devrez installez les dépendances vous même sous peine de rencontrez des erreurs.

### Installation

1. Cloner le répertoire : ``git clone https://github.com/Squid-Nayth/https/Atelier-Pro.git``

 Autre option : 
 
 - Vous pouvez également utiliser un client Git comme Github Desktop. Pour ce faire, [installer Github Desktop](https://desktop.github.com/download/) (Windows et MacOS) et cliquer sur "Clone repository".

<p align="center">
  <img width="523" height="224" alt="image" src="https://github.com/user-attachments/assets/dacd86be-49d9-4589-b326-8ca80a200d6e" />
</p>


2. Ouvrez Eclipse
3. Cliquez sur "File", puis "Import" et dans la fenêtre d'importation, cliquez sur ''Projets from Git'' puis sur "Suivant".
4. Dans la fenêtre Sélectionner la source du référentiel, cliquez sur ''Existing local repository'' et selectionnez le répertoire ou vous avez clonez le projet.
5. Suivez les instructions de l'assistant et cliquez sur ''Import existing eclipse project'' puis sur "Terminer" pour que celui-ci analyse le contenu du dossier du projet afin de trouver les projets à importer. 
6. Importez-les ensuite dans l'IDE. Le projet importé apparaîtra dans la fenêtre .
7. Ouvrir le fichier du projet dans **Eclipse** et lancer l’application depuis la classe principale (Main) du projet.

**Si vous rencontrez des problèmes au niveau de l'importation du projet sur Eclipse, consultez la section "[Import](https://help.eclipse.org/latest/topic/org.eclipse.platform.doc.user/tasks/tasks-53.xhtml?cp=0_3_10)" de la documentation d'eclipse ou contactez l'un des contributeur du projet en créant un "Issue".** 

## Base de données

### Prérequis

* Installez et lancez [Docker Desktop](https://www.docker.com/).

### Lancer la base de données

1. Copiez le contenue du fichier `.env.example` qui est dans le dossier `docker/` puis créer un fichier `.env` dans ce même dossier et coller le contenue que vous avez copier précèdemment dans ce nouveau fichier, puis sauvegarder vos modifications.
 
2. Copiez le contenue du fichier `CredentialsExample.java` qui se trouve dans le dossier `jdbc/` puis créer un fichier `Credentiales.java` dans ce même dossier et coller le contenue que vous avez copier précèdemment dans ce nouveau fichier, puis sauvegarder vos modifications.
   
3. Dans `Personnel/src/personnel/GestionPersonnel.java` ligne 25, remplacez `SERIALIZATION` par `JDBC`.

4. Dans votre terminal depuis le dossier `docker/`, lancez les conteneurs :
   ```bash
   docker compose up -d
   ```
5. phpMyAdmin est accessible sur **http://localhost:8080**, connectez-vous avec **Utilisateur** : `root` et le mot de passe défini dans `MYSQL_ROOT_PASSWORD` de votre `.env`.

### Astuces

- Réinitialiser les conteneurs en cas d'erreur :
  ```bash
  docker compose down -v
  docker compose up -d
  ```
- Vérifier si les conteneurs sont lancés :
  ```bash
  docker compose ps
  ```

## Contribution

Afin de contribuer correctement à ce dépot, veuillez vous referez au [Contributing.md](./Contributing.md), à la racine du dépôt, pour plus d’informations.
