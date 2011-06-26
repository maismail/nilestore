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

import se.sics.kompics.Request;
import se.sics.kompics.address.Address;

// TODO: Auto-generated Javadoc
/**
 * The Class NotifyonFailure.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NotifyonFailure extends Request implements
		Comparable<NotifyonFailure> {

	/** The address. */
	private final Address address;

	/** The delay. */
	private long delay;

	/**
	 * Instantiates a new notifyon failure.
	 * 
	 * @param delay
	 *            the delay
	 * @param address
	 *            the address
	 */
	public NotifyonFailure(long delay, Address address) {
		this.address = address;
		this.delay = delay;
	}

	/**
	 * Instantiates a new notifyon failure.
	 * 
	 * @param address
	 *            the address
	 */
	public NotifyonFailure(Address address) {
		this.address = address;
		this.delay = -1;
	}

	/**
	 * Gets the address.
	 * 
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * Sets the delay.
	 * 
	 * @param delay
	 *            the new delay
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	/**
	 * Gets the delay.
	 * 
	 * @return the delay
	 */
	public long getDelay() {
		return delay;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(NotifyonFailure other) {
		return Long.valueOf(delay).compareTo(other.getDelay());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NotifyonFailure [delay=" + delay + "]";
	}
}
