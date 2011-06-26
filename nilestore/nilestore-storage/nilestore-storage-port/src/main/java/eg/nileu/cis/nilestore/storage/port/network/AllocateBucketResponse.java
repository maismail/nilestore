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
import java.util.Set;

import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.ExtMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class AllocateBucketResponse.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class AllocateBucketResponse extends ExtMessage {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2305559811435849648L;

	/** The share map. */
	private final Map<Integer, String> shareMap;

	/** The alreadygot. */
	private final Set<Integer> alreadygot;

	/**
	 * Instantiates a new allocate bucket response.
	 * 
	 * @param source
	 *            the source
	 * @param destination
	 *            the destination
	 * @param shareMap
	 *            the share map
	 * @param alreadygot
	 *            the alreadygot
	 */
	public AllocateBucketResponse(ComponentAddress source,
			ComponentAddress destination, Map<Integer, String> shareMap,
			Set<Integer> alreadygot) {
		super(source, destination);
		this.shareMap = shareMap;
		this.alreadygot = alreadygot;
	}

	/**
	 * Gets the share map.
	 * 
	 * @return the share map
	 */
	public Map<Integer, String> getShareMap() {
		return shareMap;
	}

	/**
	 * Gets the alreadygot.
	 * 
	 * @return the alreadygot
	 */
	public Set<Integer> getAlreadygot() {
		return alreadygot;
	}
}
