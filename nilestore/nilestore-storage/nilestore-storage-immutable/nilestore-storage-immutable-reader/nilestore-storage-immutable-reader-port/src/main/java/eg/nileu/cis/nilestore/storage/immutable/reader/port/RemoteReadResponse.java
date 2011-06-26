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
package eg.nileu.cis.nilestore.storage.immutable.reader.port;

import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.ExtMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class RemoteReadResponse.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class RemoteReadResponse extends ExtMessage {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -881380993204184138L;

	/** The request id. */
	private final long requestId;

	/** The data. */
	private final byte[] data;

	/**
	 * Instantiates a new remote read response.
	 * 
	 * @param source
	 *            the source
	 * @param destination
	 *            the destination
	 * @param requestId
	 *            the request id
	 * @param data
	 *            the data
	 */
	public RemoteReadResponse(ComponentAddress source,
			ComponentAddress destination, long requestId, byte[] data) {
		super(source, destination);
		this.requestId = requestId;
		this.data = data;
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
	 * Gets the request id.
	 * 
	 * @return the request id
	 */
	public long getRequestId() {
		return requestId;
	}

}
