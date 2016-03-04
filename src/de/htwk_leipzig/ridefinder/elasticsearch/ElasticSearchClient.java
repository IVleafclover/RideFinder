package de.htwk_leipzig.ridefinder.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ElasticSearchClient {

	private Client client;

	private boolean connected;

	public ElasticSearchClient() {
		super();
		this.connected = false;
	}

	public void connect() {
		if (!isConnected()) {
			try {
				this.client = TransportClient
						.builder()
						.build()
						.addTransportAddress(
								new InetSocketTransportAddress(InetAddress
										.getByName("localhost"), 9300));

				setConnected(true);

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void close() {
		if (isConnected()) {
			this.client.close();
			setConnected(false);
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}
