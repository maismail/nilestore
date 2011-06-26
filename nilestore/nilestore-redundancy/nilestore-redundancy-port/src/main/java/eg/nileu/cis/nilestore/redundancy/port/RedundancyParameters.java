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
package eg.nileu.cis.nilestore.redundancy.port;

// TODO: Auto-generated Javadoc
/**
 * The Class RedundancyParameters.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class RedundancyParameters {

	/** The k. */
	private final int k;

	/** The n. */
	private final int n;

	/**
	 * Instantiates a new redundancy parameters.
	 * 
	 * @param k
	 *            the k
	 * @param n
	 *            the n
	 */
	public RedundancyParameters(int k, int n) {
		this.k = k;
		this.n = n;
	}

	/**
	 * Gets the k.
	 * 
	 * @return the k
	 */
	public int getK() {
		return k;
	}

	/**
	 * Gets the n.
	 * 
	 * @return the n
	 */
	public int getN() {
		return n;
	}

	/**
	 * Checks if is 16 bit.
	 * 
	 * @return true, if is 16 bit
	 */
	public boolean is16Bit() {
		return this.n > 256;
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
		result = prime * result + k;
		result = prime * result + n;
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
		RedundancyParameters other = (RedundancyParameters) obj;
		if (k != other.k)
			return false;
		if (n != other.n)
			return false;
		return true;
	}

}
