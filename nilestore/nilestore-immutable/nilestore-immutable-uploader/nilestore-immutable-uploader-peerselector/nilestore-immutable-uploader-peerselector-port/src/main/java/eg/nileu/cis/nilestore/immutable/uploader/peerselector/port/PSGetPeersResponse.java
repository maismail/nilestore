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
package eg.nileu.cis.nilestore.immutable.uploader.peerselector.port;

import java.util.Map;
import java.util.Set;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.StatusMsg;

// TODO: Auto-generated Javadoc
/**
 * The Class PSGetPeersResponse.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class PSGetPeersResponse extends Event {

	/** The status. */
	private final StatusMsg status;

	/** The upload servers. */
	private final Map<Integer, ComponentAddress> uploadServers;

	/** The already got. */
	private final Map<Address, Set<Integer>> alreadyGot;

	/**
	 * Instantiates a new pS get peers response.
	 * 
	 * @param status
	 *            the status
	 * @param uploadservers
	 *            the uploadservers
	 * @param alreadygot
	 *            the alreadygot
	 */
	public PSGetPeersResponse(StatusMsg status,
			Map<Integer, ComponentAddress> uploadservers,
			Map<Address, Set<Integer>> alreadygot) {
		this.status = status;
		this.uploadServers = uploadservers;
		this.alreadyGot = alreadygot;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public StatusMsg getStatus() {
		return status;
	}

	/**
	 * Gets the already got.
	 * 
	 * @return the already got
	 */
	public Map<Address, Set<Integer>> getAlreadyGot() {
		return alreadyGot;
	}

	/**
	 * Gets the upload servers.
	 * 
	 * @return the upload servers
	 */
	public Map<Integer, ComponentAddress> getUploadServers() {
		return uploadServers;
	}

}
