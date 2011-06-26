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
package eg.nileu.cis.nilestore.immutable.uploader.peerselector.tahoe2;

import se.sics.kompics.Init;
import eg.nileu.cis.nilestore.common.NilestoreAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class NsTahoe2PeerSelectorInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsTahoe2PeerSelectorInit extends Init {

	/** The self. */
	private final NilestoreAddress self;

	/**
	 * Instantiates a new ns tahoe2 peer selector init.
	 * 
	 * @param self
	 *            the self
	 */
	public NsTahoe2PeerSelectorInit(NilestoreAddress self) {
		this.self = self;
	}

	/**
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public NilestoreAddress getSelf() {
		return self;
	}

}
