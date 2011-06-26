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
package eg.nileu.cis.nilestore.immutable.downloader.node.port;

import se.sics.kompics.Event;

// TODO: Auto-generated Javadoc
/**
 * The Class GotValidatedSegment.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class GotValidatedSegment extends Event {

	/** The segment. */
	private final byte[] segment;

	/** The index. */
	private final int index;

	/** The total. */
	private final int total;

	/**
	 * Instantiates a new got validated segment.
	 * 
	 * @param segment
	 *            the segment
	 * @param index
	 *            the index
	 * @param total
	 *            the total
	 */
	public GotValidatedSegment(byte[] segment, int index, int total) {
		this.segment = segment;
		this.index = index;
		this.total = total;
	}

	/**
	 * Gets the index.
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Gets the total.
	 * 
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * Gets the segment.
	 * 
	 * @return the segment
	 */
	public byte[] getSegment() {
		return segment;
	}
}
