package de.htwk_leipzig.ridefinder.model;

import java.text.DecimalFormat;
import java.util.Map;

public class RideLoopHelper implements Comparable<RideLoopHelper> {

	private String id;

	private String provider;

	private String time;

	private String seat;

	private String price;

	private final DecimalFormat PRICEFORMAT = new DecimalFormat("#,##");

	public RideLoopHelper(final String id, final Map<String, Object> fieldsMap) {
		super();
		setId(id);
		setProvider((String) fieldsMap.get("provider"));
		setTime((String) fieldsMap.get("time"));
		setSeat((String) fieldsMap.get("seat"));
		setPrice(convertPrice((String) fieldsMap.get("price")));
	}

	private String convertPrice(final String price) {
		return PRICEFORMAT.format(Float.parseFloat(price));
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(final String provider) {
		this.provider = provider;
	}

	public String getTime() {
		return time;
	}

	public void setTime(final String time) {
		this.time = time;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(final String seat) {
		this.seat = seat;
	}

	public String getPrice() {
		return price;
	}

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

	private String makeTimeComparable(final String time) {
		if (time.length() < 5) {
			return "0" + time;
		}
		return time;
	}

}
