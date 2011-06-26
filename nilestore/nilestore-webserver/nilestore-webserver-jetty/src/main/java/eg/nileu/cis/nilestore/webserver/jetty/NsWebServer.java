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
package eg.nileu.cis.nilestore.webserver.jetty;

import java.util.HashMap;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.Handler;
import eg.nileu.cis.nilestore.webserver.AbstractWebServer;
import eg.nileu.cis.nilestore.webserver.port.WebRequest;
import eg.nileu.cis.nilestore.webserver.port.WebResponse;
import eg.nileu.cis.nilestore.webserver.servlets.AbstractServlet;

// TODO: Auto-generated Javadoc
/**
 * The Class NsWebServer.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsWebServer extends AbstractWebServer {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(NsWebServer.class);

	/** The servlets. */
	private final HashMap<String, AbstractServlet> servlets;

	/** The request id. */
	private long requestId;

	/**
	 * Instantiates a new ns web server.
	 */
	public NsWebServer() {
		super();
		servlets = new HashMap<String, AbstractServlet>();
		requestId = 0;
		subscribe(handleInit, control);
		subscribe(handleWebResponse, web);
	}

	/** The handle init. */
	private Handler<NsWebServerInit> handleInit = new Handler<NsWebServerInit>() {
		public void handle(NsWebServerInit init) {
			NsWebServerConfiguration config = init.getConfig();

			logger.info("initiated @ http://{}:{}", config.getIP()
					.getHostName(), config.getPort());

			Server server = new Server(config.getPort());
			// server.setStopAtShutdown(true);
			QueuedThreadPool qtp = new QueuedThreadPool();
			qtp.setMinThreads(1);
			qtp.setMaxThreads(config.getMaxThreads());
			qtp.setDaemon(true);
			server.setThreadPool(qtp);

			Connector connector = new SelectChannelConnector();
			connector.setHost(config.getIP().getCanonicalHostName());
			connector.setPort(config.getPort());
			server.setConnectors(new Connector[] { connector });

			try {

				servlets.putAll(init.getServlets());
				for (AbstractServlet servlet : servlets.values()) {
					servlet.setParent(thisWS);
				}
				server.addHandler(init.getServletContext());

				server.start();
			} catch (Exception e) {
				throw new RuntimeException(
						"Cannot initialize the Jetty web server", e);
			}
		}
	};

	/** The handle web response. */
	private Handler<WebResponse> handleWebResponse = new Handler<WebResponse>() {
		public void handle(WebResponse event) {
			logger.debug("Handling web response {}", event);

			String target = event.getRequestEvent().getTarget();
			synchronized (servlets) {
				if (servlets.containsKey(target)) {
					servlets.get(target).triggerWebResponse(event);
				} else {
					logger.error(
							"Error during Handling web response: target ({}) is not attached to a servlet",
							target);
				}
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eg.nileu.cis.nilestore.webserver.AbstractWebServer#triggerWebRequest(
	 * eg.nileu.cis.nilestore.webserver.port.WebRequest)
	 */
	public void triggerWebRequest(WebRequest request) {
		logger.debug("Handling request {} in thread {}", request.getTarget(),
				Thread.currentThread());
		trigger(request, web);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.webserver.AbstractWebServer#getRequestId()
	 */
	public synchronized long getRequestId() {
		return ++requestId;
	}
}
