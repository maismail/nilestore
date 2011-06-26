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
package eg.nileu.cis.nilestore.availablepeers.decentralized.port;

import org.bitpedia.util.Base32;

import eg.nileu.cis.nilestore.availablepeers.port.GetPeers;
import eg.nileu.cis.nilestore.common.Agent;

// TODO: Auto-generated Javadoc
/**
 * The Class DGetPeers.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class DGetPeers extends GetPeers {

	/** The peer selection index. */
	private final byte[] peerSelectionIndex;

	/** The sharenum. */
	private final int sharenum;

	/**
	 * Instantiates a new d get peers.
	 * 
	 * @param requestAgent
	 *            the request agent
	 * @param peerSelectionIndex
	 *            the peer selection index
	 * @param sharenum
	 *            the sharenum
	 */
	public DGetPeers(Agent requestAgent, byte[] peerSelectionIndex, int sharenum) {
		super(requestAgent);
		this.peerSelectionIndex = peerSelectionIndex;
		this.sharenum = sharenum;
	}

	/**
	 * Instantiates a new d get peers.
	 * 
	 * @param requestAgent
	 *            the request agent
	 * @param peerSelectionIndex
	 *            the peer selection index
	 * @param sharenum
	 *            the sharenum
	 */
	public DGetPeers(Agent requestAgent, String peerSelectionIndex, int sharenum) {
		this(requestAgent, Base32.decode(peerSelectionIndex), sharenum);
	}

	/**
	 * Gets the sharenum.
	 * 
	 * @return the sharenum
	 */
	public int getSharenum() {
		return sharenum;
	}

	/**
	 * Gets the peer selection index.
	 * 
	 * @return the peer selection index
	 */
	public byte[] getPeerSelectionIndex() {
		return peerSelectionIndex;
	}
}
