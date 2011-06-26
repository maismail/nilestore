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

import java.math.BigInteger;

// TODO: Auto-generated Javadoc
/**
 * The Class Base62.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class Base62 {

	/** The Constant CHARS. */
	private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	/** The Constant BASE. */
	private static final BigInteger BASE = BigInteger.valueOf(62);

	/**
	 * Instantiates a new base62.
	 */
	public Base62() {
	}

	/**
	 * Encode.
	 * 
	 * @param data
	 *            the data
	 * @return the string
	 */
	public static String encode(String data) {
		return encode(data.getBytes());
	}

	/**
	 * Encode.
	 * 
	 * @param data
	 *            the data
	 * @return the string
	 */
	public static String encode(byte[] data) {
		BigInteger input = new BigInteger(data);
		StringBuilder sb = new StringBuilder();

		while (input.compareTo(BigInteger.ZERO) == 1) {
			BigInteger[] quot_remainder = input.divideAndRemainder(BASE);
			// quotient
			input = quot_remainder[0];
			sb.insert(0, CHARS.charAt(quot_remainder[1].intValue()));
		}
		return sb.toString();
	}

	/**
	 * Decode.
	 * 
	 * @param data
	 *            the data
	 * @return the byte[]
	 */
	public static byte[] decode(String data) {
		BigInteger decoded = BigInteger.ZERO;
		for (int i = 0; i < data.length(); i++) {
			int bi = data.length() - i - 1;
			int charVal = CHARS.indexOf(data.charAt(bi));
			decoded = decoded.add(BigInteger.valueOf(charVal).multiply(
					BASE.pow(i)));
		}

		return decoded.toByteArray();
	}

}
