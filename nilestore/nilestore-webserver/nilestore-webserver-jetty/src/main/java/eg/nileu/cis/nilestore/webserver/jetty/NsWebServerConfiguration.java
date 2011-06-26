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
package eg.nileu.cis.nilestore.webserver.jetty;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * The Class NsWebServerConfiguration.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public final class NsWebServerConfiguration {

	/** The IP. */
	private final InetAddress IP;

	/** The port. */
	private final int port;

	/** The max threads. */
	private final int maxThreads;

	/**
	 * Instantiates a new ns web server configuration.
	 * 
	 * @param ip
	 *            the ip
	 * @param port
	 *            the port
	 * @param Maxthreads
	 *            the maxthreads
	 */
	public NsWebServerConfiguration(InetAddress ip, int port, int Maxthreads) {
		this.IP = ip;
		this.port = port;
		this.maxThreads = Maxthreads;
	}

	/**
	 * Gets the iP.
	 * 
	 * @return the iP
	 */
	public InetAddress getIP() {
		return IP;
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the max threads.
	 * 
	 * @return the max threads
	 */
	public int getMaxThreads() {
		return maxThreads;
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
		p.setProperty("server.ip", "" + IP.getHostAddress());
		p.setProperty("server.port", "" + port);
		p.setProperty("threads.max", "" + maxThreads);

		Writer writer = new FileWriter(file);
		p.store(writer, "eg.nileu.cis.nilestore.web.server");
	}

	/**
	 * Load.
	 * 
	 * @param file
	 *            the file
	 * @return the ns web server configuration
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static NsWebServerConfiguration load(String file) throws IOException {
		Properties p = new Properties();
		Reader reader = new FileReader(file);
		p.load(reader);

		InetAddress ip = InetAddress.getByName(p.getProperty("server.ip"));
		int port = Integer.parseInt(p.getProperty("server.port"));
		int maxThreads = Integer.parseInt(p.getProperty("threads.max"));

		return new NsWebServerConfiguration(ip, port, maxThreads);

	}
}
