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
package eg.nileu.cis.nilestore.availablepeers.decentralized;

import se.sics.kompics.Init;
import se.sics.kompics.p2p.overlay.chord.ChordConfiguration;
import eg.nileu.cis.nilestore.common.NilestoreAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class ChordPeerInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class ChordPeerInit extends Init {

	/** The chord configuration. */
	private final ChordConfiguration chordConfiguration;

	/** The self. */
	private final NilestoreAddress self;

	/**
	 * Instantiates a new chord peer init.
	 * 
	 * @param chordConfiguration
	 *            the chord configuration
	 * @param self
	 *            the self
	 */
	public ChordPeerInit(ChordConfiguration chordConfiguration,
			NilestoreAddress self) {
		this.chordConfiguration = chordConfiguration;
		this.self = self;
	}

	/**
	 * Gets the chord configuration.
	 * 
	 * @return the chord configuration
	 */
	public ChordConfiguration getChordConfiguration() {
		return chordConfiguration;
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
