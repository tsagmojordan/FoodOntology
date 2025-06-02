package com.example.food_search_app;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {
    private final SolrClient solrClient;

    public SearchController() {
        // URL Solr adaptée pour un conteneur Docker nommé 'solr'
        String solrUrl = "http://solr:8983/solr/food_collection";
        this.solrClient = new HttpSolrClient.Builder(solrUrl).build();
    }

    @GetMapping("/")
    public String searchForm() {
        return "index";
    }

    @PostMapping("/search")
    public String search(@RequestParam("query") String query, Model model) throws Exception {
        // Créer une requête Solr
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("name:" + query + " OR ingredients:" + query + " OR components:" + query + " OR preventedDiseases:" + query);
        solrQuery.setFields("id,name,ingredients,components,recipe,region,images,preventedDiseases");

        // Exécuter la requête
        QueryResponse response = solrClient.query(solrQuery);
        SolrDocumentList results = response.getResults();

        // Préparer la liste des plats pour le modèle
        List<Map<String, Object>> dishes = new ArrayList<>();
        for (var doc : results) {
            Map<String, Object> dish = new HashMap<>();
            dish.put("name", doc.getFieldValue("name") != null ? doc.getFieldValue("name") : "");
            dish.put("ingredients", doc.getFieldValue("ingredients") != null ? doc.getFieldValue("ingredients") : new ArrayList<>());
            dish.put("components", doc.getFieldValue("components") != null ? doc.getFieldValue("components") : new ArrayList<>());
            dish.put("recipe", doc.getFieldValue("recipe") != null ? doc.getFieldValue("recipe") : "");
            dish.put("region", doc.getFieldValue("region") != null ? doc.getFieldValue("region") : "");
            dish.put("images", doc.getFieldValue("images") != null ? doc.getFieldValue("images") : new ArrayList<>());
            dish.put("preventedDiseases", doc.getFieldValue("preventedDiseases") != null ? doc.getFieldValue("preventedDiseases") : new ArrayList<>());
            dishes.add(dish);
        }

        // Ajouter les résultats et la requête au modèle
        model.addAttribute("results", dishes);
        model.addAttribute("query", query);
        return "index.html";
    }
}