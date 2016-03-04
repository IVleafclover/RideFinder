package de.htwk_leipzig.ridefinder.elasticsearch;

import java.net.UnknownHostException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * ermoeglicht die Suche
 *
 * @author Christian
 *
 */
public class Search {

	/**
	 * Sucht nach Mitfahrgelegenheiten
	 *
	 * @param client
	 * @param from
	 * @param to
	 * @param date
	 * @return Suchergebnis der Mitfahrgelegenheiten
	 * @throws UnknownHostException
	 */
	public static SearchResponse search(final Client client, final String from, final String to, final String date)
			throws UnknownHostException {
		final BoolQueryBuilder searchQuery = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("from", from.toLowerCase()))
				.must(QueryBuilders.termQuery("to", to.toLowerCase())).must(QueryBuilders.termQuery("date", date));

		final SearchResponse searchResponse = client.prepareSearch("rides").setQuery(searchQuery).execute().actionGet();

		return searchResponse;
	}

	/**
	 * erstellt einen Client, verbindet diesen und sucht dann, anschliessend
	 * wird der Client wieder geschlossen
	 *
	 * @param from
	 * @param to
	 * @param date
	 * @return Suchergebnis Suchergebnis der Mitfahrgelegenheiten
	 * @throws UnknownHostException
	 */
	public static SearchResponse searchWithOutClient(final String from, final String to, final String date)
			throws UnknownHostException {
		final ElasticSearchClient client = new ElasticSearchClient();
		client.connect();

		final SearchResponse searchResponse = Search.search(client.getClient(), from, to, date);

		client.close();

		return searchResponse;
	}
}
