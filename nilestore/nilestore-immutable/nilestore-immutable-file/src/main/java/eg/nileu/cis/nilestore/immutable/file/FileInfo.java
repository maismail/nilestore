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
package eg.nileu.cis.nilestore.immutable.file;

import org.bitpedia.util.Base32;

import eg.nileu.cis.nilestore.utils.EncodingParam;
import eg.nileu.cis.nilestore.utils.hashutils.Hash;

// TODO: Auto-generated Javadoc
/**
 * The Class FileInfo.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class FileInfo {

	/** The storage index. */
	private final String storageIndex;

	/** The filerenew secret. */
	private final byte[] filerenewSecret;

	/** The filecancel secret. */
	private final byte[] filecancelSecret;

	/** The allocated size. */
	private final long allocatedSize;

	/** The num share hashes. */
	private final int numShareHashes;

	/** The share size. */
	private final long shareSize;

	/** The block size. */
	private final int blockSize;

	/** The num segments. */
	private final int numSegments;

	/** The encoding param. */
	private final EncodingParam encodingParam;

	/** The Constant MaxURIExt. */
	public static final int MaxURIExt = 1000;

	/**
	 * Instantiates a new file info.
	 * 
	 * @param encodingparam
	 *            the encodingparam
	 * @param storageIndex
	 *            the storage index
	 * @param renewSecret
	 *            the renew secret
	 * @param cancelSecret
	 *            the cancel secret
	 * @param allocatedSize
	 *            the allocated size
	 * @param numShareHashes
	 *            the num share hashes
	 * @param sharesize
	 *            the sharesize
	 * @param blocksize
	 *            the blocksize
	 * @param numsegments
	 *            the numsegments
	 */
	public FileInfo(EncodingParam encodingparam, String storageIndex,
			byte[] renewSecret, byte[] cancelSecret, long allocatedSize,
			int numShareHashes, long sharesize, int blocksize, int numsegments) {
		this.encodingParam = encodingparam;
		this.storageIndex = storageIndex;
		this.filerenewSecret = renewSecret;
		this.filecancelSecret = cancelSecret;
		this.allocatedSize = allocatedSize;
		this.numShareHashes = numShareHashes;
		this.shareSize = sharesize;
		this.blockSize = blocksize;
		this.numSegments = numsegments;
	}

	/**
	 * Gets the max uri ext.
	 * 
	 * @return the max uri ext
	 */
	public int getMaxURIExt() {
		return MaxURIExt;
	}

	/**
	 * Gets the encoding param.
	 * 
	 * @return the encoding param
	 */
	public EncodingParam getEncodingParam() {
		return encodingParam;
	}

	/**
	 * Gets the share size.
	 * 
	 * @return the share size
	 */
	public long getShareSize() {
		return shareSize;
	}

	/**
	 * Gets the block size.
	 * 
	 * @return the block size
	 */
	public int getBlockSize() {
		return blockSize;
	}

	/**
	 * Gets the num segments.
	 * 
	 * @return the num segments
	 */
	public int getNumSegments() {
		return numSegments;
	}

	/**
	 * Gets the num share hashes.
	 * 
	 * @return the num share hashes
	 */
	public int getNumShareHashes() {
		return numShareHashes;
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
	 * Gets the file renew secret.
	 * 
	 * @return the file renew secret
	 */
	public byte[] getFileRenewSecret() {
		return filerenewSecret;
	}

	/**
	 * Gets the file cancel secret.
	 * 
	 * @return the file cancel secret
	 */
	public byte[] getFileCancelSecret() {
		return filecancelSecret;
	}

	/**
	 * Gets the allocated size.
	 * 
	 * @return the allocated size
	 */
	public long getAllocatedSize() {
		return allocatedSize;
	}

	/**
	 * Gets the bucket renew secret.
	 * 
	 * @param peerID
	 *            the peer id
	 * @return the bucket renew secret
	 */
	public byte[] getBucketRenewSecret(String peerID) {
		byte[] renewSecret = Hash.bucket_renewal_secret(filerenewSecret,
				Base32.decode(peerID));

		return renewSecret;
	}

	/**
	 * Gets the bucket cancel secret.
	 * 
	 * @param peerID
	 *            the peer id
	 * @return the bucket cancel secret
	 */
	public byte[] getBucketCancelSecret(String peerID) {
		byte[] cancelSecret = Hash.bucket_cancel_secret(filecancelSecret,
				Base32.decode(peerID));
		return cancelSecret;
	}

}
