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
package eg.nileu.cis.nilestore.immutable.downloader.sharefinder;

import eg.nileu.cis.nilestore.common.ComponentAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class Share.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class Share implements Comparable<Share> {

	/** The share num. */
	private final int shareNum;

	/** The RTT. */
	private final float RTT;

	/** The self. */
	private final ComponentAddress self;

	/**
	 * Instantiates a new share.
	 * 
	 * @param sharenum
	 *            the sharenum
	 * @param RTT
	 *            the rTT
	 * @param self
	 *            the self
	 */
	public Share(int sharenum, float RTT, ComponentAddress self) {
		this.shareNum = sharenum;
		this.RTT = RTT;
		this.self = self;
	}

	/**
	 * Gets the share num.
	 * 
	 * @return the share num
	 */
	public int getShareNum() {
		return shareNum;
	}

	/**
	 * Gets the rTT.
	 * 
	 * @return the rTT
	 */
	public float getRTT() {
		return RTT;
	}

	/**
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public ComponentAddress getSelf() {
		return self;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(RTT);
		result = prime * result + ((self == null) ? 0 : self.hashCode());
		result = prime * result + shareNum;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Share other = (Share) obj;
		if (Float.floatToIntBits(RTT) != Float.floatToIntBits(other.RTT))
			return false;
		if (self == null) {
			if (other.self != null)
				return false;
		} else if (!self.equals(other.self))
			return false;
		if (shareNum != other.shareNum)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Share other) {
		return Float.compare(RTT, other.getRTT());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Share [shareNum=" + shareNum + ", RTT=" + RTT
				+ ", destAddress=" + self + "]";
	}
}
