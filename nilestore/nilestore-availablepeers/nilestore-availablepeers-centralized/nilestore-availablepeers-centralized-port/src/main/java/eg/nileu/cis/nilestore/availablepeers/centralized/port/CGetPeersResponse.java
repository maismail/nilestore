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
package eg.nileu.cis.nilestore.availablepeers.centralized.port;

import java.util.List;

import eg.nileu.cis.nilestore.availablepeers.port.GetPeersResponse;
import eg.nileu.cis.nilestore.common.NilestoreAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class CGetPeersResponse.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class CGetPeersResponse extends GetPeersResponse {

	/** The peers. */
	private final List<NilestoreAddress> peers;

	/**
	 * Instantiates a new c get peers response.
	 * 
	 * @param peersIds
	 *            the peers ids
	 * @param request
	 *            the request
	 */
	public CGetPeersResponse(List<NilestoreAddress> peersIds, CGetPeers request) {
		super(request);
		this.peers = peersIds;
	}

	/**
	 * Gets the peers.
	 * 
	 * @return the peers
	 */
	public List<NilestoreAddress> getPeers() {
		return peers;
	}
}
