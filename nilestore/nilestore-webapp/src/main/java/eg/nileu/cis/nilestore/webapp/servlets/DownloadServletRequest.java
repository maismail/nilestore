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
package eg.nileu.cis.nilestore.webapp.servlets;

import javax.servlet.http.HttpServletRequest;

import eg.nileu.cis.nilestore.interfaces.uri.IURI;
import eg.nileu.cis.nilestore.uri.BadURIException;
import eg.nileu.cis.nilestore.uri.uri;
import eg.nileu.cis.nilestore.webserver.port.ServletRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class DownloadServletRequest.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class DownloadServletRequest extends ServletRequest {

	/** The cap. */
	private IURI cap;

	/** The download cap. */
	private boolean downloadCap;

	/** The error message. */
	private String errorMessage;

	/** The save. */
	private boolean save;

	/**
	 * Instantiates a new download servlet request.
	 * 
	 * @param request
	 *            the request
	 */
	public DownloadServletRequest(HttpServletRequest request) {
		super(request);
		downloadCap = true;
		init();
	}

	/**
	 * Checks if is download cap.
	 * 
	 * @return true, if is download cap
	 */
	public boolean isDownloadCap() {
		return downloadCap;
	}

	/**
	 * Checks if is save enabled.
	 * 
	 * @return true, if is save enabled
	 */
	public boolean isSaveEnabled() {
		return save;
	}

	/**
	 * Gets the cap.
	 * 
	 * @return the cap
	 */
	public IURI getCap() {
		return cap;
	}

	/**
	 * Gets the error message.
	 * 
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Inits the.
	 */
	private void init() {
		String sparam = getParameter("save");
		if (sparam != null) {
			save = sparam.toLowerCase().equals("true");
		} else {
			save = false;
		}
		String[] data = request.getRequestURI().split(SPLIT_CHAR);
		if (data.length != 3) {
			downloadCap = false;
			errorMessage = "is not a valid uri";
			return;
		}
		String capuri = data[2];

		try {
			cap = uri.from_string(capuri);
		} catch (BadURIException e) {
			downloadCap = false;
			errorMessage = e.getMessage();
		}
	}
}
