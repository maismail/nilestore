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
package eg.nileu.cis.nilestore.storage.server;

import eg.nileu.cis.nilestore.common.NilestoreAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class NsStorageServerConfiguration.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public final class NsStorageServerConfiguration {

	/** The self. */
	private final NilestoreAddress self;

	/** The home dir. */
	private final String homeDir;

	/**
	 * Instantiates a new ns storage server configuration.
	 * 
	 * @param self
	 *            the self
	 * @param homeDir
	 *            the home dir
	 */
	public NsStorageServerConfiguration(NilestoreAddress self, String homeDir) {
		this.self = self;
		this.homeDir = homeDir;
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
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public NilestoreAddress getSelf() {
		return self;
	}

}
