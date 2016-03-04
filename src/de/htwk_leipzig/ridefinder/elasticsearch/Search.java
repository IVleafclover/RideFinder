package de.htwk_leipzig.ridefinder.elasticsearch;

import java.net.UnknownHostException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

public class Search {

	public static SearchResponse search(Client client, String from, String to, String date)
			throws UnknownHostException {
		BoolQueryBuilder searchQuery = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("from", from.toLowerCase()))
				.must(QueryBuilders.termQuery("to", to.toLowerCase())).must(QueryBuilders.termQuery("date", date));

		SearchResponse searchResponse = client.prepareSearch("rides").setQuery(searchQuery).execute()
				.actionGet();

		return searchResponse;
		
//		System.out.println(searchResponse);
//
//		for (SearchHit hit : searchResponse.getHits()) {
//			System.out.println(hit.getSourceAsString());
//		}
	}

	public static SearchResponse searchWithOutClient(String from, String to, String date) throws UnknownHostException {
		ElasticSearchClient client = new ElasticSearchClient();
		client.connect();

		SearchResponse searchResponse = Search.search(client.getClient(), from, to, date);

		client.close();

		return searchResponse;
	}
}
