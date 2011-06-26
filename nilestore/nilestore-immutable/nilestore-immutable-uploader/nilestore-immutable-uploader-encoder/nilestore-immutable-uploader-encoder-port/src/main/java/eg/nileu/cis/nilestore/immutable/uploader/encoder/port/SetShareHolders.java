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

import java.util.Map;
import java.util.Set;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;
import eg.nileu.cis.nilestore.common.ComponentAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class SetShareHolders.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class SetShareHolders extends Event {

	/** The sharemap. */
	private final Map<Integer, ComponentAddress> sharemap;

	/** The servermap. */
	private final Map<Address, Set<Integer>> servermap;

	/**
	 * Instantiates a new sets the share holders.
	 * 
	 * @param sharemap
	 *            the sharemap
	 * @param servermap
	 *            the servermap
	 */
	public SetShareHolders(Map<Integer, ComponentAddress> sharemap,
			Map<Address, Set<Integer>> servermap) {
		this.sharemap = sharemap;
		this.servermap = servermap;
	}

	/**
	 * Gets the servermap.
	 * 
	 * @return the servermap
	 */
	public Map<Address, Set<Integer>> getServermap() {
		return servermap;
	}

	/**
	 * Gets the sharemap.
	 * 
	 * @return the sharemap
	 */
	public Map<Integer, ComponentAddress> getSharemap() {
		return sharemap;
	}
}
