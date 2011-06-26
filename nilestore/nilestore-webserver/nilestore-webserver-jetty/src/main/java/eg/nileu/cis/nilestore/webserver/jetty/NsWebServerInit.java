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

import org.mortbay.jetty.servlet.Context;

import se.sics.kompics.Init;
import eg.nileu.cis.nilestore.webserver.servlets.AbstractServlet;

// TODO: Auto-generated Javadoc
/**
 * The Class NsWebServerInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public final class NsWebServerInit extends Init {

	/** The config. */
	private final NsWebServerConfiguration config;

	/** The servlets. */
	private final HashMap<String, AbstractServlet> servlets;

	/** The servlet context. */
	private final Context servletContext;

	/**
	 * Instantiates a new ns web server init.
	 * 
	 * @param config
	 *            the config
	 * @param servlets
	 *            the servlets
	 * @param servletContext
	 *            the servlet context
	 */
	public NsWebServerInit(NsWebServerConfiguration config,
			HashMap<String, AbstractServlet> servlets, Context servletContext) {
		this.config = config;
		this.servletContext = servletContext;
		this.servlets = servlets;
	}

	/**
	 * Gets the servlets.
	 * 
	 * @return the servlets
	 */
	public HashMap<String, AbstractServlet> getServlets() {
		return servlets;
	}

	/**
	 * Gets the servlet context.
	 * 
	 * @return the servlet context
	 */
	public Context getServletContext() {
		return servletContext;
	}

	/**
	 * Gets the config.
	 * 
	 * @return the config
	 */
	public NsWebServerConfiguration getConfig() {
		return config;
	}
}
