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
package eg.nileu.cis.nilestore.webserver.port;

import java.nio.charset.Charset;

import se.sics.kompics.Response;

// TODO: Auto-generated Javadoc
/**
 * The Class WebResponse.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class WebResponse extends Response {

	/** The request event. */
	private final WebRequest requestEvent;

	/** The data. */
	private final byte[] data;

	/** The part index. */
	private final int partIndex;

	/** The parts total. */
	private final int partsTotal;

	/**
	 * Instantiates a new web response.
	 * 
	 * @param requestEvent
	 *            the request event
	 * @param data
	 *            the data
	 * @param index
	 *            the index
	 * @param total
	 *            the total
	 */
	public WebResponse(WebRequest requestEvent, byte[] data, int index,
			int total) {
		super(requestEvent);
		this.data = data;
		this.requestEvent = requestEvent;
		this.partIndex = index;
		this.partsTotal = total;
	}

	/**
	 * Instantiates a new web response.
	 * 
	 * @param requestEvent
	 *            the request event
	 * @param data
	 *            the data
	 * @param index
	 *            the index
	 * @param total
	 *            the total
	 */
	public WebResponse(WebRequest requestEvent, String data, int index,
			int total) {
		this(requestEvent, data.getBytes(Charset.forName("UTF-8")), index,
				total);
	}

	/**
	 * Instantiates a new web response.
	 * 
	 * @param requestEvent
	 *            the request event
	 * @param data
	 *            the data
	 */
	public WebResponse(WebRequest requestEvent, String data) {
		this(requestEvent, data.getBytes(Charset.forName("UTF-8")), 1, 1);
	}

	/**
	 * Instantiates a new web response.
	 * 
	 * @param requestEvent
	 *            the request event
	 * @param data
	 *            the data
	 */
	public WebResponse(WebRequest requestEvent, byte[] data) {
		this(requestEvent, data, 1, 1);
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Gets the str data.
	 * 
	 * @return the str data
	 */
	public String getStrData() {
		return new String(data);
	}

	/**
	 * Gets the request event.
	 * 
	 * @return the request event
	 */
	public WebRequest getRequestEvent() {
		return requestEvent;
	}

	/**
	 * Gets the part index.
	 * 
	 * @return the part index
	 */
	public int getPartIndex() {
		return partIndex;
	}

	/**
	 * Gets the parts total.
	 * 
	 * @return the parts total
	 */
	public int getPartsTotal() {
		return partsTotal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WebResponse [partIndex/partsTotal = " + partIndex + "/"
				+ partsTotal + " ]";
	}

}
