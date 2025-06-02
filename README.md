# FoodOntology
# Saveurs du Monde - Application de Recherche Culinaire

Bienvenue dans **Saveurs du Monde**, une application web qui vous permet d'explorer des plats authentiques à partir d'une ontologie alimentaire, indexée dans Apache Solr. Découvrez des recettes, leurs ingrédients, leurs origines régionales, leurs bienfaits pour la santé, et bien plus encore, grâce à une interface moderne et intuitive avec un design en bleu et noir.

Cette application est entièrement dockerisée pour un déploiement simple et rapide, utilisant Spring Boot pour l'interface web, Solr pour la recherche, et un indexeur pour traiter les données de l'ontologie.

## Fonctionnalités
- Recherchez des plats par nom, ingrédients, composants, ou maladies prévenues (ex. : "Obesity", "Chicken").
- Affichez des détails complets : nom, ingrédients, composants nutritionnels, recette, région, images, et bienfaits santé.
- Interface utilisateur responsive avec animations fluides et design attractif.
- Déploiement automatisé via Docker Compose, avec Solr, un indexeur, et l'application Spring Boot.

## Prérequis
- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)
- Java 17 (pour compiler localement, si nécessaire)
- Maven (pour construire les fichiers JAR)

## Structure du projet
```
project/
├── app/                    # Application Spring Boot
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── SearchController.java
|   |       │   └── OntologyExtractor.java
│   │           └── SolrIndexer.java
│   │       │   └── FoodSearchAppApplication
│   │       ├── resources/
│   │       │   └── templates/
│   │       │       └── index.html
│   │       └── application.properties
│   ├── docker-compose
│   ├── pom.xml
│   └── entrypoint.sh
│   ├── Dockerfile
│   └── pom.xml
│    └── ontology.ttl 
├           # Ontologie alimentaire
├── docker-compose.yml      # Configuration Docker Compose
└── README.md               # Ce fichier
```

## Installation et exécution

### 1. Cloner le projet
Clonez ou téléchargez ce projet dans un répertoire local :
```bash
git clone <https://github.com/tsagmojordan/FoodOntology.git>
cd project
```
### 2. Placer le fichier d'ontologie
Assurez-vous que le fichier `ontology.ttl` est présent dans le répertoire ressources. Ce fichier contient les données des plats à indexer.

### 4. Construire les images Docker
Construisez les images Docker pour l'application et l'indexeur :
```bash
docker-compose build
```

### 5. Lancer les services
Démarrez les services Solr, l'indexeur, et l'application avec Docker Compose :
```bash
docker-compose up -d
```

### 6. Accéder à l'application
- **Interface utilisateur** : Ouvrez [http://localhost:8080](http://localhost:8080) dans un navigateur pour accéder à la page de recherche.
- **Administration Solr** : Vérifiez les données indexées à [http://localhost:8983/solr](http://localhost:8983/solr).
- Attendez environ 1 à 2 minutes pour que les services soient pleinement opérationnels (Solr, indexation, application).

## Exemple de recherche
Une fois l'application lancée, vous pouvez effectuer des recherches sur des plats, leurs ingrédients, ou leurs bienfaits santé. Voici un exemple :

### Recherche : "Obesity"
1. Accédez à [http://localhost:8080](http://localhost:8080).
2. Dans la barre de recherche, entrez `Obesity`.
3. Cliquez sur **Rechercher**.
4. **Résultat attendu** (basé sur l'ontologie) :
    - **Plat** : Chicken Relish Without Oil
        - **Ingrédients** : Chicken, Tomato, Onion, Spices, Chili
        - **Composants** : Protein, Fiber, Micro Nutrient
        - **Recette** : Simmer chicken with tomatoes, onions, chili, and spices in a pot for 35 minutes, stirring occasionally.
        - **Région** : Africa
        - **Images** : [https://example.com/images/chicken_relish_no_oil.jpg](https://example.com/images/chicken_relish_no_oil.jpg)
        - **Bienfaits** : Breast_Cancer, Colorectal_Cancer, Obesity
    - **Plat** : Egg Boiled
        - **Ingrédients** : Egg
        - **Composants** : Protein, Fat
        - **Recette** : Place eggs in boiling water for 10-12 minutes, then cool in cold water.
        - **Région** : Africa
        - **Images** : [https://example.com/images/egg_boiled.jpg](https://example.com/images/egg_boiled.jpg)
        - **Bienfaits** : Obesity, Type_2_Diabetes

Vous pouvez également rechercher par ingrédient (ex. : `Chicken`) ou composant (ex. : `Protein`) pour explorer d'autres plats.

## Fonctionnement des services
- **Solr** : Utilise l'image officielle `solr:9.2`, crée la collection `food_collection`, et stocke les données dans un volume persistant (`solr-data`). Accessible à [http://localhost:8983/solr](http://localhost:8983/solr).
- **Indexer** : Exécute `OntologyExtractor.java` pour convertir `ontology.ttl` en `dishes.json`, puis `SolrIndexer.java` pour indexer les données dans Solr. Ce service s'arrête après l'indexation.
- **Application** : Une application Spring Boot avec une interface web (`search.html`) qui permet de rechercher des plats. Elle attend que Solr et l'indexeur soient prêts avant de démarrer.

## Configuration Solr
La collection `food_collection` est automatiquement créée avec le schéma suivant :
| Champ              | Type            | Indexed | Stored | MultiValued |
|--------------------|-----------------|---------|--------|-------------|
| id                 | string          | true    | true   | false       |
| name               | text_general    | true    | true   | false       |
| ingredients        | text_general    | true    | true   | true        |
| components         | text_general    | true    | true   | true        |
| recipe             | text_general    | true    | true   | false       |
| region             | text_general    | true    | true   | false       |
| images             | text_general    | true    | true   | true        |
| preventedDiseases   | text_general    | true    | true   | true        |

Pour définir le schéma manuellement (si nécessaire) :
```bash
curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field": [
    {"name":"id","type":"string","indexed":true,"stored":true,"required":true,"multiValued":false},
    {"name":"name","type":"text_general","indexed":true,"stored":true},
    {"name":"ingredients","type":"text_general","indexed":true,"stored":true,"multiValued":true},
    {"name":"components","type":"text_general","indexed":true,"stored":true,"multiValued":true},
    {"name":"recipe","type":"text_general","indexed":true,"stored":true},
    {"name":"region","type":"text_general","indexed":true,"stored":true},
    {"name":"images","type":"text_general","indexed":true,"stored":true,"multiValued":true},
    {"name":"preventedDiseases","type":"text_general","indexed":true,"stored":true,"multiValued":true}
  ]
}' [http://localhost:8983/solr/food_collection/schema](http://localhost:8983/solr/food_collection/schema)
```

## Dépannage
- **Solr ne répond pas** : Vérifiez que le conteneur `solr` est en cours d'exécution (`docker ps`). Consultez les logs : `docker logs solr`.
- **Indexation échoue** : Assurez-vous que `ontology.ttl` est valide et que `dishes.json` est généré dans le conteneur `indexer`. Vérifiez les logs : `docker logs indexer`.
- **Application non accessible** : Confirmez que le port 8080 est libre et consultez les logs : `docker logs app`.
- **Aucun résultat** : Vérifiez que les données sont indexées dans Solr via l'interface admin ([http://localhost:8983/solr](http://localhost:8983/solr)).

## Arrêter l'application
Pour arrêter tous les services :
```bash
docker-compose down
```

Pour supprimer les volumes et repartir de zéro :
```bash
docker-compose down -v
```

## Contribuer
Pour contribuer au projet :
1. Forkez le dépôt.
2. Créez une branche pour vos modifications : `git checkout -b ma-fonctionnalité`.
3. Soumettez une pull request avec une description claire.

## Contact
Pour toute question, contactez :
- **Email** : jordantsagmo@gmail.com
- **Téléphone** : +1237 686 68 38 88

## Licence
© 2025 Saveurs du Monde. Tous droits réservés.
