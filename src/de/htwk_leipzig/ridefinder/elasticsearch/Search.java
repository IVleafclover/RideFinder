package de.htwk_leipzig.ridefinder.elasticsearch;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

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
	public static List<SearchHit> search(final Client client, final String from, final String to, final String date)
			throws UnknownHostException {
		final List<SearchHit> hits = new ArrayList<SearchHit>();

		final BoolQueryBuilder searchQuery = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("from", from.toLowerCase()))
				.must(QueryBuilders.termQuery("to", to.toLowerCase())).must(QueryBuilders.termQuery("date", date));

		SearchResponse searchResponse = client.prepareSearch("rides").setScroll(new TimeValue(60000))
				.setQuery(searchQuery).execute().actionGet();

		while (true) {
			for (final SearchHit hit : searchResponse.getHits().getHits()) {
				hits.add(hit);
			}
			searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(60000))
					.execute().actionGet();
			// keine Ergebnisse mehr wiedergegeben
			if (searchResponse.getHits().getHits().length == 0) {
				break;
			}
		}

		return hits;
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
	public static List<SearchHit> searchWithOutClient(final String from, final String to, final String date)
			throws UnknownHostException {
		final ElasticSearchClient client = new ElasticSearchClient();
		client.connect();

		final List<SearchHit> hits = Search.search(client.getClient(), from, to, date);

		client.close();

		return hits;
	}

	/**
	 * Sucht nach Mitfahrgelegenheiten zu Datum
	 *
	 * @param client
	 * @param date
	 * @return Suchergebnis der Mitfahrgelegenheiten
	 * @throws UnknownHostException
	 */
	public static List<SearchHit> searchByDate(final Client client, final String date) throws UnknownHostException {
		final List<SearchHit> hits = new ArrayList<SearchHit>();

		final BoolQueryBuilder searchQuery = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("date", date));

		SearchResponse searchResponse = client.prepareSearch("rides").setScroll(new TimeValue(60000))
				.setQuery(searchQuery).execute().actionGet();

		while (true) {
			for (final SearchHit hit : searchResponse.getHits().getHits()) {
				hits.add(hit);
			}
			searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(60000))
					.execute().actionGet();
			// keine Ergebnisse mehr wiedergegeben
			if (searchResponse.getHits().getHits().length == 0) {
				break;
			}
		}

		return hits;
	}
}
