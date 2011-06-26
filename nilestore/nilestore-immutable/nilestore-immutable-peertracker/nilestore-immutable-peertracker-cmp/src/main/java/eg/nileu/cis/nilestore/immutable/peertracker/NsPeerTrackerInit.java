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
package eg.nileu.cis.nilestore.immutable.peertracker;

import se.sics.kompics.Init;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.NilestoreAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class NsPeerTrackerInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public final class NsPeerTrackerInit extends Init {

	/** The self address. */
	private final NilestoreAddress selfAddress;

	/** The server address. */
	private final NilestoreAddress serverAddress;

	/** The self. */
	private final ComponentAddress self;

	/** The dest. */
	private final ComponentAddress dest;

	/**
	 * Instantiates a new ns peer tracker init.
	 * 
	 * @param self
	 *            the self
	 * @param dest
	 *            the dest
	 * @param selfAddress
	 *            the self address
	 * @param serverAddress
	 *            the server address
	 */
	public NsPeerTrackerInit(ComponentAddress self, ComponentAddress dest,
			NilestoreAddress selfAddress, NilestoreAddress serverAddress) {
		this.serverAddress = serverAddress;
		this.selfAddress = selfAddress;
		this.self = self;
		this.dest = dest;
	}

	/**
	 * Gets the self address.
	 * 
	 * @return the self address
	 */
	public NilestoreAddress getSelfAddress() {
		return selfAddress;
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
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public ComponentAddress getSelf() {
		return self;
	}

	/**
	 * Gets the dest.
	 * 
	 * @return the dest
	 */
	public ComponentAddress getDest() {
		return dest;
	}

}
