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
package eg.nileu.cis.nilestore.simulator;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;
import eg.nileu.cis.nilestore.introducer.IntroducerConfiguration;
import eg.nileu.cis.nilestore.monitor.NilestoreMonitorConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The Class NsSimulatorInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsSimulatorInit extends Init {

	/** The peer0 address. */
	private final Address peer0Address;

	/** The introducer configuration. */
	private final IntroducerConfiguration introducerConfiguration;

	/** The monitor configuration. */
	private final NilestoreMonitorConfiguration monitorConfiguration;

	/** The home dir. */
	private final String homeDir;

	/** The webport. */
	private final int webport;

	/**
	 * Instantiates a new ns simulator init.
	 * 
	 * @param peer0Address
	 *            the peer0 address
	 * @param introducerConfiguration
	 *            the introducer configuration
	 * @param monitorConfiguration
	 *            the monitor configuration
	 * @param homeDir
	 *            the home dir
	 * @param webport
	 *            the webport
	 */
	public NsSimulatorInit(Address peer0Address,
			IntroducerConfiguration introducerConfiguration,
			NilestoreMonitorConfiguration monitorConfiguration, String homeDir,
			int webport) {
		this.peer0Address = peer0Address;
		this.introducerConfiguration = introducerConfiguration;
		this.monitorConfiguration = monitorConfiguration;
		this.homeDir = homeDir;
		this.webport = webport;
	}

	/**
	 * Gets the webport.
	 * 
	 * @return the webport
	 */
	public int getWebport() {
		return webport;
	}

	/**
	 * Gets the monitor configuration.
	 * 
	 * @return the monitor configuration
	 */
	public NilestoreMonitorConfiguration getMonitorConfiguration() {
		return monitorConfiguration;
	}

	/**
	 * Gets the home dir.
	 * 
	 * @return the home dir
	 */
	public String getHomeDir() {
		return homeDir;
	}

	/**
	 * Gets the peer0 address.
	 * 
	 * @return the peer0 address
	 */
	public Address getPeer0Address() {
		return peer0Address;
	}

	/**
	 * Gets the introducer configuration.
	 * 
	 * @return the introducer configuration
	 */
	public IntroducerConfiguration getIntroducerConfiguration() {
		return introducerConfiguration;
	}

}
