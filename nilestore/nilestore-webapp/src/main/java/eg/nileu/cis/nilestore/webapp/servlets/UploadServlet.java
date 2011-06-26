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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eg.nileu.cis.nilestore.webserver.port.WebRequest;
import eg.nileu.cis.nilestore.webserver.port.WebResponse;
import eg.nileu.cis.nilestore.webserver.servlets.AbstractServlet;

// TODO: Auto-generated Javadoc
/**
 * The Class UploadServlet.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
@SuppressWarnings("serial")
public class UploadServlet extends AbstractServlet {

	/**
	 * Instantiates a new upload servlet.
	 * 
	 * @param servletId
	 *            the servlet id
	 */
	public UploadServlet(String servletId) {
		super(servletId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (!UploadServletRequest.isFileUploadRequest(req)) {
			// throw new IllegalArgumentException("Requset is not a multipart");
			resp.sendError(resp.SC_BAD_REQUEST,
					"Requst in not a multipart, plases use a 'multipart/form-data' enctype");
			return;
		}

		// String filename = req.getParameter("upfile");
		// File file= (File) req.getAttribute(upfile);

		UploadServletRequest request;
		try {
			request = new UploadServletRequest(req);

			if (request.isFileFounded()) {
				WebRequest requestEvent = new WebRequest(
						webComponent.getRequestId(), servletId, request);
				webComponent.triggerWebRequest(requestEvent);
				webResponseCollector.collect(requestEvent, resp);
			} else {
				// Error
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		webResponseCollector.addResponse(response);
	}

}
