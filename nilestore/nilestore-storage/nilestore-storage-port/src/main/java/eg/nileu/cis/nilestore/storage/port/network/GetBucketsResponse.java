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
package eg.nileu.cis.nilestore.storage.port.network;

import java.util.Map;

import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.ExtMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class GetBucketsResponse.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class GetBucketsResponse extends ExtMessage {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4351615272764554616L;

	/** The shares. */
	private final Map<Integer, String> shares;

	/**
	 * Instantiates a new gets the buckets response.
	 * 
	 * @param source
	 *            the source
	 * @param destination
	 *            the destination
	 * @param shares
	 *            the shares
	 */
	public GetBucketsResponse(ComponentAddress source,
			ComponentAddress destination, Map<Integer, String> shares) {

		super(source, destination);
		this.shares = shares;
	}

	/**
	 * Gets the shares.
	 * 
	 * @return the shares
	 */
	public Map<Integer, String> getShares() {
		return shares;
	}

}
