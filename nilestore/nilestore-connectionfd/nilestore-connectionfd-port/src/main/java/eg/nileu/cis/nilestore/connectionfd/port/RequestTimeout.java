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
package eg.nileu.cis.nilestore.connectionfd.port;

import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

// TODO: Auto-generated Javadoc
/**
 * The Class RequestTimeout.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class RequestTimeout extends Timeout {

	/** The peer address. */
	private final Address peerAddress;

	/**
	 * Instantiates a new request timeout.
	 * 
	 * @param request
	 *            the request
	 * @param peerAddress
	 *            the peer address
	 */
	public RequestTimeout(ScheduleTimeout request, Address peerAddress) {
		super(request);
		this.peerAddress = peerAddress;
	}

	/**
	 * Gets the peer address.
	 * 
	 * @return the peer address
	 */
	public Address getPeerAddress() {
		return peerAddress;
	}
}
