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
package eg.nileu.cis.nilestore.availablepeers.port;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;
import eg.nileu.cis.nilestore.common.NilestoreAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class RemovePeer.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class RemovePeer extends Event {

	/** The nilestore address. */
	private final NilestoreAddress nilestoreAddress;

	/** The address. */
	private final Address address;

	/**
	 * Instantiates a new removes the peer.
	 * 
	 * @param address
	 *            the address
	 */
	public RemovePeer(Address address) {
		this.address = address;
		this.nilestoreAddress = null;
	}

	/**
	 * Instantiates a new removes the peer.
	 * 
	 * @param nilestoreAddress
	 *            the nilestore address
	 */
	public RemovePeer(NilestoreAddress nilestoreAddress) {
		this.nilestoreAddress = nilestoreAddress;
		this.address = null;
	}

	/**
	 * Gets the nilestore address.
	 * 
	 * @return the nilestore address
	 */
	public NilestoreAddress getNilestoreAddress() {
		return nilestoreAddress;
	}

	/**
	 * Gets the address.
	 * 
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}
}
