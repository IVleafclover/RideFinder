package de.htwk_leipzig.ridefinder.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * Stellt Client-Verwaltung fuer ElasticSearch bereit
 *
 * @author Christian
 *
 */
public class ElasticSearchClient {

	/**
	 * Client-Session zu ElasticSearch
	 */
	private Client client;

	/**
	 * ist der Client mit ElasticSearch verbunden
	 */
	private boolean connected;

	/**
	 * Konstruktor
	 */
	public ElasticSearchClient() {
		super();
		this.connected = false;
	}

	/**
	 * verbindet den Client mit ElasticSearch
	 */
	public void connect() {
		if (!isConnected()) {
			try {
				this.client = TransportClient.builder().build()
						.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

				setConnected(true);

			} catch (final UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * schliesst Verbindung zu ElasticSearch
	 */
	public void close() {
		if (isConnected()) {
			this.client.close();
			setConnected(false);
		}
	}

	/**
	 * @return ist verbunden
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @param connected
	 */
	public void setConnected(final boolean connected) {
		this.connected = connected;
	}

	/**
	 * @return Client zu ElasticSearch
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @param client
	 */
	public void setClient(final Client client) {
		this.client = client;
	}

}
