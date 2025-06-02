package com.example.food_search_app;



import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SolrIndexer {
    public static boolean run() throws IOException {
        // URL Solr pour un conteneur Docker nommé 'solr'
        boolean haveStarted = false;
        String solrUrl = "http://solr:8983/solr/food_collection";
        try (SolrClient client = new HttpSolrClient.Builder(solrUrl).build()) {
            // Lire le fichier dishes.json
            String jsonString = new String(Files.readAllBytes(Paths.get("dishes.json")));
            JSONArray dishes = new JSONArray(jsonString);

            // Indexer chaque plat
            for (int i = 0; i < dishes.length(); i++) {
                JSONObject dish = dishes.getJSONObject(i);
                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("id", "dish_" + i);

                // Ajouter les champs avec vérifications
                doc.addField("name", dish.has("name") ? dish.getString("name") : "");
                doc.addField("ingredients", dish.has("ingredients") ? dish.getJSONArray("ingredients").toList() : new JSONArray());
                doc.addField("components", dish.has("components") ? dish.getJSONArray("components").toList() : new JSONArray());
                doc.addField("recipe", dish.has("recipe") ? dish.getString("recipe") : "");
                doc.addField("region", dish.has("region") ? dish.getString("region") : "");
                doc.addField("images", dish.has("images") ? dish.getJSONArray("images").toList() : new JSONArray());
                doc.addField("preventedDiseases", dish.has("preventedDiseases") ? dish.getJSONArray("preventedDiseases").toList() : new JSONArray());

                client.add(doc);
            }

            // Valider l'indexation
            client.commit();
            haveStarted = true;
            System.out.println("Données indexées avec succès dans Solr");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'indexation : " + e.getMessage());
            e.printStackTrace();
        }
        return haveStarted;
    }
}