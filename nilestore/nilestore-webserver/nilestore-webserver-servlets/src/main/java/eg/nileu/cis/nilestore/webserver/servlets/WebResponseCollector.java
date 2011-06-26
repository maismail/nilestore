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
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import eg.nileu.cis.nilestore.webserver.port.WebRequest;
import eg.nileu.cis.nilestore.webserver.port.WebResponse;

// TODO: Auto-generated Javadoc
/**
 * The Class WebResponseCollector.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class WebResponseCollector {

	/** The Constant ERRORID. */
	public static final int ERRORID = -1;

	/** The active requests. */
	private final HashMap<WebRequest, LinkedBlockingQueue<WebResponse>> activeRequests;

	/** The request timeout. */
	private final long requestTimeout;

	/**
	 * Instantiates a new web response collector.
	 * 
	 * @param requestTimeout
	 *            the request timeout
	 */
	public WebResponseCollector(long requestTimeout) {
		this.activeRequests = new HashMap<WebRequest, LinkedBlockingQueue<WebResponse>>();
		this.requestTimeout = requestTimeout;
	}

	/*
	 * Copied from Kompics framework, JettyWebServer handleRequest
	 */
	/**
	 * Collect.
	 * 
	 * @param requestEvent
	 *            the request event
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void collect(WebRequest requestEvent, HttpServletResponse response)
			throws IOException {

		LinkedBlockingQueue<WebResponse> queue = new LinkedBlockingQueue<WebResponse>();
		synchronized (activeRequests) {
			activeRequests.put(requestEvent, queue);
		}

		int expectedPart = 1;
		HashMap<Integer, WebResponse> earlyResponses = new HashMap<Integer, WebResponse>();
		do {
			WebResponse responseEvent;
			while (true) {
				try {
					responseEvent = queue.poll(requestTimeout,
							TimeUnit.MILLISECONDS);
					break; // waiting
				} catch (InterruptedException e) {
					continue; // waiting
				}
			}

			if (responseEvent != null) {
				// got error
				if (responseEvent.getPartIndex() == ERRORID
						&& responseEvent.getPartsTotal() == ERRORID) {
					// 500 error happen in server while
					response.reset();
					// response.setHeader("Content-Disposition", "inline");
					response.setHeader("Content-Type", "text/html");
					response.getWriter().write("<h2>Operation Failed</h2>");
					response.getWriter().write(responseEvent.getStrData());

					// response.sendError(response.SC_INTERNAL_SERVER_ERROR,
					// responseEvent.getStrData());
					break;
				}
				// got a response event
				if (responseEvent.getPartIndex() == expectedPart) {

					response.getOutputStream().write(responseEvent.getData());
					response.flushBuffer();

					if (expectedPart == responseEvent.getPartsTotal()) {
						// got all parts
						break;
					} else {
						// more parts expected
						expectedPart++;
						// maybe got here before
						while (earlyResponses.containsKey(expectedPart)) {
							// logger.debug("Writing response {}, part {}/{}",
							// new Object[]{requestEvent.getTarget(),
							// responseEvent.getPartIndex(),responseEvent.getPartsTotal()});
							response.getOutputStream().write(
									earlyResponses.get(expectedPart).getData());
							response.flushBuffer();
							expectedPart++;
						}

						if (expectedPart > responseEvent.getPartsTotal()) {
							// got all parts now
							break;
						}
					}
				} else {
					// got a future part
					earlyResponses.put(responseEvent.getPartIndex(),
							responseEvent);
				}
			} else {
				// request expired
				response.getWriter().println("Request expired! <br>");
				response.flushBuffer();
				// logger.debug("Request expired: {}",requestEvent.getTarget());
				break;
			}
		} while (true);

		synchronized (activeRequests) {
			activeRequests.remove(requestEvent);
		}
	}

	/**
	 * Adds the response.
	 * 
	 * @param response
	 *            the response
	 */
	public void addResponse(WebResponse response) {
		WebRequest requestEvent = response.getRequestEvent();

		LinkedBlockingQueue<WebResponse> queue;

		synchronized (activeRequests) {
			queue = activeRequests.get(requestEvent);
			if (queue != null) {
				queue.offer(response);
			} else {
				// request expired
				return;
			}
		}
	}
}
