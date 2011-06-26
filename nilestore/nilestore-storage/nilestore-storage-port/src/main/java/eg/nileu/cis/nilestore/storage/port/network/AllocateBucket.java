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

import java.util.Set;

import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.ExtMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class AllocateBucket.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class AllocateBucket extends ExtMessage {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6943653609664370815L;

	/** The storage index. */
	private final String storageIndex;

	/** The renew secret. */
	private final byte[] renewSecret;

	/** The cancel secret. */
	private final byte[] cancelSecret;

	/** The allocated size. */
	private final long allocatedSize;

	/** The share nums. */
	private final Set<Integer> shareNums;

	/**
	 * Instantiates a new allocate bucket.
	 * 
	 * @param source
	 *            the source
	 * @param destination
	 *            the destination
	 * @param storageindex
	 *            the storageindex
	 * @param renew_secret
	 *            the renew_secret
	 * @param cancel_secret
	 *            the cancel_secret
	 * @param allocated_size
	 *            the allocated_size
	 * @param sharenums
	 *            the sharenums
	 */
	public AllocateBucket(ComponentAddress source,
			ComponentAddress destination, String storageindex,
			byte[] renew_secret, byte[] cancel_secret, long allocated_size,
			Set<Integer> sharenums) {

		super(source, destination);
		this.storageIndex = storageindex;
		this.renewSecret = renew_secret;
		this.cancelSecret = cancel_secret;
		this.allocatedSize = allocated_size;
		this.shareNums = sharenums;
	}

	/**
	 * Gets the share nums.
	 * 
	 * @return the share nums
	 */
	public Set<Integer> getShareNums() {
		return shareNums;
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
	 * Gets the renew secret.
	 * 
	 * @return the renew secret
	 */
	public byte[] getRenewSecret() {
		return renewSecret;
	}

	/**
	 * Gets the cancel secret.
	 * 
	 * @return the cancel secret
	 */
	public byte[] getCancelSecret() {
		return cancelSecret;
	}

	/**
	 * Gets the allocated size.
	 * 
	 * @return the allocated size
	 */
	public long getAllocatedSize() {
		return allocatedSize;
	}

}
