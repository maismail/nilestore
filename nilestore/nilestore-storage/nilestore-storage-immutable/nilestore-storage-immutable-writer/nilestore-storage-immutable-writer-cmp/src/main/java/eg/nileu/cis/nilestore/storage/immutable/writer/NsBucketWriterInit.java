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
package eg.nileu.cis.nilestore.storage.immutable.writer;

import se.sics.kompics.Init;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.storage.AbstractStorageServer;
import eg.nileu.cis.nilestore.storage.LeaseInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class NsBucketWriterInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsBucketWriterInit extends Init {

	/** The parent. */
	private final AbstractStorageServer parent;

	/** The self. */
	private final ComponentAddress self;

	/** The dest. */
	private final ComponentAddress dest;

	/** The incoming home. */
	private final String incomingHome;

	/** The final home. */
	private final String finalHome;

	/** The max space per bucket. */
	private final long maxSpacePerBucket;

	/** The lease info. */
	private final LeaseInfo leaseInfo;

	/** The filename. */
	private final String filename;

	/**
	 * Instantiates a new ns bucket writer init.
	 * 
	 * @param parent
	 *            the parent
	 * @param self
	 *            the self
	 * @param destination
	 *            the destination
	 * @param incominghome
	 *            the incominghome
	 * @param finalhome
	 *            the finalhome
	 * @param filename
	 *            the filename
	 * @param max_space_per_bucker
	 *            the max_space_per_bucker
	 * @param lease_info
	 *            the lease_info
	 */
	public NsBucketWriterInit(AbstractStorageServer parent,
			ComponentAddress self, ComponentAddress destination,
			String incominghome, String finalhome, String filename,
			long max_space_per_bucker, LeaseInfo lease_info) {
		this.self = self;
		this.dest = destination;
		this.incomingHome = incominghome;
		this.finalHome = finalhome;
		this.filename = filename;
		this.maxSpacePerBucket = max_space_per_bucker;
		this.leaseInfo = lease_info;
		this.parent = parent;
	}

	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	public AbstractStorageServer getParent() {
		return parent;
	}

	/**
	 * Gets the filename.
	 * 
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Gets the incoming home.
	 * 
	 * @return the incoming home
	 */
	public String getIncomingHome() {
		return incomingHome;
	}

	/**
	 * Gets the final home.
	 * 
	 * @return the final home
	 */
	public String getFinalHome() {
		return finalHome;
	}

	/**
	 * Gets the max space per bucket.
	 * 
	 * @return the max space per bucket
	 */
	public long getMaxSpacePerBucket() {
		return maxSpacePerBucket;
	}

	/**
	 * Gets the lease info.
	 * 
	 * @return the lease info
	 */
	public LeaseInfo getLeaseInfo() {
		return leaseInfo;
	}

	/**
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public ComponentAddress getSelf() {
		return self;
	}

	/**
	 * Gets the dest.
	 * 
	 * @return the dest
	 */
	public ComponentAddress getDest() {
		return dest;
	}

}
