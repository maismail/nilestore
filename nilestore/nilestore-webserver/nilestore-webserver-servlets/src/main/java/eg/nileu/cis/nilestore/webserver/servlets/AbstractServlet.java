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

import javax.servlet.http.HttpServlet;

import eg.nileu.cis.nilestore.webserver.AbstractWebServer;
import eg.nileu.cis.nilestore.webserver.port.WebResponse;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractServlet.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
@SuppressWarnings("serial")
public abstract class AbstractServlet extends HttpServlet {

	/** The web component. */
	protected AbstractWebServer webComponent;

	/** The servlet id. */
	protected final String servletId;

	/** The web response collector. */
	protected WebResponseCollector webResponseCollector;

	/**
	 * Instantiates a new abstract servlet.
	 * 
	 * @param servletId
	 *            the servlet id
	 */
	public AbstractServlet(String servletId) {
		this.servletId = servletId;
		// TODO: timeout
		webResponseCollector = new WebResponseCollector(Integer.MAX_VALUE);
	}

	/**
	 * Sets the parent.
	 * 
	 * @param webComponent
	 *            the new parent
	 */
	public void setParent(AbstractWebServer webComponent) {
		this.webComponent = webComponent;
	}

	/**
	 * Trigger web response.
	 * 
	 * @param response
	 *            the response
	 */
	abstract public void triggerWebResponse(WebResponse response);
}
