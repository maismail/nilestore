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

import java.util.List;

import se.sics.kompics.Event;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.Share;

// TODO: Auto-generated Javadoc
/**
 * The Class AddShares.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class AddShares extends Event {

	/** The shares. */
	private final List<Share> shares;

	/**
	 * Instantiates a new adds the shares.
	 * 
	 * @param shares
	 *            the shares
	 */
	public AddShares(List<Share> shares) {
		this.shares = shares;
	}

	/**
	 * Gets the shares.
	 * 
	 * @return the shares
	 */
	public List<Share> getShares() {
		return shares;
	}
}
