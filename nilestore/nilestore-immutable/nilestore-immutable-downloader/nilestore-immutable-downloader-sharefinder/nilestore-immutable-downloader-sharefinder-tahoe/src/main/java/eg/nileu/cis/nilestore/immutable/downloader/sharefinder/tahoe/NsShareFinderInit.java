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
package eg.nileu.cis.nilestore.immutable.downloader.sharefinder.tahoe;

import se.sics.kompics.Init;
import eg.nileu.cis.nilestore.common.NilestoreAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class NsShareFinderInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsShareFinderInit extends Init {

	/** The self. */
	private final NilestoreAddress self;

	/** The storage index. */
	private final String storageIndex;

	/** The max outstanding requests. */
	private final int maxOutstandingRequests;

	/**
	 * Instantiates a new ns share finder init.
	 * 
	 * @param self
	 *            the self
	 * @param storageIndex
	 *            the storage index
	 * @param maxOutstandingRequests
	 *            the max outstanding requests
	 */
	public NsShareFinderInit(NilestoreAddress self, String storageIndex,
			int maxOutstandingRequests) {
		this.maxOutstandingRequests = maxOutstandingRequests;
		this.self = self;
		this.storageIndex = storageIndex;
	}

	/**
	 * Instantiates a new ns share finder init.
	 * 
	 * @param self
	 *            the self
	 * @param storageIndex
	 *            the storage index
	 */
	public NsShareFinderInit(NilestoreAddress self, String storageIndex) {
		this.self = self;
		this.storageIndex = storageIndex;
		this.maxOutstandingRequests = 10;
	}

	/**
	 * Gets the storage index.
	 * 
	 * @return the storage index
	 */
	public String getStorageIndex() {
		return storageIndex;
	}

	/**
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public NilestoreAddress getSelf() {
		return self;
	}

	/**
	 * Gets the max outstanding requests.
	 * 
	 * @return the max outstanding requests
	 */
	public int getMaxOutstandingRequests() {
		return maxOutstandingRequests;
	}

}
