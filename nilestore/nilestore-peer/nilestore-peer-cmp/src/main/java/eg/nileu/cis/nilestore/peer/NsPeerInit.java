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
package eg.nileu.cis.nilestore.peer;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;
import eg.nileu.cis.nilestore.introducer.IntroducerConfiguration;
import eg.nileu.cis.nilestore.monitor.NilestoreMonitorConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The Class NsPeerInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsPeerInit extends Init {

	/** The self. */
	private final Address self;

	/** The web port. */
	private final int webPort;

	/** The client config. */
	private final ClientConfiguration clientConfig;

	/** The boot config. */
	private final BootstrapConfiguration bootConfig;

	/** The introducer config. */
	private final IntroducerConfiguration introducerConfig;

	/** The monitor config. */
	private final NilestoreMonitorConfiguration monitorConfig;

	/** The ping config. */
	private final PingFailureDetectorConfiguration pingConfig;

	/**
	 * Instantiates a new ns peer init.
	 * 
	 * @param self
	 *            the self
	 * @param webport
	 *            the webport
	 * @param clientConfig
	 *            the client config
	 * @param bootConfig
	 *            the boot config
	 * @param introducerConfig
	 *            the introducer config
	 * @param monitorConfiguration
	 *            the monitor configuration
	 * @param pingConfiguration
	 *            the ping configuration
	 */
	public NsPeerInit(Address self, int webport,
			ClientConfiguration clientConfig,
			BootstrapConfiguration bootConfig,
			IntroducerConfiguration introducerConfig,
			NilestoreMonitorConfiguration monitorConfiguration,
			PingFailureDetectorConfiguration pingConfiguration) {
		this.self = self;
		this.webPort = webport;
		this.clientConfig = clientConfig;
		this.bootConfig = bootConfig;
		this.introducerConfig = introducerConfig;
		this.monitorConfig = monitorConfiguration;
		this.pingConfig = pingConfiguration;
	}

	/**
	 * Gets the introducer config.
	 * 
	 * @return the introducer config
	 */
	public IntroducerConfiguration getIntroducerConfig() {
		return introducerConfig;
	}

	/**
	 * Gets the ping config.
	 * 
	 * @return the ping config
	 */
	public PingFailureDetectorConfiguration getPingConfig() {
		return pingConfig;
	}

	/**
	 * Gets the monitor config.
	 * 
	 * @return the monitor config
	 */
	public NilestoreMonitorConfiguration getMonitorConfig() {
		return monitorConfig;
	}

	/**
	 * Gets the web port.
	 * 
	 * @return the web port
	 */
	public int getWebPort() {
		return webPort;
	}

	/**
	 * Gets the boot config.
	 * 
	 * @return the boot config
	 */
	public BootstrapConfiguration getBootConfig() {
		return bootConfig;
	}

	/**
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public Address getSelf() {
		return self;
	}

	/**
	 * Gets the client config.
	 * 
	 * @return the client config
	 */
	public ClientConfiguration getClientConfig() {
		return clientConfig;
	}

	/**
	 * Checks if is chord enabled.
	 * 
	 * @return true, if is chord enabled
	 */
	public boolean isChordEnabled() {
		return bootConfig != null;
	}

	/**
	 * Checks if is introducer enabled.
	 * 
	 * @return true, if is introducer enabled
	 */
	public boolean isIntroducerEnabled() {
		return introducerConfig != null;
	}
}
