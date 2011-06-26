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
package eg.nileu.cis.nilestore.storage;

import se.sics.kompics.ComponentDefinition;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractStorageServer.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public abstract class AbstractStorageServer extends ComponentDefinition {

	/** The this ss. */
	protected final AbstractStorageServer thisSS;

	/**
	 * Instantiates a new abstract storage server.
	 */
	public AbstractStorageServer() {
		thisSS = this;
	}

	/**
	 * Bucket closed.
	 * 
	 * @param id
	 *            the id
	 */
	public abstract void bucketClosed(String id);

	/**
	 * Update access time.
	 * 
	 * @param id
	 *            the id
	 * @param time
	 *            the time
	 * @param rtt
	 *            the rtt
	 */
	public abstract void updateAccessTime(String id, Long time, Long rtt);
}
