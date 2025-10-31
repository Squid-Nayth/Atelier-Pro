# Guide du Contributeur

Ce document répertorie les points les plus importants à connaître pour l’équipe de ce dépôt afin de contribuer au projet **Atelier pro**, notamment via des **rapports de bugs** ou des **pull requests**.

Ce projet est développé avec **Java, JDBC**, et intégrera plus tard **MySQL**.

> Avant de contribuer, veuillez lire le fichier [README](README.md) afin de prendre connaissance des prérequis du projet.

---

## Table des matières

* [Style du code](#Style-de-code)
* [Branches](#Branches)
* [Contribution via pull requests](#contribution-via-pull-requests)
* [Licence](#licence)

---

## Style de code

Pour assurer une meilleure lisibilité et une vision claire du travail effectué, veillez à rendre votre code **propre** et **facile à maintenir**.
Ajoutez également des commentaires **uniquement** lorsque cela est nécessaire pour apporter des précisions sur votre code.

Ce dépôt demande le respect des conventions de nommage suivantes :

* **camelCase** : pour les noms de variables et de fonctions
* **PascalCase** : pour les noms de composants
* **kebab-case** : pour les ressources (images, fichiers, etc.)

---

## Branches

Le code source du projet est divisé en plusieurs branches :

* **[Branche stable](https://github.com/Squid-Nayth/Atelier-Pro/tree/stable)** :
  Il s’agit de la version la plus stable du dépôt.
  Elle ne contient pas de bugs critiques et est principalement utilisée pour l'utilisation et la compilation du projet.

* **[Branche dev](https://github.com/Entertainment-Visual-Art-Studio/Screenfix/tree/dev)** :
  C’est la branche principale de développement, sur laquelle l’équipe travaille activement.
  Toutes les autres branches de développement sont rattachées à celle-ci.
  C’est la branche la plus active du dépôt et elle est continuellement testée par l’équipe.
  Elle peut contenir des bugs majeurs, voire ne pas compiler du tout.
  La meilleure façon de contribuer à cette branche est de **créer une nouvelle branche dérivée** d’elle, puis d’y apporter vos modifications.

* **Branches de fonctionnalités (`features/`)** :
  Les branches dont le nom commence par `features/` sont créées pour **implémenter une nouvelle fonctionnalité**.
  Les branches destinées à **améliorer une fonctionnalité existante** doivent utiliser le préfixe `features/improve/`.

* **Branches de correction (`fixes/`)** :
  Les branches dont le nom commence par `fixes/` sont créées pour **corriger un bug spécifique** présent dans la branche de développement.

* **Branches de version (`releases/`)** :
  Ces branches conservent les **anciennes versions** du site afin de garder une trace des **archives du code**.

---

Veuillez **respecter les préfixes** mentionnés ci-dessus et **créer une seule branche par problème spécifique**, qu’il s’agisse d’une **nouvelle fonctionnalité**, d’une **amélioration**, ou d’une **correction de bug**.

**Ce document est encore en cours d'écriture...**

