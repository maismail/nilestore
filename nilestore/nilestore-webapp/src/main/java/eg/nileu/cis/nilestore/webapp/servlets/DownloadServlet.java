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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.nileu.cis.nilestore.uri.CHKFileURI;
import eg.nileu.cis.nilestore.webserver.port.WebRequest;
import eg.nileu.cis.nilestore.webserver.port.WebResponse;
import eg.nileu.cis.nilestore.webserver.servlets.AbstractServlet;

// TODO: Auto-generated Javadoc
/**
 * The Class DownloadServlet.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
@SuppressWarnings("serial")
public class DownloadServlet extends AbstractServlet {

	/** The logger. */
	private final Logger logger = LoggerFactory
			.getLogger(DownloadServlet.class);

	/**
	 * Instantiates a new download servlet.
	 * 
	 * @param servletId
	 *            the servlet id
	 */
	public DownloadServlet(String servletId) {
		super(servletId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		logger.debug("Handling request {} in thread {}", "cap",
				Thread.currentThread());
		//TODO: consider the ranges header
		DownloadServletRequest request = new DownloadServletRequest(req);
		if (!request.isDownloadCap()) {
			resp.sendError(resp.SC_BAD_REQUEST, request.getErrorMessage());
			return;
		}

		WebRequest requestEvent = new WebRequest(webComponent.getRequestId(),
				servletId, request);
		webComponent.triggerWebRequest(requestEvent);

		resp.setHeader("Content-Type", "text/plain");
		if (request.isSaveEnabled()) {
			resp.setHeader("Content-Disposition", "attachment");
		} else {
			resp.setHeader("Content-Disposition", "inline");
		}
		resp.setContentLength((int) ((CHKFileURI) request.getCap()).getSize());

		webResponseCollector.collect(requestEvent, resp);
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
