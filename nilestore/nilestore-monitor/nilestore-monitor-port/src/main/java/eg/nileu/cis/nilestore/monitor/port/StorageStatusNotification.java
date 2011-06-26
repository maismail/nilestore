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
package eg.nileu.cis.nilestore.monitor.port;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;
import se.sics.kompics.network.Transport;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.storage.port.status.StorageStatusView;

// TODO: Auto-generated Javadoc
/**
 * The Class StorageStatusNotification.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class StorageStatusNotification extends Message {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -218255035412363437L;

	/** The status. */
	private final StorageStatusView status;

	/** The self. */
	private final NilestoreAddress self;

	/**
	 * Instantiates a new storage status notification.
	 * 
	 * @param source
	 *            the source
	 * @param destination
	 *            the destination
	 * @param protocol
	 *            the protocol
	 * @param self
	 *            the self
	 * @param status
	 *            the status
	 */
	public StorageStatusNotification(Address source, Address destination,
			Transport protocol, NilestoreAddress self, StorageStatusView status) {
		super(source, destination, protocol);
		this.self = self;
		this.status = status;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public StorageStatusView getStatus() {
		return status;
	}

	/**
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public NilestoreAddress getSelf() {
		return self;
	}

}
