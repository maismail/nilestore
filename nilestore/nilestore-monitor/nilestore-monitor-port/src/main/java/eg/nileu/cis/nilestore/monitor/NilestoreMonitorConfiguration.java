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
package eg.nileu.cis.nilestore.monitor;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.util.Properties;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Transport;

// TODO: Auto-generated Javadoc
/**
 * The Class NilestoreMonitorConfiguration.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NilestoreMonitorConfiguration {

	/** The enabled. */
	private final boolean enabled;

	/** The monitor server address. */
	private final Address monitorServerAddress;

	/** The client update period. */
	private final long clientUpdatePeriod;

	/** The server web port. */
	private final int serverWebPort;

	/** The protocol. */
	private final Transport protocol;

	/**
	 * Instantiates a new nilestore monitor configuration.
	 * 
	 * @param enabled
	 *            the enabled
	 * @param monitorServerAddress
	 *            the monitor server address
	 * @param clientUpdatePeriod
	 *            the client update period
	 * @param serverWebPort
	 *            the server web port
	 * @param protocol
	 *            the protocol
	 */
	public NilestoreMonitorConfiguration(boolean enabled,
			Address monitorServerAddress, long clientUpdatePeriod,
			int serverWebPort, Transport protocol) {
		this.enabled = enabled;
		this.monitorServerAddress = monitorServerAddress;
		this.clientUpdatePeriod = clientUpdatePeriod;
		this.serverWebPort = serverWebPort;
		this.protocol = protocol;
	}

	/**
	 * Checks if is enabled.
	 * 
	 * @return true, if is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Gets the monitor server address.
	 * 
	 * @return the monitor server address
	 */
	public Address getMonitorServerAddress() {
		return monitorServerAddress;
	}

	/**
	 * Gets the client update period.
	 * 
	 * @return the client update period
	 */
	public long getClientUpdatePeriod() {
		return clientUpdatePeriod;
	}

	/**
	 * Gets the server web port.
	 * 
	 * @return the server web port
	 */
	public int getServerWebPort() {
		return serverWebPort;
	}

	/**
	 * Gets the protocol.
	 * 
	 * @return the protocol
	 */
	public Transport getProtocol() {
		return protocol;
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

		p.setProperty("monitor.enabled", enabled ? "true" : "false");
		p.setProperty("client.update.period", "" + clientUpdatePeriod);
		p.setProperty("server.web.port", "" + serverWebPort);
		p.setProperty("server.ip", ""
				+ monitorServerAddress.getIp().getHostAddress());
		p.setProperty("server.port", "" + monitorServerAddress.getPort());
		p.setProperty("server.id", "" + monitorServerAddress.getId());
		p.setProperty("transport.protocol", protocol.name());

		Writer writer = new FileWriter(file);
		p.store(writer, "eg.nileu.cis.nilestore.monitor");
	}

	/**
	 * Load.
	 * 
	 * @param file
	 *            the file
	 * @return the nilestore monitor configuration
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static NilestoreMonitorConfiguration load(String file)
			throws IOException {
		Properties p = new Properties();
		Reader reader = new FileReader(file);
		p.load(reader);

		boolean enabled = Boolean
				.parseBoolean(p.getProperty("monitor.enabled"));
		InetAddress ip = InetAddress.getByName(p.getProperty("server.ip"));
		int port = Integer.parseInt(p.getProperty("server.port"));
		int id = Integer.parseInt(p.getProperty("server.id"));

		Address monitorServerAddress = new Address(ip, port, id);
		long clientUpdatePeriod = Long.parseLong(p
				.getProperty("client.update.period"));
		int serverWebPort = Integer.parseInt(p.getProperty("server.web.port"));
		Transport protocol = Enum.valueOf(Transport.class,
				p.getProperty("transport.protocol"));

		return new NilestoreMonitorConfiguration(enabled, monitorServerAddress,
				clientUpdatePeriod, serverWebPort, protocol);

	}
}
