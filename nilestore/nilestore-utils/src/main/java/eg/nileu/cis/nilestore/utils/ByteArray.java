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
package eg.nileu.cis.nilestore.utils;

import java.util.Arrays;

import org.bitpedia.util.Base32;

// TODO: Auto-generated Javadoc
/**
 * The Class ByteArray.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class ByteArray {

	/** The len. */
	private final int len;

	/** The arr. */
	private final byte[] arr;

	/**
	 * Instantiates a new byte array.
	 * 
	 * @param len
	 *            the len
	 */
	public ByteArray(int len) {
		this(new byte[len]);
	}

	/**
	 * Instantiates a new byte array.
	 * 
	 * @param arr
	 *            the arr
	 */
	public ByteArray(byte[] arr) {
		this.arr = arr;
		this.len = arr.length;
	}

	/**
	 * Gets the bytes.
	 * 
	 * @return the bytes
	 */
	public byte[] getBytes() {
		return arr;
	}

	/**
	 * Gets the arrayin base32.
	 * 
	 * @return the arrayin base32
	 */
	public String getArrayinBase32() {
		return Base32.encode(arr);
	}

	/**
	 * Gets the length.
	 * 
	 * @return the length
	 */
	public int getLength() {
		return len;
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
		result = prime * result + Arrays.hashCode(arr);
		result = prime * result + len;
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
		ByteArray other = (ByteArray) obj;
		if (!Arrays.equals(arr, other.arr))
			return false;
		if (len != other.len)
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
		return "ByteArray [len=" + len + ", arr=" + Arrays.toString(arr) + "]";
	}

}
