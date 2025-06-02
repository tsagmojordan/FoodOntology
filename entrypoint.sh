#!/bin/bash
# Exécuter l'extracteur d'ontologie
java -jar /app/OntologyExtractor.jar
# Attendre que Solr soit prêt
until $(curl --output /dev/null --silent --head --fail [invalid url, do not cite]); do
    printf '.'
    sleep 5
done
# Exécuter l'indexeur Solr
java -jar /app/SolrIndexer.jar