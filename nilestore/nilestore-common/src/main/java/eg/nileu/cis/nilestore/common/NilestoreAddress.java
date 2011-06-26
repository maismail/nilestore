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
package eg.nileu.cis.nilestore.common;

import java.math.BigInteger;
import java.util.Arrays;

import org.bitpedia.util.Base32;

import se.sics.kompics.address.Address;
import se.sics.kompics.p2p.overlay.OverlayAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class NilestoreAddress.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public final class NilestoreAddress extends OverlayAddress implements
		Comparable<NilestoreAddress> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3032426007347022715L;

	/** The peer nickname. */
	private final String peerNickname;

	/** The peer id. */
	private final String peerID;

	/** The peer id bytes. */
	private final byte[] peerIdBytes;

	/** The web port. */
	private final int webPort;

	/** The available space. */
	private final long availableSpace;

	/**
	 * Instantiates a new nilestore address.
	 * 
	 * @param peerAddress
	 *            the peer address
	 * @param webPort
	 *            the web port
	 * @param nickname
	 *            the nickname
	 * @param peerID
	 *            the peer id
	 * @param availableSpace
	 *            the available space
	 */
	public NilestoreAddress(Address peerAddress, int webPort, String nickname,
			String peerID, long availableSpace) {
		super(peerAddress);
		this.peerID = peerID;
		this.peerIdBytes = Base32.decode(peerID);
		this.peerNickname = nickname;
		this.webPort = webPort;
		this.availableSpace = availableSpace;
	}

	/**
	 * Gets the web port.
	 * 
	 * @return the web port
	 */
	public int getWebPort() {
		return webPort;
	}

	/**
	 * Gets the peer id.
	 * 
	 * @return the peer id
	 */
	public String getPeerId() {
		return peerID;
	}

	/**
	 * Gets the peer id bytes.
	 * 
	 * @return the peer id bytes
	 */
	public byte[] getPeerIdBytes() {
		return peerIdBytes;
	}

	/**
	 * Gets the nickname.
	 * 
	 * @return the nickname
	 */
	public String getNickname() {
		return peerNickname;
	}

	/**
	 * Gets the available space.
	 * 
	 * @return the available space
	 */
	public long getAvailableSpace() {
		return availableSpace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.sics.kompics.p2p.overlay.OverlayAddress#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((peerID == null) ? 0 : peerID.hashCode());
		result = prime * result + Arrays.hashCode(peerIdBytes);
		result = prime * result
				+ ((peerNickname == null) ? 0 : peerNickname.hashCode());
		result = prime * result + webPort;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.sics.kompics.p2p.overlay.OverlayAddress#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		NilestoreAddress other = (NilestoreAddress) obj;
		if (peerID == null) {
			if (other.peerID != null)
				return false;
		} else if (!peerID.equals(other.peerID))
			return false;
		if (!Arrays.equals(peerIdBytes, other.peerIdBytes))
			return false;
		if (peerNickname == null) {
			if (other.peerNickname != null)
				return false;
		} else if (!peerNickname.equals(other.peerNickname))
			return false;
		if (webPort != other.webPort)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s(%d@%s:%s)", peerNickname, peerAddress.getId(),
				peerAddress.getIp().getHostName(), peerAddress.getPort());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(NilestoreAddress o) {

		BigInteger me = new BigInteger(peerIdBytes);
		BigInteger other = new BigInteger(o.getPeerIdBytes());
		return me.compareTo(other);
	}
}
