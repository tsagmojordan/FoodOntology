package com.example.food_search_app;

import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

public class OntologyExtractor {
    public static void run() throws IOException {
        // Charger l'ontologie depuis le fichier TTL
        Model model = RDFDataMgr.loadModel("ontology.ttl");
        String ns = "http://www.semanticweb.org/jordan-tsagmo/ontologies/2025/4/untitled-ontology-5#";
        String rdfs = "http://www.w3.org/2000/01/rdf-schema#";

        // Définir les ressources et propriétés de l'ontologie
        Resource dishClass = model.getResource(ns + "Dish");
        Property labelProp = model.getProperty(rdfs + "label");
        Property composeOf = model.getProperty(ns + "composeOf");
        Property composedOf = model.getProperty(ns + "composedOf");
        Property hasRecipe = model.getProperty(ns + "hasrecipe");
        Property belongTo = model.getProperty(ns + "belongto");
        Property hasImage = model.getProperty(ns + "hasImage");
        Property mayPrevent = model.getProperty(ns + "mayPrevent");

        // Créer un tableau JSON pour stocker les plats
        JSONArray dishes = new JSONArray();

        // Itérer sur les instances de la classe Dish
        StmtIterator dishIter = model.listStatements(null, RDF.type, dishClass);
        while (dishIter.hasNext()) {
            Resource dish = dishIter.next().getSubject();
            JSONObject dishData = new JSONObject();

            // Extraire le nom du plat
            String dishName = model.getProperty(dish, labelProp) != null 
                ? model.getProperty(dish, labelProp).getString() : "Unknown";
            dishData.put("name", dishName);

            // Extraire les ingrédients
            JSONArray ingredients = new JSONArray();
            StmtIterator ingIter = model.listStatements(dish, composeOf, (RDFNode) null);
            while (ingIter.hasNext()) {
                Resource ingredient = ingIter.next().getObject().asResource();
                String ingLabel = model.getProperty(ingredient, labelProp) != null 
                    ? model.getProperty(ingredient, labelProp).getString() : "Unknown";
                ingredients.put(ingLabel);
            }
            dishData.put("ingredients", ingredients);

            // Extraire les composants nutritionnels
            JSONArray components = new JSONArray();
            StmtIterator compIter = model.listStatements(dish, composedOf, (RDFNode) null);
            while (compIter.hasNext()) {
                Resource component = compIter.next().getObject().asResource();
                String compLabel = model.getProperty(component, labelProp) != null 
                    ? model.getProperty(component, labelProp).getString() : "Unknown";
                components.put(compLabel);
            }
            dishData.put("components", components);

            // Extraire la recette
            String recipeText = "";
            if (model.getProperty(dish, hasRecipe) != null) {
                Resource recipe = model.getProperty(dish, hasRecipe).getResource();
                if (model.getProperty(recipe, model.getProperty(rdfs + "comment")) != null) {
                    recipeText = model.getProperty(recipe, model.getProperty(rdfs + "comment")).getString();
                }
            }
            dishData.put("recipe", recipeText);

            // Extraire la région
            String regionName = "";
            if (model.getProperty(dish, belongTo) != null) {
                Resource region = model.getProperty(dish, belongTo).getResource();
                if (model.getProperty(region, labelProp) != null) {
                    regionName = model.getProperty(region, labelProp).getString();
                }
            }
            dishData.put("region", regionName);

            // Extraire les images (en supprimant ^^anyURI)
            JSONArray images = new JSONArray();
            StmtIterator imgIter = model.listStatements(dish, hasImage, (RDFNode) null);
            while (imgIter.hasNext()) {
                String imageUrl = imgIter.next().getObject().toString();
                if (imageUrl.contains("^^")) {
                    imageUrl = imageUrl.substring(0, imageUrl.indexOf("^^"));
                }
                images.put(imageUrl);
            }
            dishData.put("images", images);

            // Extraire les maladies prévenues
            JSONArray preventedDiseases = new JSONArray();
            StmtIterator preventIter = model.listStatements(dish, mayPrevent, (RDFNode) null);
            while (preventIter.hasNext()) {
                Resource disease = preventIter.next().getObject().asResource();
                String diseaseLabel = model.getProperty(disease, labelProp) != null 
                    ? model.getProperty(disease, labelProp).getString() : "Unknown";
                preventedDiseases.put(diseaseLabel);
            }
            dishData.put("preventedDiseases", preventedDiseases);

            // Ajouter le plat au tableau JSON
            dishes.put(dishData);
        }

        // Sauvegarder le résultat dans un fichier JSON
        try (FileWriter file = new FileWriter("dishes.json")) {
            file.write(dishes.toString(2));
            System.out.println("Fichier JSON généré avec succès : dishes.json");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier JSON : " + e.getMessage());
            e.printStackTrace();
        }
    }
}