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
package eg.nileu.cis.nilestore.webserver;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import eg.nileu.cis.nilestore.webserver.port.Web;
import eg.nileu.cis.nilestore.webserver.port.WebRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractWebServer.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public abstract class AbstractWebServer extends ComponentDefinition {

	/** The web. */
	protected Positive<Web> web = positive(Web.class);

	/** The this ws. */
	protected final AbstractWebServer thisWS;

	/**
	 * Instantiates a new abstract web server.
	 */
	public AbstractWebServer() {
		thisWS = this;
	}

	/**
	 * Trigger web request.
	 * 
	 * @param request
	 *            the request
	 */
	public abstract void triggerWebRequest(WebRequest request);

	/**
	 * Gets the request id.
	 * 
	 * @return the request id
	 */
	public abstract long getRequestId();
}
