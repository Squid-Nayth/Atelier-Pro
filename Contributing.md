# Guide du Contributeur

Ce document présente les informations essentielles pour les membres de l’équipe souhaitant **contribuer au projet Atelier Pro**, notamment à travers des **rapports de bugs** ou des **pull requests**.

Le projet est développé en **Java** avec **JDBC**, et intégrera prochainement une base de données **MySQL**.

> 📖 Avant toute contribution, veuillez consulter le fichier [README](README.md) afin de prendre connaissance des prérequis techniques et du fonctionnement global du projet.

---

## Table des matières

* [Style de code](#style-de-code)
* [Branches](#branches)
* [Contribution via pull requests](#contribution-via-pull-requests)

---

## Style du code

Pour garantir une meilleure **lisibilité** et une **maintenance facilitée** du code, veillez à produire un code **propre**, **structuré** et **cohérent**.
Les commentaires doivent être utilisés **uniquement lorsque cela est nécessaire**, afin d’expliquer des parties complexes ou des choix particuliers.

### Conventions de nommage à respecter :

* **camelCase** pour les noms de variables et de fonctions
* **PascalCase** pour les noms de classes et de composants
* **kebab-case** pour les noms de fichiers, ressources et images
* Le **langage utilisé** pour le nommage de l’ensemble des éléments du projet (fichiers, dossiers, variables, etc.) ainsi que pour la rédaction des **commentaires** sera le **français**.
Ce choix s’explique par le fait que le code source initial est majoritairement rédigé en français, ce qui garantit une **meilleure compréhension et cohérence** au sein de l’équipe de développement.

---

## Branches

Le dépôt est organisé en plusieurs branches, chacune ayant un rôle bien défini :

### **Branche stable**

👉 [stable](https://github.com/Squid-Nayth/Atelier-Pro/tree/stable)
Cette branche contient la version la plus fiable du projet.
Elle ne présente aucun bug critique et sert de référence pour la compilation et l’utilisation du programme.

### **Branche de développement**

👉 [dev](https://github.com/Entertainment-Visual-Art-Studio/Screenfix/tree/dev)
C’est la branche principale de **développement actif**.
Toutes les nouvelles branches de fonctionnalités ou de corrections doivent en dériver.
Elle peut contenir des bugs importants ou des erreurs de compilation, car elle évolue en continu.

> 💡 Pour contribuer efficacement, créez une **nouvelle branche de fonctionnalités** à partir de `dev`, effectuez vos modifications, puis soumettez une **pull request**.

---

### **Branches de fonctionnalités**

`features/`
Ces branches sont destinées à l’**ajout d’une nouvelle fonctionnalité**.
Pour les améliorations d’éléments déjà existants, utilisez le préfixe :
`features/improve/`.

### **Branches de corrections**

`fixes/`
Elles servent à la **correction d’un bug précis** détecté dans la branche de développement.

### **Branches de versions**

`releases/`
Ces branches conservent les **versions archivées** du projet, permettant de garder une trace des évolutions et itérations du code.

---

## Bonnes pratiques

* Respectez scrupuleusement les **préfixes** de branches mentionnés ci-dessus.
* Créez **une branche par problème spécifique** (nouvelle fonctionnalité, amélioration ou correction).
* Décrivez clairement vos commits et pull requests pour faciliter la revue du code.

---

> Ce guide est **en cours de rédaction** et sera complété ultérieurement...

---


