package com.example.food_search_app;

import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@Slf4j
@SpringBootApplication
public class FoodSearchAppApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FoodSearchAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		OntologyExtractor.run();
		int i = 0;
		for (int j = 0; j < 10; j++) {
			i++;
			if (!SolrIndexer.run()) {
				log.info("tentative {} échoué;Reindexation en cours...", i);
				Thread.sleep(800);
			}else {
				log.info("tentative {} réussie!", i);
				break;
			}
		}
	}
}
