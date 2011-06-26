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
package eg.nileu.cis.nilestore.immutable.peertracker.port;

import java.util.Map;
import java.util.Set;

import se.sics.kompics.Event;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.common.Status;

// TODO: Auto-generated Javadoc
/**
 * The Class QueryResponse.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class QueryResponse extends Event {

	/** The status. */
	private final Status status;

	/** The peer tracker id. */
	private final String peerTrackerId;

	/** The server address. */
	private final NilestoreAddress serverAddress;

	/** The sharemap. */
	private final Map<Integer, ComponentAddress> sharemap;

	/** The alreadygot. */
	private final Set<Integer> alreadygot;

	/**
	 * Instantiates a new query response.
	 * 
	 * @param status
	 *            the status
	 * @param peerTrackerId
	 *            the peer tracker id
	 * @param serverAddress
	 *            the server address
	 * @param sharemap
	 *            the sharemap
	 * @param alreadygot
	 *            the alreadygot
	 */
	public QueryResponse(Status status, String peerTrackerId,
			NilestoreAddress serverAddress,
			Map<Integer, ComponentAddress> sharemap, Set<Integer> alreadygot) {

		this.status = status;
		this.sharemap = sharemap;
		this.serverAddress = serverAddress;
		this.peerTrackerId = peerTrackerId;
		this.alreadygot = alreadygot;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Gets the peer tracker id.
	 * 
	 * @return the peer tracker id
	 */
	public String getPeerTrackerId() {
		return peerTrackerId;
	}

	/**
	 * Gets the server address.
	 * 
	 * @return the server address
	 */
	public NilestoreAddress getServerAddress() {
		return serverAddress;
	}

	/**
	 * Gets the sharemap.
	 * 
	 * @return the sharemap
	 */
	public Map<Integer, ComponentAddress> getSharemap() {
		return sharemap;
	}

	/**
	 * Gets the alreadygot.
	 * 
	 * @return the alreadygot
	 */
	public Set<Integer> getAlreadygot() {
		return alreadygot;
	}

}
