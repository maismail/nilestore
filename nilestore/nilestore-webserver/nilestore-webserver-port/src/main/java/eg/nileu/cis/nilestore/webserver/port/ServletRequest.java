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

import javax.servlet.http.HttpServletRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class ServletRequest.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class ServletRequest {

	/** The SPLI t_ char. */
	protected final String SPLIT_CHAR = "/";

	/** The DES t_ fiel d_ name. */
	protected final String DEST_FIELD_NAME = "dest";

	/** The request. */
	protected final HttpServletRequest request;

	/** The dest. */
	protected int dest;

	/**
	 * Instantiates a new servlet request.
	 * 
	 * @param request
	 *            the request
	 */
	public ServletRequest(HttpServletRequest request) {
		this.request = request;
		String destparam = getParameter(DEST_FIELD_NAME);
		if (destparam != null) {
			this.dest = Integer.valueOf(destparam);
		} else {
			this.dest = -10;
		}
	}

	/**
	 * Gets the http request.
	 * 
	 * @return the http request
	 */
	public HttpServletRequest getHttpRequest() {
		return request;
	}

	/**
	 * Gets the parameter.
	 * 
	 * @param name
	 *            the name
	 * @return the parameter
	 */
	public String getParameter(String name) {
		return request.getParameter(name);
	}

	/**
	 * Checks if is directed.
	 * 
	 * @return true, if is directed
	 */
	public boolean isDirected() {
		return dest != -10;
	}

	/**
	 * Gets the destination.
	 * 
	 * @return the destination
	 */
	public int getDestination() {
		return dest;
	}
}
