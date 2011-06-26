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
package eg.nileu.cis.nilestore.webserver.servlets;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eg.nileu.cis.nilestore.webserver.port.OperationRequest;
import eg.nileu.cis.nilestore.webserver.port.WebRequest;
import eg.nileu.cis.nilestore.webserver.port.WebResponse;

// TODO: Auto-generated Javadoc
/**
 * The Class OperationalServlet.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
@SuppressWarnings("serial")
public class OperationalServlet extends AbstractServlet {

	/** The response collector. */
	private WebResponseCollector responseCollector;

	/** The home location. */
	private final String homeLocation;

	/**
	 * Instantiates a new operational servlet.
	 * 
	 * @param servletId
	 *            the servlet id
	 * @param homeLocation
	 *            the home location
	 * @param requestTimeout
	 *            the request timeout
	 */
	public OperationalServlet(String servletId, String homeLocation,
			int requestTimeout) {
		super(servletId);
		this.responseCollector = new WebResponseCollector(requestTimeout);
		this.homeLocation = homeLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		InputStream in = getClass().getResourceAsStream(homeLocation);
		byte[] buffer = new byte[1024];
		int numbytes = in.read(buffer);
		while (numbytes != -1) {
			resp.getOutputStream().write(buffer, 0, numbytes);
			resp.getOutputStream().flush();
			numbytes = in.read(buffer);
		}
		in.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		OperationRequest request = new OperationRequest(req);
		WebRequest requestEvent = new WebRequest(webComponent.getRequestId(),
				servletId, request);

		webComponent.triggerWebRequest(requestEvent);
		responseCollector.collect(requestEvent, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eg.nileu.cis.nilestore.webserver.servlets.AbstractServlet#triggerWebResponse
	 * (eg.nileu.cis.nilestore.webserver.port.WebResponse)
	 */
	@Override
	public void triggerWebResponse(WebResponse response) {
		responseCollector.addResponse(response);
	}

}
