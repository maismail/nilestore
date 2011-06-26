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
package eg.nileu.cis.nilestore.immutable.downloader.segfetcher.port;

import java.util.Map;

import se.sics.kompics.Event;
import eg.nileu.cis.nilestore.common.StatusMsg;

// TODO: Auto-generated Javadoc
/**
 * The Class GetSegmentResponse.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class GetSegmentResponse extends Event {

	/** The status. */
	private final StatusMsg status;

	/** The blocks. */
	private final Map<Integer, byte[]> blocks;

	/**
	 * Instantiates a new gets the segment response.
	 * 
	 * @param status
	 *            the status
	 * @param blocks
	 *            the blocks
	 */
	public GetSegmentResponse(StatusMsg status, Map<Integer, byte[]> blocks) {
		this.status = status;
		this.blocks = blocks;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public StatusMsg getStatus() {
		return status;
	}

	/**
	 * Gets the blocks.
	 * 
	 * @return the blocks
	 */
	public Map<Integer, byte[]> getBlocks() {
		return blocks;
	}
}
