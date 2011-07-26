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
package eg.nile.cis.nilestore.introducer.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.NetworkConfiguration;
import se.sics.kompics.network.grizzly.GrizzlyNetwork;
import se.sics.kompics.network.grizzly.GrizzlyNetworkInit;
import se.sics.kompics.network.mina.MinaNetwork;
import se.sics.kompics.network.mina.MinaNetworkInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.network.netty.NettyNetworkInit;
import eg.nileu.cis.nilestore.introducer.IntroducerConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The Class NsIntroducerMain.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsIntroducerMain extends ComponentDefinition {

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory
			.getLogger(NsIntroducerMain.class);

	/**
	 * Instantiates a new ns introducer main.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public NsIntroducerMain() throws IOException {

		final String netCmp = System.getProperty("nilestore.net.cmp");
		
		Component network = netCmp.equals("netty") ? create(NettyNetwork.class)
				: netCmp.equals("grizzly") ? create(GrizzlyNetwork.class)
						: create(MinaNetwork.class);
		Component introducer = create(NsIntroducerServer.class);

		final IntroducerConfiguration introConfiguration = IntroducerConfiguration
				.load(System.getProperty("introducer.configuration"));
		final NetworkConfiguration networkConfiguration = NetworkConfiguration
				.load(System.getProperty("network.configuration"));
		final int cl = Integer.valueOf(System.getProperty("nilestore.net.cl"));

		logger.info("initiating {}Network with Address={},compressionLevel={}",
				new Object[] { netCmp, networkConfiguration.getAddress(), cl });

		if (netCmp.equals("netty")) {
			trigger(new NettyNetworkInit(networkConfiguration.getAddress(), 5,
					cl), network.getControl());
		} else if (netCmp.equals("grizzly")) {
			trigger(new GrizzlyNetworkInit(networkConfiguration.getAddress(),
					5, cl), network.getControl());
		} else {
			trigger(new MinaNetworkInit(networkConfiguration.getAddress(), 5,
					networkConfiguration.getAddress().getPort() + 1, cl),
					network.getControl());
		}
		trigger(new NsIntroducerServerInit(introConfiguration),
				introducer.getControl());
		connect(introducer.required(Network.class),
				network.provided(Network.class));

		logger.info("introducer initiated @ {}",
				networkConfiguration.getAddress());
	}
	
}
