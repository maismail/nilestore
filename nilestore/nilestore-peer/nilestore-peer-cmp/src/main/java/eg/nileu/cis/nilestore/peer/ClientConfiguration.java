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

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import eg.nileu.cis.nilestore.utils.EncodingParam;

// TODO: Auto-generated Javadoc
/**
 * The Class ClientConfiguration.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public final class ClientConfiguration {

	/** The client nickname. */
	private final String clientNickname;

	/** The is storageserver. */
	private final boolean isStorageserver;

	/** The home dir. */
	private final String homeDir;

	/** The params. */
	private final EncodingParam params;

	/**
	 * Instantiates a new client configuration.
	 * 
	 * @param clientNickname
	 *            the client nickname
	 * @param storageEnabled
	 *            the storage enabled
	 * @param homeDir
	 *            the home dir
	 * @param params
	 *            the params
	 */
	public ClientConfiguration(String clientNickname, boolean storageEnabled,
			String homeDir, EncodingParam params) {
		this.clientNickname = clientNickname;
		this.isStorageserver = storageEnabled;
		this.homeDir = homeDir;
		this.params = params;
	}

	/**
	 * Gets the client nickname.
	 * 
	 * @return the client nickname
	 */
	public String getClientNickname() {
		return clientNickname;
	}

	/**
	 * Checks if is storageserver.
	 * 
	 * @return true, if is storageserver
	 */
	public boolean isStorageserver() {
		return isStorageserver;
	}

	/**
	 * Gets the home dir.
	 * 
	 * @return the home dir
	 */
	public String gethomeDir() {
		return homeDir;
	}

	/**
	 * Gets the encoding param.
	 * 
	 * @return the encoding param
	 */
	public EncodingParam getEncodingParam() {
		return params;
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
		p.setProperty("nickname", clientNickname);
		p.setProperty("homedir", homeDir);
		p.setProperty("storage", isStorageserver ? "true" : "false");
		// FIXME: use encodingparam to string instead
		p.setProperty("shares.k", String.valueOf(params.getK()));
		p.setProperty("shares.n", String.valueOf(params.getN()));

		Writer writer = new FileWriter(file);
		p.store(writer, "eg.nileu.cis.nilestore");
	}

	/**
	 * Load.
	 * 
	 * @param file
	 *            the file
	 * @return the client configuration
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static ClientConfiguration load(String file) throws IOException {
		Properties p = new Properties();
		Reader reader = new FileReader(file);
		p.load(reader);

		String nickname = p.getProperty("nickname");
		String homedir = p.getProperty("homedir");
		boolean storageEnabled = Boolean.parseBoolean(p.getProperty("storage"));
		int k = Integer.valueOf(p.getProperty("shares.k"));
		int n = Integer.valueOf(p.getProperty("shares.n"));

		return new ClientConfiguration(nickname, storageEnabled, homedir,
				new EncodingParam(k, n));
	}
}
