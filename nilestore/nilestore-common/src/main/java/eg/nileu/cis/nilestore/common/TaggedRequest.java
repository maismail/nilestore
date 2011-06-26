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
package eg.nileu.cis.nilestore.common;

import se.sics.kompics.Request;

// TODO: Auto-generated Javadoc
/**
 * The Class TaggedRequest.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class TaggedRequest extends Request {

	/** The request agent. */
	private final Agent requestAgent;

	/**
	 * Instantiates a new tagged request.
	 * 
	 * @param requestAgent
	 *            the request agent
	 */
	public TaggedRequest(Agent requestAgent) {
		this.requestAgent = requestAgent;
	}

	/**
	 * Gets the request agent.
	 * 
	 * @return the request agent
	 */
	public Agent getRequestAgent() {
		return requestAgent;
	}

}
