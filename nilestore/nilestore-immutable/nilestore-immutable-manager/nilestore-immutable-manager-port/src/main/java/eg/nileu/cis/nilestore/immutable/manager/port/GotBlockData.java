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
package eg.nileu.cis.nilestore.immutable.manager.port;

import se.sics.kompics.Response;
import eg.nileu.cis.nilestore.common.StatusMsg;

// TODO: Auto-generated Javadoc
/**
 * The Class GotBlockData.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class GotBlockData extends Response {

	/** The index. */
	private final int index;

	/** The total. */
	private final int total;

	/** The data. */
	private final byte[] data;

	/** The storage index. */
	private final String storageIndex;

	/** The request event. */
	private final Download requestEvent;

	/** The status. */
	private final StatusMsg status;

	/**
	 * Instantiates a new got block data.
	 * 
	 * @param requestEvent
	 *            the request event
	 * @param status
	 *            the status
	 * @param index
	 *            the index
	 * @param total
	 *            the total
	 * @param data
	 *            the data
	 * @param storageIndex
	 *            the storage index
	 */
	public GotBlockData(Download requestEvent, StatusMsg status, int index,
			int total, byte[] data, String storageIndex) {
		super(requestEvent);
		this.index = index;
		this.total = total;
		this.data = data;
		this.storageIndex = storageIndex;
		this.requestEvent = requestEvent;
		this.status = status;
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
	 * Gets the request.
	 * 
	 * @return the request
	 */
	public Download getRequest() {
		return requestEvent;
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
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Gets the storage index.
	 * 
	 * @return the storage index
	 */
	public String getStorageIndex() {
		return storageIndex;
	}
}
