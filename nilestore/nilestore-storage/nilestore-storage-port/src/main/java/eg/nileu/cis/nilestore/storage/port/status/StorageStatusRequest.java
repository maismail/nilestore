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
package eg.nileu.cis.nilestore.storage.port.status;

import eg.nileu.cis.nilestore.common.Agent;
import eg.nileu.cis.nilestore.common.TaggedRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class StorageStatusRequest.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class StorageStatusRequest extends TaggedRequest {

	/**
	 * Instantiates a new storage status request.
	 * 
	 * @param requestAgent
	 *            the request agent
	 */
	public StorageStatusRequest(Agent requestAgent) {
		super(requestAgent);
	}

	/**
	 * Instantiates a new storage status request.
	 */
	public StorageStatusRequest() {
		super(new Agent("", 0));
	}
}
