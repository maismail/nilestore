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
package eg.nileu.cis.nilestore.storage.port.network;

import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.ExtMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class GetBuckets.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class GetBuckets extends ExtMessage {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1423954563559219769L;

	/** The storage index. */
	private final String storageIndex;

	/**
	 * Instantiates a new gets the buckets.
	 * 
	 * @param source
	 *            the source
	 * @param destination
	 *            the destination
	 * @param storageIndex
	 *            the storage index
	 */
	public GetBuckets(ComponentAddress source, ComponentAddress destination,
			String storageIndex) {
		super(source, destination);
		this.storageIndex = storageIndex;
	}

	/**
	 * Gets the storage index.
	 * 
	 * @return the storage index
	 */
	public String getStorageIndex() {
		return storageIndex;
	}

}
