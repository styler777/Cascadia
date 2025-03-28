# 🐻 Cascadia - Jeu de plateau en Java

Bienvenue dans **Cascadia**, une adaptation en Java du jeu de plateau primé ! Ce jeu propose deux modes : un affichage graphique (2D) et un affichage en mode texte (1D).

---

## 📦 Pré-requis

- Java **23** ou une version plus récente
- Apache **Ant** installé ([guide d'installation Ant](https://ant.apache.org/manual/install.html))

---

## 📁 Installation

1. **Télécharger le fichier `.jar`** :
  - Téléchargez `Cascadia.jar` dans le répertoire de votre choix.

## Compilation et génération du .jar

Pour compiler les fichiers sources et générer l'exécutable :

```bash
ant jar
```

---

## 🚀 Lancement du jeu

▶️ Lancer le jeu en mode 1D (console) :

```bash
java -jar Cascadia.jar
```

## 🖼 Lancer le jeu en mode 2D (graphique) :

```bash
ant run
```

Lors du lancement, le jeu vous demandera les informations suivantes :

#### Nombre de joueurs (ex: 2)

#### Type de carte :

- F → Famille
  
- I → Intermédiaire
  

#### Mode de jeu :

- G → Graphique (2D)
  
- Texte → Mode 1D (console)
  

## 📝 Exemple d'entrée :

- Combien de joueurs ? 2
- Quel type de carte ? F
- Quel mode de jeu ? G

## 📚 Générer la Javadoc

Pour générer la documentation des classes Java :

```bash
ant javadoc
```

La documentation sera disponible dans le dossier ```doc/```.
