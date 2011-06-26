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
package eg.nileu.cis.nilestore.immutable.downloader.node;

import eg.nileu.cis.nilestore.immutable.file.UEB;
import eg.nileu.cis.nilestore.utils.EncodingParam;
import eg.nileu.cis.nilestore.utils.MathUtils;
import eg.nileu.cis.nilestore.utils.hashtree.BadHashError;
import eg.nileu.cis.nilestore.utils.hashtree.IncompleteHashTree;
import eg.nileu.cis.nilestore.utils.hashtree.NotEnoughHashesError;

// TODO: Auto-generated Javadoc
/**
 * The Class DownloadCommon.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class DownloadCommon {

	/** The ueb. */
	private UEB ueb;

	/** The share hash tree. */
	private IncompleteHashTree shareHashTree;

	/** The ciphertext hash tree. */
	private IncompleteHashTree ciphertextHashTree;

	/** The filesize. */
	private long filesize;

	/** The tail pad. */
	private int tailPad;

	/** The num segments. */
	private int numSegments;

	/** The segment num. */
	private int segmentNum;

	/** The block size. */
	private int blockSize;

	/** The tail block size. */
	private int tailBlockSize;

	/**
	 * Instantiates a new download common.
	 * 
	 * @param num_shares
	 *            the num_shares
	 * @param filesize
	 *            the filesize
	 */
	public DownloadCommon(int num_shares, long filesize) {
		shareHashTree = new IncompleteHashTree(num_shares);
		segmentNum = 0;
		this.filesize = filesize;
	}

	/**
	 * Gets the uEB.
	 * 
	 * @return the uEB
	 */
	public UEB getUEB() {
		return ueb;
	}

	/**
	 * Sets the uEB.
	 * 
	 * @param ueb
	 *            the new uEB
	 * @throws BadHashError
	 *             the bad hash error
	 * @throws NotEnoughHashesError
	 *             the not enough hashes error
	 */
	public void setUEB(UEB ueb) throws BadHashError, NotEnoughHashesError {
		this.ueb = ueb;
		numSegments = ueb.getNumSegements();
		EncodingParam pa = ueb.getCodecParams();
		EncodingParam lastpa = ueb.getTailCodecParams();
		blockSize = (int) MathUtils.div_ceil(pa.getSegmentSize(), pa.getK());
		tailBlockSize = (int) MathUtils.div_ceil(lastpa.getSegmentSize(),
				lastpa.getK());
		long tailSegmentSize = filesize % (blockSize * pa.getK());
		tailPad = (int) ((tailBlockSize * pa.getK()) - tailSegmentSize);

		ciphertextHashTree = new IncompleteHashTree(ueb.getNumSegements());

		shareHashTree.setHashes(ueb.getSharesRootHash());
		ciphertextHashTree.setHashes(ueb.getCipherTextRootHash());

	}

	/**
	 * Have ueb.
	 * 
	 * @return true, if successful
	 */
	public boolean haveUEB() {
		return ueb != null;
	}

	/**
	 * Gets the share hash tree.
	 * 
	 * @return the share hash tree
	 */
	public IncompleteHashTree getShareHashTree() {
		return shareHashTree;
	}

	/**
	 * Gets the ciphertext hash tree.
	 * 
	 * @return the ciphertext hash tree
	 */
	public IncompleteHashTree getCiphertextHashTree() {
		return ciphertextHashTree;
	}

	/**
	 * Gets the current segment num.
	 * 
	 * @return the current segment num
	 */
	public int getCurrentSegmentNum() {
		return segmentNum;
	}

	/**
	 * Next segment.
	 * 
	 * @return true, if successful
	 */
	public boolean nextSegment() {
		segmentNum++;
		if (segmentNum < numSegments) {
			return true;
		}
		segmentNum = 0;
		return false;
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
	 * Gets the block size.
	 * 
	 * @return the block size
	 */
	public int getBlockSize() {
		if (haveUEB()) {
			if (segmentNum == numSegments - 1) {
				return tailBlockSize;
			}
			return blockSize;
		}
		return 0;
	}

	/**
	 * Checks if is tail segment reached.
	 * 
	 * @return true, if is tail segment reached
	 */
	public boolean isTailSegmentReached() {
		if (segmentNum < numSegments - 1) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the padding value.
	 * 
	 * @return the padding value
	 */
	public int getPaddingValue() {
		if (segmentNum == numSegments - 1) {
			return tailPad;
		}
		return 0;
	}

	/**
	 * Gets the num segments.
	 * 
	 * @return the num segments
	 */
	public int getNumSegments() {
		return numSegments;
	}
}
