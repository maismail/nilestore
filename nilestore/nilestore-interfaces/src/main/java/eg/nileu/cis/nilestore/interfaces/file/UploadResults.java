/**
 * This file is part of the Nilestore project.
 * 
 * Copyright (C) (2011) Nile University (NU)
 *
 * Nilestore is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eg.nileu.cis.nilestore.interfaces.file;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import eg.nileu.cis.nilestore.interfaces.uri.IURI;

// TODO: Auto-generated Javadoc
/**
 * The Class UploadResults.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class UploadResults {

	// TODO: add sharemap and severmap
	// TODO: add dumptoHtml
	/** The times. */
	private Map<String, Double> times;

	/** The uri. */
	private IURI uri;

	/**
	 * Instantiates a new upload results.
	 */
	public UploadResults() {
		this.times = new HashMap<String, Double>();
	}

	/**
	 * Gets the uri.
	 * 
	 * @return the uri
	 */
	public IURI getUri() {
		return uri;
	}

	/**
	 * Sets the uri.
	 * 
	 * @param uri
	 *            the new uri
	 */
	public void setUri(IURI uri) {
		this.uri = uri;
	}

	/**
	 * Sets the timefor key.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setTimeforKey(String key, double value) {
		times.put(key, value);
	}

	/**
	 * Adds the timeto key.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void addTimetoKey(String key, double value) {
		double lastval = 0;
		if (times.containsKey(key))
			lastval = times.get(key);
		times.put(key, value + lastval);
	}

	/**
	 * Gets the times.
	 * 
	 * @return the times
	 */
	public Map<String, Double> getTimes() {
		return times;
	}

	/**
	 * Dumptohtml.
	 * 
	 * @param webAddress
	 *            the web address
	 * @return the string
	 */
	public String dumptohtml(String webAddress) {
		StringBuilder sb = new StringBuilder();

		sb.append("<h4>Upload Succesfully completed</h4>");
		sb.append("<b>File URI = </b>");
		sb.append(uri.toString());
		sb.append("<br />");

		sb.append("<a href=\"" + String.format(webAddress, uri.toString())
				+ "\"");
		sb.append("> Download Link </a>");

		sb.append("<h4> Times </h4>");
		for (Map.Entry<String, Double> entry : times.entrySet()) {
			sb.append(entry.getKey() + " = " + entry.getValue() + " msec");
			sb.append("<br />");
		}
		return sb.toString();
	}

	/**
	 * Dumpto json.
	 * 
	 * @param webAddress
	 *            the web address
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	public String dumptoJson(String webAddress) {
		JSONObject obj = new JSONObject();
		obj.put("downloadLink", String.format(webAddress, uri.toString()));
		obj.put("cap", uri.toString());
		obj.put("times", times);
		return obj.toJSONString();
	}
}
