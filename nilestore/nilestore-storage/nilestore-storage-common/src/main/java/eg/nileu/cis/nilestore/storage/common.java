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

import org.bitpedia.util.Base32;

import eg.nileu.cis.nilestore.utils.FileUtils;

;

// TODO: Auto-generated Javadoc
/**
 * The Class common.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public final class common {

	/**
	 * Storage_index_to_dir.
	 * 
	 * @param storage_index
	 *            the storage_index
	 * @return the string
	 */
	public static String storage_index_to_dir(byte[] storage_index) {
		String si = Base32.encode(storage_index);
		return storage_index_to_dir(si);
	}

	/**
	 * Storage_index_to_dir.
	 * 
	 * @param storage_index
	 *            the storage_index
	 * @return the string
	 */
	public static String storage_index_to_dir(String storage_index) {
		return FileUtils.JoinPath(storage_index.substring(0, 2), storage_index);
	}

	/**
	 * Gets the write bucket proxy id.
	 * 
	 * @param SI
	 *            the sI
	 * @param sharenum
	 *            the sharenum
	 * @return the write bucket proxy id
	 */
	public static String getWriteBucketProxyID(String SI, int sharenum) {
		String wbpID = String.format("%s-%s-%s", SI, "WBP", sharenum);
		return wbpID;
	}

	/**
	 * Gets the storage indexfrm id.
	 * 
	 * @param id
	 *            the id
	 * @return the storage indexfrm id
	 */
	public static String getStorageIndexfrmId(String id) {
		return id.split("-")[0];
	}

	/**
	 * Gets the write bucket id.
	 * 
	 * @param SI
	 *            the sI
	 * @param sharenum
	 *            the sharenum
	 * @return the write bucket id
	 */
	public static String getWriteBucketID(String SI, int sharenum) {
		String wbID = String.format("%s-%s-%s", SI, "WB", sharenum);
		return wbID;
	}

	/**
	 * Gets the read bucket id.
	 * 
	 * @param SI
	 *            the sI
	 * @param sharenum
	 *            the sharenum
	 * @return the read bucket id
	 */
	public static String getReadBucketID(String SI, int sharenum) {
		String rbId = String.format("%s-%s-%s", SI, "RB", sharenum);
		return rbId;
	}

	/**
	 * Gets the read bucket id.
	 * 
	 * @param SI
	 *            the sI
	 * @param sharenum
	 *            the sharenum
	 * @param i
	 *            the i
	 * @return the read bucket id
	 */
	public static String getReadBucketID(String SI, int sharenum, int i) {
		String rbId = String.format("%s-%s-%s-%s", SI, "RB", sharenum, i);
		return rbId;
	}

	/**
	 * Gets the read bucket proxy id.
	 * 
	 * @param SI
	 *            the sI
	 * @param sharenum
	 *            the sharenum
	 * @return the read bucket proxy id
	 */
	public static String getReadBucketProxyID(String SI, int sharenum) {
		String wbID = String.format("%s-%s-%s", SI, "RBP", sharenum);
		return wbID;
	}

	/**
	 * Gets the publisher id.
	 * 
	 * @param SI
	 *            the sI
	 * @return the publisher id
	 */
	public static String getPublisherID(String SI) {
		return String.format("%s-%s", SI, "PUB");
	}

	/**
	 * Gets the share num.
	 * 
	 * @param id
	 *            the id
	 * @return the share num
	 */
	public static int getShareNum(String id) {
		String sh = id.split("-")[2];
		return Integer.valueOf(sh);
	}
}
