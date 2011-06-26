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

import se.sics.kompics.Request;

// TODO: Auto-generated Javadoc
/**
 * The Class WebRequest.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class WebRequest extends Request {

	// FIXME: change the requestId to UUID to be more usable
	/** The target. */
	private final String target;
	// private final RequestData requestData;
	/** The request. */
	private final ServletRequest request;

	/** The id. */
	private final long id;

	/**
	 * Instantiates a new web request.
	 * 
	 * @param id
	 *            the id
	 * @param target
	 *            the target
	 * @param request
	 *            the request
	 */
	public WebRequest(long id, String target, ServletRequest request) {
		this.target = target;
		// this.requestData = requestData;
		this.request = request;
		this.id = id;
	}

	/**
	 * Gets the target.
	 * 
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Gets the request.
	 * 
	 * @return the request
	 */
	public ServletRequest getRequest() {
		return request;
	}

	/**
	 * Gets the destination.
	 * 
	 * @return the destination
	 */
	public int getDestination() {
		return request.getDestination();
	}

	/**
	 * Checks if is filter enabled.
	 * 
	 * @return true, if is filter enabled
	 */
	public boolean isFilterEnabled() {
		return request.isDirected();
	}

	/*
	 * public RequestData getRequestData() { return requestData; }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WebRequest [target=" + target + ", id=" + id + "]";
	}

}
