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
package eg.nileu.cis.nilestore.immutable.downloader.reader.port;

import eg.nileu.cis.nilestore.immutable.common.PutGetData;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCommonParameters.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class SetCommonParameters extends PutGetData {

	/** The tail block size. */
	private final int tailBlockSize;

	/** The num segments. */
	private final int numSegments;

	/** The index at share ht. */
	private final int indexAtShareHT;

	/**
	 * Instantiates a new sets the common parameters.
	 * 
	 * @param destId
	 *            the dest id
	 * @param tailBlockSize
	 *            the tail block size
	 * @param numSegments
	 *            the num segments
	 * @param indexAtShareHT
	 *            the index at share ht
	 */
	public SetCommonParameters(String destId, int tailBlockSize,
			int numSegments, int indexAtShareHT) {
		super(destId);
		this.tailBlockSize = tailBlockSize;
		this.numSegments = numSegments;
		this.indexAtShareHT = indexAtShareHT;
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
	 * Gets the index at share ht.
	 * 
	 * @return the index at share ht
	 */
	public int getIndexAtShareHT() {
		return indexAtShareHT;
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
