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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

// TODO: Auto-generated Javadoc
/**
 * The Class NetString.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NetString {

	/** The Constant COLON. */
	public static final byte COLON = ':';

	/** The Constant COMMA. */
	public static final byte COMMA = ',';

	/**
	 * To net string.
	 * 
	 * @param s
	 *            the s
	 * @return the byte[]
	 */
	public static byte[] toNetString(String s) {
		// String r = String.format("%d:%s,", s.length() , s);
		// return r;
		byte[] data = toNetString(s.getBytes(Charset.forName("UTF-8")));
		return data;
	}

	/**
	 * To net string.
	 * 
	 * @param s
	 *            the s
	 * @return the byte[]
	 */
	public static byte[] toNetString(byte[] s) {
		String len = String.valueOf(s.length);
		ByteBuffer buffer = ByteBuffer.allocate(s.length + len.length() + 2);

		buffer.put(len.getBytes());
		buffer.put(COLON);
		buffer.put(s);
		buffer.put(COMMA);
		return buffer.array();
	}

	/**
	 * To net string.
	 * 
	 * @param bs
	 *            the bs
	 * @return the byte[]
	 */
	public static byte[] toNetString(byte[]... bs) {
		byte[] data = DataUtils.join(bs);
		return toNetString(data);
	}
}
