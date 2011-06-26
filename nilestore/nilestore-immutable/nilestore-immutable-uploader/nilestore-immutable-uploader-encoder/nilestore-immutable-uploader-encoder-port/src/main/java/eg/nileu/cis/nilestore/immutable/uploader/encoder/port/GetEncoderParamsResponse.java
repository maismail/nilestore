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
package eg.nileu.cis.nilestore.immutable.uploader.encoder.port;

import se.sics.kompics.Event;

// TODO: Auto-generated Javadoc
/**
 * The Class GetEncoderParamsResponse.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class GetEncoderParamsResponse extends Event {

	/** The share size. */
	private final long shareSize;

	/** The block size. */
	private final int blockSize;

	/** The num segments. */
	private final int numSegments;

	/** The Max ext size. */
	private final int MaxExtSize;

	/**
	 * Instantiates a new gets the encoder params response.
	 * 
	 * @param sharesize
	 *            the sharesize
	 * @param blocksize
	 *            the blocksize
	 * @param numSegments
	 *            the num segments
	 */
	public GetEncoderParamsResponse(long sharesize, int blocksize,
			int numSegments) {
		this.shareSize = sharesize;
		this.blockSize = blocksize;
		this.numSegments = numSegments;
		MaxExtSize = 1000;
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
	 * Gets the max ext size.
	 * 
	 * @return the max ext size
	 */
	public int getMaxExtSize() {
		return MaxExtSize;
	}
}
