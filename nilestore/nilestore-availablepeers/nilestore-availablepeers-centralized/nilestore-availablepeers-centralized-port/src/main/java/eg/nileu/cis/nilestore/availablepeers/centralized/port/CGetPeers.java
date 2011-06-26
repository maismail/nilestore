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
package eg.nileu.cis.nilestore.availablepeers.centralized.port;

import org.bitpedia.util.Base32;

import eg.nileu.cis.nilestore.availablepeers.port.GetPeers;
import eg.nileu.cis.nilestore.common.Agent;

// TODO: Auto-generated Javadoc
/**
 * The Class CGetPeers.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class CGetPeers extends GetPeers {

	/** The peer selection index. */
	private final byte[] peerSelectionIndex;

	/** The len. */
	private final int len;

	/**
	 * Instantiates a new c get peers.
	 * 
	 * @param requestAgent
	 *            the request agent
	 * @param peerSelectionIndex
	 *            the peer selection index
	 * @param length
	 *            the length
	 */
	public CGetPeers(Agent requestAgent, byte[] peerSelectionIndex, int length) {
		super(requestAgent);
		this.len = length;
		this.peerSelectionIndex = peerSelectionIndex;
	}

	/**
	 * Instantiates a new c get peers.
	 * 
	 * @param requestAgent
	 *            the request agent
	 * @param peerSelectionIndex
	 *            the peer selection index
	 */
	public CGetPeers(Agent requestAgent, byte[] peerSelectionIndex) {
		this(requestAgent, peerSelectionIndex, -1);
	}

	/**
	 * Instantiates a new c get peers.
	 * 
	 * @param requestAgent
	 *            the request agent
	 * @param peerSelectionIndex
	 *            the peer selection index
	 * @param length
	 *            the length
	 */
	public CGetPeers(Agent requestAgent, String peerSelectionIndex, int length) {
		this(requestAgent, Base32.decode(peerSelectionIndex), length);
	}

	/**
	 * Instantiates a new c get peers.
	 * 
	 * @param requestAgent
	 *            the request agent
	 * @param peerSelectionIndex
	 *            the peer selection index
	 */
	public CGetPeers(Agent requestAgent, String peerSelectionIndex) {
		this(requestAgent, peerSelectionIndex, -1);
	}

	/**
	 * Gets the length.
	 * 
	 * @return the length
	 */
	public int getLength() {
		return len;
	}

	/**
	 * Checks if is limited.
	 * 
	 * @return true, if is limited
	 */
	public boolean isLimited() {
		return len != -1;
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
