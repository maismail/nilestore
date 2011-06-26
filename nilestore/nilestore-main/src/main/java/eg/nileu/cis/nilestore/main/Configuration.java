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
package eg.nileu.cis.nilestore.main;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.NetworkConfiguration;
import se.sics.kompics.network.Transport;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;
import eg.nileu.cis.nilestore.introducer.IntroducerConfiguration;
import eg.nileu.cis.nilestore.monitor.NilestoreMonitorConfiguration;
import eg.nileu.cis.nilestore.peer.ClientConfiguration;
import eg.nileu.cis.nilestore.utils.EncodingParam;
import eg.nileu.cis.nilestore.utils.config.ConfigFile;
import eg.nileu.cis.nilestore.utils.config.ConfigFileException;
import eg.nileu.cis.nilestore.webserver.jetty.NsWebServerConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The Class Configuration.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class Configuration {

	/** The config. */
	private final ConfigFile config;

	/** The home dir. */
	private final String homeDir;

	/** The nodetype. */
	private final String nodetype;

	/**
	 * Instantiates a new configuration.
	 * 
	 * @param config
	 *            the config
	 * @param homeDir
	 *            the home dir
	 * @param nodetype
	 *            the nodetype
	 */
	public Configuration(ConfigFile config, String homeDir, String nodetype) {
		this.config = config;
		this.homeDir = homeDir;
		this.nodetype = nodetype;
	}

	/**
	 * Sets the client config.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ConfigFileException
	 *             the config file exception
	 */
	private void setClientConfig() throws IOException, ConfigFileException {
		String _ip = config.getString("node", "ip");
		InetAddress ip = null;
		try {
			ip = InetAddress.getByName(_ip);
		} catch (UnknownHostException e) {
			System.err.println(String.format(
					"Error during parsing the node ip: Unkown Host (%s)", _ip));
			System.exit(-1);
		}

		int webPort = config.getInteger("node", "webport");
		int networkPort = config.getInteger("node", "networkport");
		int introducerportnum = config.getInteger("introducer", "port");

		_ip = config.getString("introducer", "ip");
		InetAddress introducerIp = null;
		try {
			introducerIp = InetAddress.getByName(_ip);
		} catch (UnknownHostException e) {
			System.err.println(String.format(
					"Error during parsing the introducer ip: Unkown Host (%s)",
					_ip));
			System.exit(-1);
		}

		String nickname = config.getString("node", "nickname");
		boolean storageEnabled = config.getBoolean("storage", "enabled");
		int k = config.getInteger("shares", "shares.k");
		int n = config.getInteger("shares", "shares.n");

		ClientConfiguration clientConfiguration = new ClientConfiguration(
				nickname, storageEnabled, homeDir, new EncodingParam(k, n));

		int webThreads = 2;
		NsWebServerConfiguration webServerConfiguration = new NsWebServerConfiguration(
				ip, webPort, webThreads);

		_ip = config.getString("monitor", "ip");
		InetAddress monitorIp = null;
		{
			try {
				monitorIp = InetAddress.getByName(_ip);
			} catch (UnknownHostException e) {
				System.err
						.println(String
								.format("Error during parsing the monitor ip: Unkown Host (%s)",
										_ip));
				System.exit(-1);
			}
		}

		int monitor_port = config.getInteger("monitor", "port");
		boolean monitor_enabled = config.getBoolean("monitor", "enabled");
		int monitor_updatePeriod = config.getInteger("monitor", "updateperiod");

		Address monitorServerAddress = new Address(monitorIp, monitor_port, 0);
		Address introducerServerAddress = new Address(introducerIp,
				introducerportnum, 0);
		NilestoreMonitorConfiguration monitorConfiguration = new NilestoreMonitorConfiguration(
				monitor_enabled, monitorServerAddress, monitor_updatePeriod,
				webPort, Transport.TCP);
		IntroducerConfiguration introducerConfiguration = new IntroducerConfiguration(
				introducerServerAddress, webPort);

		PingFailureDetectorConfiguration fdConfiguration = new PingFailureDetectorConfiguration(
				1000, 5000, 1000, 0, Transport.TCP);

		NetworkConfiguration networkConfiguration = new NetworkConfiguration(
				ip, networkPort, 0);

		String c = createTempFile("jetty.web.", ".conf");
		webServerConfiguration.store(c);
		System.setProperty("jetty.web.configuration", c);

		c = createTempFile("introducer.", ".conf");
		introducerConfiguration.store(c);
		System.setProperty("introducer.configuration", c);

		c = createTempFile("ping.fd.", ".conf");
		fdConfiguration.store(c);
		System.setProperty("ping.fd.configuration", c);

		c = createTempFile("network.", ".conf");
		networkConfiguration.store(c);
		System.setProperty("network.configuration", c);

		c = createTempFile("client.", ".conf");
		clientConfiguration.store(c);
		System.setProperty("client.configuration", c);

		c = createTempFile("monitor.", ".conf");
		monitorConfiguration.store(c);
		System.setProperty("monitor.configuration", c);
	}

	/**
	 * Sets the introducer config.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ConfigFileException
	 *             the config file exception
	 */
	private void setIntroducerConfig() throws IOException, ConfigFileException {
		String _ip = config.getString("introducernode", "ip");
		InetAddress ip = null;
		try {
			ip = InetAddress.getByName(_ip);
		} catch (UnknownHostException e) {
			System.err.println(String.format(
					"Error during parsing the node ip: Unkown Host (%s)", _ip));
			System.exit(-1);
		}

		int webPort = config.getInteger("introducernode", "webport");
		int networkPort = config.getInteger("introducernode", "networkport");

		Address introducerAddress = new Address(ip, networkPort, 0);
		NetworkConfiguration networkConfiguration = new NetworkConfiguration(
				ip, networkPort, 0);
		IntroducerConfiguration introducerConfiguration = new IntroducerConfiguration(
				introducerAddress, webPort);

		String c = createTempFile("introducer.", ".conf");
		introducerConfiguration.store(c);
		System.setProperty("introducer.configuration", c);

		c = createTempFile("network.", ".conf");
		networkConfiguration.store(c);
		System.setProperty("network.configuration", c);

	}

	/**
	 * Sets the monitor config.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ConfigFileException
	 *             the config file exception
	 */
	private void setMonitorConfig() throws IOException, ConfigFileException {
		String _ip = config.getString("monitornode", "ip");
		InetAddress ip = null;
		try {
			ip = InetAddress.getByName(_ip);
		} catch (UnknownHostException e) {
			System.err.println(String.format(
					"Error during parsing the monitor ip: Unkown Host (%s)",
					_ip));
			System.exit(-1);
		}

		int webPort = config.getInteger("monitornode", "webport");
		int networkPort = config.getInteger("monitornode", "networkport");

		int webThreads = 2;

		NsWebServerConfiguration webServerConfiguration = new NsWebServerConfiguration(
				ip, webPort, webThreads);
		NetworkConfiguration networkConfiguration = new NetworkConfiguration(
				ip, networkPort, 0);

		String c = createTempFile("jetty.web.", ".conf");
		webServerConfiguration.store(c);
		System.setProperty("jetty.web.configuration", c);

		c = createTempFile("network.", ".conf");
		networkConfiguration.store(c);
		System.setProperty("network.configuration", c);

	}

	/**
	 * Sets the.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ConfigFileException
	 *             the config file exception
	 */
	public void set() throws IOException, ConfigFileException {

		if (nodetype.equals("client")) {
			setClientConfig();
		} else if (nodetype.equals("introducer")) {
			setIntroducerConfig();
		} else if (nodetype.equals("monitor")) {
			setMonitorConfig();
		}
	}

	/**
	 * Creates the temp file.
	 * 
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private String createTempFile(String arg0, String arg1) throws IOException {
		File f = File.createTempFile(arg0, arg1);
		f.deleteOnExit();
		return f.getAbsolutePath();
	}

}
