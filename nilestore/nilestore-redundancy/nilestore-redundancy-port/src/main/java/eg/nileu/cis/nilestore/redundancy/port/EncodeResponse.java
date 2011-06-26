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
package eg.nileu.cis.nilestore.redundancy.port;

import se.sics.kompics.Response;
import eg.nileu.cis.nilestore.utils.ByteArray;

// TODO: Auto-generated Javadoc
/**
 * The Class EncodeResponse.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class EncodeResponse extends Response {

	/** The output buffers. */
	private final ByteArray[] outputBuffers;

	/**
	 * Instantiates a new encode response.
	 * 
	 * @param request
	 *            the request
	 * @param outputBuffers
	 *            the output buffers
	 */
	public EncodeResponse(Encode request, ByteArray[] outputBuffers) {
		super(request);
		this.outputBuffers = outputBuffers;
	}

	/**
	 * Gets the buffer.
	 * 
	 * @param index
	 *            the index
	 * @return the buffer
	 */
	public byte[] getBuffer(int index) {
		return outputBuffers[index].getBytes();
	}

	/**
	 * Gets the n.
	 * 
	 * @return the n
	 */
	public int getN() {
		return outputBuffers.length;
	}
}
