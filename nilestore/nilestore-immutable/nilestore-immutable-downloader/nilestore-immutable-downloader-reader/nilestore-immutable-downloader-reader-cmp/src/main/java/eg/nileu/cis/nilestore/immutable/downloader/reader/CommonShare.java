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
package eg.nileu.cis.nilestore.immutable.downloader.reader;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eg.nileu.cis.nilestore.immutable.downloader.reader.port.SetCommonParameters;
import eg.nileu.cis.nilestore.utils.ByteArray;
import eg.nileu.cis.nilestore.utils.hashtree.BadHashError;
import eg.nileu.cis.nilestore.utils.hashtree.IncompleteHashTree;
import eg.nileu.cis.nilestore.utils.hashtree.NotEnoughHashesError;
import eg.nileu.cis.nilestore.utils.hashutils.Hash;

// TODO: Auto-generated Javadoc
/**
 * The Class CommonShare.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class CommonShare {

	/** The block size. */
	private int blockSize;

	/** The tail block size. */
	private int tailBlockSize;

	/** The num segments. */
	private int numSegments;

	/** The index at share ht. */
	private int indexAtShareHT;

	/** The share size. */
	private int shareSize;
	// Offsets
	/** The data_offset. */
	private int data_offset;

	/** The plaintext_hash_tree_offset. */
	private int plaintext_hash_tree_offset;

	/** The crypttext_hash_tree_offset. */
	private int crypttext_hash_tree_offset;

	/** The block_hashes_offset. */
	private int block_hashes_offset;

	/** The share_hashes_offset. */
	private int share_hashes_offset;

	/** The uri_extension_offset. */
	private int uri_extension_offset;

	/** The have header. */
	private boolean haveHeader;

	/** The have common params. */
	private boolean haveCommonParams;

	/** The block hash tree. */
	private IncompleteHashTree blockHashTree;

	/**
	 * Instantiates a new common share.
	 */
	public CommonShare() {
		haveHeader = false;
		haveCommonParams = false;
	}

	/**
	 * Got header.
	 * 
	 * @param headerdata
	 *            the headerdata
	 */
	public void gotHeader(byte[] headerdata) {
		// TODO: validate the header
		ByteBuffer buffer = ByteBuffer.wrap(headerdata);
		int version = buffer.getInt();
		blockSize = buffer.getInt();
		shareSize = buffer.getInt();
		data_offset = buffer.getInt();
		plaintext_hash_tree_offset = buffer.getInt();
		crypttext_hash_tree_offset = buffer.getInt();
		block_hashes_offset = buffer.getInt();
		share_hashes_offset = buffer.getInt();
		uri_extension_offset = buffer.getInt();

		haveHeader = true;
	}

	/**
	 * Sets the common parameters.
	 * 
	 * @param event
	 *            the new common parameters
	 */
	public void setCommonParameters(SetCommonParameters event) {
		this.numSegments = event.getNumSegments();
		this.tailBlockSize = event.getTailBlockSize();
		this.indexAtShareHT = event.getIndexAtShareHT();
		this.blockHashTree = new IncompleteHashTree(numSegments);
		haveCommonParams = true;
	}

	/**
	 * Gets the index at share ht.
	 * 
	 * @return the index at share ht
	 */
	public int getIndexAtShareHT() {
		return indexAtShareHT;
	}

	/**
	 * Checks if is tail segment.
	 * 
	 * @param segmentnum
	 *            the segmentnum
	 * @return true, if is tail segment
	 */
	public boolean isTailSegment(int segmentnum) {
		return segmentnum == numSegments - 1;
	}

	/**
	 * Have header.
	 * 
	 * @return true, if successful
	 */
	public boolean haveHeader() {
		return haveHeader;
	}

	/**
	 * Have common params.
	 * 
	 * @return true, if successful
	 */
	public boolean haveCommonParams() {
		return haveCommonParams;
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
	 * Gets the tail block size.
	 * 
	 * @return the tail block size
	 */
	public int getTailBlockSize() {
		return tailBlockSize;
	}

	/**
	 * Gets the data offset.
	 * 
	 * @return the data offset
	 */
	public int getDataOffset() {
		return data_offset;
	}

	/**
	 * Gets the blocks hashes offset.
	 * 
	 * @return the blocks hashes offset
	 */
	public int getBlocksHashesOffset() {
		return block_hashes_offset;
	}

	/**
	 * Gets the ciphertext hash offset.
	 * 
	 * @return the ciphertext hash offset
	 */
	public int getCiphertextHashOffset() {
		return crypttext_hash_tree_offset;
	}

	/**
	 * Gets the uri extension offset.
	 * 
	 * @return the uri extension offset
	 */
	public int getUriExtensionOffset() {
		return uri_extension_offset;
	}

	/**
	 * Gets the share hashes offset.
	 * 
	 * @return the share hashes offset
	 */
	public int getShareHashesOffset() {
		return share_hashes_offset;
	}

	/**
	 * Sets the block root hash.
	 * 
	 * @param roothash
	 *            the new block root hash
	 */
	public void setBlockRootHash(ByteArray roothash) {
		Map<Integer, ByteArray> root = new HashMap<Integer, ByteArray>();
		root.put(0, roothash);
		try {
			blockHashTree.setHashes(root);
		} catch (BadHashError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnoughHashesError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the block hash tree.
	 * 
	 * @return the block hash tree
	 */
	public IncompleteHashTree getBlockHashTree() {
		return blockHashTree;
	}

	/**
	 * Need hashes to validate.
	 * 
	 * @param segmentnum
	 *            the segmentnum
	 * @return true, if successful
	 */
	public boolean needHashesToValidate(int segmentnum) {
		List<Integer> needed = blockHashTree.neededHashes(segmentnum, true);
		return !needed.isEmpty();
	}

	/**
	 * Check block.
	 * 
	 * @param data
	 *            the data
	 * @param segmentnum
	 *            the segmentnum
	 * @throws BadHashError
	 *             the bad hash error
	 * @throws NotEnoughHashesError
	 *             the not enough hashes error
	 */
	public void checkBlock(byte[] data, int segmentnum) throws BadHashError,
			NotEnoughHashesError {
		byte[] blockHash = Hash.block_hash(data);
		blockHashTree.setLeafHash(segmentnum, blockHash);
	}
}
