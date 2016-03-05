package de.htwk_leipzig.ridefinder.model;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Helper fuer die Lister der Suchergbenisse
 *
 * @author Christian
 *
 */
public class RideLoopHelper implements Comparable<RideLoopHelper> {

	/**
	 * Bezeichner fuer keinen angegebenen Preis
	 */
	private final static String NULLPRICE = "null";

	/**
	 * externer Link zum Suchergebnis des Providers
	 */
	private String id;

	/**
	 * Provider der Mitfahrgelegenheit
	 */
	private String provider;

	/**
	 * Uhrzeit
	 */
	private String time;

	/**
	 * freie Plaetze
	 */
	private String seat;

	/**
	 * Preis
	 */
	private String price;

	/**
	 * Formatter fuer die Preiskonvertierung
	 */
	private final DecimalFormat PRICEFORMAT = new DecimalFormat("#,##");

	/**
	 * Konstruktor
	 *
	 * @param id
	 * @param fieldsMap
	 */
	public RideLoopHelper(final String id, final Map<String, Object> fieldsMap) {
		super();
		setId(id);
		setProvider((String) fieldsMap.get("provider"));
		setTime((String) fieldsMap.get("time"));
		setSeat((String) fieldsMap.get("seat"));
		setPrice(convertPrice((String) fieldsMap.get("price")));
	}

	/**
	 * Wandelt den Preis in einen String um
	 *
	 * @param priceToFormat
	 * @return umgeandelter Preis
	 */
	private String convertPrice(final String priceToFormat) {
		if (priceToFormat.equals("null")) {
			return "-";
		}
		return PRICEFORMAT.format(Float.parseFloat(priceToFormat));
	}

	/**
	 * @return der Link
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * @return der Provider
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * @param provider
	 */
	public void setProvider(final String provider) {
		this.provider = provider;
	}

	/**
	 * @return Uhrzeit
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time
	 */
	public void setTime(final String time) {
		this.time = time;
	}

	/**
	 * @return freie Plaetze
	 */
	public String getSeat() {
		return seat;
	}

	/**
	 * @param seat
	 */
	public void setSeat(final String seat) {
		this.seat = seat;
	}

	/**
	 * @return Preis
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param price
	 */
	public void setPrice(final String price) {
		this.price = price;
	}

	@Override
	public int compareTo(final RideLoopHelper other) {
		String thisTime = this.getTime();
		String otherTime = other.getTime();

		thisTime = makeTimeComparable(thisTime);
		otherTime = makeTimeComparable(otherTime);

		return thisTime.compareTo(otherTime);
	}

	/**
	 * konvertiert Uhrzeiten so, dass sie verglichen werden koennen
	 *
	 * @param timeToConvert
	 * @return konvertierter Zeit-String
	 */
	private String makeTimeComparable(final String timeToConvert) {
		if (time.length() < 5) {
			return "0" + time;
		}
		return time;
	}

}
