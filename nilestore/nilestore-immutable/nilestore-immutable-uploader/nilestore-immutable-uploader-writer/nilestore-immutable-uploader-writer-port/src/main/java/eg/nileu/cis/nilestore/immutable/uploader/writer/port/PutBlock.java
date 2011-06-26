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
package eg.nileu.cis.nilestore.immutable.uploader.writer.port;

import eg.nileu.cis.nilestore.immutable.common.PutGetData;

// TODO: Auto-generated Javadoc
/**
 * The Class PutBlock.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public final class PutBlock extends PutGetData {

	/** The segnum. */
	private final int segnum;

	/** The block data. */
	private final byte[] blockData;

	/**
	 * Instantiates a new put block.
	 * 
	 * @param segnum
	 *            the segnum
	 * @param data
	 *            the data
	 * @param destid
	 *            the destid
	 */
	public PutBlock(int segnum, byte[] data, String destid) {
		super(destid);
		this.segnum = segnum;
		this.blockData = data;
	}

	/**
	 * Gets the segnum.
	 * 
	 * @return the segnum
	 */
	public int getSegnum() {
		return segnum;
	}

	/**
	 * Gets the block data.
	 * 
	 * @return the block data
	 */
	public byte[] getBlockData() {
		return blockData;
	}

}
