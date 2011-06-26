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
package eg.nileu.cis.nilestore.connectionfd;

import se.sics.kompics.Init;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;
import eg.nileu.cis.nilestore.common.NilestoreAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class NsConnectionFailureDetectorInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsConnectionFailureDetectorInit extends Init {

	/** The delay. */
	private final long delay;

	/** The self. */
	private final NilestoreAddress self;

	/** The ping fd configuration. */
	private final PingFailureDetectorConfiguration pingFDConfiguration;

	/**
	 * Instantiates a new ns connection failure detector init.
	 * 
	 * @param self
	 *            the self
	 * @param delay
	 *            the delay
	 * @param pingConfiguration
	 *            the ping configuration
	 */
	public NsConnectionFailureDetectorInit(NilestoreAddress self, int delay,
			PingFailureDetectorConfiguration pingConfiguration) {
		this.delay = delay;
		this.self = self;
		this.pingFDConfiguration = pingConfiguration;
	}

	/**
	 * Gets the ping fd configuration.
	 * 
	 * @return the ping fd configuration
	 */
	public PingFailureDetectorConfiguration getPingFDConfiguration() {
		return pingFDConfiguration;
	}

	/**
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public NilestoreAddress getSelf() {
		return self;
	}

	/**
	 * Gets the delay.
	 * 
	 * @return the delay
	 */
	public long getDelay() {
		return delay;
	}
}
