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
package eg.nileu.cis.nilestore.introducer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.util.Properties;

import se.sics.kompics.address.Address;

// TODO: Auto-generated Javadoc
/**
 * The Class IntroducerConfiguration.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public final class IntroducerConfiguration {

	/** The introducer address. */
	private final Address introducerAddress;

	/** The introducer web port. */
	private final int introducerWebPort;

	/**
	 * Instantiates a new introducer configuration.
	 * 
	 * @param introducerAddress
	 *            the introducer address
	 * @param introducerWebPort
	 *            the introducer web port
	 */
	public IntroducerConfiguration(Address introducerAddress,
			int introducerWebPort) {
		this.introducerAddress = introducerAddress;
		this.introducerWebPort = introducerWebPort;
	}

	/**
	 * Gets the introducer address.
	 * 
	 * @return the introducer address
	 */
	public Address getIntroducerAddress() {
		return introducerAddress;
	}

	/**
	 * Gets the introducer web port.
	 * 
	 * @return the introducer web port
	 */
	public int getIntroducerWebPort() {
		return introducerWebPort;
	}

	/**
	 * Store.
	 * 
	 * @param file
	 *            the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void store(String file) throws IOException {
		Properties p = new Properties();
		p.setProperty("introducer.web.port", "" + introducerWebPort);
		p.setProperty("introducer.ip", ""
				+ introducerAddress.getIp().getHostAddress());
		p.setProperty("introducer.port", "" + introducerAddress.getPort());
		p.setProperty("introducer.id", "" + introducerAddress.getId());

		Writer writer = new FileWriter(file);
		p.store(writer, "eg.nileu.cis.nilestore.introducer");
	}

	/**
	 * Load.
	 * 
	 * @param file
	 *            the file
	 * @return the introducer configuration
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static IntroducerConfiguration load(String file) throws IOException {
		Properties p = new Properties();
		Reader reader = new FileReader(file);
		p.load(reader);

		InetAddress ip = InetAddress.getByName(p.getProperty("introducer.ip"));
		int port = Integer.parseInt(p.getProperty("introducer.port"));
		int id = Integer.parseInt(p.getProperty("introducer.id"));

		Address introducerAddress = new Address(ip, port, id);
		int webport = Integer.parseInt(p.getProperty("introducer.web.port"));

		return new IntroducerConfiguration(introducerAddress, webport);
	}

}
